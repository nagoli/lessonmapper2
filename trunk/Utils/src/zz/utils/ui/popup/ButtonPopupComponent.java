/**
 * Created by IntelliJ IDEA.
 * User: Guillaume
 * Date: 26 mars 2003
 * Time: 02:36:54
 */
package zz.utils.ui.popup;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;

import zz.utils.Utils;


/**
 * A popup componenet that automatically handles click on a button to toggle the popup.
 */
public class ButtonPopupComponent extends PopupComponent implements ActionListener
{
	public ButtonPopupComponent (JComponent popup, String aTitle)
	{
		this (popup, aTitle, null);
	}
	
	public ButtonPopupComponent (JComponent popup, String aTitle, Icon aIcon)
	{
		this (popup, new JButton(aTitle, aIcon));
	}
	
	public ButtonPopupComponent (JComponent popup, JButton aButton)
	{
		super (popup, aButton);
		aButton.setMargin(Utils.NULL_INSETS);
		aButton.addActionListener(this);
	}

	public ButtonPopupComponent (JButton aButton)
	{
		super (null, aButton);
		aButton.addActionListener (this);
	}

	public void actionPerformed (ActionEvent e)
	{
		togglePopup();
	}
}
