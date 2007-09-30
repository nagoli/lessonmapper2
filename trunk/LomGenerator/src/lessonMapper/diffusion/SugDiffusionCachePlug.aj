/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

/*import java.util.List;
import lessonMapper.diffusion.SugDifHandler;
import lessonMapper.diffusion.SugFunctionHandler;*/
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

/**
 * this aspect plug the system of cache on the Diffusion process
 * if the resdif method is called the cache is used instead.
 * but if the cache calls the resdif method, resdif proceed
 * but if the resdif method is called recursivly again the cache is used
 * 
 */
public aspect SugDiffusionCachePlug {

	static public boolean isActive(){
		return SugDiffusionCache.IsActive;
	}
	
	
	pointcut sugDif(LOM aL, LOMAttribute aA): 
		execution(LOMValue Diffusion.SugDif(LOM , LOMAttribute))
			&& args( aL,  aA);  
	

	
	/**
	 * call the cache if it exists and return null or execute init()
	 */
	LOMValue around(LOM aLOM, LOMAttribute aAttribute): sugDif(aLOM,aAttribute)&& if (isActive())
	{
		
		if (SugDiffusionCache.getInstance().hasCacheFor(aLOM, aAttribute) ){
			return SugDiffusionCache.getInstance().getCacheFor(aLOM, aAttribute);
		} else {
			LOMValue theValue = proceed(aLOM, aAttribute);
			SugDiffusionCache.getInstance().setCacheFor(aLOM, aAttribute,theValue);
			return theValue;
		}
	}
	
	
	
	
	
	/*
	
	pointcut initSugDif(): 
		execution(List<SugFunctionHandler> SugDifHandler.init());  
	
	pointcut getValueSugDif(): 
		execution(LOMValue SugDifHandler.getValue());  

	*/
	/**
	 * call the cache if it exists and return null or execute init()
	 */
/*	List<SugFunctionHandler> around(): initSugDif()
	{
		SugDifHandler theHandler = (SugDifHandler) thisJoinPoint.getTarget();
		if (SugDiffusionCache.getInstance().hasCacheFor(theHandler.itsLOM, theHandler.itsLOMAttribute) ){
			return null;
		} else return proceed();
	}
	*/
	/**
	 * internResDif must use cache if called recursively
	 */
/*	LOMValue around(): getValueSugDif()
	{
		SugDifHandler theHandler = (SugDifHandler) thisJoinPoint.getTarget();
		if (SugDiffusionCache.getInstance().hasCacheFor(theHandler.itsLOM, theHandler.itsLOMAttribute) ){
			return SugDiffusionCache.getInstance().getCacheFor(theHandler.itsLOM, theHandler.itsLOMAttribute);
		}
		else {
			LOMValue theValue = proceed();
			SugDiffusionCache.getInstance().setCacheFor(theHandler.itsLOM, theHandler.itsLOMAttribute,theValue);
			return theValue;
		}
	
	}
	*/
}