/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
