/*
 * Created on Aug 10, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.action;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ItemEvent;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.eventHandler.PDragEventHandlerExclusive;
import edu.umd.cs.piccolo.event.PPanEventHandler;

/**
 * The Class DragMode.
 * 
 * @author omotelet
 * 
 * action modifying the interaction mode in order to enable draging of elements
 * and panning
 */
public class DragMode extends LessonMapperToggleAction {

	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
    
    /** The ITS text. */
    public static String ITSText = "dragMode";
    
    /** The its drag event handler. */
    public PDragEventHandlerExclusive itsDragEventHandler;
    
    /** The its pan event handler. */
    public PPanEventHandler itsPanEventHandler;
    
    /** The ITS instance. */
    public static DragMode ITSInstance = new DragMode();
    
    /**
	 * The Constructor.
	 */
    public DragMode() {
        super(ITSText);
    }
    
    /**
	 * aggrega o saca el listener for dragging.
	 * 
	 * @param aE
	 *            the a e
	 */
    public void itemStateChanged(ItemEvent aE) {
      
            if (getModel().isSelected()){
                getMainCanvas().setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                if (itsPanEventHandler == null)
                   itsPanEventHandler = new PPanEventHandler();
               getMainCanvas().setPanEventHandler(new PPanEventHandler());
                if (itsDragEventHandler == null)
                    itsDragEventHandler = new PDragEventHandlerExclusive();
                getMainCanvas().addInputEventListener(itsDragEventHandler );
                //System.out.println("selecteddrag");
            }
            else {
                getMainCanvas().setCursor(Cursor.getDefaultCursor());
                getMainCanvas().setPanEventHandler(null);
                getMainCanvas().removeInputEventListener(itsDragEventHandler);
                //System.out.println("unselecteddrag");
            } 
        }


    
    /* (non-Javadoc)
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent aE) {
       

    }

    /* (non-Javadoc)
     * @see dcc.lessonMapper2.ui.action.LessonMapperToggleAction#getHelpText()
     */
    public String getHelpText() {
        return LessonMapper2.getInstance().getLangComment("dragMode");
    }
    
    
    
}
