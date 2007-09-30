/*
 * LessonMapper 2.
 Copyright (C) Olivier Motelet.
 This program is free software; you can redistribute it and/or
 modify it under the terms of the GNU General Public License
 as published by the Free Software Foundation; either version 2
 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.
 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.configuration;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.exist.query.LOMRankingExist;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import util.Couple;

/**
 * this class is responsible for holding the probabilities that a suggestion
 * results this probability is build with the xml resource
 * resources/SuggestionProbability.xml
 * 
 * The main of this class is responsible for building this file
 * 
 * @author omotelet
 */
public class SuggestionProbabilityExist {

	/**
	 * 
	 */
	public static final BigDecimal epsilon = new BigDecimal(0.00001,
			LOMRankingExist.ITSPrecision);

	/**
	 * 
	 */
	static SuggestionProbabilityExist ITSInstance;

	/**
	 * 
	 * 
	 * @return
	 */
	public static SuggestionProbabilityExist getInstance() {
		if (ITSInstance == null)
			ITSInstance = new SuggestionProbabilityExist();
		return ITSInstance;
	}

	/**
	 * 
	 */
	Map<Couple<LOMAttribute, LOMRelationType>, BigDecimal> itsProbas = new HashMap<Couple<LOMAttribute, LOMRelationType>, BigDecimal>();

	/**
	 * 
	 * 
	 * @param aURL
	 */
	public SuggestionProbabilityExist() {
		try {
			List<String> theList = LocalExistUtils
					.localXMLQuery("doc('/db/LMConfig/"
							+ ProbabilityBuilder.SugProba + "')");
			if (!theList.isEmpty()) {
				Document theFile = new SAXBuilder().build(theList.get(0));
				Element theRoot = (Element) theFile.getContent().get(0);
				List theChildren = theRoot.getChildren("SuggestionProbability");
				for (Iterator iter = theChildren.iterator(); iter.hasNext();) {
					Element element = (Element) iter.next();
					LOMAttribute theAttribute = LOMAttribute
							.buildFromXMLElement(element
									.getChild("LOMAttribute"));
					LOMRelationType theRelationType = LOMRelationType
							.buildFromXMLElement(element
									.getChild("LOMRelationType"));
					BigDecimal theProba = new BigDecimal(element
							.getChildTextTrim("Proba"),
							LOMRankingExist.ITSPrecision);
					setProbaFor(theAttribute, theRelationType, theProba);
				}
			}
		} catch (Exception e) {
			System.out
					.println("error in suggestionProbability file :  no specialized Probability is taken into account !");
		}
	}

	/**
	 * return the proba for aAttribute and aRelationType.
	 * 
	 * @param aRelationType
	 * @param aAttribute
	 * 
	 * @return
	 */
	public BigDecimal getProbaFor(LOMAttribute aAttribute,
			LOMRelationType aRelationType) {
		// aRelationType=aRelationType.getContrary();
		BigDecimal theProba;
		Couple<LOMAttribute, LOMRelationType> theCouple = new Couple<LOMAttribute, LOMRelationType>(
				aAttribute, aRelationType);
		if (itsProbas.containsKey(theCouple)) {
			theProba = itsProbas.get(theCouple);
			if (theProba.doubleValue() > 1 - epsilon.doubleValue())
				theProba = BigDecimal.ONE.subtract(epsilon,
						LOMRankingExist.ITSPrecision);
			if (theProba.doubleValue() < epsilon.doubleValue())
				theProba = epsilon;
		} else
			theProba = epsilon;
		return theProba;

		// return new BigDecimal(0.7);
	}

	/**
	 * set the proba for aAttribute and aRelationType.
	 * 
	 * @param aRelationType
	 * @param aAttribute
	 * @param aProba
	 * 
	 * @return
	 */
	public double setProbaFor(LOMAttribute aAttribute,
			LOMRelationType aRelationType, BigDecimal aProba) {
		BigDecimal theExProba = itsProbas.put(
				new Couple<LOMAttribute, LOMRelationType>(aAttribute,
						aRelationType), aProba);
		if (theExProba != null)
			return theExProba.doubleValue();
		return 0;
	}

}
