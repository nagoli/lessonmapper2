/*
 * LessonMapper 2.Copyright (C) Olivier Motelet.This program is free software; you can redistribute it and/ormodify it under the terms of the GNU General Public Licenseas published by the Free Software Foundation; either version 2of the License, or (at your option) any later version.This program is distributed in the hope that it will be useful,but WITHOUT ANY WARRANTY; without even the implied warranty ofMERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See theGNU General Public License for more details.You should have received a copy of the GNU General Public Licensealong with this program; if not, write to the Free SoftwareFoundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
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
