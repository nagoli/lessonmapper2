package dcc.lessonMapper2.ui.graph.element.lom;

/*
 * LessonMapper 2.
Copyright (C) Olivier Motelet.

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
import java.util.Hashtable;

import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * this aspect is responsible for decorating the SeelctionableLOM with some Icon corresponding to their LOM
 */

public  aspect LOMIconUIPlug {

		
		pointcut thePaint(PPaintContext aPaintContext): 
		execution( void SelectionableLOM.paintAfterChildren(PPaintContext)) && args(aPaintContext);

		/*pointcut theRepaintFrom(PBounds aPBounds): 
		execution(public void SelectionableLOM.repaintFrom(PBounds, ..)) && args(aPBounds,..);
*/
		static Hashtable<SelectionableLOM, LOMIconUI> ITSSelectionableLOMToIconTable = new Hashtable<SelectionableLOM, LOMIconUI>();
		
		
		/**
		 * when a new lom is updated
		 * the icon are also updated
		 */
		 after(): execution(void SelectionableLOM.updateView()){
			SelectionableLOM theSelectionableLOM = (SelectionableLOM) thisJoinPoint.getTarget();
			if (!ITSSelectionableLOMToIconTable.contains(theSelectionableLOM))
				ITSSelectionableLOMToIconTable.put(theSelectionableLOM,new LOMIconUI(theSelectionableLOM));
			ITSSelectionableLOMToIconTable.get(theSelectionableLOM).update();
		}
		
		/**
		 * add the icons to the selected nodes
		 */
		after(PPaintContext aPaintContext): thePaint(aPaintContext) {
			SelectionableLOM theSelectionableLOM = (SelectionableLOM) thisJoinPoint.getTarget();
			ITSSelectionableLOMToIconTable.get(theSelectionableLOM).paint(aPaintContext);
		}

		/**
		 * grow the repaint area of the selected nodes in order to enable icon
		 *  painting
		 */
	/*	before(PBounds aPBounds): theRepaintFrom(aPBounds){
			aPBounds.height = aPBounds.height + LOMIconUI.ITSDefaultIconHeight;
		}
*/
	
	
	
	
	
}
