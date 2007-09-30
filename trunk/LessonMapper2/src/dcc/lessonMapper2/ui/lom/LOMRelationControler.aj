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
package dcc.lessonMapper2.ui.lom;

import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import lessonMapper.lom.LOMRelation;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.lom.LOMRelationTitleUI;
import dcc.lessonMapper2.ui.graph.element.lom.LOMRelationUI;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * this aspect is in charge of linking the typedEdges with a LOMRelation. When a
 * change is done on a LOM Relation, the change is diffused to the edge. A
 * relationtype chooser is accessible via double click on the motionNode
 */
public aspect LOMRelationControler implements PInputEventListener {

	Map<LOMRelation, Vector<LOMRelationUI>> ITSRelationToUIMap = new HashMap<LOMRelation, Vector<LOMRelationUI>>();

	/**
	 * maintain the RelationToUI map up-to-date
	 */
	before(LOMRelation newVal): set(LOMRelation LOMRelationUI.itsLOMRelation) && args(newVal){
		LOMRelationUI theRelationUI = (LOMRelationUI) thisJoinPoint.getTarget();
		LOMRelation oldVal = theRelationUI.getLOMRelation();
		if (oldVal != null)
			getUIFor(oldVal).remove(theRelationUI);
		getUIFor(newVal).add(theRelationUI);
	}

	public Vector<LOMRelationUI> getUIFor(LOMRelation aRelation) {
		if (!ITSRelationToUIMap.containsKey(aRelation))
			ITSRelationToUIMap.put(aRelation, new Vector<LOMRelationUI>());
		return ITSRelationToUIMap.get(aRelation);
	}

	/**
	 * aggregate a new LOMrelationChooser when doubleclicking on
	 * LOMRelationTitleUI
	 */
	after(): initialization(LOMRelationTitleUI+.new(..)){
		LOMRelationTitleUI theTitleUI = (LOMRelationTitleUI) thisJoinPoint
				.getTarget();
		theTitleUI.addInputEventListener(this);
	}

	/**
	 * update UIs when a change occured in the relationType
	 */
	after(): call(void LOMRelation.setLOMRelationType(..)) {
	    LOMRelation theRelation = (LOMRelation)thisJoinPoint.getTarget();
	    Vector<LOMRelationUI> theUIs = ITSRelationToUIMap.get(theRelation);
	    if (theUIs!=null){
	    	for (LOMRelationUI theUI : theUIs) {
	    		theUI.updateUI();
	    	}
	    }
	}

	public void processEvent(PInputEvent aEvent, int type) {
		if (type == MouseEvent.MOUSE_CLICKED && aEvent.getClickCount() == 2) {
			PNode theNode = aEvent.getPickedNode();
			if (theNode instanceof LOMRelationTitleUI) {
				LOMRelationTitleUI theTitleUI = (LOMRelationTitleUI) theNode;
				PCanvas theCanvas = LessonMapper2.getInstance().getMainCanvas();
				PCamera theTopCamera = theCanvas.getCamera();
				Point2D theNewLocation = aEvent
						.getPositionRelativeTo(theTopCamera);
				LOMRelationChooser theChooser = new LOMRelationChooser(
						theTitleUI.getRelationUI().getLOMRelation());
				theChooser.show(theCanvas, (int) theNewLocation.getX(),
						(int) theNewLocation.getY());
			}
			aEvent.setHandled(true);
		}
	}

}
