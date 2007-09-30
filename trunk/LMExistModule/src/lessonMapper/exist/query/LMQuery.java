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
package lessonMapper.exist.query;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import lessonMapper.exist.LocalExistUtils;
import lessonMapper.exist.query.training.LMQueryTraining;
import lessonMapper.exist.query.training.QueryTest;
import lessonMapper.lom.LOM;

import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import util.SortedScoreRankList;

public class LMQuery {

	String itsID;
	QueryTest itsTest;
	public static int RESULT_NB = 10;
	List<LOM> itsEntireDBLOMs = LOMRankingExist.getEntireDBLOMs();

	public LMQuery(String aQueryID) {
		itsID = aQueryID;
		// initialize fields
		List<String> theResults = LocalExistUtils
				.localXMLQuery("collection('/db/LMQuery')/pendingQueries/query[id/text() eq '"
						+ aQueryID + "']");
		try {
			SAXBuilder theBuilder = new SAXBuilder();
			if (theResults != null) {
				Element theQuery = theBuilder.build(
						new StringReader(theResults.get(0))).getRootElement();
				String theID = theQuery.getChildTextTrim("id");
				String theQueryNodeID = theQuery.getChildText("queryNodeID");
				String theKeywordQuery = theQuery.getChildText("keywordQuery");
				itsTest = new QueryTest(theID, theQueryNodeID, theKeywordQuery);

			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * return the results of the query as an ordered list of LOM XML strings
	 * 
	 * @return
	 */

	public List<String> getResults() {
		
		System.out.println("begin query execution");
		SortedScoreRankList<LOM> theRankBoostList;
		// if the RB ranker is empty use LUcene result instead
		if (LMQueryTraining.getRanker().isEmpty()){
			System.out.println("use Lucene init...");
			theRankBoostList = itsTest.getLOMRanking().getLuceneResults();
			System.out.print("ok");
		}
		else{
			System.out.println("use RB init.");
			theRankBoostList = new SortedScoreRankList<LOM>();
			for (LOM theLOM : LOMRankingExist.getEntireDBLOMs())
				try {
					theRankBoostList.add(theLOM, LMQueryTraining.getRanker().getHypothesisFor(
							itsTest.getLOMRanking(), theLOM));
					System.out.print(".");
				} catch (Exception e) {
					System.out.println(e);
					e.printStackTrace();
				}
			System.out.print(".");
		}
		List<String> theLOMXMLs = new ArrayList<String>();
		//get the first 20 LOMs
		System.out.print("Build result lists");
		Set<Integer> theRanks = theRankBoostList.itsSortedRankToScoreMap.keySet();
		for (Integer theRank : theRanks) {
			List<LOM> theLOMs = theRankBoostList.getObjectsForRank(theRank);
			if (theLOMs==null) break;
			for (LOM theLom2 : theLOMs) {
				theLOMXMLs.add(theLom2.toXMLString());
				if (theLOMXMLs.size()>RESULT_NB) break;
			}
			if (theLOMXMLs.size()>RESULT_NB) break;
		}
		return theLOMXMLs;
	}

}
