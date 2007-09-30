/*
 * LessonMapper 2.
 */
package lessonMapper.query;

import java.math.BigDecimal;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lessonMapper.diffusion.Diffusion;
import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

/**
 * this class is responsible for holding the probabilities that a suggestion
 * results this probability is build with the xml resource
 * resources/SuggestionProbability.xml
 * 
 * The main of this class is responsible for building this file
 * 
 * @author omotelet
 */
public class RestrictionProbability {

	/**
	 * 
	 */
	private static final URL ITSPropertyFile = RestrictionProbability.class
			.getResource("resources/RestrictionProbability.xml");
 
	/**
	 * 
	 */
	static RestrictionProbability ITSInstance ;

	/**
	 * 
	 * 
	 * @return 
	 */
	public static RestrictionProbability getInstance() {
		if (ITSInstance == null) ITSInstance= new RestrictionProbability(
				ITSPropertyFile);
		return ITSInstance;
	}

	/**
	 * 
	 */
	Map<LOMAttribute, BigDecimal> itsProbas = new HashMap<LOMAttribute, BigDecimal>();

	/**
	 * 
	 */
	public RestrictionProbability() {
	}
	
	
	/**
	 * 
	 * 
	 * @param aURL 
	 */
	public RestrictionProbability(URL aURL) {
		try {
			Document theFile = new SAXBuilder().build(aURL);
			Element theRoot = (Element) theFile.getContent().get(0);
			List theChildren = theRoot.getChildren("RestrictionProbability");
			for (Iterator iter = theChildren.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				LOMAttribute theAttribute = LOMAttribute
						.buildFromXMLElement(element.getChild("LOMAttribute"));
				BigDecimal theProba = new BigDecimal(element.getChildTextTrim("Proba"),LOMRanking.ITSPrecision);
				setProbaFor(theAttribute, theProba);
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out
					.println("error in restrictionProbability file :  no specialized Probability is taken into account !");
		}
	}

	/**
	 * return the proba for aAttribute.
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public BigDecimal getProbaFor(LOMAttribute aAttribute) {

		if (itsProbas.containsKey(aAttribute))
			return itsProbas.get(aAttribute);
		System.out.println("default value for restriction proba is used");
		return new BigDecimal(0.5,LOMRanking.ITSPrecision);
	}

	/**
	 * set the proba for aAttribute.
	 * 
	 * @param aAttribute 
	 * @param aProba 
	 * 
	 * @return 
	 */
	public BigDecimal setProbaFor(LOMAttribute aAttribute, BigDecimal aProba) {
		BigDecimal theExProba = itsProbas.put(aAttribute, aProba);
		if (theExProba != null)
			return theExProba;
		return BigDecimal.ZERO;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Element makeXMLElement() {
		Element theRoot = new Element("RestrictionProbabilityList");
		for (Entry<LOMAttribute, BigDecimal> theEntry : itsProbas.entrySet()) {
			Element theSuggestionProbability = new Element(
					"RestrictionProbability");
			Element theAttribute = LOMAttribute.makeXMLElement(theEntry
					.getKey());
			Element theProba = new Element("Proba");
			theProba.setText("" + theEntry.getValue());
			theSuggestionProbability.addContent(theAttribute);
			theSuggestionProbability.addContent(theProba);
			theRoot.addContent(theSuggestionProbability);
		}
		return theRoot;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aProbas 
	 * @param aAttribute 
	 */
	public static void extractProba(LOM aLOM, LOMAttribute aAttribute,
			List<Double> aProbas) {
		LOMValue theValue = aAttribute.getValueIn(aLOM);
		// do not take into account non assigned element
		if (theValue == null)
			return;
		// we check if their is a restriction if the value complete restriction
		LOMRestrictionSet theRestrictionSet = Diffusion
				.ResDif(aLOM, aAttribute);
		if (theRestrictionSet != null) {
			double theScore = theRestrictionSet.restrictionsSatisfiedBy(
					theValue).size();
			double theTotal = theRestrictionSet.getLOMRestrictionValues()
					.size();
			if (theTotal != 0)
				aProbas.add(theScore / theTotal);
		}
	}

	/**
	 * 
	 * 
	 * @param aLOMList 
	 */
	public static void updateProbabilityValuesWith(List<LOM> aLOMList) {
		// Calculate suggestion proba for the loms given aLOMList2
		ITSInstance = new RestrictionProbability();
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			List<Double> theProbas = new ArrayList<Double>();
			for (LOM theLOM : aLOMList) {
				extractProba(theLOM, theAttribute, theProbas);
			}
			if (theProbas.size() != 0) {
				double theSum = 0;
				for (double theDouble : theProbas)
					theSum += theDouble;
				
				getInstance().setProbaFor(theAttribute,
						new BigDecimal(theSum).divide(new BigDecimal(theProbas.size()),LOMRanking.ITSPrecision));
			}
		}
	}
}