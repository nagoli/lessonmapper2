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

import java.util.ArrayList;
import java.util.List;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.lom.LOM;
import lessonMapper.lom.diffuse.LOMSuggestionProbability;

import org.jdom.output.XMLOutputter;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;

/**
 * calculate the suggestion probabilities for the exist DB calling it.
 */
public class ProbabilityBuilder {

	/**
	 * 
	 * 
	 * @param args
	 */

	

	public static String SugProba = "suggestionProbabilities.xml";

	/**
	 * retrieve all the LOMs and calculate the probabilities for suggestion
	 * the result is placed in the db at /db/LMConfig/suggestionProbability.xml
	 * the access to this collection is done as "admin" password null
	 */
	
	public static void buildProba() {
System.out.println("Build Proba Starting");
		List<String> theResults = LocalExistUtils.localXMLQuery(
				"declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; collection('/db/lom')/ims:lom");

		List<LOM> theLOMList = new ArrayList<LOM>();
		for (String theXMLString : theResults) {
			LOM theLOM = new LOM(theXMLString);
			if (theLOM != null)
				theLOMList.add(theLOM);
		}

		LOMSuggestionProbability.updateProbabilityValuesWith(theLOMList);

		try {
		
			Collection col = DatabaseManager.getCollection("xmldb:exist:/db/LMConfig","admin",null);
			XMLResource doc = (XMLResource) col.createResource(SugProba,
					"XMLResource");
			doc.setContent(new XMLOutputter().outputString(LOMSuggestionProbability.getLOMInstance()
							.makeXMLElement()));
			col.storeResource(doc);
			System.out.println("Build Proba Ending");
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("Build Proba Failing");
		}

	}

	

	
}
