/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboostDFP;

import java.util.Collection;
import java.util.List;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import rossi.dfp.dfp;
import util.SortedScoreRankList;

/**
 * 
 */
public abstract class Ranker implements Comparable<Ranker>{

	
	/**
	 * return theScore for aLOM in aLOMRanking.
	 * 
	 * @param aLOM 
	 * @param aLOMRanking 
	 * 
	 * @return 
	 */
	public dfp getScore(LOMRanking aLOMRanking, LOM aLOM) {
		return new dfp(""+getSortedScoreRankList(aLOMRanking).getNormalizedScoreFor(aLOM));
	}
	
	/**
	 * return -1* rank for aLOM in aLOMRanking.
	 * 
	 * @param aLOM 
	 * @param aLOMRanking 
	 * 
	 * @return 
	 */
	public int getRank(LOMRanking aLOMRanking, LOM aLOM) {
		return -1*getSortedScoreRankList(aLOMRanking).getRankFor(aLOM);
	}
	
	/**
	 * return the LOM of rank -1*aRank.
	 * 
	 * @param aLOMRanking 
	 * @param aRank 
	 * 
	 * @return 
	 */
	public List<LOM> getLOM(LOMRanking aLOMRanking,int aRank){
		return getSortedScoreRankList(aLOMRanking).getObjectsForRank(-1*aRank);
	}
	
	/**
	 * return the LOM for the Score between theInfScore(exclusive) to the SupScore (nclusive).
	 * 
	 * @param theInfScore 
	 * @param aLOMRanking 
	 * @param theSupScore 
	 * 
	 * @return 
	 */
	public Collection<LOM> getLOMForScore(LOMRanking aLOMRanking,double theSupScore, double theInfScore){
		return getSortedScoreRankList(aLOMRanking).getObjectsForNormalizedScores(theSupScore, theInfScore);
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aLOMRanking 
	 * 
	 * @return 
	 */
	public boolean contains(LOMRanking aLOMRanking,LOM aLOM){
		return getSortedScoreRankList(aLOMRanking).contains(aLOM);
	}
		
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Ranker";
	}
	
	
	
	/**
	 * return a list of lom sorted in common order (better at beginning).
	 * 
	 * @param aLOMRanking 
	 * 
	 * @return 
	 */
	abstract SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) ;

	
}
