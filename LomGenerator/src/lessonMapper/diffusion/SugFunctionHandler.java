/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.List;

import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelationType;

/**
 * 
 */
public abstract class SugFunctionHandler {

	/**
	 * 
	 */
	private SugFunctionHandler itsParentFunction;

	/**
	 * 
	 */
	protected LOMAttribute itsLOMAttribute;

	/**
	 * 
	 */
	protected LOMRelationType itsRelationType;

	/**
	 * 
	 * 
	 * @return 
	 */
	public SugFunctionHandler getParentFunction() {
		return itsParentFunction;
	}

	/**
	 * 
	 * 
	 * @param aParentFunction 
	 */
	public void setParentFunction(SugFunctionHandler aParentFunction) {
		itsParentFunction = aParentFunction;
	}

	/**
	 * 
	 * 
	 * @param aTypeList 
	 * 
	 * @return 
	 */
	public List<LOMRelationType> getRelationTypeHistory(
			List<LOMRelationType> aTypeList) {
		if (itsRelationType != null )
			aTypeList.add(itsRelationType);
		if (itsParentFunction != null)
			return itsParentFunction.getRelationTypeHistory(aTypeList);
		return aTypeList;
	}

	/**
	 * 
	 * 
	 * @return 
	 */
	public abstract List<SugFunctionHandler> init();

	/**
	 * 
	 */
	public abstract void callback();

}