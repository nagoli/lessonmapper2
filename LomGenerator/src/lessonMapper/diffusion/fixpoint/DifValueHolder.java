/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package lessonMapper.diffusion.fixpoint;

import java.util.Collection;

import lessonMapper.lom.LOM;


/**
 * abstract class for Diffusion Value Holder
 * a difusion value holder has a method getValue.
 * 
 * @author omotelet
 */

public abstract class DifValueHolder {

	/**
	 * return the value associated with aLOM and aAttribute
	 * return a new value for aLOM and aAttribute if not exisiting.
	 * 
	 * @param aLOM 
	 * 
	 * @return 
	 */
	public abstract DifValue getValue(LOM aLOM);

	
	/**
	 * 
	 */
	public abstract void reset();


	/**
	 * 
	 * 
	 * @param aListOfChangedLOMs 
	 */
	public abstract void initUpdate(Collection<LOM> aListOfChangedLOMs) ;
	
	/**
	 * 
	 */
	public abstract void endUpdate();
	
	/**
	 * 
	 * 
	 * @return 
	 */
	public abstract int size();
}
