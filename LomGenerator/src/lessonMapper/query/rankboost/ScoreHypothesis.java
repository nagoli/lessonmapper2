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
public class ScoreHypothesis extends Hypothesis {

	public ScoreHypothesis() {
		super();
	}
	
	
	/**
	 * 
	 * 
	 * @param aRanker 
	 * @param aTeta 
	 */
	public ScoreHypothesis(Ranker aRanker, double aTeta) {
		super(aRanker, aTeta);
	}

	
	/**
	 * return 1 if rank > teta else 0.
	 * 
	 * @param theCouple 
	 * 
	 * @return 
	 */
	public int getValueFor(Couple<LOMRanking,LOM> theCouple){
		if (itsRanker.getScore(theCouple.getLeftElement(),theCouple.getRightElement())>(getDoubleTeta()))
			return 1;
		return 0;
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Hypothesis#toString()
	 */
	@Override
	public String toString() {
		return "Score-"+super.toString();
	}
	
	
	
}