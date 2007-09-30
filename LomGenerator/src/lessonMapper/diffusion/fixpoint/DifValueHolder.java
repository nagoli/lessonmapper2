/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion.fixpoint;

import java.util.Collection;

import lessonMapper.lom.LOM;


/**
 * abstract class for Diffusion Value Holder
 * a difusion value holder has a method getValue.
 * 
 * @author omotelet
 */

public abstract class DifValueHolder {

	/**
	 * return the value associated with aLOM and aAttribute
	 * return a new value for aLOM and aAttribute if not exisiting.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public abstract DifValue getValue(LOM aLOM);

	
	/**
	 * 
	 */
	public abstract void reset();


	/**
	 * 
	 * 
	 * @param aListOfChangedLOMs 
	 */
	public abstract void initUpdate(Collection<LOM> aListOfChangedLOMs) ;
	
	/**
	 * 
	 */
	public abstract void endUpdate();
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public abstract int size();
}