package lessonMapper.diffusion;

import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;

public class LOMHeuristicTable extends HeuristicTable {

	@Override
	public boolean put(String aAttributeName, String aRelationName,
			Class aHeuristic) {
		return put(LOMAttribute.getLOMAttribute(aAttributeName),
					LOMRelationType.getLOMRelationType(aRelationName), aHeuristic);
		
	}

}
