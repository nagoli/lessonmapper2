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
public class ResLuceneRanker extends ResRanker {

	
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public ResLuceneRanker(LOMAttribute aAttribute) {
		super(aAttribute);
	}
	
	public ResLuceneRanker() {
		super();
	}

	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.ResRanker#getSortedScoreRankList(lessonMapper.query.LOMRanking)
	 */
	@Override
	SortedScoreRankList<LOM> getSortedScoreRankList(LOMRanking aLOMRanking) {
		return aLOMRanking.getRestrictionLuceneResults(itsAttribute);
	}
	
	 
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.ResRanker#toString()
	 */
	@Override
	public String toString() {
		return super.toString()+ "*Lucene";
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.query.rankboost.ResRanker#compareTo(lessonMapper.query.rankboost.Ranker)
	 */
	public int compareTo(Ranker aRanker) {
		if (aRanker.getClass() != getClass()) return getClass().getName().compareTo(aRanker.getClass().getName());
		ResLuceneRanker theRanker = (ResLuceneRanker)aRanker;
		if (theRanker.itsAttribute != itsAttribute) return itsAttribute.getName().compareTo(theRanker.itsAttribute.getName());
		return 0;
	}
}