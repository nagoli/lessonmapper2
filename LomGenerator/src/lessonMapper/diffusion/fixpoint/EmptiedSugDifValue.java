/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;

/**
 * sug value that does not return any result when lomvalue is asked.
 * 
 * @author omotelet
 */


public class EmptiedSugDifValue extends SugDifValue {

	
	/**
	 * 
	 * 
	 * @param aLOM 
	 * @param aAttribute 
	 */
	public EmptiedSugDifValue(LOMAttribute aAttribute, LOM aLOM) {
		super(aAttribute, aLOM);
	}

	/**
	 * do not return lom value.
	 * 
	 * @return 
	 */
	@Override
	public LOMValue getLOMValue() {
		return null;
	}
	
	
	
}
