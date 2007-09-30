/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.validation.CMV;

import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import util.Couple;

/**
 * 
 */
public class ValidationCache {

	static ValidationCache itsInstance = new ValidationCache();

	static public ValidationCache getInstance() {
		return itsInstance;
	}

	Map<Couple<LOM, LOMAttribute>, ValidationState> itsValidationCache = new HashMap<Couple<LOM, LOMAttribute>, ValidationState>();

	Map<LOM, ValidationStats> itsStatsCache = new HashMap<LOM, ValidationStats>();

	public void reset() {
		itsStatsCache.clear();
		itsValidationCache.clear();
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public void updateCache(LOM aLOM, LOMAttribute aAttribute) {
		ValidationState theState = Validation.getValidationState(aLOM,
				aAttribute);
		itsValidationCache.put(new Couple<LOM, LOMAttribute>(aLOM, aAttribute),
				theState);
		itsStatsCache.put(aLOM, Validation.getStatsFor(aLOM,null));
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public ValidationStats getStatCacheFor(LOM aLOM) {
		ValidationStats theStats = itsStatsCache.get(aLOM);
		if (theStats == null)
			return new ValidationStats();
		return theStats;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aLOMAttribute 
	 * 
	 * @return 
	 */
	public ValidationState getStateCacheFor(LOM aLOM, LOMAttribute aLOMAttribute) {
		ValidationState theState = itsValidationCache
				.get(new Couple<LOM, LOMAttribute>(aLOM, aLOMAttribute));
		if (theState == null)
			return new ValidationState(aLOMAttribute);
		else
			return theState;
	}

	

}
