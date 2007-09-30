/*
 * Created on 20-may-2004
 *
 */
package zz.utils.ui.popup;

import java.awt.AWTEvent;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.AWTEventListener;
import java.awt.geom.Point2D;

import javax.swing.JComponent;
import javax.swing.JFrame;

import zz.utils.Utils;


/**
 * A popup that lays at a specific position in a container
 */
public class RelativePopup extends AbstractPopup implements AWTEventListener
{
	private JComponent itsParent;
	
	private Point itsPosition;

	/**
	 * Creates a relative popup.
	 * @param aContent The component that is displayed when the popup is shown
	 * @param aParent The parent component in which the popup appears.
	 * @param aPosition Coordinates of the popup relative to the parent element.
	 */
	public RelativePopup(JComponent aContent, JComponent aParent, Point aPosition)
	{
		super(aContent);
		itsParent = aParent;
		itsPosition = aPosition;
	}
	/**
	 * create a relative popup with a Point2D but its dimensions will be casted as integers 
	 * @param aContent
	 * @param aParent
	 * @param aPosition
	 */
	public RelativePopup(JComponent aContent, JComponent aParent, Point2D aPosition)
	{
		this(aContent,aParent, new Point((int)aPosition.getX(),(int)aPosition.getY()));
	}
	
	
	
	public JComponent getOwner()
	{
		return itsParent;
	}
	
	protected Rectangle getPopupBounds ()
	{
		Point theParentPosition = itsParent.getLocationOnScreen();

		int theX = itsPosition.x + theParentPosition.x;
		int theY = itsPosition.y + theParentPosition.y;
		Dimension thePreferredSize = getContent().getPreferredSize();
		int theW = thePreferredSize.width;
		int theH = thePreferredSize.height;
		return new Rectangle (theX, theY, theW, theH);
	}

	public void setPosition(int x, int y) {
		itsPosition.x = x;
		itsPosition.y=y;
		if (isPopupShown()) {
			hide();
			show();
		}
	}
	
	public Point getPosition(){
		return itsPosition;
	}
	
	
	protected JFrame getOwnerFrame()
	{
		return (JFrame) Utils.getFrame(itsParent);
	}

	public void eventDispatched(AWTEvent arg0)
	{
		
		
	}
}