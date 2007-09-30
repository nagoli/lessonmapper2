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

package dcc.lessonMapper2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;

import lor.LOR;

import org.apache.commons.io.FileUtils;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.input.SAXBuilder;

import util.system.FileManagement;

/**
 * This class read the content of the lm2.ini file and adjust lessonmapper2
 * parameters with the content of this file
 * 
 * @author omotelet
 * 
 */
public class LMInitializer {

	
	public static void testSignedInner(){
		System.out.println("from same package");
		 LessonMapper2.getInstance().new SaveProjectAsTask(null);
	}
	
	/**
	 * first XPATH of the config second the class using the configuration third
	 * the variable of the configuration
	 */
	public static ArrayList<String[]> ITSConfigParameters = new ArrayList<String[]>();
	static {
		ITSConfigParameters.add(new String[] { "repositoryURL", "lor.LOR",
				"DBAddress" });
		ITSConfigParameters.add(new String[] { "language",
				"lessonMapper.LangManager", "DEFAULT" });
		ITSConfigParameters
				.add(new String[] { "useRepositoryConfiguration",
						"dcc.lessonMapper2.LMInitializer",
						"useRepositoryConfiguration" });
	}

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

	public static final String iniFilesDirectory = System
			.getProperty("user.home")
			+ File.separator + "LM2Config";
	public static final String iniFileLocation = iniFilesDirectory
			+ File.separator + "lm2.ini";
	public static final URL defaultIniFile = LMInitializer.class
			.getResource("resources/lm2.ini");

	// is static for facilitating reflection
	public static Boolean useRepositoryConfiguration = true;

	public Document itsIniDocument;
	boolean isIniDocumentChanged = false;

	File itsIniFile;

	public void init() {
		initIniFile();
		initializeParameters();
		initializeLocations();
	}

	void initIniFile() {
		File theIni = new File(iniFileLocation);
		if (!theIni.exists())
			try {
				LessonMapper2.getInstance().isFirstTimeOpen=true;
				FileManagement.getFileManagement().copy(defaultIniFile, theIni);
				System.out.println("Preparing tutorial ");
				File theZip = new File(iniFilesDirectory
						+ File.separator + LessonMapper2.ITSTutorialName + ".zip");

				InputStream theStream = getClass().getResourceAsStream(
						"resources/" + LessonMapper2.ITSTutorialName + ".zip");
				FileOutputStream theWriter = new FileOutputStream(theZip);
				int s;
				while ((s = theStream.read()) != -1) {
					theWriter.write(s);
				}
				FileManagement.unzip(theZip, new File(
						iniFilesDirectory));
			} catch (Exception e) {
				System.out
						.println("problem when copying ini file in the home directory "
								+ iniFileLocation);
			}
		itsIniFile = theIni;
		SAXBuilder theSAXBuilder = new SAXBuilder();
		theSAXBuilder.setValidation(false);
		theSAXBuilder.setIgnoringElementContentWhitespace(true);
		try {
			itsIniDocument = theSAXBuilder.build(itsIniFile);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	void initializeParameters() {
		try {
			for (String[] theParameter : ITSConfigParameters) {
				JDOMXPath theXPath = new JDOMXPath("lm2/" + theParameter[0]);
				String theResult = theXPath.valueOf(itsIniDocument);
				if (theResult.equals(""))
					continue;
				Field theField = Class.forName(theParameter[1]).getField(
						theParameter[2]);
				// only work on static field
				if (theField.getType().equals(String.class))
					theField.set(null, theResult);
				if (theField.getType().equals(Boolean.class))
					theField.set(null, new Boolean(theResult));
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("error in the lm2.ini parameters");
		}
	}

	/**
	 * init locations of configuration files retrieve the file from the server
	 * if it UseRepositoryConfiguration was set to true if it is the case copy
	 * the distant configuration and update the ini file.
	 */
	void initializeLocations() {
		for (String[] theParameter : ITSConfigLocations) {
			File theFile = new File(iniFilesDirectory + File.separator
					+ theParameter[3]);
			if (useRepositoryConfiguration) {
				try {
				URL theSourceURL = new URL("http://" + LOR.DBAddress
						+ "/exist/servlet/db/LMConfig/" + theParameter[3]);
					FileUtils.copyURLToFile(theSourceURL, theFile);
					System.out.println("File " + theParameter[3]
							+ " taken from repository");
				} catch (Exception e) {
					// copy crashed
				}
			}
			if (theFile.exists()) {
				try{
					Field theField = Class.forName(theParameter[1]).getField(
						theParameter[2]);
				// only work on static field
				if (theField.getType().equals(URL.class)) {
					theField.set(null, theFile.toURI().toURL());
				}
				System.out.println("Configuration file " + theParameter[3]
						+ " loaded");
				}catch(Exception e){e.printStackTrace();}
			}
		}
	}

}
