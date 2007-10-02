/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

/**
 * 
 */
public abstract class AttributeBasedDifValue extends DifValue {

	/**
	 * 
	 */
	LOMAttribute itsAttribute;
	
	/**
	 * 
	 */
	boolean isUpdatedLOMValue=false;
	
	/**
	 * 
	 */
	LOMValue itsLOMValueCache;
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public AttributeBasedDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super(aLOM);
	    itsAttribute = aAttribute;
	}
	
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValue getLOMValue(){
		if (isUpdatedLOMValue) return itsLOMValueCache;
		if (itsLOM == null){
			System.out.println("Intend to manipulate LOMValue when LOM is not defined");
			return null;
		}
		itsLOMValueCache =  itsAttribute.getValueIn(itsLOM);
		isUpdatedLOMValue = true;
		return itsLOMValueCache;
	}
	
	
	
	
	
}