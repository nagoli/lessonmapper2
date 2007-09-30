/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboost;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import util.Couple;

/**
 * 
 */
public class BipartiteScoreBoost extends BipartiteRankBoostAbstract {


	/**
	 * 
	 */
	static protected List<Double> itsThresholdList;
	static {
		itsThresholdList = new ArrayList<Double>();
		for (double i =1 ; i>=-0.01; i-=0.01)
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
	public BipartiteScoreBoost(int aRoundNb, List<LOMRanking> aQueryList,
			List<Couple<LOMRanking, LOM>> aWholeLOMList,
			List<Couple<LOMRanking, LOM>> aExpectedLOMList) {
		super(aRoundNb,aQueryList,aWholeLOMList,aExpectedLOMList);
		
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
	public Couple<Hypothesis, Double> weakLearn(List<Couple<LOMRanking, LOM>> aLOMList, BipartiteDistribution aDistribution, List<Ranker> aRankerList) {
		Hypothesis theWeakHypothesis = null;
		//init the Pi(x) with pi(x) = d(x)s(x) since we are in the bipartite case
		Map<Couple<LOMRanking, LOM>, Double> thePiMap = new HashMap<Couple<LOMRanking, LOM>, Double>();
		for (Couple<LOMRanking, LOM> theLOM : aLOMList) 
			thePiMap.put(theLOM, aDistribution.getD(theLOM)*(aDistribution.getS(theLOM)));
	
		double r = ZERO;
		//look for the weakHypothesis		
		for (Ranker theRanker : aRankerList) {
			double L = ZERO;
			for (int j = 1; j < itsThresholdList.size(); j++) {
				double theSupThresholdSum = ZERO;
					for (LOMRanking theRanking : itsQueryList){
						Collection<LOM> theLOMs = theRanker.getLOMForScore(
								theRanking, itsThresholdList.get(j-1),itsThresholdList.get(j) );
						if (theLOMs!=null) 
							for (LOM theLOM : theLOMs) 
								theSupThresholdSum = theSupThresholdSum+(thePiMap.get(new Couple<LOMRanking, LOM>(theRanking, theLOM)));
					}
				L = L+(theSupThresholdSum);
				if (Math.abs(L)>(Math.abs(r))) {
					Hypothesis theHypothesis = new ScoreHypothesis(theRanker,
							itsThresholdList.get(j));
					// cummulative weaklearner checking
					if (getAlpha(L)+(getAlpha(theHypothesis))>(ZERO)){
						r = L;
						theWeakHypothesis = theHypothesis;
					}
				}
			}
		}
		return new Couple<Hypothesis, Double>(theWeakHypothesis, getAlpha(r));
	}
	
}