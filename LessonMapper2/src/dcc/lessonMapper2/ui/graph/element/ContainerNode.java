/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.graph.element;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import util.ui.ShadowMaker;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;
import edu.umd.cs.piccolox.handles.PBoundsHandle;
import edu.umd.cs.piccolox.util.PBoundsLocator;

/**
 * The Class ContainerNode.
 */
public class ContainerNode extends PLayer implements PropertyChangeListener,
		PInputEventListener, SelectionableNode, HelperSupport {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	// private Rectangle itsBorder;
	/** The its stroke size. */
	public int itsStrokeSize = 3;

	/** The ITS minimum bounds. */
	public static Rectangle2D ITSMinimumBounds = new Rectangle(0, 0, 70, 70);

	/** The ITS poly inset. */
	public  int ITSPolyInset = 1;

	public  int ITSInsets = 2;
	
	/** The ITS color. */
	public static Color ITSColor = LMUI.ITSBlueColor;

	/** The is active. */
	public boolean isActive;

	/** The its always pickable nodes. */
	Set<PNode> itsAlwaysPickableNodes = new HashSet<PNode>();


	/**
	 * The Constructor.
	 */
	public ContainerNode() {
		super();
		setBounds(ITSMinimumBounds);
		setPaint(LMUI.ITSSandColor);
		// itsBorder = new Rectangle(getFullBounds().getBounds());
		 //setPaint(Color.BLACK);
		// addBoundsHandles();
		setActive(false);
		addInputEventListener(this);
		
	}

	/**
	 * extends addChild so that new children are scaled with
	 * LessonMapper2.ITSLevelZoomFActor
	 * 
	 * @param aChild
	 *            the a child
	 * @param aIndex
	 *            the a index
	 */
	public void addChild(int aIndex, PNode aChild) {
		aChild.setGlobalScale(LessonMapper2.ITSlevelZoomFactor);
		super.addChild(aIndex, aChild);
	}

	/**
	 * this method is overriden in order to assume the fact that a border
	 * external to.
	 * 
	 * @param localBounds
	 *            the local bounds
	 * @param childOrThis
	 *            the child or this
	 */
	public void repaintFrom(PBounds localBounds, PNode childOrThis) {
		//localBounds.inset(-2 * itsStrokeSize, -2 * itsStrokeSize);
		super.repaintFrom(localBounds, childOrThis);
	}

	/**
	 * add child nodes without scaling.
	 * 
	 * @param aChild
	 *            the a child
	 */
	public void addChildWithoutScaling(PNode aChild) {
		int insertIndex = getChildrenCount();
		super.addChild(insertIndex, aChild);
	}

	/**
	 * customized version that overpass the scaling of children.
	 */
	public void addBoundsHandles() {
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createEastLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createWestLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createNorthLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createSouthLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createNorthEastLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createNorthWestLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createSouthEastLocator(this)));
		addChildWithoutScaling(new PBoundsHandle(PBoundsLocator
				.createNorthWestLocator(this)));

	}

	/**
	 * Removes the bounds handles.
	 */
	public void removeBoundsHandles() {
		ArrayList<PBoundsHandle> handles = new ArrayList<PBoundsHandle>();

		Iterator i = getChildrenIterator();
		while (i.hasNext()) {
			PNode each = (PNode) i.next();
			if (each instanceof PBoundsHandle) {
				handles.add((PBoundsHandle)  each);
			}
		}
		removeChildren(handles);
	}

	/**
	 * attempt to make the border following nodes movments.
	 * 
	 * @param arg0
	 *            the arg0
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		// setPathTo(getFullBounds().getBounds());
	}

	/**
	 * override the original method of PNode so that nodes bounds are not
	 * included and reinitialize bounds to fullBounds.
	 * 
	 * @param dstBounds
	 *            the dst bounds
	 * 
	 * @return the p bounds
	 */
	public PBounds computeFullBounds(PBounds dstBounds) {
		PBounds result = getUnionOfChildrenBounds(dstBounds);
		PBounds theResultConsideringMinimum = new PBounds();
		Rectangle.union(result.inset(0, 0), ITSMinimumBounds, //-5
				theResultConsideringMinimum);
		setBounds(theResultConsideringMinimum);
		localToParent(theResultConsideringMinimum);
		return theResultConsideringMinimum;
	}

	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.PNode#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	public void paint(PPaintContext aPaintContext) {
		//super.paint(aPaintContext);
		
		Graphics2D theGraphics2D = aPaintContext.getGraphics();
		double theScale = aPaintContext.getScale();
		float theStrokeSize = itsStrokeSize;
		float theShadowSize = 0.8f;
		if (theScale > 5){
			theStrokeSize = theStrokeSize / 2;
			theShadowSize= theShadowSize /2;
		}
		
		else if (theScale > 2){
			theStrokeSize = theStrokeSize / 1.5f ;
			theShadowSize=theShadowSize /2;
		}
		theGraphics2D.setStroke(new BasicStroke(theStrokeSize));
		PBounds theBounds = getFullBounds();
		parentToLocal(theBounds);
	//	double theInterBorderSpace = 0.5+theStrokeSize;
		
	/*	if (theScale <= -1) {
			theBounds.width = theBounds.width
					- (2 * itsStrokeSize + 2 * ITSPolyInset);
			theBounds.height = theBounds.height
					- (2 * itsStrokeSize + 2 * ITSPolyInset);
		}*/
		
		theBounds.inset(-ITSInsets, -ITSInsets);
		RoundRectangle2D.Double theShape = new RoundRectangle2D.Double(theBounds.x,theBounds.y,theBounds.width,theBounds.height,10,10);
		ShadowMaker.paintShadow(theGraphics2D, theShape, theShadowSize, theStrokeSize, ITSColor, LMUI.ITSSandColor);
		theGraphics2D.setPaint(getPaint());
		theGraphics2D.fill(theShape);
		theGraphics2D.setStroke(new BasicStroke(theStrokeSize));
		theGraphics2D.setPaint(ITSColor);
		theGraphics2D.draw(theShape);
		
		//theGraphics2D.draw(new RoundRectangle2D.Double(theBounds.x+theInterBorderSpace,theBounds.y+theInterBorderSpace,theBounds.width-2*theInterBorderSpace,theBounds.height-2*theInterBorderSpace,10,10));
		/*if (theScale <= 5) {
			theGraphics2D.setStroke(new BasicStroke(theStrokeSize/2,
					BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			int[] thePoly1X = { (int) (theBounds.width + theBounds.x),
					(int) (theBounds.width + theBounds.x + ITSPolyInset),
					(int) (theBounds.width + theBounds.x + ITSPolyInset),
					(int) (theBounds.x + ITSPolyInset),
					(int) (theBounds.x + ITSPolyInset) };
			int[] thePoly1Y = { (int) (theBounds.y + ITSPolyInset),
					(int) (theBounds.y + ITSPolyInset),
					(int) (theBounds.y + theBounds.height + ITSPolyInset),
					(int) (theBounds.y + theBounds.height + ITSPolyInset),
					(int) (theBounds.y + theBounds.height) };
			theGraphics2D.drawPolyline(thePoly1X, thePoly1Y, 5);
			int[] thePoly2X = { thePoly1X[1], thePoly1X[1] + ITSPolyInset,
					thePoly1X[1] + ITSPolyInset, thePoly1X[4] + ITSPolyInset,
					thePoly1X[4] + ITSPolyInset };
			int[] thePoly2Y = { thePoly1Y[1] + ITSPolyInset,
					thePoly1Y[1] + ITSPolyInset, thePoly1Y[3] + ITSPolyInset,
					thePoly1Y[3] + ITSPolyInset, thePoly1Y[3] };
			theGraphics2D.drawPolyline(thePoly2X, thePoly2Y, 5);
			int[] thePoly3X = { thePoly2X[1], thePoly2X[1] + ITSPolyInset,
					thePoly2X[1] + ITSPolyInset, thePoly2X[4] + ITSPolyInset,
					thePoly2X[4] + ITSPolyInset };
			int[] thePoly3Y = { thePoly2Y[1] + ITSPolyInset,
					thePoly2Y[1] + ITSPolyInset, thePoly2Y[3] + ITSPolyInset,
					thePoly2Y[3] + ITSPolyInset, thePoly2Y[3] };
			theGraphics2D.drawPolyline(thePoly3X, thePoly3Y, 5);
		}*/
		
	}

	/**
	 * set if this ContainerNode isd the current ActiveLayer or not.
	 * 
	 * @param aBool
	 *            the active
	 */
	public void setActive(boolean aBool) {
		isActive = aBool;
		if (isActive) {
			setPickable(false);
			setChildrenPickable(true);
			setPaint(LMUI.ITSActiveColor);
		} else {
			setPickable(true);
			setChildrenPickable(false);
			setPaint(LMUI.ITSUNActiveColor);
		}
	}

	/**
	 * Checks if is active.
	 * 
	 * @return true, if is active
	 */
	public boolean isActive() {
		return isActive;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent,
	 *      int)
	 */
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent, int)
	 */
	public void processEvent(PInputEvent aEvent, int aType) {
		//System.out.println(aEvent+" enter layer ");
		if (!isActive) {
			if (aType == MouseEvent.MOUSE_CLICKED &&
					aEvent.getButton() == MouseEvent.BUTTON1 &&
					aEvent.getClickCount() == 2 && !aEvent.isHandled()) {
				//System.out.println(aEvent+" do layer ");
				LessonMapper2.getInstance().enterLayer(this);
			}
		}
	}

	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.graph.element.SelectionableNode#detach()
	 */
	public void detach() {
		removeFromParent();
	}

	
	/**
	 * add a new node to the always pickable children.
	 * 
	 * @param aNode
	 *            the a node
	 */
	public void addAlwaysPickableNode(PNode aNode) {
		itsAlwaysPickableNodes.add(aNode);
	}
	
	/**
	 * remove a node to the always pickable children.
	 * 
	 * @param aNode
	 *            the a node
	 */
	public void removeAlwaysPickableNode(PNode aNode) {
		itsAlwaysPickableNodes.remove(aNode);
	}
	
	/**
	 * this method is overidden so that children registered as alwaysPickable
	 * are picked even if getChildrePickable is false.
	 * 
	 * @param aPickPath
	 *            the a pick path
	 * 
	 * @return true, if pick
	 */
	@Override
	protected boolean pick(PPickPath aPickPath) {
		boolean isPicked = false;
		if (!getChildrenPickable()) {
			for (PNode theNode : itsAlwaysPickableNodes) {
				if (getChildrenReference().contains(theNode))
					isPicked |= theNode.fullPick(aPickPath);
			}
		}
		return isPicked;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
	 */
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
	 */
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("containerNode");
	}
	
	public PNode getPNode() {
		return this;
	}
}
