/*
 * LessonMapper 2.
 */
package lessonMapper.query.rankboostDFP;

import lessonMapper.lom.LOM;
import lessonMapper.query.LOMRanking;
import util.SortedScoreRankList;

/**
 * 
 */
public class LuceneRanker extends Ranker {

	

	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboostDFP.Ranker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getLuceneResults();
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboostDFP.Ranker#toString()
	 */
	@Override
	public String toString() {
		return "Lucene";
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Ranker aRanker) {
		if (aRanker.getClass() != getClass()) return getClass().getName().compareTo(aRanker.getClass().getName());
		return 0;
	}
	
}