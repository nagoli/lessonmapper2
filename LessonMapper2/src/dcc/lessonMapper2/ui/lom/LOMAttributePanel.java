/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.lom;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMVocabulary;
import util.Couple;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;

/**
 * LOMAttributePanel shows the label of an attribute then thevalue for the Lom
 * associated with setLOM, or addLOM
 * 
 * LOM attribute Panel remain centralized so that changed may be preserved
 * without saving when LOMAttributePanel is no more displayed.
 * 
 * @author omotelet
 */

public class LOMAttributePanel extends JPanel {

	/** The ITS attribute panels. */
	protected static Map<LOMAttribute, LOMAttributePanel> ITSAttributePanels = new HashMap<LOMAttribute, LOMAttributePanel>();

	/**
	 * Gets the LOM attribute panel.
	 * 
	 * @param aAttribute
	 *            the a attribute
	 * 
	 * @return the LOM attribute panel
	 */
	public static LOMAttributePanel getLOMAttributePanel(LOMAttribute aAttribute) {
		LOMAttributePanel theAttributePanel = ITSAttributePanels
				.get(aAttribute);
		if (theAttributePanel == null) {
			theAttributePanel = new LOMAttributePanel(aAttribute);
			ITSAttributePanels.put(aAttribute, theAttributePanel);
		}
		return theAttributePanel;
	}
	
	

	/** The ITS title font. */
	public static Font ITSTitleFont = new Font("SanSerif", Font.BOLD, 12);

	/** The ITS element font. */
	public static Font ITSElementFont = new Font("SanSerif", Font.PLAIN, 12);

	/** The its attribute. */
	protected LOMAttribute itsAttribute;

	/** The its title pane. */
	protected Box itsTitlePane;

	/** The its active LOM attribute uis. */
	protected Vector<LOMAttributeUI> itsActiveLOMAttributeUis = new Vector<LOMAttributeUI>();

	/** The its LOM map. */
	protected Map<LOM, LOMAttributeUI> itsLOMMap = new HashMap<LOM, LOMAttributeUI>();

	/** The is vocabulary empty. */
	boolean isVocabularyEmpty;

	/**
	 * The Constructor.
	 * 
	 * @param aAttribute
	 *            the a attribute
	 */
	private LOMAttributePanel(LOMAttribute aAttribute) {
		super();
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
		setMinimumSize(new Dimension(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width,30));
		itsAttribute = aAttribute;
		LOMVocabulary theVocabulary = itsAttribute.getLOMVocabulary();
		isVocabularyEmpty = theVocabulary.getVocabularySet().isEmpty();
		initTitlePane();

		initDisplay();
	}

	
	
	/**
	 * Inits the title pane.
	 */
	public void initTitlePane() {
		//itsTitlePane = new Box(BoxLayout.X_AXIS);
		//JLabel theLabel = new JLabel("", JLabel.LEFT);
		//theLabel.setFont(ITSTitleFont);
		//theLabel.setText(itsAttribute.getLabel());
		setToolTipText(itsAttribute.getComment());
		setBackground(LMUI.ITSSandColor);
		//itsTitlePane.add(theLabel);
		//itsTitlePane.add(Box.createVerticalGlue());
		setBorder(BorderFactory.createTitledBorder(BorderFactory.createEtchedBorder(EtchedBorder.LOWERED),
				/*"<html><b><i>"+*/itsAttribute.getLabel()/*+"</i></b></html>"*/) );
		
	}

	/**
	 * Inits the display.
	 */
	public void initDisplay() {
		removeAll();
		//add(itsTitlePane);
		//itsTitlePane.setAlignmentX(Component.LEFT_ALIGNMENT);
		if (itsActiveLOMAttributeUis.isEmpty())
			add(new JLabel(LessonMapper2.getInstance().getLangComment("LOMAttributeDefault"), JLabel.LEFT));
		else
			for (LOMAttributeUI iterable_element : itsActiveLOMAttributeUis) {
				add(iterable_element);
				iterable_element.invalidate();
				//iterable_element.revalidate();
				iterable_element.setAlignmentX(Component.LEFT_ALIGNMENT);
			}
		invalidate();
		//revalidate();
		//add(Box.createVerticalGlue());
	}

	/**
	 * Sets the LOM.
	 * 
	 * @param aLOMColorMap
	 *            the LOM
	 */
	public void setLOM(Collection<Couple<LOM, Color>> aLOMColorMap) {

		itsActiveLOMAttributeUis.clear();
		for (Couple<LOM,Color> theLOMColor : aLOMColorMap) {
			LOMAttributeUI theUI = getLOMAttributeUI(theLOMColor.getLeftElement());
			theUI.setFont(ITSElementFont);
			theUI.setBackground(theLOMColor.getRightElement());
			itsActiveLOMAttributeUis.add(theUI);
		}
		initDisplay();
	}

	/**
	 * Adds the LOM.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aColor
	 *            the a color
	 */
	public void addLOM(LOM aLOM, Color aColor) {
		itsActiveLOMAttributeUis.clear();
		LOMAttributeUI theUI = getLOMAttributeUI(aLOM);
		theUI.setFont(ITSElementFont);
		theUI.setBackground(aColor);
		itsActiveLOMAttributeUis.add(theUI);
		initDisplay();
	}

/**
 * return the LOMAttributeUI already used for this LOM. create a new
 * appropriated one when there was not yet created
 * 
 * @param aLOM
 *            the a LOM
 * 
 * @return the LOM attribute UI
 */
	public LOMAttributeUI getLOMAttributeUI(LOM aLOM) {
		if (itsLOMMap.containsKey(aLOM)) {
			return itsLOMMap.get(aLOM);
		} else {
			LOMAttributeUI theUI;
			if (isVocabularyEmpty)
				theUI = new LOMAttributeTextUI(aLOM, itsAttribute);
			else
				theUI = new LOMAttributeComboUI(aLOM, itsAttribute,
						itsAttribute.getLOMVocabulary());
			theUI.setToolTipText(itsAttribute.getComment());
			itsLOMMap.put(aLOM, theUI);
			return theUI;
		}
	}

}
