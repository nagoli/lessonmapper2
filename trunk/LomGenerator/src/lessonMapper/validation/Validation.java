/*
 * LessonMapper 2.
 */
package lessonMapper.validation;

import static lessonMapper.validation.ValidationState.State.Valid;

import java.util.List;

import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.diffusion.fixpoint.Value;
import lessonMapper.diffusion.fixpoint.ResDifValueHolder;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;
import java.util.Collection;


/**
 * this class enables the validation
 * of lom.
 * 
 * @author omotelet
 */
public class Validation {
	
	
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static ValidationState getValidationState(LOM aLOM, LOMAttribute aAttribute) {
		if (aLOM == null || aAttribute == null ) return null;
		LOMValue theValue = aAttribute.getValueIn(aLOM);
		if (theValue==null) return new ValidationState(aAttribute);
		if (theValue.isEmpty()) return new ValidationState(aAttribute);
		//if the LOM Repository is empty validation can be
		//calculated for LOMValueInt but not LOMValueSet.
		//LOMRestrictionSet theRestrictionSet =  Diffusion.ResDif(aLOM,aAttribute);
		Value theRes = ResDifValueHolder.getInstance(aAttribute).getValue(aLOM).getValue();
		if (theRes!=null) {
			LOMRestrictionSet theRestrictionSet= (LOMRestrictionSet) theRes;
			List<LOMRestrictionValue> theRestrictions= theRestrictionSet.restrictionsNotSatisfiedBy(theValue);
			if (!theRestrictions.isEmpty())
				return new ValidationState(aAttribute,theRestrictions);
		}
		return new ValidationState(aAttribute,Valid);
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static ValidationStats getStatsFor(LOM aLOM, Collection<LOMAttribute> theAttributeList) {
		if (theAttributeList==null) theAttributeList = LOMAttribute.getRegisteredAttribute();
		ValidationStats theStats = new ValidationStats();
		for( LOMAttribute theAttribute: theAttributeList) {
			theStats.addValidationState(getValidationState(aLOM,theAttribute));
		}
		return theStats;
	}
	
	
}