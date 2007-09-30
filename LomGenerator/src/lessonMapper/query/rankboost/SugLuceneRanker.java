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
public class SugLuceneRanker extends SugRanker {

	
	public SugLuceneRanker() {
	super();
	}
		
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public SugLuceneRanker(LOMAttribute aAttribute) {
		super(aAttribute);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.SugRanker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getSuggestionLuceneResults(itsAttribute);
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.SugRanker#toString()
	 */
	@Override
	public String toString() {
		return super.toString() +  "*Lucene";
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.SugRanker#compareTo(lessonMapper.query.rankboost.Ranker)
	 */
	public int compareTo(Ranker aRanker) {
		if (aRanker.getClass() != getClass()) return getClass().getName().compareTo(aRanker.getClass().getName());
		SugLuceneRanker theRanker = (SugLuceneRanker)aRanker;
		if (theRanker.itsAttribute != itsAttribute) return itsAttribute.getName().compareTo(theRanker.itsAttribute.getName());
		return 0;
	}
	
	
}