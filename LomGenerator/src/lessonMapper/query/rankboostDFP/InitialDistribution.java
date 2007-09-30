/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboostDFP;

import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import util.Couple;

/**
 * initial distribution is based on the bipartite rankboost definition:
 * we have to disjoint subset X0 and X1 respectively the not expected lom set and the expected lom set
 * this distribution will rank all X1 over X0 and say nothing about the others.
 * 
 * @author omotelet
 */
public class InitialDistribution extends BipartiteDistribution {

	
  
	/**
	 * 
	 * 
	 * @param aWholeLOMList 
	 * @param aExpectedLOMList 
	 */
	public InitialDistribution(List<Couple<LOMRanking,LOM>> aExpectedLOMList, List<Couple<LOMRanking,LOM>> aWholeLOMList) {
		super();
		itsExpectedLOMList = aExpectedLOMList;
		itsWholeLOMList = aWholeLOMList;
		for (Couple<LOMRanking,LOM> theLom : itsExpectedLOMList) {
			itsVFunction.put(theLom, ONE.divide(new dfp(""+itsExpectedLOMList.size())));
			isExpectedMap.put(theLom, true);
		}
		for (Couple<LOMRanking,LOM> theLom : itsWholeLOMList) 
			if (!itsVFunction.containsKey(theLom)){
				itsVFunction.put(theLom, ONE.divide(new dfp(""+(itsWholeLOMList.size()-itsExpectedLOMList.size()))));
				isExpectedMap.put(theLom, false);
			}
	}

	
	
	
	
}