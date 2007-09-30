/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.exist.query.training;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import lessonMapper.query.rankboost.BipartiteRankBoost;
import lessonMapper.query.rankboost.BipartiteRankBoostAbstract;
import lessonMapper.query.rankboost.RankBoostRanker;
import util.Couple;

/**
 * this class is reponsible for launching a test sequence and display the result
 * on graph and save the results in a file.
 * 
 * @author omotelet
 */
public class TrainingLauncher {

	/** The round. */
	static int round = 0;

	/** The Constant maxRound. */
	static final int maxRound = 3;

	/** The Constant trainingProportion. */
	static final int trainingProportion = 75;

	/** The rank boost round. */
	static  int rankBoostRound = 500;
	
	/** The is one for training. */
	static boolean isOneForTraining = false;
	
		/** The its training tests. */
	LearningTest[] itsTrainingTests;

	
	/** The training position. */
	int position = 0, trainingPosition = 0;

	/** The ITS date. */
	static Date ITSDate = new Date(System.currentTimeMillis());

	/** The ITS date string. */
	@SuppressWarnings("deprecation")
	static String ITSDateString = "" + ITSDate.getMonth() + "_"
			+ ITSDate.getDate() + "_" + ITSDate.getHours() + "_"
			+ ITSDate.getMinutes() + "_";




	/** The its rank boost ranker list. */
	protected List<RankBoostRanker> itsRankBoostRankerList;
	

	/**
	 * The Constructor.
	 * 
	 * @param testNb
	 *            the test nb
	 * @param learningTestNb
	 *            the learning test nb
	 */
	public TrainingLauncher(int learningTestNb) {
		itsTrainingTests = new LearningTest[learningTestNb];
	}

	

	/**
	 * Adds the training test.
	 * 
	 * @param theTest
	 *            the test
	 */
	public void addTrainingTest(LearningTest theTest) {
		itsTrainingTests[trainingPosition] = theTest;
		trainingPosition++;
	}

	/**
	 * Train rank boost.
	 * 
	 * @return true, if train rank boost
	 * 
	 * @throws Exception
	 *             the exception
	 */
	public RankBoostRanker trainRankBoost() throws Exception {
		List<LOMRanking> theQueryList = new ArrayList<LOMRanking>();
		List<Couple<LOMRanking, LOM>> theWholeLOMList = new ArrayList<Couple<LOMRanking, LOM>>(), theExpectedLOMList = new ArrayList<Couple<LOMRanking, LOM>>();
		try {
			for (int i = 0; i < itsTrainingTests.length; i++) {
				boolean isInit = itsTrainingTests[i].init();
				if (isInit) {
					theQueryList.add(itsTrainingTests[i].itsLOMRanking);
					theWholeLOMList.addAll(itsTrainingTests[i].getAllLOMs());
					theExpectedLOMList.addAll(itsTrainingTests[i]
							.getExpectedLOMs());
				}
				itsTrainingTests[i] = null;
				LOM.resetCache();
				//System.gc();
			}
			System.out.println("training rankboost");
			
			BipartiteRankBoostAbstract theRB =  new BipartiteRankBoost(
					rankBoostRound, theQueryList, theWholeLOMList,
					theExpectedLOMList, true); 
			theRB.buildHypothesis();
			return theRB;
		} catch (Exception e) {
			System.out.println("error during training");
			throw e;
		}
	}

}