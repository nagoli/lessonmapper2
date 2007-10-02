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
public class SugRanker extends AttributeBasedRanker {

	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public SugRanker(LOMAttribute aAttribute) {
		super(aAttribute);
	}

	public SugRanker() {
	super();
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Ranker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getSuggestionResults(itsAttribute);
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.Ranker#toString()
	 */
	@Override
	public String toString() {
		return itsAttribute.getName();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Ranker aRanker) {
		if (aRanker.getClass() != getClass()) return getClass().getName().compareTo(aRanker.getClass().getName());
		SugRanker theRanker = (SugRanker)aRanker;
		if (theRanker.itsAttribute != itsAttribute) return itsAttribute.getName().compareTo(theRanker.itsAttribute.getName());
		return 0;
	}
	
	
}