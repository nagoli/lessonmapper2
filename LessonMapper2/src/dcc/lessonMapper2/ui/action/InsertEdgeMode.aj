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
package dcc.lessonMapper2.ui.action;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import javax.swing.ImageIcon;

import dcc.lessonMapper2.LMProject;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.InsertEdgeHandler;
/**
 * @author omotelet
 * 
 * This action is responsible for the action of insertion of edge
 */
public class InsertEdgeMode extends LessonMapperToggleAction {

	private static final long serialVersionUID = 1L;
    public static String ITSText = "insertEdgeMode";
    public static String ITSIcon = LMUI.ITSNewRelationIcon;
    //this field is used by the static aspect to access the unique instance
    // insertEdgeMode
    public static InsertEdgeMode ITSInstance = new InsertEdgeMode();
 
    /**
     * Return the current insertEdgeHandler return null if insertEdgeMode is not
     * currently active
     */
    public static InsertEdgeHandler getInsertEdgeHandler() {
        if (ITSInstance!=null)
        	if (ITSInstance.getModel()!=null 
        		&& ITSInstance.getModel().isSelected())
            return ITSInstance.itsInsertEdgeHandler;
        return null;
    }

    public InsertEdgeHandler itsInsertEdgeHandler = new InsertEdgeHandler();

    /**
     *  
     */
    public InsertEdgeMode() { 
 	   super(null,new ImageIcon(LessonMapper2.getInstance().getUI().getClass().getResource(ITSIcon)));
 	      }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent aE) {
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
     */
    public void itemStateChanged(ItemEvent aE) {
        if (getModel().isSelected()) {
            getMainCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            itsInsertEdgeHandler.init();
            getMainCanvas().addInputEventListener(itsInsertEdgeHandler);
            //System.out.println("selectedSelect");
        } else {
            getMainCanvas().setCursor(Cursor.getDefaultCursor());
            getMainCanvas().removeInputEventListener(itsInsertEdgeHandler);
            //System.out.println("unselectedSelect");
        }
    }
    
    public String getHelpText() {
        return LessonMapper2.getInstance().getLangComment("insertEdge");
    }
    
    static public aspect ActiveLayerListener {

        pointcut update():  execution(public void LMProject.updateActiveLayer());

        after(): update(){
            if (ITSInstance.getModel().isSelected()) {
                getInsertEdgeHandler().init();
            }

        }
    }

}
