/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import util.Couple;

/**
 * 
 */
public class BipartiteRankBoost extends BipartiteRankBoostAbstract {


	/**
	 * 
	 */
	static protected List<Integer> itsThresholdList;
	static {
		 itsThresholdList = new ArrayList<Integer>();
		for (int i =-1 ; i>=-1*LOMRanking.getEntireDBSize()-1; i--)
			itsThresholdList.add(i);
	}
	
	
	/**
	 * 
	 * 
	 * @param aQueryList 
	 * @param aRoundNb 
	 * @param aExpectedLOMList 
	 * @param aWholeLOMList 
	 */
	public BipartiteRankBoost(int aRoundNb, List<LOMRanking> aQueryList,
			List<Couple<LOMRanking, LOM>> aWholeLOMList,
			List<Couple<LOMRanking, LOM>> aExpectedLOMList) {
		super(aRoundNb,aQueryList,aWholeLOMList,aExpectedLOMList);
	}

	
	
	/**
	 * 
	 * 
	 * @param aQueryList 
	 * @param aIsManual 
	 * @param aRoundNb 
	 * @param aExpectedLOMList 
	 * @param aWholeLOMList 
	 */
	public BipartiteRankBoost(int aRoundNb, List<LOMRanking> aQueryList, List<Couple<LOMRanking, LOM>> aWholeLOMList, List<Couple<LOMRanking, LOM>> aExpectedLOMList, boolean aIsManual) {
		super(aRoundNb, aQueryList, aWholeLOMList, aExpectedLOMList, aIsManual);
	}



	/**
	 * implementation of weak learn with q=0 (default value)
	 * return a weak hypotesis (fi, teta) and alpha (based on the third proposition:  see getAlpha)
	 * (algo fig 4.2)
	 * 
	 * @param aRankerList 
	 * @param aDistribution 
	 * @param aLOMList 
	 * 
	 * @return 
	 */
	public Couple<Hypothesis, dfp> weakLearn(List<Couple<LOMRanking, LOM>> aLOMList, BipartiteDistribution aDistribution, List<Ranker> aRankerList) {
		Hypothesis theWeakHypothesis = null;
		//init the Pi(x) with pi(x) = d(x)s(x) since we are in the bipartite case
		Map<Couple<LOMRanking, LOM>, dfp> thePiMap = new HashMap<Couple<LOMRanking, LOM>, dfp>();
		for (Couple<LOMRanking, LOM> theLOM : aLOMList) 
			thePiMap.put(theLOM, aDistribution.getD(theLOM).multiply(aDistribution.getS(theLOM)));
	
		dfp r = ZERO;
		//look for the weakHypothesis		
		for (Ranker theRanker : aRankerList) {
			dfp L = ZERO;
			for (int j = 1; j < itsThresholdList.size(); j++) {
				dfp theSupThresholdSum = ZERO;
				for (int rank = itsThresholdList.get(j - 1); rank > itsThresholdList
						.get(j); rank--)
					for (LOMRanking theRanking : itsQueryList){
						List<LOM> theLOMList = theRanker.getLOM(theRanking, rank);
						if (theLOMList!= null)
							for (LOM theLOM: theLOMList)
								if (theLOM!=null) 
									theSupThresholdSum= theSupThresholdSum.add(thePiMap.get(new Couple<LOMRanking, LOM>(theRanking, theLOM)));
					}
				L = L.add(theSupThresholdSum);
				if (dfp.copysign(L, ONE).greaterThan(dfp.copysign(r,ONE))) {
					Hypothesis theHypothesis = new RankHypothesis(theRanker,itsThresholdList.get(j));
					// cummulative weaklearner checking
					if (/*true*/ getAlpha(L).add(getAlpha(theHypothesis)).greaterThan(ZERO)){
						r = L;
						theWeakHypothesis = theHypothesis;
					}
				}
			}
		}
		return new Couple<Hypothesis, dfp>(theWeakHypothesis, getAlpha(r));
	}
	
}
