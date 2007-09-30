/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
