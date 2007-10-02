/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboost;



import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import util.Couple;



/**
 * 
 */
public class RankHypothesis extends Hypothesis {

	/**
	 * 
	 * 
	 * @param aRanker 
	 * @param aTeta 
	 */
	public RankHypothesis(Ranker aRanker, double aTeta) {
		super(aRanker, aTeta);
	}

	public RankHypothesis(){
		super();
	}
	/**
	 * return 1 if rank > teta else 0.
	 * 
	 * @param theCouple 
	 * 
	 * @return 
	 */
	public int getValueFor(Couple<LOMRanking,LOM> theCouple){
		if (itsRanker.getRank(theCouple.getLeftElement(),theCouple.getRightElement())> getDoubleTeta())
			return 1;
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Hypothesis#toString()
	 */
	@Override
	public String toString() {
		return "Rank-"+super.toString();
	}
	
}