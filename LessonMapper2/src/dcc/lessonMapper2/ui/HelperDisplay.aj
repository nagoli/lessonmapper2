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
package dcc.lessonMapper2.ui;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JComponent;
import javax.swing.JTextField;

import dcc.lessonMapper2.LessonMapper2;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * @author omotelet
 * 
 * This class is responsible for displaying help sentences of components
 * implementing HelperSupport
 */
public class HelperDisplay extends JTextField implements PInputEventListener,
		MouseListener, HelperSupport {

	protected static HelperDisplay ITSInstance = new HelperDisplay();

	private static final long serialVersionUID = 1L;

	public static HelperDisplay getInstance() {
		return ITSInstance;

	}

	protected String itsHelpText = LessonMapper2.getInstance().getLangComment("helper");

	protected String itsText;

	public HelperDisplay() {
		//setForeground(LMUI.ITSSandColor);
		setBackground(LMUI.ITSSandColor);
		setText(itsHelpText);
		setEditable(false);
	}

	public void update(HelperSupport aSupport) {
		String oldHelp = itsText;
		itsText = aSupport.getHelp();
		if (oldHelp != itsText)
			setText( itsText);
	}

	/**
	 * call update method
	 */
	public void processEvent(PInputEvent aEvent, int type) {
		if (type == MouseEvent.MOUSE_ENTERED) {
			PNode theNode = aEvent.getPickedNode();
			if (theNode instanceof HelperSupport)
				update((HelperSupport) aEvent.getPickedNode());
			else {
				System.out.println(theNode.getClass() + " not HelperSupport");
			}
		}
	}

	/**
	 * call update method
	 */
	public void mouseEntered(MouseEvent e) {
		update((HelperSupport) e.getSource());
	}

	public void mouseClicked(MouseEvent e) {
	}

	public void mousePressed(MouseEvent e) {
	}

	public void mouseReleased(MouseEvent e) {
	}

	public void mouseExited(MouseEvent e) {
	}

	public String getHelp() {
		return itsHelpText;
	}

	static public aspect HelperSupportObserver {
		pointcut theHelperSupportConstructor(): 
            initialization(HelperSupport.new(..));

		after(): theHelperSupportConstructor() {
			HelperSupport theSupport = (HelperSupport) thisJoinPoint
					.getTarget();

			if (theSupport instanceof HelperDisplay) {
				HelperDisplay theJComponent = (HelperDisplay) theSupport;
				theJComponent.addMouseListener(theJComponent);
			}

			if (theSupport instanceof PNode) {
				PNode thePNode = (PNode) theSupport;
				thePNode.addInputEventListener(getInstance());
				// System.out.println(thePNode.getListenerList());
			}
			if (theSupport instanceof JComponent) {
				JComponent theJComponent = (JComponent) theSupport;
				theJComponent.addMouseListener(getInstance());
				// System.out.println(theJComponent.getFocusListeners());
			}
			// System.out.println(theSupport.getHelp());
		}

	}

}
