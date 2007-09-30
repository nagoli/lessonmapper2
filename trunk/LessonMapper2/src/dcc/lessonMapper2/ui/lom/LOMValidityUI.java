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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.Collection;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.JComponent;
import javax.swing.JScrollPane;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.validation.CMV.Validation;
import lessonMapper.validation.CMV.ValidationState;
import lessonMapper.validation.CMV.ValidationStats;
import util.ui.StyledLabel;
import zz.utils.ui.popup.RelativePopup;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.SelectionBorderManager;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import diffuse.models.res.ResCMV;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.util.PAffineTransform;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The Class LOMValidityUI.
 */
public class LOMValidityUI extends PNode implements HelperSupport,
		PInputEventListener {

	/** The Constant ITSWIDTH. */

	public final static int ITSWIDTH = 5;

	public final static int ITSINSET = 1;
	
	public final static int ITSWIDTHTOTAL = ITSWIDTH +2*ITSINSET;
	
	/** The Constant ITSHEIGHT. */
	public final static int ITSHEIGHT = 3*ITSWIDTH+2*ITSINSET;
	
	/** The Constant ITSValidColor. */
	public final static Color ITSValidColor = Color.green;//new Color(145, 220, 125);

	/** The Constant ITSNotDefinedColor. */
	public final static Color ITSNotDefinedColor =  new Color(251, 233, 38);

	/** The Constant ITSNotValidColor. */
	public final static Color ITSNotValidColor = Color.red;

	/** The its LOM. */
	LOM itsLOM;

	/** The its selectionable LOM. */
	SelectionableLOM itsSelectionableLOM;

	/** The its popup. */
	RelativePopup itsPopup;

	/** The its stats. */
	ValidationStats itsStats;

	
	private static Collection<LOMAttribute> ITSVisibleAttributes;
	
	public static Collection<LOMAttribute> getVisibleAttributes(){
		if (ITSVisibleAttributes==null){
			ITSVisibleAttributes = new Vector<LOMAttribute>();
			ITSVisibleAttributes.addAll(LOMAttributeBaseSet.getAttributeList());
			ITSVisibleAttributes.addAll(LOMAttributeTree.getAttibuteList());
		}
		return ITSVisibleAttributes;
	}
	
	/**
	 * set to true when has focus
	 */
	boolean hasFocus=false;
	/**
	 * The Constructor.
	 * 
	 * @param aSelectionableLOM
	 *            the a selectionable LOM
	 */
	public LOMValidityUI(SelectionableLOM aSelectionableLOM) {
		super();
		
		itsSelectionableLOM = aSelectionableLOM;
		itsPopup = new RelativePopup(null, LessonMapper2.getInstance()
				.getMainCanvas(), new Point(0, 0));
		itsPopup.setAutoHide(true);
		addInputEventListener(this);
		setLOM(aSelectionableLOM.getLOM());
	}

	/**
	 * Sets the LOM.
	 * 
	 * @param aLOM
	 *            the LOM
	 */
	public void setLOM(LOM aLOM) {
		itsLOM = aLOM;
		setBounds(0,0, ITSWIDTHTOTAL, ITSHEIGHT);
		update();
	}

	
	
	/**
	 * Update.
	 */
	public void update() {
		if (itsLOM == null)
			return;
		itsStats = Validation.getStatsFor(itsLOM, getVisibleAttributes()); 
	
		itsPopup.setContent(buildPanel());
		itsPopup.getContent().setPreferredSize(new Dimension(200, 300));
	}

	/**
	 * Builds the panel.
	 * 
	 * @return the j component
	 */
	public JComponent buildPanel() {
				Box theMainBox = Box.createVerticalBox();
				theMainBox.setBackground(null);
		if (itsStats.getNotValidNb() != 0) {
			theMainBox.add(new StyledLabel("-"
					+ LessonMapper2.getInstance().getLangComment("notvalid")
					+ ":", ITSNotValidColor, Font.ITALIC + Font.BOLD));
			for (ValidationState theState : itsStats.getNotValids()) {
				theMainBox.add(new StyledLabel(theState.getAttribute()
						.getLabel()
						+ ":", ITSNotValidColor));
				for (ResCMV theValue : theState
						.getUncompliedResCMVs()) {
					theMainBox.add(new StyledLabel("   " + theValue.prettyPrint(),
							ITSNotValidColor));
				}
			}
		}
		if (itsStats.getNotDefinedNb() != 0) {
			theMainBox.add(new StyledLabel("-"
					+ LessonMapper2.getInstance().getLangComment("notdefined")
					+ ":", ITSNotDefinedColor, Font.ITALIC + Font.BOLD));
			for (ValidationState theState : itsStats.getNotDefineds()) {
				theMainBox.add(new StyledLabel(theState.getAttribute()
						.getLabel(), ITSNotDefinedColor));
			} 
		}
		if (itsStats.getValidNb() != 0) {
			theMainBox.add(new StyledLabel(
					"-" + LessonMapper2.getInstance().getLangComment("valid")
							+ ":", ITSValidColor, Font.ITALIC + Font.BOLD));
			for (ValidationState theState : itsStats.getValids()) {
				theMainBox.add(new StyledLabel(theState.getAttribute()
						.getLabel(), ITSValidColor));
			}
		}
		JScrollPane theScroll = new JScrollPane(theMainBox);
		theScroll.setBackground(LMUI.ITSDarkSandColor);
		theScroll.getViewport().setBackground(LMUI.ITSDarkSandColor);
		return theScroll;
	}

	
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent,
	 *      int)
	 */
	public void processEvent(PInputEvent aEvent, int aType) {
		if (aType == MouseEvent.MOUSE_CLICKED
				&& aEvent.getButton() == MouseEvent.BUTTON1
				&& aEvent.getClickCount() == 2 && itsLOM != null) {
			Point2D thePosition = aEvent.getCanvasPosition();
			itsPopup.setPosition((int) thePosition.getX(), (int) thePosition
					.getY());
			itsPopup.show();
			aEvent.setHandled(true);
		}
		
	}

	/**
	 * paint the color semaphore representing validation state invalid stats
	 * take larger space than valid stat.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	@Override
	protected void paint(PPaintContext aPaintContext) {
		PAffineTransform theTransform = null;
		Graphics2D theGraphics = aPaintContext.getGraphics();
		if (LessonMapper2.getInstance().isShownValidationState|| hasFocus || SelectionBorderManager.getInstance().isSelected(itsSelectionableLOM)){
			if ((aPaintContext.getCamera()==LessonMapper2.getInstance().getMainCanvas().getCamera())
					&& (itsStats != null)) {
				int theNotValidImportance = 2;
				int theTotal = theNotValidImportance * itsStats.getNotValidNb()
						+ itsStats.getNotDefinedNb() + itsStats.getValidNb();
				if (theTotal != 0) {
					// if (true) {// if circle
					double theScale = aPaintContext.getScale();
					theTransform = new PAffineTransform();
					if (theScale > 5) {
						theTransform.setScale(0.4);
						theTransform.setOffset(1, 1);
					} else theTransform.setOffset(-2,0);
					aPaintContext.pushTransform(theTransform);
					//System.out.println(aPaintContext.getScale());
	
					theGraphics.setStroke(new BasicStroke(0.5f));
					theGraphics.setPaint(LMUI.ITSSandColor);
					theGraphics.fillRoundRect(0, 0, ITSWIDTHTOTAL,
							ITSHEIGHT , 2, 2);
					theGraphics.setPaint(Color.black);
					theGraphics.drawRoundRect(0, 0, ITSWIDTHTOTAL,
							ITSHEIGHT , 2, 2);
	
					theGraphics.setStroke(new BasicStroke(0.1f));
					if (itsStats.getNotValidNb() > 0) {
						theGraphics.setPaint(ITSNotValidColor);
						theGraphics.fillOval(ITSINSET, ITSINSET, ITSWIDTH, ITSWIDTH);
					}
					if (itsStats.getNotDefinedNb() > 0) {
						theGraphics.setPaint(ITSNotDefinedColor);
						theGraphics.fillOval(ITSINSET, ITSINSET + ITSWIDTH, ITSWIDTH,
								ITSWIDTH);
					}
					if (itsStats.getValidNb() > 0) {
						theGraphics.setPaint(ITSValidColor);
						theGraphics.fillOval(ITSINSET, ITSINSET + 2 * ITSWIDTH,
								ITSWIDTH, ITSWIDTH);
					}
					theGraphics.setPaint(Color.black);
					theGraphics.drawOval(ITSINSET, ITSINSET, ITSWIDTH, ITSWIDTH);
					theGraphics.drawOval(ITSINSET, ITSINSET + ITSWIDTH, ITSWIDTH,
							ITSWIDTH);
					theGraphics.drawOval(ITSINSET, ITSINSET + 2 * ITSWIDTH, ITSWIDTH,
							ITSWIDTH);
					aPaintContext.popTransform(theTransform);
					// } else {
					// theGraphics.setStroke(new BasicStroke(0.1f));
					// theGraphics.setPaint(ITSNotValidColor);
					// int theNotValidHeight = theNotValidImportance
					// * ITSHEIGHT
					// * itsStats.getNotValidNb() / theTotal;
					// if (itsStats.getNotValidNb()>0){
					// theGraphics.fillRoundRect(ITSHEIGHT
					// - theNotValidHeight+2*insets, -ITSWIDTH,
					// theNotValidHeight+1,ITSWIDTH+1,2,2);
					// theGraphics.setPaint(Color.BLACK);
					// theGraphics.drawRoundRect(ITSHEIGHT
					// - theNotValidHeight+2*insets, -ITSWIDTH,
					// theNotValidHeight+1,ITSWIDTH+1,2,2);
					// }
					// theGraphics.setPaint(ITSNotDefinedColor);
					// int theNotDefinedHeight = ITSHEIGHT
					// * itsStats.getNotDefinedNb() / theTotal;
					// if (itsStats.getNotDefinedNb()>0){
					// theGraphics.fillRoundRect(ITSHEIGHT
					// - theNotValidHeight - theNotDefinedHeight+insets, -ITSWIDTH,
					// theNotDefinedHeight+1,ITSWIDTH+1,2,2);
					// theGraphics.setPaint(Color.BLACK);
					// theGraphics.drawRoundRect(ITSHEIGHT
					// - theNotValidHeight - theNotDefinedHeight+insets, -ITSWIDTH,
					// theNotDefinedHeight+1,ITSWIDTH+1,2,2);
					// }
					// theGraphics.setPaint(ITSValidColor);
					// int theValidHeight = ITSHEIGHT * itsStats.getValidNb() /
					// theTotal;
					// if (itsStats.getValidNb()>0){
					// theGraphics.fillRoundRect(0, -ITSWIDTH,
					// theValidHeight+1,ITSWIDTH+1,2,2);
					// theGraphics.setPaint(Color.BLACK);
					// theGraphics.drawRoundRect(0, -ITSWIDTH,
					// theValidHeight+1,ITSWIDTH+1,2,2);
					// }
					// }
					
				}
			}
		}
		super.paint(aPaintContext);
		
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
	 */
	public String getHelp() {
		if (itsStats == null)
			return "";
		return LessonMapper2.getInstance().getLangComment("validity1") + " "
				+ itsStats.getNotValidNb() + ", "
				+ LessonMapper2.getInstance().getLangComment("notvalid") + ", "
				+ itsStats.getNotDefinedNb() + " "
				+ LessonMapper2.getInstance().getLangComment("notdefined")
				+ ", " + itsStats.getValidNb() + " "
				+ LessonMapper2.getInstance().getLangComment("valid") + ". "
				+ LessonMapper2.getInstance().getLangComment("validity2");
	}

}
