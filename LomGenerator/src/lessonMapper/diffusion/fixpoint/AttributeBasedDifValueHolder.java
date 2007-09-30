/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOMAttribute;

/**
 * 
 */
public abstract class AttributeBasedDifValueHolder extends DifValueHolder {

/**
 * 
 */
LOMAttribute itsAttribute;
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public  AttributeBasedDifValueHolder(LOMAttribute aAttribute){
		itsAttribute = aAttribute;
	}
	
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Holder For : "+itsAttribute;
	}
	
	
}