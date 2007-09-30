/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboostDFP;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.query.LOMRanking;
import util.SortedScoreRankList;

/**
 * 
 */
public class SugRanker extends Ranker {

	/**
	 * 
	 */
	LOMAttribute itsAttribute;
	
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public SugRanker(LOMAttribute aAttribute) {
		super();
		itsAttribute = aAttribute;
	}

	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboostDFP.Ranker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getSuggestionResults(itsAttribute);
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboostDFP.Ranker#toString()
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