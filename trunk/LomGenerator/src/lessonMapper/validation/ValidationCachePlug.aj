/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.validation;



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
