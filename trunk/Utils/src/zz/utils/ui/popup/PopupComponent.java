/**
 * PopupComponent.java
 *
 * @author Guillaume POTHIER
 */

package zz.utils.ui.popup;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;

import zz.utils.ui.TransparentPanel;

/**
 * A component that handles a popup component. PopupComponent has a content, which
 * is the component that is always displayed, and a popup, the component that pops
 * up on the screen when requested.<p>
 * If the popup implements the interface {@link PopupInterface}, it can have access to its PopupComponent.
 * If it implements the interface {@link PopupListener}, it is automatically notified
 * when the popup is shown/hidden.
 */
public class PopupComponent extends TransparentPanel implements PopupListener
{
	private List<PopupListener> itsPopupListeners = new ArrayList<PopupListener> ();

	private StickyPopup itsPopup;

	public PopupComponent ()
	{
		this (null, null);
	}

	public PopupComponent (JComponent popup)
	{
		this (popup, null);
	}

	public PopupComponent (JComponent popup, JComponent content)
	{
		itsPopup = new StickyPopup (popup, content);
		itsPopup.addPopupListener(this);
		setLayout (new BorderLayout ());
		setContent (content);
	}

	public void addPopupListener (PopupListener aListener)
	{
		itsPopupListeners.add (aListener);
	}

	public void removePopupListener (PopupListener aListener)
	{
		itsPopupListeners.remove (aListener);
	}

	private void firePopupShown ()
	{
		for (Iterator theIterator = itsPopupListeners.iterator (); theIterator.hasNext ();)
		{
			PopupListener theListener = (PopupListener) theIterator.next ();
			theListener.popupShown();
		}
	}

	private void firePopupHidden ()
	{
		for (Iterator theIterator = itsPopupListeners.iterator (); theIterator.hasNext ();)
		{
			PopupListener theListener = (PopupListener) theIterator.next ();
			theListener.popupHidden();
		}
	}

	public void popupHidden ()
	{
		firePopupHidden();
	}

	public void popupShown ()
	{
		firePopupShown();
	}

	public void setPreferredDirection (int aPreferredDirection)
	{
		itsPopup.setPreferredDirection(aPreferredDirection);
	}

	/**
	 * If set to true, the popup will be hidden when the mouse is clicked outside
	 * the popup.
	 */
	public void setAutoHide (boolean aAutoDisappear)
	{
		itsPopup.setAutoHide(aAutoDisappear);
	}

	public void setPopupComponent (JComponent aPopup)
	{
		itsPopup.setContent(aPopup);
	}

	/**
	 * Returns the content displayed by the popup.
	 */
	public JComponent getPopupComponent ()
	{
		return itsPopup.getContent();
	}

	public StickyPopup getPopup ()
	{
		return itsPopup;
	}

	public void setContent (JComponent aContent)
	{
		removeAll ();
		if (aContent != null) add (aContent, BorderLayout.CENTER);
		itsPopup.setTriggerComponent(aContent);
	}

	public boolean isPopupShown ()
	{
		return itsPopup.isPopupShown();
	}

	public void togglePopup ()
	{
		itsPopup.togglePopup();
	}


	public void showPopup ()
	{
		itsPopup.show();
	}

	public void hidePopup ()
	{
		itsPopup.hide();
	}
}

