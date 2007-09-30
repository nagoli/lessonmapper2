/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueInt;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;

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
public class IntersectionProbability {

	/**
	 * 
	 */
	private static final URL ITSPropertyFile = IntersectionProbability.class
			.getResource("resources/IntersectionProbability.xml");

	/**
	 * 
	 */
	public static final double epsilon = 0.001;

	/**
	 * 
	 */
	static IntersectionProbability ITSInstance = new IntersectionProbability(
			ITSPropertyFile);

	/**
	 * 
	 * 
	 * @return 
	 */
	public static IntersectionProbability getInstance() {
		return ITSInstance;
	}

	/**
	 * 
	 */
	Map<LOMAttribute, Double> itsProbas = new HashMap<LOMAttribute, Double>();

	/**
	 * 
	 * 
	 * @param aURL 
	 */
	public IntersectionProbability(URL aURL) {
		try {
			Document theFile = new SAXBuilder().build(aURL);
			Element theRoot = (Element) theFile.getContent().get(0);
			List theChildren = theRoot.getChildren("IntersectionProbability");
			for (Iterator iter = theChildren.iterator(); iter.hasNext();) {
				Element element = (Element) iter.next();
				LOMAttribute theAttribute = LOMAttribute
						.buildFromXMLElement(element.getChild("LOMAttribute"));
				Double theProba = new Double(element.getChildTextTrim("Proba"));
				setProbaFor(theAttribute, theProba);
			}
		} catch (Exception e) {
			System.out
					.println("error in IntersectionProbability file :  no specialized Probability is taken into account !");
		}
	}

	/**
	 * return the proba for aAttribute and aRelationType.
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public double getProbaFor(LOMAttribute aAttribute) {
		double theProba = 0.5;
		if (itsProbas.containsKey(aAttribute))
			theProba = itsProbas.get(aAttribute);
		if (theProba > 1 - epsilon)
			theProba = 1 - epsilon;
		if (theProba < epsilon)
			theProba = epsilon;
		return theProba;
	}

	/**
	 * set the proba for aAttribute and aRelationType.
	 * 
	 * @param aAttribute 
	 * @param aProba 
	 * 
	 * @return 
	 */
	public double setProbaFor(LOMAttribute aAttribute, double aProba) {
		Double theExProba = itsProbas.put(aAttribute, aProba);
		if (theExProba != null)
			return theExProba;
		return 0;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public Element makeXMLElement() {
		Element theRoot = new Element("IntersectionProbabilityList");
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			Element theIntersectionProbability = new Element(
					"IntersectionProbability");
			Element theAttributeElement = LOMAttribute
					.makeXMLElement(theAttribute);
			Element theProba = new Element("Proba");
			theProba.setText("" + getProbaFor(theAttribute));
			theIntersectionProbability.addContent(theAttributeElement);
			theIntersectionProbability.addContent(theProba);
			theRoot.addContent(theIntersectionProbability);
		}
		return theRoot;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param theOtherLOM 
	 * @param aProbas 
	 * @param aAttribute 
	 */
	public static void extractProba(LOM aLOM, LOM theOtherLOM,
			LOMAttribute aAttribute, List<Double> aProbas) {
		LOMValue theValue = aAttribute.getValueIn(aLOM);
		if (theValue == null)
			return;
		LOMValue theOtherValue = aAttribute.getValueIn(theOtherLOM);
		if (theOtherValue == null)
			return;
		double theScore = 0;
		double theTotal = 0;
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
			for (StringTokenizer theTokenizer = new StringTokenizer(
					theOtherValue.getValue(), LOM.ITSTokenizerLimits );
			theTokenizer.hasMoreTokens();) {
				String theToken = theTokenizer.nextToken();
				if (!theToken.trim().equals(""))
					if (theValue
							.contains(new LOMValueSet(theToken, aAttribute))) {
						theScore = 1; //
						break;
					}
			}
			theTotal = 1;
		} else if (theValue instanceof LOMValueVocabularySet){
			//TODO
			System.out.println("NOT IMPLEMENTED");
		}
		if (theTotal != 0) {
			aProbas.add(theScore / theTotal); // total =1
		}

	}

	/**
	 * 
	 * 
	 * @param aLOMList 
	 */
	public static void updateProbabilityValuesWith(List<LOM> aLOMList) {
		// Calculate Intersection proba for the loms given aLOMList2
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			List<Double> theProbas = new ArrayList<Double>();
			for (int i = 0; i < aLOMList.size(); i++)
				for (int j = i + 1; j < aLOMList.size(); j++) {
					LOM theLOM = aLOMList.get(i);
					LOM theOtherLOM = aLOMList.get(j);
					extractProba(theLOM, theOtherLOM, theAttribute, theProbas);
				}
			double theSum = 0;
			for (double theDouble : theProbas)
				theSum += theDouble;
			getInstance().setProbaFor(theAttribute, theSum / theProbas.size());
		}
	}

}
