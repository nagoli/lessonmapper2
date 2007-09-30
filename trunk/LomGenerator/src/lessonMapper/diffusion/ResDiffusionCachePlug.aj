/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;


import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

/**
 * this aspect plug a system of cache on the Diffusion process
 * when the resdif method is called the cache is used instead.
 * when the cache calls the resdif method, resdif proceed
 * but if the resdif method is called recursivly again the cache is used
 * 
 */
public aspect ResDiffusionCachePlug {
 
	
	static public boolean isActive(){
		return ResDiffusionCache.IsActive;
	}
	
	
	pointcut resDif(LOM aL, LOMAttribute aA): 
		execution(LOMRestrictionSet Diffusion.ResDif(LOM , LOMAttribute))
			&& args( aL,  aA);  
	

	
	/**
	 * call the cache if it exists and return null or execute init()
	 */
	LOMRestrictionSet around(LOM aLOM, LOMAttribute aAttribute): resDif(aLOM,aAttribute)&& if (isActive())
	{
		
		if (ResDiffusionCache.getInstance().hasCacheFor(aLOM, aAttribute) ){
			return ResDiffusionCache.getInstance().getCacheFor(aLOM, aAttribute);
		} else {
			LOMRestrictionSet theSet = proceed(aLOM, aAttribute);
			ResDiffusionCache.getInstance().setCacheFor(aLOM, aAttribute,theSet);
			return theSet;
		}
	}
	
	
}