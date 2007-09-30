/*
 * LessonMapper 2.
 */
package lessonMapper.validation.CMV;



/**
 * this class caches the validation state of the couple Lom, LOMAttribute
 * Cache is updated when a new restriction is updated in the diffusionCache
 * @author omotelet
 *
 */

/**
 * 
 */
public class  ValidationCachePlug {

	
	/**
	 * catch the execution of getValidationState and return cache instead
	 */
/*	ValidationState around(LOM aLOM, LOMAttribute aLOMAttribute): 
		execution(ValidationState Validation.getValidationState(LOM, LOMAttribute))
		&& args( aLOM,aLOMAttribute) 
		&& ! (cflow(execution(void ValidationCache.updateCache(LOM, LOMAttribute)))
			&& !cflow(execution(ValidationStats Validation.getStatsFor(LOM))))	
		{
		return ValidationCache.getInstance().getStateCacheFor(aLOM, aLOMAttribute);
	}*/
	
	/**
	 * catch the execution of getStatsFor and return cache instead
	 */
	/*ValidationStats around(LOM aLOM): 
		execution(ValidationStats Validation.getStatsFor(LOM))
		&& args( aLOM) && !within(ValidationCache)
		&& !cflow(execution(void ValidationCache.updateCache(LOM, LOMAttribute)))
		{
		return ValidationCache.getInstance().getStatCacheFor(aLOM);
	}*/

	/**
	 * catch the updates to the DiffusionCache 
	 * and updates the validaitoncache in case of changes
	 */
/*	after(LOM aLOM, LOMAttribute aAttribute, boolean isValueUpdate)
	returning (boolean aBool):
		execution(boolean DiffusionCache.setRestrictionFor(LOM, LOMAttribute, LOMRestrictionSet)) 
			&& args(aLOM,aAttribute,..)&& 
			cflow(execution(void updateRestrictionFor(LOM, LOMAttribute ,
			Set<LOM> , boolean )) && args(.., isValueUpdate))
			{
			if (aBool||isValueUpdate ) {
				ValidationCache.getInstance().updateCache(aLOM,aAttribute);
			}
	}
	*/
	
	

	
}