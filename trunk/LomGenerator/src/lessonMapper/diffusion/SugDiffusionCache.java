/*
 * LessonMapper 2.
 */
package lessonMapper.diffusion;

import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;
import util.Couple;

/**
 * 
 */
public class SugDiffusionCache {

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
	static SugDiffusionCache ITSInstance = new SugDiffusionCache();
	
	/**
	 * 
	 * 
	 * @return 
	 */
	static SugDiffusionCache getInstance(){
		return ITSInstance;
	}
	
	/**
	 * 
	 */
	Map< Couple<LOM, LOMAttribute>, LOMValue> itsCache = new HashMap<Couple<LOM,LOMAttribute>, LOMValue>();
	
	/**
	 * 
	 */
	public void resetCache(){
		itsCache.clear();
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
		return itsCache.containsKey(new Couple<LOM, LOMAttribute>(aLOM,aAttribute));
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public LOMValue getCacheFor(LOM aLOM ,LOMAttribute aAttribute ){
		//System.out.println("use sug cache");
		return itsCache.get(new Couple<LOM, LOMAttribute>(aLOM,aAttribute));
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aValue 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public LOMValue setCacheFor(LOM aLOM ,LOMAttribute aAttribute,LOMValue aValue ){
		return itsCache.put(new Couple<LOM, LOMAttribute>(aLOM,aAttribute),aValue);
	}
	
	
}