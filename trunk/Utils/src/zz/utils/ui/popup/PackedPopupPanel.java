/*
 * Created on 10-nov-2004
 */
package zz.utils.ui.popup;

import javax.swing.JComponent;
import javax.swing.JPanel;

import zz.utils.ui.StackLayout;

/**
 * A panel that can be used as popup content that resizes the popup
 * as its contents change. It can also be used as a regular panel.
 * <p>
 * This class doesn't create or show the popup; it is the reponsibility of the user
 * to create the popup.
 * @author gpothier
 */
public class PackedPopupPanel extends JPanel
{
	public PackedPopupPanel()
	{
		setLayout(new StackLayout());
	}
	
	/**
	 * Closes the popup that contains this panel.
	 */
	protected void close()
	{
		setContent(null);
		if (! PopupUtils.hidePopup(this))
		{
			revalidate();
			repaint();
		}
	}

	/**
	 * Changes the current content and resizes the popup.
	 */
	public void setContent (JComponent aComponent)
	{
		removeAll();
		if (aComponent != null) add (aComponent);
		if (! PopupUtils.revalidatePopup(this))
		{
			revalidate();
			repaint();
		}
	}
}
