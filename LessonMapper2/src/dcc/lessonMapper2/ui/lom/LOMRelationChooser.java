/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.lom;

/**
 * LOMRelationChooser display a list og PText corresponding to 
 * the available relation types.
 * Clicking out of the 
 */

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.LOMRelationType;
import util.ui.ColorMosaic;
;

/**
 * The Class LOMRelationChooser.
 */
public class LOMRelationChooser extends JPopupMenu implements ActionListener{

	/** The Constant ITSRelationTypes. */
	public static final List<LOMRelationType> ITSRelationTypes = LOMRelationType.getAvailableVisibleTypes(); 
	
	/** The its relation. */
	LOMRelation itsRelation;
	
	/** The ITS relation color map. */
	public static Map<LOMRelationType, Color> ITSRelationColorMap = new HashMap<LOMRelationType, Color>();
	
	/**
	 * Gets the color for type.
	 * 
	 * @param theType
	 *            the type
	 * 
	 * @return the color for type
	 */
	public static Color getColorForType(LOMRelationType theType){
		if (!ITSRelationColorMap.containsKey(theType))
			ITSRelationColorMap.put(theType, ColorMosaic.nextRandomColor(5845711));
		return ITSRelationColorMap.get(theType);
	}
	
	/**
	 * The Constructor.
	 * 
	 * @param aRelation
	 *            the a relation
	 */
	public LOMRelationChooser(LOMRelation aRelation){
		super();
		//BoxLayout.Y_AXIS);
		
		itsRelation = aRelation;
		//addFocusListener(this);
		for (LOMRelationType theType : ITSRelationTypes) {
			JMenuItem theLabel = new JMenuItem(theType.getLabel());
			add(theLabel);
			theLabel.setActionCommand(theType.getName());
			theLabel.addActionListener(this);
			//theLabel.setForeground(ITSRelationColorMap.get(theType));
			//theLabel.setBackground(ITSRelationColorMap.get(theType));
			//theLabel.setMaximumSize(new Dimension(150,25));		
		}
	}

	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent aE) {
		String theCommand = aE.getActionCommand();
		if (theCommand != null)
			itsRelation.setLOMRelationType(theCommand);
	}
	
	
	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {
		System.out.println(ITSRelationTypes);
	}


}
