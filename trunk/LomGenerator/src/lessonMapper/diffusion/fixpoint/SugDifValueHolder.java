/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;


import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;


/**
 * this class keeps all the values of the LOM considered in the application.
 * 
 * @author omotelet
 */
public class SugDifValueHolder extends AttributeBasedDifValueHolder{

	
	/**
	 * 
	 */
	static Map<LOMAttribute,SugDifValueHolder> ITSInstances = new HashMap<LOMAttribute, SugDifValueHolder>();
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static SugDifValueHolder getInstance(LOMAttribute aAttribute){
		SugDifValueHolder theHolder = ITSInstances.get(aAttribute);
		if (theHolder == null) {
			theHolder = new SugDifValueHolder(aAttribute);
			ITSInstances.put(aAttribute, theHolder);
		}
		return theHolder;
	}
	
	/**
	 * 
	 */
	boolean isUpdated =false;
	
	/**
	 * 
	 */
	Set<LOM> itsUpdatedValues = new HashSet<LOM>();
	
	
	/**
	 * 
	 */
	Map<LOM, SugDifValue>  itsSugValues = new HashMap<LOM,SugDifValue>();

	
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public SugDifValueHolder(LOMAttribute aAttribute){
		super(aAttribute);
	}
	
	/**
	 * 
	 * 
	 * @param aHolderToClone 
	 */
	public SugDifValueHolder(SugDifValueHolder aHolderToClone){
		super( aHolderToClone.itsAttribute);
		itsSugValues = new HashMap<LOM, SugDifValue>(aHolderToClone.itsSugValues);
	}
	
	
	
	/**
	 * return the SugValue associated with aLOM and aAttribute
	 * return null if there is no value associated.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public SugDifValue	getValue(LOM aLOM){
		SugDifValue theValue = itsSugValues.get(aLOM);
		if ((isUpdated && !itsUpdatedValues.contains(aLOM))|| theValue == null){
				theValue = createValue(aLOM);
				if (isUpdated) itsUpdatedValues.add(aLOM);
		}
		return theValue;
	}
	
	
	
	
	/**
	 * return the SugValue associated with aLOM and aAttribute
	 * return null if there is no value associated.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	protected SugDifValue createValue(LOM aLOM){
		SugDifValue theValue= new SugDifValue(itsAttribute,aLOM);
		itsSugValues.put(aLOM,theValue);
		return theValue;
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#reset()
	 */
	@Override
	public void reset() {
		itsSugValues.clear();
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#initUpdate(java.util.Collection)
	 */
	@Override
	public void initUpdate( Collection<LOM> aListOfChangedLOMs) {
		isUpdated =true;
		itsUpdatedValues.clear();
		//reset();
		/*for (LOM theLom : aListOfChangedLOMs) 
			getValue(theLom).initUpdate();	*/	
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#endUpdate()
	 */
	@Override
	public void endUpdate() {
		isUpdated=false;
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.AttributeBasedDifValueHolder#toString()
	 */
	@Override
	public String toString() {
		return "Suggestions("+itsAttribute.getName()+")";
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#size()
	 */
	@Override
	public int size() {
		return itsSugValues.size();
	}
	
}
