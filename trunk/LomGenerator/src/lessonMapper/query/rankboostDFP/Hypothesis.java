/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;



import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import util.Couple;

/**
 * a hypothesis function is defined as in 4.9:
 * h(x) = 1 if fi(x) >teta
 * h(x) = 0 if fi(x)<= teta
 * h(x) = q if fi(x) is not defined
 * 
 * @author omotelet
 */

public abstract class Hypothesis implements Comparable<Hypothesis>{

	 /**
 	 * 
 	 */
 	static int ITSTetaPrecision=3; 
	
	
	/**
	 * 
	 */
	Ranker itsRanker;
	
	/**
	 * 
	 */
	int teta;

	
	/**
	 * 
	 * 
	 * @param aRanker 
	 * @param aTeta 
	 */
	public Hypothesis(Ranker aRanker, double aTeta) {
		super();
		itsRanker = aRanker;
		teta = roundTeta(aTeta);

	}
	
	/**
	 * round teta to avoid precision problem.
	 * 
	 * @param aTeta 
	 * 
	 * @return 
	 */
	protected int roundTeta(double aTeta) {
		return (int) Math.rint(aTeta * Math.pow(10, ITSTetaPrecision));
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public double getDoubleTeta() {
		return ((double) teta) * Math.pow(10, -1 * ITSTetaPrecision);
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public dfp getDFPTeta() {
		return new dfp(""+teta).multiply(dfp.one.power10(-1 * ITSTetaPrecision));
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Hypothesis aHypothesis) {
		if (aHypothesis.getClass() != getClass()) return getClass().getName().compareTo(aHypothesis.getClass().getName());
		int theComparison = itsRanker.compareTo(aHypothesis.itsRanker);
		if (theComparison != 0) return theComparison;
		if (teta == aHypothesis.teta) return 0;
		if (teta < aHypothesis.teta) return -1;
		return 1;	 
	}
	
	/**
	 * return 1 if rank/score > teta else 0.
	 * 
	 * @param theCouple 
	 * 
	 * @return 
	 */
	public abstract int getValueFor(Couple<LOMRanking,LOM> theCouple);
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		//return itsRanker.toString()+" with teta="+teta;
		return itsRanker + " " + getDFPTeta();
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return itsRanker.hashCode()+teta;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object aObj) {
		if (aObj instanceof Hypothesis){
			Hypothesis theHypothesis = (Hypothesis)aObj;
			return itsRanker.equals(theHypothesis.itsRanker) && teta==theHypothesis.teta;
		}
		return false;
	
	}
	
	
}
