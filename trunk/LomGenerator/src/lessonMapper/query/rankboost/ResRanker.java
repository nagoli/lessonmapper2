/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.query.rankboost;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.query.LOMRanking;
import util.SortedScoreRankList;

/**
 * 
 */
public class ResRanker extends AttributeBasedRanker {

	 /**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public ResRanker(LOMAttribute aAttribute) {
		super(aAttribute);
	}

	public ResRanker() {
		super();
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Ranker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getRestrictionResults(itsAttribute);
	}
	
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Ranker#toString()
	 */
	@Override
	public String toString() {
		return "Restriction-"+ itsAttribute.getName();
	}
	
	public int compareTo(Ranker aRanker) {
		if (aRanker.getClass() != getClass()) return getClass().getName().compareTo(aRanker.getClass().getName());
		ResRanker theRanker = (ResRanker)aRanker;
		if (theRanker.itsAttribute != itsAttribute) return itsAttribute.getName().compareTo(theRanker.itsAttribute.getName());
		return 0;
	}
}
