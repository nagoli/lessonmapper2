package dcc.lessonMapper2.ui.eventHandler;

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
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.RoundRectangle2D;

import lessonMapper.lom.LOM;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.action.InsertEdgeMode;
import dcc.lessonMapper2.ui.action.SelectionMode;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolox.event.PNotificationCenter;

public aspect SelectionBorderManagerPlug {

		static SelectionHandler itsHandler;
		
		pointcut theSelectionHandlerCreation():
			initialization(public SelectionHandler.new(..));

		pointcut thePaint(PPaintContext aPaintContext): 
		execution( void SelectionableNode.paint(PPaintContext)) && args(aPaintContext);

		pointcut theRepaintFrom(PBounds aPBounds): 
		execution(public void SelectionableNode.repaintFrom(PBounds, ..)) && args(aPBounds,..);

		/**
		 * declare theSelectionBorderManager as a listener for the new created
		 * selectionHandler
		 */
		after(): theSelectionHandlerCreation(){
			itsHandler = (SelectionHandler) (thisJoinPoint
					.getTarget());
			PNotificationCenter.defaultCenter()
					.addListener(SelectionBorderManager.ITSInstance, "selectionChanged",
							SelectionHandler.SELECTION_CHANGED_NOTIFICATION,
							itsHandler);
		}

		
		/**
		 * when a new lom is given to a selected node
		 * the node is unselected and selected  again after application of the new lom  
		 */
		void around(LOM aLOM): execution(void SelectionableLOM.setLOM(LOM)) && args(aLOM){
			PNode theNode = (PNode) thisJoinPoint.getTarget();
			if (isSelected(theNode)){
				itsHandler.unselect(theNode);
				proceed(aLOM);
				itsHandler.select(theNode);
			}
			else {
				proceed(aLOM);
			}
		}
		
		/**
		 * add the dash border to the selected nodes
		 * but not the active layer
		 * change title color
		 */
		after(PPaintContext aPaintContext): thePaint(aPaintContext) {
			PNode theNode = (PNode) thisJoinPoint.getTarget();
			if (aPaintContext.getCamera()==LessonMapper2.getInstance().getMainCanvas().getCamera() && isSelected(theNode)){
				// System.out.println(theNode);
			
				//aPaintContext.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
				Graphics2D theGraphics2D = aPaintContext.getGraphics();
				double theScale = aPaintContext.getScale();
				float theStrokeSize = SelectionBorderManager.itsStrokeSize;
				float theDivFactor=1f;				
				if (theScale > 5) 
					theDivFactor =6; 
				else if (theScale > 2) 
					theDivFactor =2;
				
				theGraphics2D.setStroke(new BasicStroke(theStrokeSize/theDivFactor,
						BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND, 1f,
						new float[] { 5/theDivFactor, 3/theDivFactor }, 0));
				if (theNode instanceof SelectionableLOM) {
					SelectionableLOM theLOMNode = (SelectionableLOM) theNode;
					Color theColor = SelectionBorderManager.ITSInstance.getLOMColor(
							theLOMNode.getLOM()).getRightElement();
					theGraphics2D.setPaint(theColor);
				} else
					theGraphics2D.setPaint(Color.black);
				PBounds theBounds = theNode.getBounds();
				//if the node to decorate is the active layer we decorate itsTitleNode
				if (theNode==LessonMapper2.getInstance().getActiveLayer()) {
					//System.out.println("active layer considered for decoration");
					if (theNode instanceof GenericActivity) {
						GenericActivity theActivity = (GenericActivity) theNode;
						theBounds = theActivity.getTitleUI().getScaledBounds(aPaintContext);
					} 
				}
				theBounds.inset(-SelectionBorderManager.itsInsets/theDivFactor, -SelectionBorderManager.itsInsets/theDivFactor);
				
				//System.out.println("decorate"+theDivFactor);
				theGraphics2D.draw(new RoundRectangle2D.Double(theBounds.x, theBounds.y,theBounds.width,theBounds.height,10/theDivFactor,10/theDivFactor));
			}
			
		}

		/**
		 * grow the repaint area of the selected nodes in order to enable dash
		 * border painting
		 */
		before(PBounds aPBounds): theRepaintFrom(aPBounds){
			PNode theNode = (PNode) thisJoinPoint.getTarget();
			if (isSelected(theNode)) {
				aPBounds.inset(-2 * SelectionBorderManager.itsInsets, -2 * SelectionBorderManager.itsInsets);
			}
		}

		public boolean isSelected(PNode aPNode) {
			SelectionHandler theSelectionHandler = SelectionMode
					.getSelectionHandler();
			InsertEdgeHandler theInsertEdgeHandler = InsertEdgeMode
					.getInsertEdgeHandler();
			if (theSelectionHandler != null)
				return SelectionBorderManager.ITSInstance.isSelected(aPNode);
			else if (theInsertEdgeHandler != null)
				return theInsertEdgeHandler.isFirstNode(aPNode);
			else
				return false;
		}
	
	
	
	
	
	
}
