/*
 * LessonMapper 2.
 */
package lessonMapper.validation;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

import util.Couple;

/**
 * 
 */
public class ValidationCache {

	/**
	 * 
	 */
	static String serializationFile = "validationCache.data";

	/**
	 * 
	 */
	static ValidationCache itsInstance = new ValidationCache();

	/**
	 * 
	 * 
	 * @return 
	 */
	static public ValidationCache getInstance() {
		return itsInstance;
	}

	/**
	 * 
	 */
	Map<Couple<LOM, LOMAttribute>, ValidationState> itsValidationCache = new HashMap<Couple<LOM, LOMAttribute>, ValidationState>();

	/**
	 * 
	 */
	Map<LOM, ValidationStats> itsStatsCache = new HashMap<LOM, ValidationStats>();

	/**
	 * 
	 */
	public void reset() {
		itsStatsCache.clear();
		itsValidationCache.clear();
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public void updateCache(LOM aLOM, LOMAttribute aAttribute) {
		ValidationState theState = Validation.getValidationState(aLOM,
				aAttribute);
		itsValidationCache.put(new Couple<LOM, LOMAttribute>(aLOM, aAttribute),
				theState);
		itsStatsCache.put(aLOM, Validation.getStatsFor(aLOM,null));
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public ValidationStats getStatCacheFor(LOM aLOM) {
		ValidationStats theStats = itsStatsCache.get(aLOM);
		if (theStats == null)
			return new ValidationStats();
		return theStats;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aLOMAttribute 
	 * 
	 * @return 
	 */
	public ValidationState getStateCacheFor(LOM aLOM, LOMAttribute aLOMAttribute) {
		ValidationState theState = itsValidationCache
				.get(new Couple<LOM, LOMAttribute>(aLOM, aLOMAttribute));
		if (theState == null)
			return new ValidationState(aLOMAttribute);
		else
			return theState;
	}

	/**
	 * return a element based on the values of aCache.
	 * 
	 * @param aCache 
	 * 
	 * @return 
	 */
	public static Element makeXMLElement(ValidationCache aCache) {
		Element theRoot = new Element("ValidationCache");
		Element theValidationList = new Element("ValidationList");
		for (Entry<Couple<LOM, LOMAttribute>, ValidationState> theEntry : aCache.itsValidationCache
				.entrySet()) {
			Element theValidationItem = new Element("ValidationItem");
			theValidationItem.addContent(LOM.makeXMLElement(theEntry.getKey()
					.getLeftElement()));
			theValidationItem.addContent(LOMAttribute.makeXMLElement(theEntry
					.getKey().getRightElement()));
			theValidationItem.addContent(ValidationState
					.makeXMLElement(theEntry.getValue()));
			theValidationList.addContent(theValidationItem);
		}
		theRoot.addContent(theValidationList);
		Element theStatsList = new Element("StatsList");
		for (Entry<LOM, ValidationStats> theEntry : aCache.itsStatsCache
				.entrySet()) {
			Element theStatsItem = new Element("StatsItem");
			theStatsItem.addContent(LOM.makeXMLElement(theEntry.getKey()));
			theStatsItem.addContent(ValidationStats.makeXMLElement(theEntry
					.getValue()));
			theStatsList.addContent(theStatsItem);
		}
		theRoot.addContent(theStatsList);
		return theRoot;
	}

	/**
	 * return aCache with the value stored in aElement. if aCache is null make a
	 * new ValidationCache;
	 * 
	 * @param aElement 
	 * @param aCache 
	 * 
	 * @return 
	 */
	public static ValidationCache buildFromXMLElement(Element aElement,
			ValidationCache aCache) {
		Element theValidationList = aElement.getChild("ValidationList");
		for (Iterator iter = theValidationList.getChildren().iterator(); iter
				.hasNext();) {
			Element element = (Element) iter.next();
			LOM theLOM = LOM.buildFromXMLElement(element.getChild("LOM"));
			LOMAttribute theAttribute = LOMAttribute
					.buildFromXMLElement(element.getChild("LOMAttribute"));
			ValidationState theValidationState = ValidationState
					.buildFromXMLElement(element.getChild("ValidationState"));
			if (theLOM == null || theAttribute == null)
				try {
					System.out.println("<--- Missing LOM or LOMAttribute in restriction cache");
					new XMLOutputter(Format.getPrettyFormat()).output(element,
							System.out);
					System.out.println("--->");
				} catch (Exception e) {
					e.printStackTrace();
				}
			else
				aCache.itsValidationCache.put(new Couple<LOM, LOMAttribute>(
						theLOM, theAttribute), theValidationState);
		}
		Element theStatsList = aElement.getChild("StatsList");
		for (Iterator iter = theStatsList.getChildren().iterator(); iter
				.hasNext();) {
			Element element = (Element) iter.next();
			aCache.itsStatsCache.put(LOM.buildFromXMLElement(element
					.getChild("LOM")), ValidationStats
					.buildFromXMLElement(element.getChild("ValidationStats")));
		}
		return aCache;
	}

	/**
	 * 
	 * 
	 * @param aElement 
	 * 
	 * @return 
	 */
	public static ValidationCache buildFromXMLElement(Element aElement) {
		return buildFromXMLElement(aElement, new ValidationCache());
	}

}