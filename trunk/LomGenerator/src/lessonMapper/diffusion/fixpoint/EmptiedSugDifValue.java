/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

/**
 * sug value that does not return any result when lomvalue is asked.
 * 
 * @author omotelet
 */


public class EmptiedSugDifValue extends SugDifValue {

	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public EmptiedSugDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super(aAttribute, aLOM);
	}

	/**
	 * do not return lom value.
	 * 
	 * @return 
	 */
	@Override
	public LOMValue getLOMValue() {
		return null;
	}
	
	
	
}