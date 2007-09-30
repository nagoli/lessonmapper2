package lessonMapper.lom.diffuse;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueInt;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;
import lessonMapper.lom.util.LOMRelationBuffer;
import lessonMapper.query.LOMRanking;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import diffuse.metadata.MetadataSetAttribute;
import diffuse.metadata.MetadataSetRelationType;
import diffuse.models.sug.SuggestionProbability;

public class LOMSuggestionProbability extends SuggestionProbability {

	public static  URL ITSPropertyFile = LOMSuggestionProbability.class
	.getResource("resources/suggestions.xml");
	
	
	public static void init() {
		 ITSInstance = new LOMSuggestionProbability(
				ITSPropertyFile);
	}
	
	
	public static LOMSuggestionProbability getLOMInstance()  throws Exception{
		if (ITSInstance instanceof LOMSuggestionProbability) {
			LOMSuggestionProbability theInstance = (LOMSuggestionProbability) ITSInstance;
			return theInstance;
		}
		else throw new Exception("Suggestion Probability not initialized as LOMSuggestionProbability");
	}
	
	
	
	public LOMSuggestionProbability(URL aURL) {
		try {
			Document theFile = new SAXBuilder().build(aURL);
			Element theRoot = (Element) theFile.getContent().get(0);
			List theChildren = theRoot.getChildren("SuggestionProbability");
			for (Iterator iter = theChildren.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				MetadataSetAttribute theAttribute = LOMAttribute
						.buildFromXMLElement(element.getChild("LOMAttribute"));
				MetadataSetRelationType theRelationType = LOMRelationType
						.buildFromXMLElement(element
								.getChild("LOMRelationType"));
				BigDecimal theProba = new BigDecimal(element.getChildTextTrim("Proba"),LOMRanking.ITSPrecision);
				setProbaFor(theAttribute, theRelationType, theProba);
			}
		} catch (Exception e) {
			System.out
					.println("error in suggestionProbability file :  no specialized Probability is taken into account !");
		}
	}

	
	/**
	 * 
	 * 
	 * @return 
	 */
	public Element makeXMLElement() {
		Element theRoot = new Element("SuggestionProbabilityList");
	for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) 
		for (LOMRelationType theRelation: LOMRelationType.getAvailableTypes()){
			Element theSuggestionProbability = new Element(
					"SuggestionProbability");
			Element theAttributeElement = LOMAttribute.makeXMLElement(theAttribute);
			Element theRelationElement = LOMRelationType.makeXMLElement(theRelation);
			Element theProba = new Element("Proba");
			theProba.setText("" + getProbaFor(theAttribute,theRelation));
			theSuggestionProbability.addContent(theAttributeElement);
			theSuggestionProbability.addContent(theRelationElement);
			theSuggestionProbability.addContent(theProba);
			theRoot.addContent(theSuggestionProbability);
		}
		return theRoot;
	}
	
	
	
	/**
	 * extrat the probability that the target has similar element with the source
	 * |l1 inter L2| / |l1|.
	 * 
	 * @param aLOM 
	 * @param aProbas 
	 * @param aAttribute 
	 */
	public static void extractProba(LOM aLOM, LOMAttribute aAttribute,
			Map<LOMRelationType, List<Double>> aProbas) {
		LOMValue theValue = aAttribute.getValueIn(aLOM);
		if (theValue == null)
			return;
		Set<LOMRelation> theRelations = LOMRelationBuffer.getRelationsIn(aLOM);
		for (LOMRelation theRelation : theRelations) {
			LOMRelationType theType = theRelation.getLOMRelationType();
			LOM theOtherLOM = theRelation.getTargetLOM();
			if (theOtherLOM == null)
				continue;
			LOMValue theOtherValue = aAttribute.getValueIn(theOtherLOM);
			if (theOtherValue == null)
				continue;
			double theScore = 0;
			int theTotal = 0;
			if (theValue instanceof LOMValueInt) {
				int theSug = ((LOMValueInt) theOtherValue).getIntValue();
				int theVal = ((LOMValueInt) theValue).getIntValue();
				double theProportionalVal = theVal * 100.0 / theSug;
				// we aggregate the proportional porcentage to
				// the score
				theScore = (100 - Math.abs(100 - theProportionalVal)) * 1.0 / 100;
				// we aggregate 1 to the total score
				theTotal = 1;
			} else if (theValue instanceof LOMValueSet) {
				// we check is the intersection between theVal and theOtherVal
				// is non nul
				/*for (StringTokenizer theTokenizer = new StringTokenizer(
						theOtherValue.getValue(), ", "); theTokenizer
						.hasMoreTokens();) {
					String theToken = theTokenizer.nextToken();
					if (!theToken.trim().equals(""))
						if (theValue.contains(new LOMValueSet(theToken,
								aAttribute))) {
							theScore = 1; //
							break; 
						}
				}*/
				//calculate vector distance between both values
					//theScore = ((LOMValueSet)theValue).getTermSimilarityWith(theOtherValue);
				//calculate the proba of othervalue to be in the value i.e. the proba of theOtherValue knowing theValue
					theScore = ((LOMValueSet)theValue).getTermSimilarityProbaWith(theOtherValue);	
				theTotal = 1;
				}
			else if (theValue instanceof LOMValueVocabularySet){
				theScore = ((LOMValueVocabularySet)theValue).getTermSimilarityProbaWith(theOtherValue);	
				theTotal = 1;
			}
			if (theTotal != 0) {
				List<Double> theProbaList = aProbas.get(theType);
				if (theProbaList == null) {
					theProbaList = new ArrayList<Double>();
					aProbas.put(theType, theProbaList);
				}
				theProbaList.add(theScore / theTotal); // total =1
			}
		}
	}

	/**
	 * 
	 * 
	 * @param aLOMList 
	 */
	public static void updateProbabilityValuesWith(List<LOM> aLOMList) {
		// Calculate suggestion proba for the loms given aLOMList2
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			Map<LOMRelationType, List<Double>> theProbas = new HashMap<LOMRelationType, List<Double>>();
			for (LOM theLOM : aLOMList) {
				extractProba(theLOM, theAttribute, theProbas);
			}
			for (MetadataSetRelationType theType : theProbas.keySet()) {
				double theSum = 0;
				List<Double> theList = theProbas.get(theType);
				for (double theDouble : theList)
					theSum += theDouble;
				try{
					getLOMInstance().setProbaFor(theAttribute, theType,
						new BigDecimal(theSum).divide(new BigDecimal(theList.size()),LOMRanking.ITSPrecision));
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	
}
