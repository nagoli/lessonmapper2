/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.ui.lom;

import java.awt.Color;
import java.net.URL;
import java.util.Collection;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import util.Couple;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.SelectionBorderManager;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;
/**
 * The Class LOMAttributeBaseSet.
 */
public class LOMAttributeBaseSet extends JScrollPane {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
	
	/** The ITS attribute list. */
	public static URL ITSAttributeList= LessonMapper2.class.getResource("resources/TitleLOMProfile.xml");
	
	static Collection<LOMAttribute> ITSAttributes;
	public static Collection<LOMAttribute> getAttributeList(){
		if (ITSAttributes == null)
			ITSAttributes = LOMAttribute.getAttributeList(ITSAttributeList);
		return ITSAttributes;
	}
	
	
	
	
	
	/** The its attributes. */
	Collection<LOMAttribute> itsAttributes = new Vector<LOMAttribute>();
	
	/** The its attribute U is. */
	Vector<LOMAttributePanel> itsAttributeUIs= new Vector<LOMAttributePanel>();
	
	/** The its box. */
	JPanel itsBox ;
	
	/**
	 * The Constructor.
	 */
	public LOMAttributeBaseSet() {
		super();
		itsBox = new JPanel();
		itsBox.setLayout(new BoxLayout(itsBox,BoxLayout.Y_AXIS));
		setViewportView(itsBox);
		setBackground(null);
		getViewport().setBackground(null);
		itsAttributes = getAttributeList();
		for (LOMAttribute theAttribute : itsAttributes) {
			LOMAttributePanel theUI = LOMAttributePanel.getLOMAttributePanel(theAttribute);
			itsAttributeUIs.add(theUI);
		}
		
		for (LOMAttributePanel theUI: itsAttributeUIs){
			itsBox.add(theUI);
		}
		itsBox.add(Box.createVerticalGlue());
		
		rebuildPanel();
		
		PNotificationCenter.defaultCenter().addListener(this,
				"LOMSelectionChanged",
				SelectionBorderManager.LOM_SELECTION_CHANGED_EVENT,
				SelectionBorderManager.ITSInstance);
		setMaximumSize(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE);
		//setBackground(null);
	}

	/**
	 * rebuild the panel : remove everything and add again
	 */
	public void rebuildPanel(){
		/*itsBox.removeAll();
		for (LOMAttributePanel theUI: itsAttributeUIs){
			itsBox.add(theUI);
		}
		itsBox.add(Box.createVerticalGlue());*/
		itsBox.revalidate();
		repaint();
	}
	
	/**
	 * update the LOMPanel with the new Selection of LOM elements.
	 * 
	 * @param aNotification
	 *            the a notification
	 */
	public void LOMSelectionChanged(PNotification aNotification){
		Collection<Couple<LOM, Color>> theSelectedLOMMap = SelectionBorderManager.ITSInstance.getSelectedLOMMap();
		for (LOMAttributePanel thePanel : itsAttributeUIs) {
			thePanel.setLOM(theSelectedLOMMap); 
		}
		rebuildPanel();
		
	}

	
	
}