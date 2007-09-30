/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.graph.element;


import java.awt.Color;
import java.awt.Stroke;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * User: omotelet Date: Jul 21, 2005 Time: 5:03:08 PM.
 */
public class MotionNode extends PText implements SelectionableNode, HelperSupport{

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The Constant ITSDefaultColor. */
	public final static Color ITSDefaultColor = Color.BLACK;
	
	/** The Constant ITSDefaultString. */
	public final static String ITSDefaultString = "NotDefined";
	
	
	/** The its edge. */
	protected TypedEdge itsEdge; 
	
    /**
	 * The Constructor.
	 * 
	 * @param aEdge
	 *            the a edge
	 */
    public MotionNode(TypedEdge aEdge) {
       this();
        setTypedEdge(aEdge);
   }
    
    /**
	 * The Constructor.
	 */
    public MotionNode() {
        super();
       // setBounds(0,0,20,20);
        setPaint(LMUI.ITSSandColor);
        setText(ITSDefaultString);
        setColor(ITSDefaultColor);
        setPickable(true);
        setScale(0.4);
    }

    /**
	 * Sets the typed edge.
	 * 
	 * @param aEdge
	 *            the typed edge
	 */
    public void setTypedEdge(TypedEdge aEdge){
    	itsEdge = aEdge;
    	
    }
    
    /**
	 * Sets the color.
	 * 
	 * @param aColor
	 *            the color
	 */
    public void setColor(Color aColor){
    	setTextPaint(aColor);
    	if (itsEdge!=null) itsEdge.setStrokePaint(aColor);
    }
    
    
    /** The its selection border. */
    protected Stroke itsSelectionBorder;
	
	/**
	 * Sets the selection border.
	 * 
	 * @param aSelectionBorder
	 *            the selection border
	 */
	public void setSelectionBorder(Stroke aSelectionBorder) {
		itsSelectionBorder =  aSelectionBorder;
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
	
	/* (non-Javadoc)
	 * @see dcc.lessonMapper2.ui.graph.element.SelectionableNode#detach()
	 */
	public void detach() {
		itsEdge.detach();
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
