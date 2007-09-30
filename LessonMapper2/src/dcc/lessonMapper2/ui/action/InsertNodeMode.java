/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.action;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.Icon;

import util.ui.ToggleButtonModel;
import dcc.lessonMapper2.ui.eventHandler.InsertNodeHandler;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class InsertNodeMode.
 * 
 * @author omotelet
 * 
 * this class change the interaction mode of lessonmapper so that node can be
 * added on the position of the mouse cursor
 */
public abstract class InsertNodeMode extends LessonMapperToggleAction {
    
    /** The its insert node handler. */
    public InsertNodeHandler itsInsertNodeHandler;
    
    /**
	 * Gets the node to insert.
	 * 
	 * @return the node to insert
	 */
    public abstract PNode getNodeToInsert(); 
    
    
        /**
		 * The Constructor.
		 */
    public InsertNodeMode() {
        super();
      }
    
    /**
	 * The Constructor.
	 * 
	 * @param aName
	 *            the a name
	 */
    public InsertNodeMode(String aName) {
        super(aName);
     }
    
    /**
	 * The Constructor.
	 * 
	 * @param aIcon
	 *            the a icon
	 * @param aName
	 *            the a name
	 */
    public InsertNodeMode(String aName, Icon aIcon) {
        super(aName, aIcon);
     }
    
    /**
	 * The Constructor.
	 * 
	 * @param aModel
	 *            the a model
	 * @param aIcon
	 *            the a icon
	 * @param aName
	 *            the a name
	 */
    public InsertNodeMode(String aName, Icon aIcon, ToggleButtonModel aModel) {
        super(aName, aIcon, aModel);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent aE) {
           if (getModel().isSelected()){
                //System.out.println("selectedInsert");
                getMainCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
                if (itsInsertNodeHandler == null) itsInsertNodeHandler= new InsertNodeHandler(this);
                getMainCanvas().addInputEventListener(itsInsertNodeHandler);       
            }
            else{
                //System.out.println("unselectedInsert");
                getMainCanvas().setCursor(Cursor.getDefaultCursor());
                getMainCanvas().removeInputEventListener(itsInsertNodeHandler);   
            }
         }


    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent aE) {
      
    }

}
