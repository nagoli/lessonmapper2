/*
 * LessonMapper 2.
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