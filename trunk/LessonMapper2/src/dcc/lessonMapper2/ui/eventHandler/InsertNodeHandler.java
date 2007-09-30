/*
 * Created on Aug 9, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.action.InsertNodeMode;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * The Class InsertNodeHandler.
 * 
 * @author omotelet
 * 
 * This class is responible for inserting a new node in the current ActiveLaeyer
 * of lessonMapper It is associated with a InsetNodeAction specifying the node
 * to insert with getNodeToInsert()
 */
public class InsertNodeHandler extends PBasicInputEventHandler {

  

    /** The unique insert. */
    protected boolean uniqueInsert = true; //aUnique insertion

    /** The its action. */
    protected InsertNodeMode itsAction;
    
    

    /**
	 * The Constructor.
	 * 
	 * @param aAction
	 *            the a action
	 */
    public InsertNodeHandler(InsertNodeMode aAction) {
        itsAction=aAction;
    }

  
    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd.cs.piccolo.event.PInputEvent)
     */
    public void mousePressed(PInputEvent aEvent) {
        PLayer theParent = LessonMapper2.getInstance().getActiveLayer();
        //if (aEvent.getPickedNode() != theParent)
        //    return;
        PNode theNodeToInsert = itsAction.getNodeToInsert();
        
        if (theNodeToInsert != null) {
            theNodeToInsert.setOffset(theParent.globalToLocal(aEvent
                    .getCamera().localToGlobal(aEvent.getPosition())));
            theParent.addChild(theNodeToInsert);
            theParent.repaint();
        }
        if (uniqueInsert)
            LessonMapper2.getInstance().getUI().switchToSelectionMode();
    }

  
}
