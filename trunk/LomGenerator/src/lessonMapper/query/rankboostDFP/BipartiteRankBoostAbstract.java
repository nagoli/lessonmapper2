/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import rossi.dfp.dfpmath;
import util.Couple;

/**
 * 
 */
public abstract class BipartiteRankBoostAbstract extends RankBoostRanker{

	//static public MathContext ITSPrecision = MathContext.DECIMAL64;
	//static public MathContext ITSLowPrecision = MathContext.DECIMAL32;
	/**
	 * 
	 */
	static public long ITSPrecision = 16;
	
	/**
	 * 
	 */
	static public boolean isLongTraining = true;
	
	/**
	 * 
	 */
	protected int itsRoundNb;
	
	/**
	 * 
	 */
	protected List<Couple<LOMRanking, LOM>> itsWholeLOMList;
	
	/**
	 * 
	 */
	protected List<Couple<LOMRanking, LOM>> itsExpectedLOMList;
	
	/**
	 * 
	 */
	protected List<LOMRanking> itsQueryList;

	/**
	 * 
	 */
	protected List<Ranker> itsRankerList;
	{
		itsRankerList = new ArrayList<Ranker>();
		if (isLongTraining){
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				itsRankerList.add(new SugRanker(theAttribute));
			}
			for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
				itsRankerList.add(new ResRanker(theAttribute));
			}
		}
		itsRankerList.add(new LuceneRanker());
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			itsRankerList.add(new SugLuceneRanker(theAttribute));
		}
		for (LOMAttribute theAttribute : LOMRanking.getAttributeList()) {
			itsRankerList.add(new ResLuceneRanker(theAttribute));
		}
	}
	
	/**
	 * 
	 * 
	 * @param aQueryList 
	 * @param aRoundNb 
	 * @param aExpectedLOMList 
	 * @param aWholeLOMList 
	 */
	public BipartiteRankBoostAbstract(int aRoundNb, List<LOMRanking> aQueryList,
			List<Couple<LOMRanking, LOM>> aWholeLOMList,
			List<Couple<LOMRanking, LOM>> aExpectedLOMList) {
		this(aRoundNb, aQueryList,aWholeLOMList,aExpectedLOMList,false);
	}
	
	
	/**
	 * 
	 * 
	 * @param aQueryList 
	 * @param aRoundNb 
	 * @param aExpectedLOMList 
	 * @param aWholeLOMList 
	 * @param isManual 
	 */
	public BipartiteRankBoostAbstract(int aRoundNb, List<LOMRanking> aQueryList,
			List<Couple<LOMRanking, LOM>> aWholeLOMList,
			List<Couple<LOMRanking, LOM>> aExpectedLOMList,boolean isManual) {
		itsRoundNb = aRoundNb;
		itsWholeLOMList = aWholeLOMList;
		itsExpectedLOMList = aExpectedLOMList;
		itsQueryList = aQueryList;
		itsHypothesisAndAlphaList = new LinkedHashMap<Hypothesis, dfp>();
		if (!isManual) buildHypothesis();
	}

	
	
	
	/**
	 * 
	 * 
	 * @param aRankerList 
	 * @param aDistribution 
	 * @param aLOMList 
	 * 
	 * @return 
	 */
	public abstract Couple<Hypothesis, dfp> weakLearn(List<Couple<LOMRanking, LOM>> aLOMList, BipartiteDistribution aDistribution, List<Ranker> aRankerList) ;
		
	
	/**
	 * build Hypothesis taking into account itsRoundNb.
	 */
	public void buildHypothesis() {
		BipartiteDistribution theDistribution = getInitialDistribution();
		for (int i = 0; i < itsRoundNb; i++) {
			theDistribution = nextRound(theDistribution);
			if (i%5 == 0) System.out.print("."+i);
			itsRoundDone++;
		}
	}

	/**
	 * build Hypothesis taking into account itsRoundNb
	 * it Takes a snapshop everytime there is reach one of the roundToTakeIntoAccount ordered list.
	 * 
	 * @param roundToTakeIntoAccount 
	 * @param aListOfRankerSnapShot 
	 */
	public void buildHypothesis(List<RankBoostRanker> aListOfRankerSnapShot, int[] roundToTakeIntoAccount) {
		BipartiteDistribution theDistribution = getInitialDistribution();
		int j=0;
		for (int i = 0; i < itsRoundNb; i++) {
			theDistribution = nextRound(theDistribution);
			if (i%5 == 0) System.out.print("."+i);
			if (j < roundToTakeIntoAccount.length && roundToTakeIntoAccount[j]==i) {
				aListOfRankerSnapShot.add(new RankBoostRanker(this));
				j++;
			}
			itsRoundDone++;
		}
	}

	

	/**
	 * 
	 * 
	 * @return 
	 */
	public InitialDistribution getInitialDistribution() {
		return new InitialDistribution(itsExpectedLOMList, itsWholeLOMList);
	}


	/**
	 * 
	 * 
	 * @param theDistribution 
	 * 
	 * @return 
	 */
	public BipartiteDistribution nextRound(BipartiteDistribution theDistribution) {
		Couple<Hypothesis, dfp> theCouple = weakLearn(itsWholeLOMList,
				theDistribution, itsRankerList);
		theDistribution = new RoundDistribution(theDistribution, theCouple
				.getLeftElement(), theCouple.getRightElement());
		dfp alpha = getAlpha(theCouple.getLeftElement()); 
		itsHypothesisAndAlphaList.put(theCouple.getLeftElement(),theCouple.getRightElement().add(alpha));
		return theDistribution;
	}

	
	/**
	 * return alpha = 0.5 * ln ((1+r)/(1-r))     (def 4.6)
	 * 
	 * @param r 
	 * 
	 * @return 
	 */
	public dfp getAlpha(dfp r) {
		return dfpmath.ln(ONE.add(r).divide(ONE.subtract(r))).divide(TWO);
	}

	/**
	 * return theAlpha for an hypotesis equivalent to the Parameter aHypothesis.
	 * 
	 * @param aHypothesis 
	 * 
	 * @return 
	 */
	public dfp getAlpha(Hypothesis aHypothesis) {
		if (itsHypothesisAndAlphaList.containsKey(aHypothesis)) return itsHypothesisAndAlphaList.get(aHypothesis);
		return ZERO; 
	}

	

}