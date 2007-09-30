/*
 * Created on Aug 8, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

import java.awt.event.InputEvent;

import dcc.lessonMapper2.LessonMapper2;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;

/**
 * If there is a double click in the background then it goes upper layer 
 * 
 * @author omotelet
 */
public class GoUpperLayerHandler extends PBasicInputEventHandler {

    /**
	 * The Constructor.
	 */
    public GoUpperLayerHandler() {
        setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd.cs.piccolo.event.PInputEvent)
     */
    public void mousePressed(PInputEvent aEvent) {
        if (aEvent.getClickCount()==2)
        	LessonMapper2.getInstance().goUpperLayer();
    }

    
}
