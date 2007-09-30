/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.validation.CMV;

import static lessonMapper.validation.CMV.ValidationState.State.Valid;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import diffuse.models.res.DownResCMVHolderStorage;
import diffuse.models.res.ResCMV;
import diffuse.models.res.UpResCMVHolderStorage;
import diffuse.propagation.CMV;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;


/**
 * this class enables the validation
 * of lom.
 * 
 * @author omotelet
 */
public class Validation {
	
	
	
	/**
	 * 
	 * return a validation state for a LOM and aAttribute
	 * @param aLOM 
	 * @param aAttribute 
	 * 
	 * @return 
	 */
	public static ValidationState getValidationState(LOM aLOM, LOMAttribute aAttribute) {
		if (aLOM == null || aAttribute == null ) return null;
		LOMValue theValue = aAttribute.getValueIn(aLOM);
		if (theValue==null) return new ValidationState(aAttribute);
		if (theValue.isEmpty()) return new ValidationState(aAttribute);
		CMV theDown = DownResCMVHolderStorage.getInstance().getHolderFor(aAttribute).getUpdater(aLOM).getDiffusionWithoutOriginal();
		CMV theUp = UpResCMVHolderStorage.getInstance().getHolderFor(aAttribute).getUpdater(aLOM).getDiffusionWithoutOriginal();
		if (theDown !=null|| theUp !=null) {
			ResCMV  theDownResCMV = null, theUpResCMV=null;
			if (theDown!=null) theDownResCMV=(ResCMV) theDown;
			if (theUp!=null) theUpResCMV=(ResCMV) theUp;
			List<ResCMV> theUnCompliedResCMVs=new ArrayList<ResCMV>();
			if (theDownResCMV!=null && !theDownResCMV.comply(theValue))
					theUnCompliedResCMVs.add(theDownResCMV);
			if (theUpResCMV!=null && !theUpResCMV.comply(theValue))
				theUnCompliedResCMVs.add(theUpResCMV);
			if (theUnCompliedResCMVs.size()!=0)
				return new ValidationState(aAttribute,theUnCompliedResCMVs);
		}
		return new ValidationState(aAttribute,Valid); 
	}
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public static ValidationStats getStatsFor(LOM aLOM, Collection<LOMAttribute> theAttributeList) {
		if (theAttributeList==null) theAttributeList = LOMAttribute.getRegisteredAttribute();
		ValidationStats theStats = new ValidationStats();
		for( LOMAttribute theAttribute: theAttributeList) {
			theStats.addValidationState(getValidationState(aLOM,theAttribute));
		}
		return theStats;
	}
	
	
}
