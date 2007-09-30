/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

/**
 * 
 */
public abstract class AttributeBasedDifValue extends DifValue {

	/**
	 * 
	 */
	LOMAttribute itsAttribute;
	
	/**
	 * 
	 */
	boolean isUpdatedLOMValue=false;
	
	/**
	 * 
	 */
	LOMValue itsLOMValueCache;
	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public AttributeBasedDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super(aLOM);
	    itsAttribute = aAttribute;
	}
	
	
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public LOMValue getLOMValue(){
		if (isUpdatedLOMValue) return itsLOMValueCache;
		if (itsLOM == null){
			System.out.println("Intend to manipulate LOMValue when LOM is not defined");
			return null;
		}
		itsLOMValueCache =  itsAttribute.getValueIn(itsLOM);
		isUpdatedLOMValue = true;
		return itsLOMValueCache;
	}
	
	
	
	
	
}
