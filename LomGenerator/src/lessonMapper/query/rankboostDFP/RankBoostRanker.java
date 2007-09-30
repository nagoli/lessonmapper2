/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboostDFP;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import util.Couple;

/**
 * 
 */
public class RankBoostRanker implements Comparable<RankBoostRanker>{

	/**
	 * 
	 */
	static public dfp ZERO = dfp.zero;
	
	/**
	 * 
	 */
	static public dfp ONE = dfp.one;
	
	/**
	 * 
	 */
	static public dfp TWO = dfp.two;
	
	/**
	 * 
	 */
	static public dfp MINUSONE = dfp.one.negate();
	
	/**
	 * 
	 */
	protected Map<Hypothesis, dfp> itsHypothesisAndAlphaList;
	
	/**
	 * 
	 */
	protected int itsRoundDone;
	
	/**
	 * 
	 */
	public RankBoostRanker(){
	}
	
	/**
	 * 
	 * 
	 * @param aRankBoostRanker 
	 */
	public RankBoostRanker(RankBoostRanker aRankBoostRanker){
		itsHypothesisAndAlphaList= new LinkedHashMap<Hypothesis, dfp>(aRankBoostRanker.itsHypothesisAndAlphaList);
		itsRoundDone = aRankBoostRanker.itsRoundDone;
	}
	

	/**
	 * return hypothesis map.
	 * 
	 * @return 
	 */
	public Map<Hypothesis, dfp> getHypothesisMap() {
		return itsHypothesisAndAlphaList;
	}
	
	/**
	 * return the score for a LOM and aLOMRanking.
	 * 
	 * @param aLOM 
	 * @param aRanking 
	 * 
	 * @return 
	 */
	public double getHypothesisFor(LOMRanking aRanking, LOM aLOM) {
		dfp theSum = ZERO; 
		for (Entry<Hypothesis, dfp> theCouple : itsHypothesisAndAlphaList.entrySet()) {
			if (theCouple.getKey().getValueFor(
					new Couple<LOMRanking, LOM>(aRanking, aLOM)) == 1)
			theSum = theSum.add(theCouple.getValue());
		}
		return Double.parseDouble(theSum.toString());
	}

	/**
	 * return number of round calculated for building the hypothesis of this RBRanker.
	 * 
	 * @return 
	 */
	 public int getRoundDone() {
		 return itsRoundDone;
	 }
	
	 /* (non-Javadoc)
 	 * @see java.lang.Comparable#compareTo(java.lang.Object)
 	 */
 	public int compareTo(RankBoostRanker aO) {
		return new Integer(itsRoundDone).compareTo(aO.getRoundDone());
	}
	 
}