/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.lom;


import java.awt.Rectangle;

import lessonMapper.lom.LOM;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import edu.umd.cs.piccolo.nodes.PImage;

/**
 * The Class LOMChangeNotifierUI.
 */
public class LOMChangeNotifierUI extends PImage implements HelperSupport{
	
	/** The ITS icon. */
	public static String ITSIcon = "resources/applyIcon.gif" ;
	
	/** The its LOM. */
	protected LOM itsLOM;
	
	/** The its image bounds. */
	protected Rectangle itsImageBounds;
	
	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 */
	public LOMChangeNotifierUI(LOM aLOM){
		super(LOMChangeNotifierUI.class.getResource(ITSIcon));
		itsLOM = aLOM;
		//setOffset(-5,-5);
		//setPickable(false);
	}
	
	
	
	/**
	 * Gets the LOM.
	 * 
	 * @return the LOM
	 */
	public LOM getLOM(){
		return itsLOM;
	}
	
	
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
	 */
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("saveIcon");
	}

	
	
}
