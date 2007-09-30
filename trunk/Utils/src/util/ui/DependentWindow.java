package util.ui;

import java.awt.Frame;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;

/**
 * create a window that will deseappered when its owner is iconified or closed
 * it will reappeared if its owner is restored
 * @author omotelet
 *
 */

public class DependentWindow extends JDialog {

	protected boolean isIconified=false;

	public DependentWindow(Frame aOwner) {
		super(aOwner);
	}
	
	public DependentWindow(Frame aOwner,String aTitle) {
		super(aOwner,aTitle);
	}
	
	@Override
	protected void processWindowStateEvent(WindowEvent aE) {
		//super.processWindowStateEvent(aE);
		if (aE.getNewState()==WindowEvent.WINDOW_CLOSED)
			setVisible(false);
		if (aE.getNewState()== WindowEvent.WINDOW_ICONIFIED) 
			if (isVisible()) {
				setVisible(false);
				isIconified = true;
			}
		if (aE.getNewState() == WindowEvent.WINDOW_DEICONIFIED)
			if (isIconified) {
				isIconified = false;
				setVisible(true);
			}
	}
	
	
}
