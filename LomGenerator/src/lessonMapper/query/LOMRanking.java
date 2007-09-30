/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query;

import java.math.BigDecimal;
import java.math.MathContext;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.Vector;

import lessonMapper.diffusion.Diffusion;
import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.diffusion.fixpoint.DifValueHolder;
import lessonMapper.diffusion.fixpoint.EmptiedSugDifValueHolder;
import lessonMapper.diffusion.fixpoint.FixPointValueDiffusion;
import lessonMapper.diffusion.fixpoint.SugDifValue;
import lessonMapper.diffusion.fixpoint.SugDifValueHolder;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;
import lessonMapper.lom.diffuse.LOMSuggestionProbability;
import lessonMapper.query.lucene.IndexDB;
import lor.LOR;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.tartarus.snowball.SnowballProgram;

import util.Couple;
import util.SortedScoreRankList;

/**
 * Result ranking is based on the suggestion values generated by the diffusion
 * framework. A result obtained a additional point when a attribute value match
 * with one of the suggestion. Better rank is given to results accumulating more
 * points.
 * 
 * @author omotelet
 */

public class LOMRanking {

	
	public static URL ITSRankingAttributes = LOMQuery.class.getResource("resources/queryAttributes.xml");

	/**
	 * 
	 */
	public static final double ITSDiffusionDecrease = 0.5;

	/**
	 * 
	 */
	public static final double epsilon = 0.0001;

	/**
	 * 
	 */
	public static final MathContext ITSPrecision = MathContext.DECIMAL64;

	/**
	 * 
	 */
	public static final Comparator<Double> ITSreverseComparator = new Comparator<Double>() {
		public int compare(Double aO1, Double aO2) {
			return -1 * Double.compare(aO1, aO2);
		}
	};

	/**
	 * 
	 */
	protected static Vector<LOMAttribute> ITSAttributes ;

	
	
	public static Vector<LOMAttribute> getAttributeList() {
		if (ITSAttributes==null)
			ITSAttributes = LOMAttribute.getAttributeList(ITSRankingAttributes);
		return ITSAttributes;
	}
	
	
	/**
	 * 
	 */
	protected LOM itsLOM;

	/**
	 * 
	 */
	protected String itsKeywordQuery;

	/**
	 * 
	 */
	protected SortedScoreRankList<LOM> itsLuceneResults;

	/**
	 * 
	 */
	protected Map<LOMAttribute, SortedScoreRankList<LOM>> itsSuggestionResultMap,itsRestrictionResultMap,
										itsSuggestionLuceneResultMap,itsRestrictionLuceneResultMap;
	

	/**
	 * 
	 */
	protected boolean isInitLucene = false, isInitSuggestion = false,
	isInitSuggestionLucene = false, isInitRestrictionLucene = false, isInitRestriction = false;

	/**
	 * 
	 */
	protected static List<LOM> ITSEntireDBLOMs;
	

	/**
	 * 
	 */
	public static Map<Couple<LOM,LOMAttribute> ,DifValueHolder> ITSEntireDBEmptiedSugValueHolders;
	
	
	public static int getEntireDBSize(){
		return ITSEntireDBLOMs.size();
	}
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public static List<LOM> getEntireDBLOMs() {
		if (ITSEntireDBLOMs == null) {
			List<String> theResults = LOR.INSTANCE
					.xmlQuery("declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; /ims:lom");
			ITSEntireDBLOMs = new ArrayList<LOM>();
			for (String element : theResults)
				if (!element.equals("")) {
					LOM theLOM = LOM.getLOMWithXMLRepresentation(element);
					ITSEntireDBLOMs.add(theLOM);
				}
			Collections.sort(ITSEntireDBLOMs, new Comparator<LOM>() {
				public int compare(LOM aO1, LOM aO2) {
					return aO1.getID().compareTo(aO2.getID());
				}
			});
			for (LOMAttribute theAttribute : getAttributeList()) {
				SugDifValueHolder theHolder = new SugDifValueHolder(theAttribute);
				FixPointValueDiffusion.diffuseChangesOf( ITSEntireDBLOMs,theHolder);
				List<LOM> theDBCopy = new ArrayList<LOM>();
				theDBCopy.addAll(ITSEntireDBLOMs);
				for (LOM theLOM : ITSEntireDBLOMs) {
					Collections.shuffle(theDBCopy);
					EmptiedSugDifValueHolder theEmptiedHolder = EmptiedSugDifValueHolder.setInstanceFor(theLOM, theHolder);
					FixPointValueDiffusion.diffuseChangesOf( theDBCopy,theEmptiedHolder);
				}
			}
		}
		return ITSEntireDBLOMs;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aKeywordQuery 
	 */
	public LOMRanking(LOM aLOM, String aKeywordQuery) {
		itsLOM = aLOM;
		itsKeywordQuery = aKeywordQuery;
	}

	/**
	 * 
	 */
	public void initLuceneResults() {
		try {
			BooleanQuery.setMaxClauseCount(1000 * 1000);
			IndexReader reader = IndexReader.open(IndexDB.ITSIndexLocation);
			Searcher searcher = new IndexSearcher(reader);
			Analyzer analyzer = new StandardAnalyzer();
			QueryParser parser = new QueryParser("content", analyzer);
			//version with double wild card
			/*String theReformulatedQuery = SnowballProgram
					.stemListAndAddLimiters(itsKeywordQuery, "es", "zk*", "*");*/
			// version for stemmed index
			String theReformulatedQuery = SnowballProgram
			.stemList(itsKeywordQuery, "es");
			Query query = parser.parse(theReformulatedQuery);
			Hits hits = searcher.search(query);
			itsLuceneResults = new SortedScoreRankList<LOM>(true,false);
			for (int i = 0; i < hits.length(); i++) {
				LOM theLOM = LOM.getLOM(hits.doc(i).get("id"));
				itsLuceneResults.add(theLOM, (double) hits.score(i));
			}
			for (LOM theLOM : ITSEntireDBLOMs) 
				if (!itsLuceneResults.contains(theLOM)) itsLuceneResults.add(theLOM, 10^-SortedScoreRankList.ITSPrecision);
			reader.close();
			isInitLucene = true;
		} catch (Exception e) {
			e.printStackTrace();
			isInitLucene = false;
		}
	}

	/**
	 * calculate the distance between the scores of the suggestions of the document to be ranked
	 * and the score of the expected suggestions
	 * Distance  is based on kullback-leibler relative entropy
	 * KL(Pr || Pexp) = Sum_x ( Pr(x) * log(Pr(x)/Pexp(x)) )
	 * scores are not taken into account in case of not corresponding with therestriction given in parameter.
	 * 
	 * @param theSet 
	 * @param theRankedSuggestionScore 
	 * @param theExpectedSuggestionScore 
	 * @param theAttribute 
	 * 
	 * @return 
	 */
	public double kullbackLeiblerSimilarity(
			Map<String, Double> theRankedSuggestionScore,
			Map<String, Double> theExpectedSuggestionScore,
			LOMRestrictionSet theSet, LOMAttribute theAttribute) {
		double theDistance = 0;
		for (String theString : theExpectedSuggestionScore.keySet()) {
			boolean isSatisfy = true;
			if (theSet != null && !theSet.isEmpty())
				for (LOMRestrictionValue theRestriction : theSet
						.getLOMRestrictionValues()) {
					isSatisfy = theRestriction.isSatisfiedBy(new LOMValueVocabularySet(
							theString, theAttribute));
					if (!isSatisfy)
						break;
				}
			if (isSatisfy) {
				double theRankedScore = epsilon;
				if (theRankedSuggestionScore.containsKey(theString))
					theRankedScore = theRankedSuggestionScore.get(theString);
				theDistance += theRankedScore
						* (Math.log(theRankedScore) - Math
								.log(theExpectedSuggestionScore.get(theString)));
			}
		}

		theDistance = Math.abs(theDistance) + 1;
		//if (theDistance <1) return 1;
		return 1 / theDistance;
	}

	/**
	 * return  the similarity between the two score.
	 * WARNING the set extracted from the map given in parameters should be deterministically ordered 
	 * in order to get deterministic final results
	 * 
	 * @param theSet 
	 * @param theRankedSuggestionScore 
	 * @param theExpectedSuggestionScore 
	 * @param theAttribute 
	 * 
	 * @return 
	 */
	public double vectorSimilarity(
			Map<String, BigDecimal> theRankedSuggestionScore,
			Map<String, BigDecimal> theExpectedSuggestionScore,
			LOMRestrictionSet theSet, LOMAttribute theAttribute) {
		BigDecimal theDistance = BigDecimal.ZERO;
		BigDecimal theSquaredRanked = BigDecimal.ZERO, theSquaredExpected = BigDecimal.ZERO;
		
		
		//List<Couple<BigDecimal, BigDecimal>> theConsideredStrings = new ArrayList<Couple<BigDecimal, BigDecimal>>();
		Set<String> theAllKeys = new LinkedHashSet<String>();
		theAllKeys.addAll(theExpectedSuggestionScore.keySet());
		theAllKeys.addAll(theRankedSuggestionScore.keySet());
		
		for (String theString : theAllKeys) {
			boolean isSatisfy = true;
			if (theSet != null && !theSet.isEmpty())
				for (LOMRestrictionValue theRestriction : theSet
						.getLOMRestrictionValues()) {
					isSatisfy = theRestriction.isSatisfiedBy(new LOMValueVocabularySet(
							theString, theAttribute));
					if (!isSatisfy)
						break;
				}
			if (isSatisfy) {
				boolean isAggreagatedDistanceNull = false;
				BigDecimal theRankedScore;
				if (theRankedSuggestionScore.containsKey(theString)) {
					theRankedScore = theRankedSuggestionScore.get(theString);
					theSquaredRanked = theSquaredRanked.add(theRankedScore
							.pow(2), ITSPrecision);
				}
				else
					theRankedScore = BigDecimal.ZERO;
				
				BigDecimal theExpectedScore;
				if ( theExpectedSuggestionScore.containsKey(theString)){
					theExpectedScore = theExpectedSuggestionScore
						.get(theString);
					theSquaredExpected = theSquaredExpected.add(theExpectedScore.pow(2), ITSPrecision);
				}
				else theExpectedScore = BigDecimal.ZERO;
				
				if (!isAggreagatedDistanceNull)
					theDistance = theDistance.add(theRankedScore
						.multiply(theExpectedScore), ITSPrecision);
			}
		}
		if (theSquaredRanked.signum() == 0 || theSquaredExpected.signum() == 0)
			return 0;
		BigDecimal theDividand = theSquaredRanked.multiply(theSquaredExpected,
				ITSPrecision);
		return theDistance.divide(
				new BigDecimal(StrictMath.sqrt(theDividand.doubleValue()),
						ITSPrecision), ITSPrecision).doubleValue();
	}

	/**
	 * do the compilation of the values of the suggestion using the Noisy Or method
	 * i.e. for each value we give 1-Product(1-ScoreForPath)
	 * 
	 * @param aSuggestion 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static Map<String, Double> compileSuggestionScore(
			LOMValue aSuggestion, LOMAttribute aAttribute) {
		Map<String, Double> theScores = new LinkedHashMap<String, Double>();
		if (aSuggestion instanceof LOMValueSet)
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theSug : ((LOMValueSet) aSuggestion)
					.getSet()) {
				StringTokenizer theStringTokenizer = new StringTokenizer(theSug
						.getLeftElement(), LOM.ITSTokenizerLimits );
				for (String theToken; theStringTokenizer.hasMoreTokens();) {
					theToken = SnowballProgram.stem(
							theStringTokenizer.nextToken(), "es").toLowerCase();
					if (!theScores.containsKey(theToken)) {
						theScores.put(theToken, 1.0);
					}
					// theScore* (1-score(path))
					theScores.put(theToken, theScores.get(theToken)
							* (1.0 - getScore(theSug.getRightElement(),
									aAttribute)));
				}
			}
		// theScore = 1- theScore
		for (String theString : theScores.keySet()) {
			// set the minimum score to epsilon
			double theScore = Math.max(1.0 - theScores.get(theString), epsilon);
			theScores.put(theString, theScore);
		}
		return theScores;
	}

	/**
	 * return the score for a suggestion path.
	 * 
	 * @param aAttribute 
	 * @param aPath 
	 * 
	 * @return 
	 */
	public static double getScore(List<Couple<LOM, LOMRelationType>> aPath,
			LOMAttribute aAttribute) {
		if (aPath != null) {
			/*double theDecrease = ITSDiffusionDecrease;
			 for (Couple<LOM, LOMRelationType> theCouple : aPath) {
			 theDecrease *= ITSDiffusionDecrease*SuggestionProbability.getInstance().getProbaFor(aAttribute, theCouple.getRightElement());
			 } 
			 return theDecrease;*/
			return Math.pow(ITSDiffusionDecrease, aPath.size() + 1);
		}
		return ITSDiffusionDecrease;
	}

	/**
	 * do the compilation of the values of the suggestion using
	 * the probas of the intersections and taking the max.
	 * 
	 * @param useStem 
	 * @param aSuggestion 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static Map<String, BigDecimal> compileSuggestionScoreBasedOnProba(
			LOMValue aSuggestion, LOMAttribute aAttribute, boolean useStem) {
		Map<String, BigDecimal> theScores = new LinkedHashMap<String, BigDecimal>(); //we use a tree map to insure next calculus resitance
		if (aSuggestion instanceof LOMValueSet)
			for (Couple<String, List<Couple<LOM, LOMRelationType>>> theSug : ((LOMValueSet) aSuggestion)
					.getSet()) {
				StringTokenizer theStringTokenizer = new StringTokenizer(theSug
						.getLeftElement(), LOM.ITSTokenizerLimits );
				for (String theToken; theStringTokenizer.hasMoreTokens();) {
					if (useStem)
						theToken = SnowballProgram.stem(
								theStringTokenizer.nextToken(), "es")
								.toLowerCase();
					else
						theToken = theStringTokenizer.nextToken().toLowerCase()
								.trim();
					BigDecimal theScoreWithProba = getScoreWithProba(theSug
							.getRightElement(), aAttribute);
					if (theScoreWithProba.doubleValue() >= 0) { //set a minimum score for considering an element
						if (!theScores.containsKey(theToken)) {
							theScores.put(theToken, theScoreWithProba);
						} else
							theScores.put(theToken, theScores.get(theToken)
									.max(theScoreWithProba));
					}
				}
			}
		return theScores;
	}

	/**
	 * return the score for a suggestion path.
	 * 
	 * @param aAttribute 
	 * @param aPath 
	 * 
	 * @return 
	 */
	public static BigDecimal getScoreWithProba(
			List<Couple<LOM, LOMRelationType>> aPath, LOMAttribute aAttribute) {
		BigDecimal theDecrease = BigDecimal.ONE;
		if (aPath != null)
			for (Couple<LOM, LOMRelationType> theCouple : aPath) {
				
				BigDecimal theProba;
				try {
					theProba= LOMSuggestionProbability.getLOMInstance()
					.getProbaFor(aAttribute, theCouple.getRightElement());
				} catch (Exception e) {
					e.printStackTrace();
					theProba=BigDecimal.ONE;
				}
				theDecrease = theDecrease.multiply(theProba, ITSPrecision);
			}
		return theDecrease;
	}

	/**
	 * 
	 */
	static Map<Couple<LOM, LOMAttribute>, Map<String, BigDecimal>> ITSSuggestionProbaCache = new HashMap<Couple<LOM, LOMAttribute>, Map<String, BigDecimal>>();

	/**
	 * initSuggestionResult based on the fixed point diffusion.
	 */
	public void initSuggestionResultsFixedPoint() {
		itsSuggestionResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			SugDifValue theExpectedSugValue = SugDifValueHolder.getInstance(theAttribute)
												.getValue(itsLOM);
			for (LOM theLOM : getEntireDBLOMs()) {
				SugDifValue theDBSugValue = (SugDifValue)EmptiedSugDifValueHolder.
							getInstanceFor(theLOM, theAttribute).getValue(theLOM);		
				Map<String, BigDecimal> theDBList = theDBSugValue.getSuggestionListAsStemmedBigDecimal();
				Map<String, BigDecimal> theExpectedList = theExpectedSugValue.getSuggestionListAsStemmedBigDecimal();
				//remove the terms with score 1 form the list it is equivalent to calculate the sug 
				//in the query with the suggested term   
				for (String theString : theDBSugValue.getLOMValuesAsList()){
					theDBList.remove(theString);
					theExpectedList.remove(theString);
				}
			
				double theScore = vectorSimilarity(theDBList,
						theExpectedList, null, theAttribute);
				theScoreList.add(theLOM, theScore);
			}
			itsSuggestionResultMap.put(theAttribute, theScoreList);
		}
		isInitSuggestion = true;
	}

	/**
	 * 
	 */
	public void initSuggestionResults(){
		initSuggestionResultsFixedPoint();
	}
	
	
	
	/**
	 * initSuggestion results using the breathfirst diffusion system.
	 */
	public void initSuggestionResultsBreathFirst() {
		itsSuggestionResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			for (LOM theLOM : getEntireDBLOMs()) {
				LOMValue theExpectedSuggestion = Diffusion.SugDif(itsLOM,
						theAttribute);
				Map<String, BigDecimal> theSuggestionProbas;
				Couple<LOM, LOMAttribute> theCouple = new Couple<LOM, LOMAttribute>(
						theLOM, theAttribute);
				//use a cache to calculate suggestionproba of DB elt
				if (!ITSSuggestionProbaCache.containsKey(theCouple)) {
					LOMValue theSuggestionToBeRanked = Diffusion.SugDif(theLOM,
							theAttribute);
					//Add value of ranked element
					if (false){
						if (theSuggestionToBeRanked == null)
						 theSuggestionToBeRanked = theAttribute
						 .getValueIn(theLOM);
						 else
						 theSuggestionToBeRanked.addValue(theAttribute
						 .getValueIn(theLOM));
					}
					theSuggestionProbas = compileSuggestionScoreBasedOnProba(
							theSuggestionToBeRanked, theAttribute, true);
					ITSSuggestionProbaCache.put(theCouple, theSuggestionProbas);
					//ITSSugCacheLog.println(theCouple.toString() + theSuggestionProbas.toString());
				} else
					theSuggestionProbas = ITSSuggestionProbaCache
							.get(theCouple);
				Map<String, BigDecimal> theExpectedSuggestionProbas = compileSuggestionScoreBasedOnProba(
						theExpectedSuggestion, theAttribute, true);
				//LOMRestrictionSet theRestriction = Diffusion.ResDif(itsLOM,theAttribute);
				double theScore = vectorSimilarity(theSuggestionProbas,
						theExpectedSuggestionProbas, null, theAttribute);
				theScoreList.add(theLOM, theScore);
			}
			itsSuggestionResultMap.put(theAttribute, theScoreList);
		}
		isInitSuggestion = true;
	}

	/**
	 * 
	 */
	public void initRestrictionResults() {
		itsRestrictionResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			for (LOM theLOM : getEntireDBLOMs()) {
				LOMRestrictionSet theRestriction = Diffusion.ResDif(itsLOM,
						theAttribute);
				if (theRestriction != null && !theRestriction.isEmpty()) {
					LOMValue theValue = theAttribute.getValueIn(theLOM);
					// calculate the proportion of satisfied restriction
					int theTotalIntermediateScore = theRestriction
							.getLOMRestrictionValues().size();
					int theIntermediateScore = theRestriction
							.restrictionsSatisfiedBy(theValue).size();
					theScoreList.add(theLOM, theIntermediateScore * 1.0
							/ theTotalIntermediateScore);
				}
			}
			itsRestrictionResultMap.put(theAttribute, theScoreList);
		}
		isInitRestriction = true;
	}
	
	
	/**
	 * 
	 */
	public void initRestrictionLuceneResults() {
		if (!isInitRestriction) initRestrictionResults();
		if (!isInitLucene) initLuceneResults();
		itsRestrictionLuceneResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			for (LOM theLOM : getEntireDBLOMs()) 
				theScoreList.add(theLOM, 
						itsLuceneResults.getScoreFor(theLOM)
						*itsRestrictionResultMap.get(theAttribute).getScoreFor(theLOM));
			itsRestrictionLuceneResultMap.put(theAttribute, theScoreList);
		}
		isInitRestrictionLucene = true;
	}
	
	
	
	/**
	 * 
	 */
	public void initSuggestionLuceneResults() {
		if (!isInitSuggestion) initSuggestionResults();
		if (!isInitLucene) initLuceneResults();
		itsSuggestionLuceneResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			for (LOM theLOM : getEntireDBLOMs()) 
				theScoreList.add(theLOM, 
						itsLuceneResults.getScoreFor(theLOM)
						*itsSuggestionResultMap.get(theAttribute).getScoreFor(theLOM));
			itsSuggestionLuceneResultMap.put(theAttribute, theScoreList);
		}
		isInitSuggestionLucene = true;
	}

	/**
	 * 
	 */
	public void initIfNeeded() {
		if (!isInitSuggestion)
			initSuggestionResults();
		if (!isInitRestriction)
			initRestrictionResults();
		if (!isInitLucene)
			initLuceneResults();
		if (!isInitRestrictionLucene)
			initRestrictionLuceneResults();
		if (!isInitSuggestionLucene)
			initSuggestionLuceneResults();
	}

	/**
	 * 
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public SortedScoreRankList<LOM> getSuggestionResults(LOMAttribute aAttribute) {
		if (!isInitSuggestion)
			initSuggestionResults();
		return itsSuggestionResultMap.get(aAttribute);
	}
	
	
	/**
	 * return suggestion times lucene values.
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public SortedScoreRankList<LOM> getSuggestionLuceneResults(LOMAttribute aAttribute) {
		if (!isInitSuggestionLucene)
			initSuggestionLuceneResults();
		return itsSuggestionLuceneResultMap.get(aAttribute);
	}

	/**
	 * 
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public SortedScoreRankList<LOM> getRestrictionResults(
			LOMAttribute aAttribute) {
		if (!isInitRestriction)
			initRestrictionResults();
		return itsRestrictionResultMap.get(aAttribute);
	}

	/**
	 * return restriction times lucene value.
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public SortedScoreRankList<LOM> getRestrictionLuceneResults(
			LOMAttribute aAttribute) {
		if (!isInitRestrictionLucene)
			initRestrictionLuceneResults();
		return itsRestrictionLuceneResultMap.get(aAttribute);
	}
		
	/**
	 * 
	 * 
	 * @return 
	 */
	public SortedScoreRankList<LOM> getLuceneResults() {
		if (!isInitLucene)
			initLuceneResults();
		return itsLuceneResults;
	}

	/**
	 * 
	 * 
	 * @param aRanking 
	 * 
	 * @return 
	 */
	public List<String> diff(LOMRanking aRanking) {
		List<String> theDiff = new ArrayList<String>();
		List<String> theLuceneDiff = getLuceneResults().diff(
				aRanking.getLuceneResults());
		if (!theLuceneDiff.isEmpty()) {
			theDiff.add("+++lucene diff");
			theDiff.addAll(theLuceneDiff);
		}
		for (LOMAttribute theAttribute : getAttributeList()) {
			List<String> theSuggestionDiff = getSuggestionResults(theAttribute)
					.diff(aRanking.getSuggestionResults(theAttribute));
			if (!theSuggestionDiff.isEmpty()) {
				theDiff.add("+++Suggestion diff for " + theAttribute.getName());
				theDiff.addAll(theSuggestionDiff);
			}
			List<String> theRestrictionDiff = getRestrictionResults(
					theAttribute).diff(
					aRanking.getRestrictionResults(theAttribute));
			if (!theRestrictionDiff.isEmpty()) {
				theDiff
						.add("+++Restriction diff for "
								+ theAttribute.getName());
				theDiff.addAll(theRestrictionDiff);
			}
		}
		return theDiff;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public LOM getQueryLOM() {
		return itsLOM;
	}



}