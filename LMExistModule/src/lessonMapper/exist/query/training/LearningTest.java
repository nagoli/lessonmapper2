/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.query.training;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import util.Couple;

/**
 * The Class LearningTest.
 */
public class LearningTest extends QueryTest {

	
	/** The its expected LOMI ds. */
	List<String> itsExpectedLOMIDs;
	
	/** The its expected LO ms. */
	List<LOM> itsExpectedLOMs;
	
	/**
	 * The Constructor.
	 * 
	 * @param aGraphPath
	 *            the a graph path
	 * @param aId
	 *            the a id
	 * @param aExpectedLOMs
	 *            the a expected LO ms
	 * @param aQueryLOM
	 *            the a query LOM
	 * @param aKeywordQuery
	 *            the a keyword query
	 */
	public LearningTest(String aId, String aQueryLOM,  String aKeywordQuery, List<String> aExpectedLOMs) {
		super(aId, aQueryLOM,  aKeywordQuery);
		itsExpectedLOMIDs = aExpectedLOMs;
	}

	/**
	 * The Constructor.
	 * 
	 * @param aTest
	 *            the a test
	 */
	public LearningTest(LearningTest aTest){
		super(aTest);
		itsExpectedLOMIDs=aTest.itsExpectedLOMIDs;
		itsExpectedLOMs=aTest.itsExpectedLOMs;
	}
	
	
	
	/**
	 * Gets the expected LO ms.
	 * 
	 * @return the expected LO ms
	 */
	public List<Couple<LOMRanking,LOM>> getExpectedLOMs(){
		List<Couple<LOMRanking, LOM>> theList = new ArrayList<Couple<LOMRanking,LOM>>();
		for (LOM theLOM : itsExpectedLOMs) 
			theList.add(new Couple<LOMRanking,LOM>(itsLOMRanking,theLOM));
		return theList;
	}
	
	/**
	 * Gets the all LO ms.
	 * 
	 * @return the all LO ms
	 */
	public List<Couple<LOMRanking,LOM>> getAllLOMs(){
		List<Couple<LOMRanking, LOM>> theList = new ArrayList<Couple<LOMRanking,LOM>>();
		for (LOM theLOM : ITSEntireDBLOMs) 
			theList.add(new Couple<LOMRanking,LOM>(itsLOMRanking,theLOM));
		return theList;
	}

	/**
	 * init the tests for this query.
	 * 
	 * @return true, if init
	 */
	public boolean init() {
		if (!super.init())
			return false;
		if (itsExpectedLOMs==null) {
			System.out.println("Loading expcted LOM for query" + itsID);
			//register entireDBLOMs
			for (LOM theLOM : ITSEntireDBLOMs)
				LOM.registerLOM(theLOM);
			itsExpectedLOMs = new ArrayList<LOM>();
			for (String theString : itsExpectedLOMIDs)
				itsExpectedLOMs.add(LOM.getLOM(theString));
		} else System.out.println("Using caches expected LOM for query " + itsID);
		itsLOMRanking.initIfNeeded();
		return true;
	}

	/**
	 * Gets the precision.
	 * 
	 * @param theResults
	 *            the results
	 * 
	 * @return the precision
	 */
	public double getPrecision(List<LOM> theResults) {
		return getPrecision(theResults, ITSResultMaxNb);
	}

	/**
	 * return the precision of theResults with maxNb as the further rank
	 * considered.
	 * 
	 * @param theResults
	 *            the results
	 * @param maxNb
	 *            the max nb
	 * 
	 * @return the precision
	 */
	public double getPrecision(List<LOM> theResults, int maxNb) {
		double theRelevantRetrievalLOMNb = getRelevantResultNb(theResults,
				maxNb);
		double theRetrievedLOMNb = theResults.size();
		return theRetrievedLOMNb < maxNb ? (theRelevantRetrievalLOMNb / theRetrievedLOMNb)
				: (theRelevantRetrievalLOMNb / maxNb);
	}

	/**
	 * return the number of expected results in theResults with maxNb as the
	 * further rank considered.
	 * 
	 * @param theResults
	 *            the results
	 * @param maxNb
	 *            the max nb
	 * 
	 * @return the relevant result nb
	 */
	public int getRelevantResultNb(List<LOM> theResults, int maxNb) {
		int theRelevantRetrievalLOMNb = 0;
		for (String theLOMID : itsExpectedLOMIDs) {
			int thePosition = getIndexOfID(theResults, theLOMID);
			if (thePosition >= 0 && thePosition < maxNb)
				theRelevantRetrievalLOMNb++;
		}
		return theRelevantRetrievalLOMNb;
	}

	/**
	 * return the number of expected results in theResults.
	 * 
	 * @param theResults
	 *            the results
	 * 
	 * @return the relevant result nb
	 */
	public int getRelevantResultNb(Collection<LOM> theResults) {
		int theRelevantRetrievalLOMNb = 0;
		for (String theLOMID : itsExpectedLOMIDs) {
			if (containsID(theResults, theLOMID))
				theRelevantRetrievalLOMNb++;
		}
		return theRelevantRetrievalLOMNb;
	}

	/**
	 * return a list with the rank of the expected results in theResults ranks
	 * are given between 1 and n (wether position are between 0 and n-1).
	 * 
	 * @param theResults
	 *            the results
	 * 
	 * @return the relevant result ranks
	 */
	public List<Integer> getRelevantResultRanks(List<LOM> theResults) {
		List<Integer> theRelevantRetrievalLOMNb = new ArrayList<Integer>();
		for (String theLOMID : itsExpectedLOMIDs) {
			int thePosition = getIndexOfID(theResults, theLOMID);
			if (thePosition >= 0)
				theRelevantRetrievalLOMNb.add(thePosition + 1);
		}
		return theRelevantRetrievalLOMNb;
	}

	/**
	 * return a list with the couples rank in the expected results list + rank
	 * of the expected results in theResults ranks are given between 1 and n
	 * (wether position are between 0 and n-1) list is ordereded by position.
	 * 
	 * @param theResults
	 *            the results
	 * 
	 * @return the relevant result positions
	 */
	public List<Point> getRelevantResultPositions(List<LOM> theResults) {
		List<Point> theRelevantRetrievalLOMNb = new ArrayList<Point>();
		int i = 1;
		for (String theLOMID : itsExpectedLOMIDs) {
			int thePosition = getIndexOfID(theResults, theLOMID);
			if (thePosition >= 0)
				theRelevantRetrievalLOMNb.add(new Point(i, thePosition + 1));
			else
				theRelevantRetrievalLOMNb.add(new Point(i, 10000));
			i++;
		}
		Collections
				.sort(theRelevantRetrievalLOMNb, itsResultPositionComparator);
		return theRelevantRetrievalLOMNb;
	}

	/**
	 * Gets the recall.
	 * 
	 * @param theResults
	 *            the results
	 * 
	 * @return the recall
	 */
	public double getRecall(List<LOM> theResults) {
		return getRecall(theResults, ITSResultMaxNb);
	}

	/**
	 * return the recall of theResults theResults with maxNb as the further rank
	 * considered.
	 * 
	 * @param theResults
	 *            the results
	 * @param maxNb
	 *            the max nb
	 * 
	 * @return the recall
	 */
	public double getRecall(List<LOM> theResults, int maxNb) {
		double theRelevantRetrievalLOMNb = getRelevantResultNb(theResults,
				maxNb);
		double theRelevantLOMNb = itsExpectedLOMIDs.size();
		return theRelevantLOMNb < maxNb ? (theRelevantRetrievalLOMNb / theRelevantLOMNb)
				: (theRelevantRetrievalLOMNb / maxNb);
	}

	/**
	 * build the recall precision values for this result set.
	 * 
	 * @param aResultList
	 *            the a result list
	 * 
	 * @return the recall precision list
	 */
	protected List<Point2D.Double> getRecallPrecisionList(List<LOM> aResultList) {
		List<Point2D.Double> theRecallPrecisionList = new ArrayList<Point2D.Double>();
		int theExpectedResultNb = itsExpectedLOMIDs.size();
		int theRetrievedExpectedResultNb = 0;
		int theResultNb = 0;
		for (LOM theLOM : aResultList) {
			theResultNb++;
			if (itsExpectedLOMIDs.contains(theLOM.getID().trim()))
				theRetrievedExpectedResultNb++;
			double precision = theRetrievedExpectedResultNb * 1.0 / theResultNb;
			double recall = theRetrievedExpectedResultNb * 1.0
					/ theExpectedResultNb;
			theRecallPrecisionList.add(new Point2D.Double(recall, precision));
		}
		return theRecallPrecisionList;
	}
	
}
