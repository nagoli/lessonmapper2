/*
 * LessonMapper 2.
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