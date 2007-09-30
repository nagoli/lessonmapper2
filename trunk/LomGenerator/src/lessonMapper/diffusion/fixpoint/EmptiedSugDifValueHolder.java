/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import util.Couple;

/**
 * maintain a list of holder for.
 * 
 * @author omotelet
 */

public class EmptiedSugDifValueHolder extends SugDifValueHolder {

	/**
	 * 
	 */
	static Map<Couple<LOM, LOMAttribute>, EmptiedSugDifValueHolder> ITSInstances = new HashMap<Couple<LOM, LOMAttribute>, EmptiedSugDifValueHolder>();

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static EmptiedSugDifValueHolder getInstanceFor(LOM aLOM,
			LOMAttribute aAttribute) {
		Couple<LOM, LOMAttribute> theCouple = new Couple<LOM, LOMAttribute>(
				aLOM, aAttribute);
		EmptiedSugDifValueHolder theHolder = ITSInstances.get(theCouple);
		if (theHolder == null) {
			theHolder = new EmptiedSugDifValueHolder(aLOM, aAttribute);
			ITSInstances.put(theCouple, theHolder);
		}
		return theHolder;
	}

	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aValueHolderToClone 
	 * 
	 * @return 
	 */
	public static EmptiedSugDifValueHolder setInstanceFor(LOM aLOM,
			SugDifValueHolder aValueHolderToClone) {
		Couple<LOM, LOMAttribute> theCouple = new Couple<LOM, LOMAttribute>(
				aLOM, aValueHolderToClone.itsAttribute);
		EmptiedSugDifValueHolder theHolder = new EmptiedSugDifValueHolder(aLOM, aValueHolderToClone);
		ITSInstances.put(theCouple, theHolder);
		return theHolder;
	}

	/**
	 * 
	 * 
	 * @param aLOMList 
	 * @param aValueHolderToClone 
	 */
	public static void initInstancesFor(
			Collection<LOM> aLOMList, SugDifValueHolder aValueHolderToClone) {
		for (LOM theLom : aLOMList) {
			setInstanceFor(theLom, aValueHolderToClone);
		}
	}

	/**
	 * 
	 */
	LOM itsRootLOM;

	/**
	 * 
	 * 
	 * @param aRootLOM 
	 * @param aAttribute 
	 */
	public EmptiedSugDifValueHolder(LOM aRootLOM, LOMAttribute aAttribute) {
		super(aAttribute);
		itsRootLOM = aRootLOM;
	}

	/**
	 * make a clone of aHolderToCopy
	 * but replace the value for aRootLOM by an EmptyValue.
	 * 
	 * @param aRootLOM 
	 * @param aHolderToCopy 
	 */
	public EmptiedSugDifValueHolder(LOM aRootLOM, SugDifValueHolder aHolderToCopy) {
		super(aHolderToCopy);
		itsRootLOM = aRootLOM;
		//set theEmptiedValue
		createValue(itsRootLOM);
	}

	/**
	 * same as in sugvalueHolder but if the LOM is the rootLOM then the
	 * created value is a EmptiedSugValue.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	@Override
	public SugDifValue createValue(LOM aLOM) {
		if (aLOM == itsRootLOM) {
			SugDifValue theValue = new EmptiedSugDifValue(itsAttribute, aLOM);
			itsSugValues.put(aLOM, theValue);
			return theValue;
		}
		return super.createValue(aLOM);
	}

}
