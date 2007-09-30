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
package lessonMapper.exist.query.training;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.query.rankboost.RankBoostRanker;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.modules.XMLResource;


public class LMQueryTraining {

	public static final String ITSRankerFile= "RBHypothesis.xml";
	
	private static RankBoostRanker ITSRanker;
	
	public static RankBoostRanker getRanker(){
		if (ITSRanker ==null){
			loadRanker();
		}
		return ITSRanker;
	}
	
	public static void setRanker(RankBoostRanker aRBRanker){
		ITSRanker=aRBRanker;
		saveRanker();
	}
	
	
	public static void loadRanker(){
		try {
			List<String> theResults = LocalExistUtils.localXMLQuery("doc('/db/LMQuery/"
					+ ITSRankerFile + "')");
			SAXBuilder theBuilder = new SAXBuilder();
			Element theRanker = theBuilder.build(
					new StringReader(theResults.get(0))).getRootElement();
			ITSRanker=RankBoostRanker.buildFromXMLInstance(theRanker);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void saveRanker(){
		try {
			Collection col = DatabaseManager.getCollection("xmldb:exist:/db/LMQuery","admin",null);
			XMLResource doc = (XMLResource) col.getResource(ITSRankerFile);
			if (doc==null) 
				doc = (XMLResource) col.createResource(ITSRankerFile,
			"XMLResource");
			doc.setContent(new XMLOutputter().outputString(RankBoostRanker.makeXMLInstance(ITSRanker)));
			col.storeResource(doc);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	public static boolean train(){
			try {
			List<String> theResults =LocalExistUtils.localXMLQuery("collection('/db/LMQuery')/completeQueries/query");
			SAXBuilder theBuilder = new SAXBuilder();
			
			TrainingLauncher theLauncher = new TrainingLauncher(theResults.size());
			for (String theString : theResults) {
				try {
					Element theQuery = theBuilder.build(
							new StringReader(theString)).getRootElement();
					String theID = theQuery.getChildTextTrim("id");
					String theQueryNodeID =theQuery.getChildText("queryNodeID");
					String theKeywordQuery =theQuery.getChildText("keywordQuery");
					String theExpectedLOMs = theQuery.getChildText("expectedLOMIDs");
					List<String> theExpectedLOMIDs = new ArrayList<String>();
					for (StringTokenizer theTokenizer = new StringTokenizer(
							theExpectedLOMs); theTokenizer.hasMoreTokens();) {
						theExpectedLOMIDs.add(theTokenizer.nextToken());
					}
					LearningTest theLearningTest = new LearningTest(theID,
							theQueryNodeID, theKeywordQuery, theExpectedLOMIDs);
					theLauncher.addTrainingTest(theLearningTest);
					
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			RankBoostRanker theRanker = theLauncher.trainRankBoost();
			if (theRanker!= null) setRanker(theRanker);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
		
	}
	
	
	
	
}
