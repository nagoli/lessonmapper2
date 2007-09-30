/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.lom.diffuse.LOMSuggestionProbability;
import lor.LOR;

import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * 
 */
public class ProbabilityBuilder {
	
	/**
	 * 
	 * 
	 * @param args 
	 */
	
	
	public static void buildProba(String aOutputFile) {
	    // build the SuggestionProbability.xml with the Repository
		List<String> theResults = LOR.INSTANCE
				.xmlQuery("declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; /ims:lom ");
		List<LOM> theLOMList = new ArrayList<LOM>();
		for (String theXMLString : theResults) {
			LOM theLOM = new LOM(theXMLString);
			if (theLOM != null)
				theLOMList.add(theLOM);
		}
		
		LOMSuggestionProbability.updateProbabilityValuesWith(theLOMList);
		
		
		//		 Store the extracted proba to xml file
		XMLOutputter theOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			File theFile = new File(aOutputFile);
			theOutputter.output(LOMSuggestionProbability.getLOMInstance().makeXMLElement(), new FileWriter(
					theFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SuggestionProbability was not written");
		}
	}
	
	
	
	
	
	
	
	public static void main(String[] args) {
	    // build the SuggestionProbability.xml with the Repository
		List<String> theResults = LOR.INSTANCE
				.xmlQuery("declare namespace ims='http://www.imsglobal.org/xsd/imsmd_v1p2'; /ims:lom ");
		List<LOM> theLOMList = new ArrayList<LOM>();
		for (String theXMLString : theResults) {
			LOM theLOM = new LOM(theXMLString);
			if (theLOM != null)
				theLOMList.add(theLOM);
		}
		RestrictionProbability.updateProbabilityValuesWith(theLOMList);
		LOMSuggestionProbability.updateProbabilityValuesWith(theLOMList);
		IntersectionProbability.updateProbabilityValuesWith(theLOMList);
		
		//		 Store the extracted proba to xml file
		XMLOutputter theOutputter = new XMLOutputter(Format.getPrettyFormat());
		try {
			File theFile = new File(
					"/USERDISK/develop/LomGenerator/src/lessonMapper/query/resources/RestrictionProbability.xml");
			theOutputter.output(RestrictionProbability.getInstance().makeXMLElement(), new FileWriter(
					theFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("RestrictionProbability was not written");
		}
		try {
			File theFile = new File(
					"/USERDISK/develop/LomGenerator/src/lessonMapper/query/resources/SuggestionProbability.xml");
			theOutputter.output(LOMSuggestionProbability.getLOMInstance().makeXMLElement(), new FileWriter(
					theFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("SuggestionProbability was not written");
		}
		try {
			File theFile = new File(
					"/USERDISK/develop/LomGenerator/src/lessonMapper/query/resources/IntersectionProbability.xml");
			theOutputter.output(IntersectionProbability.getInstance().makeXMLElement(), new FileWriter(
					theFile));
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("IntersectionProbability was not written");
		}
	}
	}
