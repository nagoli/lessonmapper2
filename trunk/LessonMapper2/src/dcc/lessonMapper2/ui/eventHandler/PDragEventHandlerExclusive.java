/*
 * LessonMapper 2.
 */
package dcc.lessonMapper2.ui.eventHandler;


import edu.umd.cs.piccolo.event.PDragEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;

/**
 * User: omotelet Date: Jul 21, 2005 Time: 6:01:14 PM.
 */
public class PDragEventHandlerExclusive extends PDragEventHandler{

    /**
	 * The Constructor.
	 */
    public PDragEventHandlerExclusive() {
        super();    //To change body of overridden methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PDragEventHandler#startDrag(edu.umd.cs.piccolo.event.PInputEvent)
     */
    protected void startDrag(PInputEvent event) {
        super.startDrag(event);
        event.setHandled(true);//To change body of overridden methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PDragEventHandler#drag(edu.umd.cs.piccolo.event.PInputEvent)
     */
    protected void drag(PInputEvent event) {
        super.drag(event);
        event.setHandled(true);//To change body of overridden methods use File | Settings | File Templates.
    }

    /* (non-Javadoc)
     * @see edu.umd.cs.piccolo.event.PDragEventHandler#endDrag(edu.umd.cs.piccolo.event.PInputEvent)
     */
    protected void endDrag(PInputEvent event) {
        super.endDrag(event);
        event.setHandled(true);//To change body of overridden methods use File | Settings | File Templates.
    }

}