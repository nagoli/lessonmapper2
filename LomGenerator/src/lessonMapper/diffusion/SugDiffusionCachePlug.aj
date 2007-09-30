/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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