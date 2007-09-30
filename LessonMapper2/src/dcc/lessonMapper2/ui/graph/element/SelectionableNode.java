/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.graph.element;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The Interface SelectionableNode.
 * 
 * @author omotelet
 * 
 * This interface must be implemented by PNode suceptible to be selectioned
 * Warning: in the current implementation SelectionableNode MUST ALSO BE
 * subclasses of PNodes
 */
public interface SelectionableNode {

    /**
	 * Paint.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
    public void paint(PPaintContext aPaintContext);
    
    /**
	 * Repaint from.
	 * 
	 * @param localBounds
	 *            the local bounds
	 * @param childOrThis
	 *            the child or this
	 */
    public void repaintFrom(PBounds localBounds, PNode childOrThis);
    
    /**
	 * Detach.
	 */
    public void detach();
    
    
    public PNode getPNode();
    
    
}
