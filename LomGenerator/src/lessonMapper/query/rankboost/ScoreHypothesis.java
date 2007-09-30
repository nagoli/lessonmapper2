/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
