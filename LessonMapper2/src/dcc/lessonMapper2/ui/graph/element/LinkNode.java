/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.graph.element;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: omotelet Date: Jul 21, 2005 Time: 4:48:41 PM.
 */
public class LinkNode extends PPath implements SelectionableNode,
		HelperSupport, PInputEventListener{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/**
	 * The Constructor.
	 */
	public LinkNode() {
		super();
		//setPathToEllipse(0, 0, 30, 30);
		//setStroke(new BasicStroke(2));
		//setPaint(LMUI.ITSDarkSandColor);
		setPaint(LMUI.ITSUNActiveColor);
		setPickable(true);
		addInputEventListener(this);
		
	}

	
	/**
	 * override for enabling aspect access.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	public void paint(PPaintContext aPaintContext) {
		super.paint(aPaintContext);
	}

	/**
	 * override for enabling aspect access.
	 * 
	 * @param localBounds
	 *            the local bounds
	 * @param childOrThis
	 *            the child or this
	 */
	public void repaintFrom(PBounds localBounds, PNode childOrThis) {
		super.repaintFrom(localBounds, childOrThis);
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
	       return LessonMapper2.getInstance().getLangComment("linkNode");
	       
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent,
	 *      int)
	 */
	/** The is zoom. */
	boolean isZoom = false;
	
	/** The transfom rate. */
	float theTransfomRate=2;
	
	/* (non-Javadoc)
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent, int)
	 */
	public void processEvent(PInputEvent aEvent, int aType) {
		/*if (aType == MouseEvent.MOUSE_CLICKED && aEvent.getClickCount() == 2) {
			if (!isZoom) {
				
				setScale(getScale() * theTransfomRate);
				isZoom = true;
			} else {
				setScale(getScale() / theTransfomRate);
				isZoom = false;
			}
		}*/
	}

	/**
	 *  remove from its parent activity relation removal is managed by GenericActivity.removeChild()
	 */
	public void detach() {
		removeFromParent();
	}
	
	
	public PNode getPNode() {
		return this;
	}
	
}
