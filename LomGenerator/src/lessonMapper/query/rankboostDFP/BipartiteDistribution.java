/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;

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
public abstract class BipartiteDistribution {

	/**
	 * 
	 */
	static public long ITSPrecision = BipartiteRankBoostAbstract.ITSPrecision;
	
	/**
	 * 
	 */
	static public dfp ZERO = BipartiteRankBoostAbstract.ZERO;
	
	/**
	 * 
	 */
	static public dfp ONE = BipartiteRankBoostAbstract.ONE;
	
	/**
	 * 
	 */
	static public dfp TWO = BipartiteRankBoostAbstract.TWO;
	
	/**
	 * 
	 */
	static public dfp MINUSONE = BipartiteRankBoostAbstract.MINUSONE;
	
	/**
	 * 
	 */
	List<Couple<LOMRanking,LOM>> itsExpectedLOMList, itsWholeLOMList;
	
	/**
	 * 
	 */
	Map<Couple<LOMRanking,LOM>, Boolean> isExpectedMap = new HashMap<Couple<LOMRanking,LOM>, Boolean>();
	
	/**
	 * 
	 */
	boolean isSumVExpectedInit = false;

	/**
	 * 
	 */
	dfp itsSumVExpected;

	/**
	 * 
	 */
	boolean isSumVNotExpectedInit = false;

	/**
	 * 
	 */
	dfp itsSumVNotExpected;

	/**
	 * 
	 */
	Map<Couple<LOMRanking, LOM>, dfp> itsVFunction = new HashMap<Couple<LOMRanking, LOM>, dfp>();

	/**
	 * definition 4.7 :  D(x0,x1) = v(x0)*v(x1)
	 * 
	 * @param aX1 
	 * @param aX0 
	 * 
	 * @return 
	 */
	public dfp getD(Couple<LOMRanking, LOM> aX0, Couple<LOMRanking, LOM> aX1) {
		if (isExpected(aX1))
			return itsVFunction.get(aX0).multiply(itsVFunction.get(aX1));
		return ZERO;
	}

	/**
	 * definition 4.7 :  D(x) = v(x)*sum (v(x1)) for x in X0
	 * v(x)*sum(v(x0))  for x in x1
	 * 
	 * @param aX 
	 * 
	 * @return 
	 */
	public dfp getD(Couple<LOMRanking, LOM> aX) {
		if (isExpected(aX))
			return getV(aX).multiply(getSumVNotExpected());
		else return getV(aX).multiply(getSumVExpected());
	}

	/**
	 * 
	 * 
	 * @param aX 
	 * 
	 * @return 
	 */
	public dfp getS(Couple<LOMRanking, LOM> aX) {
		if (isExpected(aX))
			return ONE;
		return MINUSONE;
	}

	/**
	 * 
	 * 
	 * @param theCouple 
	 * 
	 * @return 
	 */
	public dfp getV(Couple<LOMRanking, LOM> theCouple) {
		return itsVFunction.get(theCouple);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public dfp getSumVExpected() {
		if (!isSumVExpectedInit||itsSumVExpected==null) {
			itsSumVExpected = ZERO;
			for (Couple<LOMRanking, LOM> theCouple : getExpectedLOMList())
				itsSumVExpected = itsSumVExpected.add(getV(theCouple));
			isSumVExpectedInit=true;
		}
		return itsSumVExpected;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public dfp getSumVNotExpected() {
		if (!isSumVNotExpectedInit|| itsSumVNotExpected == null) {
			itsSumVNotExpected = ZERO;
			for (Couple<LOMRanking, LOM> theCouple : getLOMList()){
				if (!isExpected(theCouple))
					itsSumVNotExpected = itsSumVNotExpected.add(getV(theCouple));
			}
			isSumVNotExpectedInit =true;
		}
		return itsSumVNotExpected;
	}

	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public boolean isExpected(Couple<LOMRanking,LOM> aLOM) {
		return isExpectedMap.get(aLOM);
	}

	
	/**
	 * 
	 * 
	 * @return 
	 */
	public List<Couple<LOMRanking,LOM>> getLOMList() {
		return itsWholeLOMList;
	}
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public List<Couple<LOMRanking,LOM>> getExpectedLOMList() {
		return itsExpectedLOMList;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#finalize()
	 */
	@Override
	protected void finalize() throws Throwable {
		super.finalize();
	}
	
	
}
