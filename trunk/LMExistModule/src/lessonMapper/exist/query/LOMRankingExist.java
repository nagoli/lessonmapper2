/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.query;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lessonMapper.diffusion.fixpoint.FixPointValueDiffusion;
import lessonMapper.diffusion.fixpoint.SugDifValue;
import lessonMapper.diffusion.fixpoint.SugDifValueHolder;
import lessonMapper.exist.LocalExistUtils;
import lessonMapper.exist.configuration.SuggestionProbabilityExist;
import lessonMapper.exist.lucene.LuceneIndexer;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.query.LOMRanking;
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

public class LOMRankingExist extends LOMRanking{

	
	
	
	
		/**
	 * 
	 * 
	 * @return 
	 */
	public static List<LOM> getEntireDBLOMs() {
		//TODO manage entireDB to reflect new elements of the db
		//TODO use emptied holder as in original version
		
		if (ITSEntireDBLOMs == null) {
			System.out.println("Begin diffusion on DB ");
			List<String> theResults = LocalExistUtils.
			        localXMLQuery("declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; collection('/db/lom')/ims:lom");
			ITSEntireDBLOMs = new ArrayList<LOM>();
			for (String element : theResults)
				if (!element.equals("")) {
					LOM theLOM = LOM.getLOMWithXMLRepresentation(element);
					ITSEntireDBLOMs.add(theLOM);
				} 
			System.out.println("    Lom initialized ");
  
			System.out.println("    Init diffusion: ");
			try{
				for (LOMAttribute theAttribute : getAttributeList()) {
				System.out.print(" +");
				SugDifValueHolder theHolder = new SugDifValueHolder(theAttribute);
				FixPointValueDiffusion.diffuseChangesOf( ITSEntireDBLOMs,theHolder);
				/*List<LOM> theDBCopy = new ArrayList<LOM>();
				theDBCopy.addAll(ITSEntireDBLOMs);
				for (LOM theLOM : ITSEntireDBLOMs) {
					System.out.print(".");
					EmptiedSugDifValueHolder theEmptiedHolder = EmptiedSugDifValueHolder.setInstanceFor(theLOM, theHolder);
					FixPointValueDiffusion.diffuseChangesOf( theDBCopy,theEmptiedHolder);
				}*/
				}
				}catch(Exception e){
					System.out.println("Problem during diffusion:" );
					e.printStackTrace();
				}
				
			System.out.println(" Diffusion on DB done");
		}
		return ITSEntireDBLOMs;
	}

	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aKeywordQuery 
	 */
	public LOMRankingExist(LOM aLOM, String aKeywordQuery) {
		super(aLOM, aKeywordQuery);
	}

	/**
	 * 
	 */
	public void initLuceneResults() {
		try{
			itsLuceneResults= LuceneIndexer.getLuceneResults(itsKeywordQuery);
			for (LOM theLOM : ITSEntireDBLOMs) 
				if (!itsLuceneResults.contains(theLOM)) itsLuceneResults.add(theLOM, 10^-SortedScoreRankList.ITSPrecision);
			isInitLucene = true;
		} catch (Exception e) {
			e.printStackTrace();
			isInitLucene = false;
		}
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
				BigDecimal theProba = SuggestionProbabilityExist.getInstance().getProbaFor(aAttribute, theCouple.getRightElement());
				theDecrease = theDecrease.multiply(theProba, ITSPrecision);
			}
		return theDecrease;
	}

	/**
	 * replace the super by a version that does not take into account the exclusion of the considered lom in the suggestion calculus
	 */
	public void initSuggestionResultsFixedPoint() {
		itsSuggestionResultMap = new HashMap<LOMAttribute, SortedScoreRankList<LOM>>();
		for (LOMAttribute theAttribute : getAttributeList()) {
			SortedScoreRankList<LOM> theScoreList = new SortedScoreRankList<LOM>();
			SugDifValue theExpectedSugValue = SugDifValueHolder.getInstance(theAttribute)
												.getValue(itsLOM);
			for (LOM theLOM : getEntireDBLOMs()) {
				SugDifValue theDBSugValue = (SugDifValue)SugDifValueHolder.
							getInstance(theAttribute).getValue(theLOM);		
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

	

}