/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
