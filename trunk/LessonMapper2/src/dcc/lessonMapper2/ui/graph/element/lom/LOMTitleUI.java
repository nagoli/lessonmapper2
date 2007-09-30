package dcc.lessonMapper2.ui.graph.element.lom;

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;


import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.PopupLOMAttributeUI;
import dcc.lessonMapper2.ui.lom.LOMValidityUI;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * PText node for holding LOM titles
 * - delegates click events to a PopupLOMAttributeUI assigned to the title attribute
 * - change its size depending on the current scale.
 * - modified size is accessible with getScaledBounds call with a specific camera
 * 
 */
public class LOMTitleUI extends PText implements HelperSupport {

	
	
	
	/** The its camera text transform map. */
	Map<PCamera, PAffineTransform> itsCameraTextTransformMap = new HashMap<PCamera, PAffineTransform>();

	/** The its camera scale table. */
	Map<PCamera, Double> itsCameraScaleTable = new HashMap<PCamera, Double>();
	
	Map<PCamera, PBounds> itsCameraScaledBoundTable = new HashMap<PCamera, PBounds>();
	

	/** The its title editor. */
	PopupLOMAttributeUI itsTitleEditor;

	SelectionableLOM itsSelectionableLOM;
	
	boolean isValidScaleBuffer =true;
	boolean isInitialized=true;
	
	/**
	 * The Constructor.
	 * 
	 * @param aString
	 *            the a string
	 * @param aGenericActivity
	 *            TODO
	 */
	public LOMTitleUI(SelectionableLOM theSelectionableLOM, String aString) {
		super("");
		itsSelectionableLOM = theSelectionableLOM;
		setOffset(LOMValidityUI.ITSWIDTHTOTAL, 2);
		itsTitleEditor = new PopupLOMAttributeUI(LOMAttribute
				.getLOMAttribute("general/title"));
		addInputEventListener(itsTitleEditor);
		setPickable(true);
		setPaint(new Color(LMUI.ITSSandColor.getRed(), LMUI.ITSSandColor
				.getGreen(), LMUI.ITSSandColor.getBlue(), 200));
		setTextPaint(Color.black);// LMUI.ITSDarkSandColor.darker());
		setConstrainWidthToTextWidth(false);
		//setWidth(itsSelectionableLOM.get)
		//setWidth(itsSelectionableLOM.getPNode().getWidth()-LOMValidityUI.ITSWIDTHTOTAL);
		//setBounds(0, 0, 1, 1);
		 setText(aString);
		
	}

	
	
	/**
	 * 
	 * return the associated SelectionableLOM
	 */
	public SelectionableLOM getSelectionableLOM(){
		return itsSelectionableLOM;
	}
	
	
	
	/**
	 * Rewrite the full pick method to take into account the 
	 * scaled bounds instead of the fullbounds of the node
	 * 
	 * @param aPickPath
	 *            the a pick path
	 * 
	 * @return true, if full pick
	 */
	@Override
	public boolean fullPick(PPickPath pickPath) {
		/*PAffineTransform theTextTransform = itsCameraTextTransformMap
				.get(aPickPath.getTopCamera());
		aPickPath.pushTransform(theTextTransform);
		itsPickedBounds = aPickPath.getPickBounds();
		boolean theBool = super.fullPick(aPickPath);
		aPickPath.popTransform(theTextTransform);
		return theBool;*/
		if ((getPickable() /*|| getChildrenPickable()*/) && getScaledBounds(pickPath.getTopCamera()).intersects(pickPath.getPickBounds())) {
				pickPath.pushNode(this);
				
				boolean thisPickable = getPickable() && pickPath.acceptsNode(this);
					
				if (thisPickable) {
					if (pick(pickPath)) {
						//System.out.println("pick title");
						return true;
					}
				}
			

				if (thisPickable) {
					if (pickAfterChildren(pickPath)) {
						//System.out.println("pick title after children");
						return true;
					}
				}

				pickPath.popNode(this);
			}

			return false;
		}

	public boolean isTitleOfActiveLayer(){
		return (itsSelectionableLOM==LessonMapper2.getInstance().getActiveLayer());
	}

	
	
	public void setLOM(LOM aLOM) {
		itsTitleEditor.setLOM(aLOM);
	}

	public void setText(String aTitle) {
		if (aTitle == null || aTitle.equals("")  )
			aTitle = LessonMapper2.getInstance().getLangComment("notitle");
		// else
		// theTitle = Utils.insertInString(theTitle, "-\n", 14);

		// theTitle+="\n"+Validation.getStatsFor(itsLOM);
		//clear the camera bounds cache
		if (isInitialized){
			itsCameraScaledBoundTable.clear();
			isValidScaleBuffer = false;
		}
		super.setText(aTitle);
	
	}
	
	/**
	 * return the bounds as they are when the additional scale is applied
	 * add its offset  in the returned PBounds
	 * This implementation return getBounds() if the scaled bounds where not calculated
	 * If you have access to the paintcontext use getScaledBound(PPAintContext) instead
	 * 
	 * @return
	 */
	public PBounds getScaledBounds(PCamera aCamera){
		if (!itsCameraScaledBoundTable.containsKey(aCamera)) return getBounds();
		return (PBounds)itsCameraScaledBoundTable.get(aCamera).clone();
	}
	
	/**
	 * return the bounds as they are when the additional scale is applied
	 * add its offset  in the returned PBounds
	 * Calculate them if they are not available
	 * @return
	 */
	public PBounds getScaledBounds(PPaintContext aPaintContext){
		if (!itsCameraScaledBoundTable.containsKey(aPaintContext.getCamera())) updateScaleBuffer(aPaintContext);
		return (PBounds)itsCameraScaledBoundTable.get(aPaintContext.getCamera()).clone();
	}
	
	

	/**
	 * Paint.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	@Override
	protected void paint(PPaintContext aPaintContext) {
		//System.out.println("arg");
		double theScale = aPaintContext.getScale();
		double theLimitScale = 3;
		PCamera theCurrentCamera = aPaintContext.getCamera();
		if (theScale<theLimitScale && isConstrainWidthToTextWidth() && theCurrentCamera==LessonMapper2.getInstance().getMainCanvas().getCamera()) {
			setConstrainWidthToTextWidth(false); 
			setWidth(itsSelectionableLOM.getPNode().getWidth());
		}
		if (theScale>theLimitScale  && !isConstrainWidthToTextWidth()&& theCurrentCamera==LessonMapper2.getInstance().getMainCanvas().getCamera())
			{setConstrainWidthToTextWidth(true); 
			}
		if (!isValidScaleBuffer || !itsCameraScaleTable.containsKey(theCurrentCamera) 
				|| theScale != (double) itsCameraScaleTable
						.get(theCurrentCamera)) {
			updateScaleBuffer(aPaintContext);
			isValidScaleBuffer=true;
		}
		PAffineTransform theTextTransform = itsCameraTextTransformMap
				.get(theCurrentCamera);
		aPaintContext.pushTransform(theTextTransform);
		
		super.paint(aPaintContext);
		//aPaintContext.getGraphics().draw()
		aPaintContext.popTransform(theTextTransform);
		/*aPaintContext.getGraphics().setPaint(Color.black);
		System.out.println(getScaledBounds(aPaintContext));
		aPaintContext.getGraphics().draw(getScaledBounds(aPaintContext).inset(-10, -10));*/
		// aPaintContext.getGraphics().draw(getBounds());
	}



	protected void updateScaleBuffer(PPaintContext aPaintContext) {
		double theScale = aPaintContext.getScale();
		PCamera theCurrentCamera = aPaintContext.getCamera();
		boolean mustBeScaled = theScale > 1;
		PAffineTransform theTransform = new PAffineTransform();
		if (mustBeScaled) {
			double theAdditionalScale = Math.pow(1 - 0.15, theScale);
			double theAdditionalOffset =1;
			theTransform.setScale(theAdditionalScale);
			theTransform.setOffset(0,theAdditionalOffset);
		} else {
			double theAdditionalOffset =0.5;
			theTransform.setOffset(theAdditionalOffset, theAdditionalOffset);
		}
		PBounds theScaledBounds= new PBounds();
		theTransform.transform(getBounds(),theScaledBounds);
		theScaledBounds.x+=getOffset().getX();
		theScaledBounds.y+=getOffset().getY();
		itsCameraScaledBoundTable.put(theCurrentCamera,theScaledBounds);
		itsCameraScaleTable.put(theCurrentCamera, theScale);
		itsCameraTextTransformMap.put(theCurrentCamera, theTransform);
	}

	/**
	 * Gets the help.
	 * 
	 * @return the help
	 */
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("titleNode");
	}

}