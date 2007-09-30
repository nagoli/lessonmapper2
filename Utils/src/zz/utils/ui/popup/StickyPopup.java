/*
 * Created on 20-may-2004
 *
 */
package zz.utils.ui.popup;

import java.awt.Rectangle;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;

import zz.utils.Utils;


/**
 * A popup that displays next to a given component.
 */
public class StickyPopup extends AbstractPopup 
{
	private JComponent itsTriggerComponent;

	/**
	 * The direction where to place the popup. Must be one of JLabel.TOP, JLabel.BOTTOM, JLabel.LEFT,
	 * JLabel.RIGHT
	 */
	private int itsPreferredDirection = JLabel.BOTTOM;


	public StickyPopup (JComponent aContent, JComponent aTriggerComponent)
	{
		super (aContent);
		setTriggerComponent(aTriggerComponent);
	}

	
	public JComponent getOwner()
	{
		return itsTriggerComponent;
	}
	
	/**
	 * @param aPreferredDirection Specifies in which direction the popup should open if
	 * there is no space constraint.
	 * One of JLabel.TOP, JLabel.BOTTOM, JLabel.LEFT, JLabel.RIGHT
	 */
	public void setPreferredDirection (int aPreferredDirection)
	{
		itsPreferredDirection = aPreferredDirection;
	}

	public JComponent getTriggerComponent ()
	{
		return itsTriggerComponent;
	}

	public void setTriggerComponent (JComponent aTriggerComponent)
	{
		itsTriggerComponent = aTriggerComponent;
	}


	protected Rectangle getPopupBounds ()
	{
		return PopupUtils.computePopupBounds(getOwnerFrame(), itsTriggerComponent, 
				itsPreferredDirection, getContent().getPreferredSize());
	}

	protected JFrame getOwnerFrame()
	{
		return (JFrame) Utils.getFrame(itsTriggerComponent);
	}

}