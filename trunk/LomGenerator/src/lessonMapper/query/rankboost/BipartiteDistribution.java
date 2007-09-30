/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboost;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
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
	static public double ZERO = 0;
	
	/**
	 * 
	 */
	static public double ONE = 1;
	
	/**
	 * 
	 */
	static public double TWO = 2;
	
	/**
	 * 
	 */
	static public double MINUSONE = -1;
	
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
	double itsSumVExpected;

	/**
	 * 
	 */
	boolean isSumVNotExpectedInit = false;

	/**
	 * 
	 */
	double itsSumVNotExpected;

	/**
	 * 
	 */
	Map<Couple<LOMRanking, LOM>, Double> itsVFunction = new HashMap<Couple<LOMRanking, LOM>, Double>();

	/**
	 * definition 4.7 :  D(x0,x1) = v(x0)*v(x1)
	 * 
	 * @param aX1 
	 * @param aX0 
	 * 
	 * @return 
	 */
	public double getD(Couple<LOMRanking, LOM> aX0, Couple<LOMRanking, LOM> aX1) {
		if (isExpected(aX1))
			return itsVFunction.get(aX0)*itsVFunction.get(aX1);
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
	public double getD(Couple<LOMRanking, LOM> aX) {
		if (isExpected(aX))
			return getV(aX)*getSumVNotExpected();
		else return getV(aX)*getSumVExpected();
	}

	/**
	 * 
	 * 
	 * @param aX 
	 * 
	 * @return 
	 */
	public double getS(Couple<LOMRanking, LOM> aX) {
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
	public double getV(Couple<LOMRanking, LOM> theCouple) {
		return itsVFunction.get(theCouple);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public double getSumVExpected() {
		if (!isSumVExpectedInit) {
			itsSumVExpected = ZERO;
			for (Couple<LOMRanking, LOM> theCouple : getExpectedLOMList())
				itsSumVExpected = itsSumVExpected +getV(theCouple);
			isSumVExpectedInit=true;
		}
		return itsSumVExpected;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public double getSumVNotExpected() {
		if (!isSumVNotExpectedInit) {
			itsSumVNotExpected = ZERO;
			for (Couple<LOMRanking, LOM> theCouple : getLOMList()){
				if (!isExpected(theCouple))
					itsSumVNotExpected = itsSumVNotExpected+getV(theCouple);
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
