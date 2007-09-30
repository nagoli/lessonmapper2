/*
 * Created on Aug 8, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

import java.awt.Dimension;
import java.awt.event.InputEvent;
import java.awt.geom.Rectangle2D;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;

/**
 * The Class ZoomTemporaryToPickedNode.
 * 
 * @author omotelet
 */
public class ZoomTemporaryToPickedNode extends PBasicInputEventHandler {

	/** The ITS instance. */
	public static ZoomTemporaryToPickedNode  ITSInstance = new ZoomTemporaryToPickedNode();
	
	/** The its previous bounds. */
	public PBounds itsPreviousBounds;
	
    /**
	 * The Constructor.
	 */
    public ZoomTemporaryToPickedNode() {
        setEventFilter(new PInputEventFilter(InputEvent.BUTTON2_MASK));
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd.cs.piccolo.event.PInputEvent)
     */
    public void mousePressed(PInputEvent aEvent) {
       PCamera theCamera = aEvent.getCamera();
       itsPreviousBounds = theCamera.getViewBounds();
       PBounds zoomToBounds;
       PNode thePickedNode = aEvent.getPickedNode();
      // only zoom on SelectionableNode
       while (!(thePickedNode instanceof SelectionableNode)) {
    	   thePickedNode = aEvent.getPath().nextPickedNode();
    	   if ( thePickedNode == null) return;
       }
       Dimension theViewInset = (Dimension) LessonMapper2.ITSViewInsets
               .clone();
       double theLayerScale = thePickedNode.getGlobalScale();
       zoomToBounds = thePickedNode.getGlobalFullBounds().inset(
               theViewInset.getWidth() * theLayerScale,
               theViewInset.getHeight() * theLayerScale);
       theCamera.animateViewToCenterBounds(theCamera.globalToLocal(zoomToBounds),
               true, 500);
       //write the lom id to console
       System.out.println("zoomed node: "+ ((thePickedNode instanceof SelectionableLOM)?((SelectionableLOM)thePickedNode).getLOM().getID():" is not LOM"));
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mouseReleased(edu.umd.cs.piccolo.event.PInputEvent)
     */
    @Override
    public void mouseReleased(PInputEvent aEvent) {
    	PCamera theCamera = aEvent.getCamera();
    	 theCamera.animateViewToCenterBounds(theCamera.globalToLocal(itsPreviousBounds),
                 true, 500);
    }
    
    /**
	 * make the camera aCamera zoom to the current active layer.
	 * 
	 * @param aCamera
	 *            the a camera
	 */
    public static void zoomtoActiveFor(PCamera aCamera) {
        PBounds zoomToBounds;
        PLayer theActiveLayer = LessonMapper2.getInstance().getActiveLayer();
        Dimension theViewInset = (Dimension) LessonMapper2.ITSViewInsets
                .clone();
        double theLayerScale = theActiveLayer.getGlobalScale();
        zoomToBounds = theActiveLayer.getGlobalFullBounds().inset(
                theViewInset.getWidth() * theLayerScale,
                theViewInset.getHeight() * theLayerScale);
        aCamera.animateViewToCenterBounds(aCamera.globalToLocal(zoomToBounds),
                true, 500);
    }

    /**
	 * make the camera aCamera zoom to the parentLayer of the current active
	 * layer Display the current active layer in case there is no parent.
	 * 
	 * @param aCamera
	 *            the a camera
	 */
    public static void zoomtoParentActiveFor(PCamera aCamera) {
        PBounds zoomToBounds;
        PLayer theParentActiveLayer = null;
        
        if (!LessonMapper2.getInstance().getPreviousActiveLayers().isEmpty())
            theParentActiveLayer = (PLayer) LessonMapper2.getInstance()
                    .getPreviousActiveLayers().peek();
        else
            theParentActiveLayer = LessonMapper2.getInstance().getActiveLayer();
        
        Dimension theViewInset = (Dimension) LessonMapper2.ITSViewInsets
                .clone();
        double theLayerScale = theParentActiveLayer.getGlobalScale();
        zoomToBounds = theParentActiveLayer.getGlobalFullBounds().inset(
                theViewInset.getWidth() * theLayerScale,
                theViewInset.getHeight() * theLayerScale);
        aCamera.animateViewToCenterBounds(aCamera.globalToLocal(zoomToBounds),
                true, 500);

    }
    
    /**
	 * make the camera aCamera zoom of the porcentage aRate sacaled by the
	 * current ActiveLayer scaleRate aRate value is In ]0,1[ for zoom out , and
	 * >1 for zoom in negative rate make zoom out.
	 * 
	 * @param aRate
	 *            the a rate
	 * @param aCamera
	 *            the a camera
	 */
    public static void zoom(PCamera aCamera, double aRate) {
    	PAffineTransform newTransform = aCamera.getViewTransform()	;
    	Rectangle2D theViewBounds = aCamera.getViewBounds();
    	newTransform.scaleAboutPoint(aRate, theViewBounds.getCenterX(), theViewBounds.getCenterY());
		 aCamera.animateViewToTransform(newTransform, 200);
    }
    
    
    
    
}
