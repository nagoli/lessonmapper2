/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import util.Couple;

/**
 * this class is reponsable for caching all the restrictions and suggestions
 * available in the current graph of LOMs.
 * 
 * For cache to work all new relation (or deletion) should be immediately
 * declare to the cache with the appropriate methods
 * 
 * Also change in the values of the lom should be declared to the cache
 * 
 * methods for declaring changes to the cache are synchronized
 * 
 * @author omotelet
 */

public class ResDiffusionCache {

/**
 * 
 */
static public boolean IsActive = false;
	
	/**
	 * 
	 * 
	 * @param aBool 
	 */
	static public void setActive(boolean aBool){
		IsActive = aBool;
	}
	
	
	/**
	 * 
	 */
	private static ResDiffusionCache ITSInstance = new ResDiffusionCache();

	/**
	 * 
	 * 
	 * @return 
	 */
	public static ResDiffusionCache getInstance() {
		return ITSInstance;
	}

	/**
	 * 
	 */
	Map<Couple<LOM, LOMAttribute>, LOMRestrictionSet> itsRestrictionCache = new HashMap<Couple<LOM, LOMAttribute>, LOMRestrictionSet>();

	
	/**
	 * 
	 */
	public void reset() {
		itsRestrictionCache.clear();
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public boolean hasCacheFor(LOM aLOM ,LOMAttribute aAttribute ){
		return itsRestrictionCache.containsKey(new Couple<LOM, LOMAttribute>(aLOM,aAttribute));
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public LOMRestrictionSet getCacheFor(LOM aLOM ,LOMAttribute aAttribute ){
		return itsRestrictionCache.get(new Couple<LOM, LOMAttribute>(aLOM,aAttribute));
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * @param aSet 
	 * 
	 * @return 
	 */
	public LOMRestrictionSet setCacheFor(LOM aLOM ,LOMAttribute aAttribute,LOMRestrictionSet aSet ){
		return itsRestrictionCache.put(new Couple<LOM, LOMAttribute>(aLOM,aAttribute),aSet);
	}
	
	
	
	
}