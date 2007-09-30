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
 * Value holder for restriction diffusion
 */
public class ResDifValueHolder extends AttributeBasedDifValueHolder{

	
	
	/**
	 * 
	 */
	static Map<LOMAttribute,ResDifValueHolder> ITSInstances = new HashMap<LOMAttribute, ResDifValueHolder>();
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static ResDifValueHolder getInstance(LOMAttribute aAttribute){
		ResDifValueHolder theHolder = ITSInstances.get(aAttribute);
		if (theHolder == null) {
			theHolder = new ResDifValueHolder(aAttribute);
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
	Map<LOM, ResDifValue>  itsResValues = new HashMap<LOM,ResDifValue>();
	
	
	/**
	 * 
	 * 
	 * @param aAttribute 
	 */
	public ResDifValueHolder(LOMAttribute aAttribute) {
		super(aAttribute);
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#getValue(lessonMapper.lom.LOM)
	 */
	@Override
	public ResDifValue getValue(LOM aLOM) {
		ResDifValue theValue = itsResValues.get(aLOM);
		if ((isUpdated && !itsUpdatedValues.contains(aLOM)) || theValue == null){
			theValue = createValue(aLOM);
			if (isUpdated) itsUpdatedValues.add(aLOM);
		}
		return theValue;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public ResDifValue createValue(LOM aLOM){
		ResDifValue theResDifValue = new ResDifValue(itsAttribute,aLOM);
		itsResValues.put(aLOM, theResDifValue);
		return theResDifValue;
	}
	
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#initUpdate(java.util.Collection)
	 */
	@Override
	public void initUpdate(Collection<LOM> aListOfChangedLOMs) {
		isUpdated =true;  //reset();
		itsUpdatedValues.clear();
		//for (LOM theLom : aListOfChangedLOMs) 
		//	getValue(theLom).initUpdate();		
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#endUpdate()
	 */
	@Override
	public void endUpdate() {
		isUpdated=false;
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#reset()
	 */
	@Override
	public void reset() {
		itsResValues.clear();
	}

	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.AttributeBasedDifValueHolder#toString()
	 */
	@Override
	public String toString() {
		return "Restrictions("+itsAttribute.getName()+")";
	}
	
	/* (non-Javadoc)
	 * @see lessonMapper.diffusion.fixpoint.DifValueHolder#size()
	 */
	@Override
	public int size() {
		return itsResValues.size();
	}
	
}
