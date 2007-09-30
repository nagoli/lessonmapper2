/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMRelationType;

/**
 * 
 */
public abstract class DifValue {

	
	/**
	 * 
	 */
	LOM itsLOM;
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 */
	public DifValue(LOM aLOM){
		itsLOM = aLOM;
	}
	
	
	/**
	 * return a new Value
	 * This value corresponds to the diffusion value resulting from
	 * diffusing this value through aRelationType
	 * This value is then combine with the target value with @see addAll.
	 * 
	 * @return 
	 */
	public abstract Value getDiffusion();
	
	
	/**
	 * return a new Value
	 * This value corresponds to the value resulting from
	 * diffusing this value through aRelationType
	 * this value may differ from getDiffusion
	 * in general it contains only the value related to the context excluding
	 * its own values.
	 * 
	 * @return 
	 */
	public abstract Value getValue();
	
	
	/**
	 * add the diffusion for aRelationTpye and aLOM
	 * return true if the operation has modified the already guarded relation
	 * else return false.
	 * 
	 * @param aLOM 
	 * @param aValue 
	 * @param aRelationType 
	 * 
	 * @return 
	 */
	public abstract boolean addDiffusion(LOMRelationType aRelationType, LOM aLOM, Value aValue);
	
	/**
	 * should be call when a node has been modified.
	 */
	public abstract void initUpdate();
	
}