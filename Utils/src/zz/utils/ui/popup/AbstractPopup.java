/*
 * Created on 20-may-2004
 *

 */
package zz.utils.ui.popup;

import java.awt.Component;
import java.awt.Frame;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JRootPane;
import javax.swing.JWindow;

import zz.utils.ui.TransparentPanel;


/**
 * Base class for displaying popups.
 * Delegates to subclasses the determination of where to display the popup.
 * @see #show
 * @see #hide
 */
public abstract class AbstractPopup 
{
	private JComponent itsContent;

	private boolean itsShown = false;

	/**
	 * Whether the popup should be automatically hidden
	 * when the user clicks outside of it.
	 * It is possible to limit the autohide sensitive zone
	 * to a particular component.
	 */
	private boolean itsAutoHide = true;
	
	/**
	 * The component which is sensitive to autohide,
	 * ie. a click in this component closes the popup.
	 * If this component is null and autohide is true,
	 * the whole frame is sensitive.
	 */
	private JComponent itsAutohideComponent;

	private PopupWindow itsPopupWindow;

	
	
	/**
	 * Permits to implement the auto-hide feature.
	 */
	private JComponent itsScreen;

	private List<PopupListener> itsPopupListeners = new ArrayList<PopupListener> ();

	/**
	 * permit to add a glasspane to the popup windows
	 */
	private Component itsGlassPane;
	
	
	public void setGlassPane(Component aGlassPane) {
		itsGlassPane = aGlassPane;
		if (itsPopupWindow!=null) itsPopupWindow.setGlassPane(aGlassPane);
	}
	public Component getGlassPane() {
		return itsGlassPane;
	}
	
	/**
	 * Creates a popup object.
	 *
	 * @param aContent The content of the popup window.
	 */
	public AbstractPopup (JComponent aContent)
	{
		setContent (aContent);
		PopupManager.getInstance().registerPopup(this);
	}

	/**
	 * Returns the component that owns this popup. When the owner component is 
	 * removed, the popup should be hidden.
	 */
	public abstract JComponent getOwner ();
	
	public void addPopupListener (PopupListener aListener)
	{
		itsPopupListeners.add (aListener);
	}

	public void removePopupListener (PopupListener aListener)
	{
		itsPopupListeners.remove (aListener);
	}

	void firePopupShown ()
	{
		if (itsContent instanceof PopupListener)
		{
			PopupListener thePopupListener = (PopupListener) itsContent;
			thePopupListener.popupShown ();
		}

		for (Iterator theIterator = itsPopupListeners.iterator (); theIterator.hasNext ();)
		{
			PopupListener theListener = (PopupListener) theIterator.next ();
			theListener.popupShown();
		}
	}

	void firePopupHidden ()
	{
		if (itsContent instanceof PopupListener)
		{
			PopupListener thePopupListener = (PopupListener) itsContent;
			thePopupListener.popupHidden ();
		}

		for (Iterator theIterator = itsPopupListeners.iterator (); theIterator.hasNext ();)
		{
			PopupListener theListener = (PopupListener) theIterator.next ();
			theListener.popupHidden();
		}
	}

	private void prepare ()
	{
		JFrame theOwner = getOwnerFrame();
		
		theOwner.addComponentListener(new ComponentAdapter ()
		{
			public void componentHidden (ComponentEvent e)
			{
				hide();
			}

			public void componentResized (ComponentEvent e)
			{
				repositionPopup();
			}

			public void componentMoved (ComponentEvent e)
			{
				repositionPopup();
			}
		});
		theOwner.addWindowListener(new WindowAdapter ()
		{
			public void windowClosed (WindowEvent e)
			{
				hide();
			}

			public void windowDeactivated(WindowEvent aE)
			{
				hide();
			}
			
			
		});

		itsPopupWindow = new PopupWindow (theOwner, this);
		if (itsGlassPane!=null) itsPopupWindow.setGlassPane(itsGlassPane);
//		itsPopupWindow.setFocusable(false);
	}

	/**
	 * If set to true, the popup will be hidden when the mouse is clicked outside
	 * the popup.
	 */
	public void setAutoHide (boolean aAutoHide)
	{
		itsAutoHide = aAutoHide;
	}

	public JComponent getAutohideComponent()
	{
		return itsAutohideComponent;
	}
	
	public void setAutohideComponent(JComponent aAutohideComponent)
	{
		itsAutohideComponent = aAutohideComponent;
	}
	
	public void setContent (JComponent aContent)
	{
		if (itsContent != aContent)
		{
			itsContent = aContent;
			if (isPopupShown())
			{
				hide();
				show();
			}
		}
	}

	/**
	 * Returns the content displayed by the popup.
	 */
	public JComponent getContent ()
	{
		return itsContent;
	}

	public boolean isPopupShown ()
	{
		return itsShown;
	}

	public void togglePopup ()
	{
		if (itsShown) hide ();
		else show ();
	}

	/**
	 * Returns the bounds where to display the popup.
	 */
	protected abstract Rectangle getPopupBounds ();
	
	/**
	 * Returns the frame that contains the popup.
	 */
	protected abstract JFrame getOwnerFrame ();

	/**
	 * Repositions the popup and the screen.
	 * Called when the owner frame changes bounds, or when the
	 * popup needs to be revalidated.
	 */
	private void repositionPopup ()
	{
		if (! itsShown) return;

		Rectangle thePopupBounds = getPopupBounds();
		itsPopupWindow.setBounds (thePopupBounds);
		
		if (itsScreen != null)
		{
			JLayeredPane lp = getOwnerFrame().getLayeredPane ();
			itsScreen.setBounds(0, 0, lp.getWidth(), lp.getHeight());
		}
		itsPopupWindow.toFront();
	}

	/**
	 * Displays the popup window on the screen, next to the trigger component.
	 * Tries to respect the preferred direction.
	 */
	public void show ()
	{
		show (true);
	}

	/**
	 * Displays the popup window on the screen, next to the trigger component.
	 * Tries to respect the preferred direction.
	 */
	public void show (boolean aNotify)
	{
		if (itsShown) return;
		if (getContent () == null) return;

		JRootPane theRootPane = getOwnerFrame().getRootPane();

		if (itsPopupWindow == null) prepare();

		JLayeredPane lp = theRootPane.getLayeredPane ();

		itsPopupWindow.setContentPane (getContent());
		itsPopupWindow.setBounds(getPopupBounds());
		itsPopupWindow.setVisible(true);

		if (itsAutoHide)
		{
			Rectangle theSensitiveZone;
			if (itsAutohideComponent != null)
			{
				Point theZoneLocation = itsAutohideComponent.getLocationOnScreen();
				Point theFrameLocation = lp.getLocationOnScreen();
				
				int theX = theZoneLocation.x - theFrameLocation.x;
				int theY = theZoneLocation.y - theFrameLocation.y;
				int theW = itsAutohideComponent.getWidth();
				int theH = itsAutohideComponent.getHeight();
				
				theSensitiveZone = new Rectangle (theX, theY, theW, theH);
			}
			else
			{
				theSensitiveZone = new Rectangle (0, 0, lp.getWidth(), lp.getHeight());
			}
			
			itsScreen = new TransparentPanel (null);
			lp.add (itsScreen, JLayeredPane.POPUP_LAYER);
			lp.moveToFront(itsScreen);
			itsScreen.setBounds(theSensitiveZone);
			itsScreen.addMouseListener(new MouseAdapter ()
			{
				public void mousePressed(MouseEvent aE)
				{
					hide ();
				}
			});
		}
		else
		{
			itsScreen = null;
		}

		itsShown = true;

		if (aNotify) firePopupShown();
	}

	/**
	 * Hides the popup.
	 */
	public void hide ()
	{
		hide (true);
	}

	/**
	 * Hides the popup.
	 */

	public void hide (boolean aNotify)
	{
		if (! itsShown) return;
		if (itsAutoHide)
		{
			JFrame theOwner = getOwnerFrame();
			theOwner.getLayeredPane().remove (itsScreen);
		}
		itsPopupWindow.setVisible(false);
		itsShown = false;

		if (aNotify) firePopupHidden();
	}

	public void revalidatePopup ()
	{
		itsContent.revalidate();
		repositionPopup();
		itsPopupWindow.validate();
		itsContent.repaint();
	}
	
	public static class PopupWindow extends JWindow
	{
		private AbstractPopup itsPopup;
		
		
		public PopupWindow(Frame aOwner, AbstractPopup aPopup)
		{
			super(aOwner);
			itsPopup = aPopup;
		}
		
		public AbstractPopup getPopup()
		{
			return itsPopup;
		}
	}

}