/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.query.training;

import java.awt.Point;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lessonMapper.exist.query.GraphLoader;
import lessonMapper.exist.query.LOMRankingExist;
import lessonMapper.lom.LOM;

/**
 * a query test is responsible for executing the query and keeping trace of the
 * results.
 * 
 * @author omotelet
 */

public  class QueryTest {

	/** The ITS entire DBLO ms. */
	static List<LOM> ITSEntireDBLOMs = LOMRankingExist.getEntireDBLOMs();

	/** The ITS result max nb. */
	static int ITSResultMaxNb = 40;

	/** The its query LOMID. */
	String itsQueryLOMID;

	/** The its stats. */
	//private QueryResultStat itsStats;

	/** The its keyword query. */
	String itsKeywordQuery;

	

	

	/** The its LOM query. */
	//LOMQuery itsLOMQuery;

	/** The its LOM ranking. */
	LOMRankingExist itsLOMRanking;

	/** The its ID. */
	String itsID;

	/** The its suggestion score map. */
	Map<LOM, Double> itsSuggestionScoreMap = new HashMap<LOM, Double>();

	/** The its basic comparator. */
	Comparator<LOM> itsBasicComparator = new Comparator<LOM>() {
		public int compare(LOM o1, LOM o2) {
			double theScore1 = itsSuggestionScoreMap.get(o1);
			double theScore2 = itsSuggestionScoreMap.get(o2);
			if (theScore1 < theScore2)
				return 1;
			if (theScore1 == theScore2)
				return 0;
			else
				return -1;
		};
	};

	/** The its result position comparator. */
	Comparator<Point> itsResultPositionComparator = new Comparator<Point>() {
		public int compare(Point o1, Point o2) {
			return o1.y < o2.y ? 1 : (o1.y == o2.y ? 0 : -1);
		};
	};

	/**
	 * The Constructor.
	 * 
	 * @param expectedLOMs
	 *            the expected LO ms
	 * @param aGraphPath
	 *            the a graph path
	 * @param aQueryLOM
	 *            the a query LOM
	 * @param aKeywordQuery
	 *            the a keyword query
	 * @param id
	 *            the id
	 */
	public QueryTest(String id, String aQueryLOM, 
			String aKeywordQuery) {
		itsID = id;
		itsQueryLOMID = aQueryLOM;
		itsKeywordQuery = aKeywordQuery;
		init();
	}

	/**
	 * The Constructor.
	 * 
	 * @param aTest
	 *            the a test
	 */
	public QueryTest(QueryTest aTest) {
		itsID = aTest.itsID;
		itsQueryLOMID = aTest.itsQueryLOMID;
		itsKeywordQuery = aTest.itsKeywordQuery;
		itsLOMRanking = aTest.itsLOMRanking;
		init();
	}

	/**
	 * init test process and return a LOMRanking.
	 * 
	 * @return true, if init
	 */
	public boolean init() {
		if (itsLOMRanking == null ) {
			System.out.println("Loading graph for query" + itsID);
			try {
				//load all the lOMs
				new GraphLoader(itsID);
			} catch (Exception e) {
				e.printStackTrace();
			}
			// System.out.println("Building query ");
			LOM theQueryNode = LOM.getLOM(itsQueryLOMID);
			if (theQueryNode == null) {
				System.out.println("invalid query node");
				return false;
			}
			itsLOMRanking = new LOMRankingExist(theQueryNode, itsKeywordQuery);
			itsLOMRanking.initIfNeeded();
		} else System.out.println("Using cache for query " + itsID);
		return true;

	}



	

	public int getIndexOfID(List<LOM> aLOMs, String aID) {
		for (LOM theLom : aLOMs) {
			if (theLom.getID().equals(aID))
				return aLOMs.indexOf(theLom);
		}
		return -1;
	}

	/**
	 * Contains ID.
	 * 
	 * @param aLOMs
	 *            the a LO ms
	 * @param aID
	 *            the a ID
	 * 
	 * @return true, if contains ID
	 */
	public boolean containsID(Collection<LOM> aLOMs, String aID) {
		for (LOM theLom : aLOMs) {
			if (theLom.getID().equals(aID))
				return true;
		}
		return false;
	}

	public LOMRankingExist getLOMRanking() {
		return itsLOMRanking;
	}



}
