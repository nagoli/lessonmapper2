/*
 * Created on Aug 12, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The Class PreviewLocalizer.
 * 
 * @author omotelet
 * 
 * PreviewLocalizer draws a rectangle corresponding to the position of the
 * camera of the main canvas
 */
public class PreviewLocalizer extends PPath implements PropertyChangeListener{

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The its camera. */
    protected PCamera itsCamera;
    
    /**
	 * The Constructor.
	 * 
	 * @param aCamera
	 *            the a camera
	 */
    public PreviewLocalizer(PCamera aCamera){
        itsCamera = aCamera;
        itsCamera.addPropertyChangeListener(this);
        //setPathToEllipse(2, 5, 5, 5);
        //setPaint(new Color(96,125,191,200));
        setPaint(LMUI.ITSPurpleTransparent);
        setStroke(null);
    }

    /**
	 * Adapt the size when the camera change ...
	 * 
	 * @param aEvt
	 *            the a evt
	 */
    public void propertyChange(PropertyChangeEvent aEvt) {
        PBounds theViewBounds = itsCamera.getViewBounds();
		setPathToEllipse((float)theViewBounds.x,(float)theViewBounds.y,(float)theViewBounds.width,(float)theViewBounds.height);
		
    }
    
   /* @Override
    protected void paint(PPaintContext aPaintContext) {
        PBounds theViewBounds = itsCamera.getViewBounds();
		setPathToEllipse((float)theViewBounds.x,(float)theViewBounds.y,(float)theViewBounds.width,(float)theViewBounds.height);
 
    }*/
    
    
    
}
