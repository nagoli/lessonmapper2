/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
