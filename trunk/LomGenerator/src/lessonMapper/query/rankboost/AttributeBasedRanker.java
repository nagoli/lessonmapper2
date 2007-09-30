package lessonMapper.query.rankboost;

import lessonMapper.lom.LOMAttribute;

public abstract class AttributeBasedRanker extends Ranker {

	/**
	 * 
	 */
	protected LOMAttribute itsAttribute;

	public AttributeBasedRanker(LOMAttribute aAttribute) {
		super();
		itsAttribute=aAttribute;
	}
	
	public AttributeBasedRanker() {
		super();
	}

}