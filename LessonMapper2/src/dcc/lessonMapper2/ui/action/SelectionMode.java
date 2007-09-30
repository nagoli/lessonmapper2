/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package dcc.lessonMapper2.ui.action;

import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.ImageIcon;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.SelectionHandler;
import edu.umd.cs.piccolo.event.PPanEventHandler;

/**
 * The Class SelectionMode.
 */
public class SelectionMode extends LessonMapperToggleAction {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The ITS text. */
    public static String ITSText = "selectionMode";
    
    /** The ITS icon. */
    public static String ITSIcon = LMUI.ITSSelectionIcon;
    

    //this field is used by the static aspect to access the unique instance SelectionMode
    /** The ITS instance. */
    public static SelectionMode ITSInstance = new SelectionMode();
    
    /**
	 * Return the current selectionHandler return null if selectionMode is not
	 * currently active.
	 * 
	 * @return the selection handler
	 */
    public static SelectionHandler getSelectionHandler(){
        if (ITSInstance.getModel().isSelected()) 
        return ITSInstance.itsSelectionHandler;
        else return null;
    }
    
    /** The its selection handler. */
    public SelectionHandler itsSelectionHandler;
    
    /** The its pan event handler. */
    public PPanEventHandler itsPanEventHandler = new PPanEventHandler();

    /**
	 * The Constructor.
	 */
    public SelectionMode() {
 	   super(null,new ImageIcon(LessonMapper2.getInstance().getUI().getClass().getResource(ITSIcon)));
    }
   
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent aE) {

    }
    
    /**
	 * init the selectionHandler in order to match with current active layer.
	 */	
    public void initSelectionHandler() {
        //itsCanvas.removeInputEventListener(itsSelectionHandler);
        if (itsSelectionHandler==null) 
        	itsSelectionHandler = new SelectionHandler(getMainCanvas().getLayer(),
                getLessonMapper().getActiveLayer(), getMainCanvas().getCamera());
        else {
        	itsSelectionHandler.reinit(getMainCanvas().getLayer(),
                   getLessonMapper().getActiveLayer());
        }
        
       // itsCanvas.addInputEventListener(itsSelectionHandler);
    }

    /* (non-Javadoc)
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent aE) {
        if (getModel().isSelected()) {
            initSelectionHandler();
            getMainCanvas().setPanEventHandler(itsPanEventHandler);
            getMainCanvas().addInputEventListener(itsSelectionHandler);
           
            //System.out.println("selectedSelect");
        } else {
            getMainCanvas().removeInputEventListener(itsSelectionHandler);
            getMainCanvas().setPanEventHandler(null);
            //itsLessonMapper.setSelectionHandler(null);
            //System.out.println("unselectedSelect");
        }
    }

   
    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
     */
    public String getHelpText() {
        return  LessonMapper2.getInstance().getLangComment("selectMode");
    }
    
}
