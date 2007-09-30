/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

/**
 * this aspect plug the system of cache on the Diffusion process
 * if the resdif method is called the cache is used instead.
 * but if the cache calls the resdif method, resdif proceed
 * but if the resdif method is called recursivly again the cache is used
 * 
 */
public aspect DiffusionCachePlug {

	static public boolean isActive(){
		return DiffusionCache.IsActive;
	}
	
	
	pointcut mainResDif(LOM aLom, LOMAttribute aAttribute): 
		execution(LOMRestrictionSet Diffusion.ResDif(LOM, LOMAttribute))
		&& args(aLom, aAttribute);  
	
	/*pointcut internResDif(LOM aLom, LOMAttribute aAttribute): 
		call(LOMRestrictionSet Diffusion.ResDif(LOM, LOMAttribute,Set))
		&& args(aLom, aAttribute,Set);  
*/
	pointcut update():
		execution( Set DiffusionCache.updateRestrictionFor(LOM, LOMAttribute, boolean));
	
	
	//Set<LOM> itsProcessedLOMs = new HashSet<LOM>();
	
	/**
	 * ResDif must use cache value if not called from the cache
	 */
	LOMRestrictionSet around(LOM aLOM, LOMAttribute aAttribute):
		!cflow(update()) && mainResDif(aLOM,aAttribute) && if (isActive())
	{
		//if (itsProcessedLOMs)
		//theProcessedLOMs.add(aLOM);
		 LOMRestrictionSet theSet = DiffusionCache.getInstance().getRestrictionFor(aLOM,aAttribute);
		 return theSet;
	}
	
	/**
	 * internResDif must use cache if called recursively
	 */
	/*LOMRestrictionSet around(LOM aLOM, LOMAttribute aAttribute):
		 internResDif(aLOM, aAttribute) && withincode(* Diffusion.PhiRes(..))
	{
		return DiffusionCache.getInstance().getRestrictionFor(aLOM,aAttribute);
	
	}*/
}