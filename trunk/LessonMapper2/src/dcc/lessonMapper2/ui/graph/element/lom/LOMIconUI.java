package dcc.lessonMapper2.ui.graph.element.lom;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.geom.AffineTransform;
import java.util.Hashtable;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.eventHandler.SelectionBorderManager;

import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMVocabularyElement;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;



/**
 * paint icons on the nodes that are selected
 * change the scope of this action to the selected objects
 * For ex: repaint on title ui if it is the selectionable lom
 * @author omotelet
 *
 */
public class LOMIconUI implements PInputEventListener{

	/** The ITS default icon height. */
	public  int ITSDefaultIconHeight = 20;

	/** The ITS default icon width. */
	public  int ITSDefaultIconWidth = 25;
	
	static String[] ITSAttributesToDisplay = new String[]{"educational/difficulty","educational/semanticdensity","educational/intendedenduserrole","educational/interactivitytype"};
	static Hashtable<Image,Double> ITSImageToScaleTable = new Hashtable<Image, Double>();
	
	SelectionableLOM itsSelectionableLOM;
	
	Hashtable<String,Image> itsAttributeToImageTable = new Hashtable<String, Image>(ITSAttributesToDisplay.length);
	
	boolean isZoomed = false;
	double itsZoomFactor = 2;
	double itsLeftStartPoint = -5;
	public LOMIconUI(SelectionableLOM aSelectionableLOM){
		itsSelectionableLOM = aSelectionableLOM;
		itsSelectionableLOM.getPNode().addInputEventListener(this);
		itsLeftStartPoint = (itsSelectionableLOM.getPNode().getWidth() - ITSDefaultIconWidth * ITSAttributesToDisplay.length)/2;

	}
	
	
	
	
	public void paint(PPaintContext aPaintContext){
		Graphics2D theGraphics2D = aPaintContext.getGraphics();
		//aPaintContext.
		double theZoomFactor =1;
		boolean isPaintedOutside = aPaintContext.getScale()<2;
		if (isZoomed&&aPaintContext.getScale()<2) theZoomFactor *= itsZoomFactor;
			
		if (aPaintContext.getScale()*theZoomFactor>1 &&
				(SelectionBorderManager.getInstance().isSelected(itsSelectionableLOM)) && (itsSelectionableLOM!=LessonMapper2.getInstance().getActiveLayer())){
			PBounds theNodeBounds  = itsSelectionableLOM.getPNode().getBounds();
			double theShift = itsLeftStartPoint;
			for (String theAttribute : ITSAttributesToDisplay) {
				Image theImage = itsAttributeToImageTable.get(theAttribute);
				if (theImage!=null) {
					AffineTransform theTransform = new AffineTransform();
					theTransform.translate(theShift*theZoomFactor, theNodeBounds.height-(isPaintedOutside?1:1)*ITSDefaultIconHeight/1.5);
					if (!ITSImageToScaleTable.contains(theImage)){ 
						double theWidth = (double)ITSDefaultIconWidth / (double) theImage.getWidth(null) ;
						double theHeight = (double)ITSDefaultIconHeight / (double) theImage.getHeight(null)  ;
						double theRatio = Math.min(theWidth, theHeight);
						ITSImageToScaleTable.put(theImage, theRatio);
					}
					double theScale = ITSImageToScaleTable.get(theImage)*theZoomFactor;
					//System.out.println(theScale);
					theTransform.scale(theScale,theScale);
					theGraphics2D.drawImage(theImage, theTransform,null);
				}
				theShift += ITSDefaultIconWidth;
			}
		}
	}
	
	
	/**
	 * update the attribute images
	 */
	public void update(){
		for (String theAttribute : ITSAttributesToDisplay) {
			Image theImage = getImage(theAttribute);
			if (theImage!= null) itsAttributeToImageTable.put(theAttribute, theImage);
		}
		repaint();
	}
	
	public void repaint(){
		double theZoomFactor = isZoomed?itsZoomFactor:1;
		PNode theNode = itsSelectionableLOM.getPNode();
		PBounds theBounds = new PBounds(itsLeftStartPoint,
				theNode.getBounds().height-ITSDefaultIconHeight/1.5,
				ITSAttributesToDisplay.length*ITSDefaultIconWidth*theZoomFactor,
				ITSDefaultIconHeight*theZoomFactor);
		if (SelectionBorderManager.getInstance().isSelected(itsSelectionableLOM))
			itsSelectionableLOM.repaintFrom(
				theBounds,
				theNode);
	}
	
	
	public Image getImage(String anAttributeName) {
		LOMAttribute theAttribute = LOMAttribute
				.getLOMAttribute(anAttributeName);
		LOMVocabularyElement theValue = theAttribute.getLOMVocabulary()
				.getVocabularyElement(
						theAttribute.getValueIn(itsSelectionableLOM.getLOM()).getValue());
		return theValue.getCompatibleImage();
	}



	public void processEvent(PInputEvent aEvent, int aType) {
		if (aEvent.isLeftMouseButton()){
			if (aType == MouseEvent.MOUSE_PRESSED){
				isZoomed = true;
				itsSelectionableLOM.getPNode().repaint();
			}
			if (aType == MouseEvent.MOUSE_DRAGGED){
				isZoomed = false;
				itsSelectionableLOM.getPNode().repaint();
			}
			if (aType == MouseEvent.MOUSE_RELEASED){
				isZoomed = false;
				itsSelectionableLOM.getPNode().repaint();
			}
		}
		
	}
	

	
}
