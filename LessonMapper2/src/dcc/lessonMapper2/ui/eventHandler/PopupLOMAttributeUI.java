/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.eventHandler;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import zz.utils.ui.popup.RelativePopup;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.lom.LOMAttributeComboUI;
import dcc.lessonMapper2.ui.lom.LOMAttributeTextUI;
import dcc.lessonMapper2.ui.lom.LOMAttributeUI;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;


/**
 * this event listener shows a popup window for an attribute specified in
 * parameter when the component conerns with this listener is double cliked.
 * 
 * save and undo event have the effect to close the popup
 * 
 * TExtField used in PopupLOMAttributeUI reacts to ENTER key event as a save/close event
 * @author omotelet
 */
public class PopupLOMAttributeUI implements PInputEventListener{

	/** The its attribute. */
	LOMAttribute itsAttribute;
	
	/** The its LOM. */
	LOM itsLOM;
	
	/** The its UI. */
	LOMAttributeUI itsUI;
	
	/** The its popup. */
	RelativePopup itsPopup;
	
	/**
	 * The Constructor.
	 * 
	 * @param aAttribute
	 *            the a attribute
	 */
	public PopupLOMAttributeUI( LOMAttribute aAttribute) {
		itsAttribute = aAttribute;	
		itsPopup = new RelativePopup(null, LessonMapper2.getInstance().getMainCanvas(), 
				new Point(0,0));
		itsPopup.setAutoHide(true);
	}
	
	/**
	 * Sets the LOM.
	 * 
	 * @param aLOM
	 *            the LOM
	 */
	public void setLOM(LOM aLOM) {
		itsLOM = aLOM;
		if (aLOM != null) {
			if (itsAttribute.getLOMVocabulary().isEmpty()){
			LOMAttributeTextUI theTextUI = new LOMAttributeTextUI(aLOM,itsAttribute) ;
			theTextUI.getTextField().addKeyListener(new KeyListener(){
				public void keyPressed(KeyEvent aE) {
					// save the 
					if (aE.getKeyCode() == KeyEvent.VK_ENTER){
						aE.consume();
						closePopup();
					}
				}
				public void keyReleased(KeyEvent aE) {
				}
				public void keyTyped(KeyEvent aE) {
				}
			});
			itsUI=theTextUI;
			}
			else {
				LOMAttributeComboUI theComboUI = new LOMAttributeComboUI(aLOM,itsAttribute,itsAttribute.getLOMVocabulary());
				itsUI = theComboUI;
			}
			itsPopup.setContent(itsUI);
			itsUI.setPreferredSize(new Dimension(250,35));
			itsUI.setBackground(SelectionBorderManager.getInstance().getLOMColor(itsLOM).getRightElement());
			
		}
	}
	
	/* (non-Javadoc)
	 * show popup on double click
	 * @see edu.umd.cs.piccolo.event.PInputEventListener#processEvent(edu.umd.cs.piccolo.event.PInputEvent, int)
	 */
	public void processEvent(PInputEvent aEvent, int arg1) {
		//System.out.println(aEvent+" enter popup ");
		if (arg1 == MouseEvent.MOUSE_CLICKED && aEvent.getButton() == MouseEvent.BUTTON1 && 
				aEvent.getClickCount() == 2 && itsLOM !=null) {
			//System.out.println(aEvent+" do popup ");
			Point2D thePosition = aEvent.getCanvasPosition();
			itsPopup.setPosition((int)thePosition.getX(),(int)thePosition.getY());
			itsPopup.show();
			aEvent.setHandled(true);
		}
		
	}
	
	/**
	 * Close popup.
	 */
	public void closePopup() {
			itsPopup.hide();
	}

}
