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

package lessonMapper.exist;

import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

/**
 * This class read the content of the lm2.ini file and adjust lessonmapper2
 * parameters with the content of this file
 * 
 * @author omotelet
 * 
 */
public class LMExistInitializer {

	// should be changed if stated on another port
	public static String DBAddress = "localhost:8080";

	/**
	 * first XPATH of the config second the class using the configuration third
	 * the variable of the configuration fourth theConfigFile name in the loca
	 * configuration directory
	 */
	public static ArrayList<String[]> ITSConfigLocations = new ArrayList<String[]>();
	static {
		ITSConfigLocations.add(new String[] { "restrictionRuleLocation",
				"lessonMapper.lom.diffuse.ResRuleBuilder", "ITSHeuristicsURL",
				"restrictionRules.xml" });
		ITSConfigLocations.add(new String[] { "suggestionProbaLocation",
				"lessonMapper.lom.diffuse.LOMSuggestionProbability", "ITSPropertyFile",
				"suggestionProbabilities.xml" });
		ITSConfigLocations.add(new String[] { "visibleAttributeListLocation",
				"dcc.lessonMapper2.ui.lom.LOMAttributeTree",
				"ITSLOMAttributeList", "visibleAttributeList.xml" });
		ITSConfigLocations.add(new String[] { "resAndSugAttributeListLocation",
				"lessonMapper.query.LOMRanking", "ITSRankingAttributes",
				"resAndSugAttributeList.xml" });
		ITSConfigLocations.add(new String[] { "relationLocation",
				"lessonMapper.lom.LOMRelationType", "ITSDescriptionURL",
				"relation.rdf" });
		ITSConfigLocations.add(new String[] { "educationalLocation",
				"lessonMapper.lom.LOMVocabulary", "educationalURL",
				"educational.rdf" });
		ITSConfigLocations
				.add(new String[] { "generalLocation",
						"lessonMapper.lom.LOMVocabulary", "generalURL",
						"general.rdf" });
		ITSConfigLocations.add(new String[] { "annotationLocation",
				"lessonMapper.lom.LOMVocabulary", "annotationURL",
				"annotation.rdf" });
		ITSConfigLocations.add(new String[] { "clasificationLocation",
				"lessonMapper.lom.LOMVocabulary", "clasificationURL",
				"clasification.rdf" });
		ITSConfigLocations.add(new String[] { "lifecycleLocation",
				"lessonMapper.lom.LOMVocabulary", "lifecycleURL",
				"lifecycle.rdf" });
		ITSConfigLocations.add(new String[] { "metametadataLocation",
				"lessonMapper.lom.LOMVocabulary", "metametadataURL",
				"metametadata.rdf" });
		ITSConfigLocations.add(new String[] { "rightsLocation",
				"lessonMapper.lom.LOMVocabulary", "rightsURL", "rights.rdf" });
		ITSConfigLocations.add(new String[] { "technicalLocation",
				"lessonMapper.lom.LOMVocabulary", "technicalURL",
				"technical.rdf" });
	}

	

	/**
	 * init locations of configuration files retrieve the file from the server
	 * if it UseRepositoryConfiguration was set to true if it is the case copy
	 * the distant configuration and update the ini file.
	 */
	public static void initializeLocations() {
		for (String[] theParameter : ITSConfigLocations)
			// check if the config is present in the repository
			try {
				System.out.println(".");
				URL theSourceURL = new URL("http://" + DBAddress
						+ "/exist/servlet/db/LMConfig/" + theParameter[3]);
				theSourceURL.getContent();
				Field theField = Class.forName(theParameter[1]).getField(
						theParameter[2]);
				// only work on static field
				if (theField.getType().equals(URL.class))
					theField.set(null, theSourceURL);
				System.out.println(theParameter[3] + " was updated");
			} catch (Exception e) {
				System.out
						.println(theParameter[3]
								+ " not present in the LMConfig directory of the repository");
			}
	}

}
