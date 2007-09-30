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
package dcc.lessonMapper2.ui.lom;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.rmi.dgc.VMID;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.TransferHandler;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;

import org.jdom.Element;

import dcc.lessonMapper2.UndoManager;
import dcc.lessonMapper2.UndoableCommand;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.SuggestionCloudTest;

/**
 * LOMAttributeTextUI is associated to a LOM object and aLomAttribute. It
 * display the value of aLOMAttribute for aLOM. If applied, changes done to the
 * attribute are propagated to the LOM object.
 * 
 * @author omotelet
 */

public class LOMAttributeTextUI extends LOMAttributeUI implements
		DocumentListener {

	/** The its node. */
	protected Element itsNode;

	/** The its text field. */
	protected LOMTextField itsTextField;

	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aLOMAttribute
	 *            the a LOM attribute
	 */
	public LOMAttributeTextUI(LOM aLOM, LOMAttribute aLOMAttribute) {
		super();
		itsTextField = new LOMTextField();
		itsTextField.addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent aE) {
				//showDiffusionResults();
			}

			public void mouseEntered(MouseEvent aE) {
				showDiffusionResults();
			}

			public void mouseExited(MouseEvent aE) {
			}

			public void mousePressed(MouseEvent aE) {
			}

			public void mouseReleased(MouseEvent aE) {
			}
		});
		itsLOM = aLOM;
		itsLOMAttribute = aLOMAttribute;
		itsNode = (Element) itsLOMAttribute.getNodesIn(itsLOM).get(0);
		itsTextField.getDocument().addDocumentListener(this);
		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

		add(itsTextField);
		//add(itsApplyButton);
		//add(itsCancelButton);
		//add(itsDiffusionButton);
		cancel();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.lom.LOMAttributeUI#cancel()
	 */
	public boolean cancel() {
		itsTextField.setText(itsNode.getText());
		setModified(false);
		return true;
	}

	public LOMTextField getTextField() {
		return itsTextField;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.lom.LOMAttributeUI#saveChanges()
	 */
	public boolean saveChanges() {
		final String theOldValue = itsNode.getText(), theNewValue = itsTextField.getText();
		UndoManager.execute(new UndoableCommand(){
			public void proceed() {
				itsNode.setText(theNewValue);
			}
			public void undo() {
				itsNode.setText(theOldValue);
				itsTextField.setText(theOldValue);
			}
			public void redo() {
				itsNode.setText(theNewValue);
				itsTextField.setText(theNewValue);
			}
		});
		System.out.println("call");
		setModified(false);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.lom.LOMAttributeUI#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color aBg) {
		super.setBackground(aBg);
		if (itsTextField != null)
			itsTextField.setBackground(aBg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font aFont) {
		if (itsTextField != null)
			itsTextField.setFont(aFont);
		super.setFont(aFont);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
	 */
	public void insertUpdate(DocumentEvent aE) {
		setModified(!itsNode.getText().equals(itsTextField.getText()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
	 */
	public void removeUpdate(DocumentEvent aE) {
		setModified(!itsNode.getText().equals(itsTextField.getText()));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
	 */
	public void changedUpdate(DocumentEvent aE) {

	}

	/**
	 * this implementation of JTextField authorized a special type of drag and
	 * drop based on the property "LOMValue".
	 * 
	 * @author omotelet
	 */
	public class LOMTextField extends JTextArea implements FocusListener{

		/**
		 * The Constructor.
		 */
		public LOMTextField() {
			super();
			setFocusTraversalKeysEnabled(true);
			setTransferHandler(new TransferHandler("lomvalue"));
			setWrapStyleWord(true);
			setLineWrap(true);
			setMaximumSize(new Dimension(
					LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width
						/*	- itsApplyButton.getPreferredSize().width
							- itsCancelButton.getPreferredSize().width
							- itsDiffusionButton.getPreferredSize().width*/ - 10
					// insets of the border of the attribute panel
					, 300));
			addFocusListener(this);
		//	System.out.println("ok");
		}

		
		/**
		 * return the selected text or the whole text if nothing is selected.
		 * 
		 * @return the lomvalue
		 */
		public String getLomvalue() {
			String theSelection = getSelectedText();
			if (theSelection != null && !theSelection.equals(""))
				return theSelection;
			else
				return getText();
		}

		/**
		 * add aString to the current value. Add a coma (',') if there is
		 * already an exisitin value
		 * 
		 * @param aString
		 *            the lomvalue
		 */
		public void setLomvalue(String aString) {
			String theCurrentText = getText();
			if (theCurrentText == null || "".equals(theCurrentText))
				setText(aString);
			else
				setText(theCurrentText + ", " + aString);
			if (SuggestionCloudTest.ISACTIVE) {
				SuggestionCloudTest.getInstance().endDragEvent(itsLOMAttribute);
			}
		}


		public void focusGained(FocusEvent aE) {
		
		}


		public void focusLost(FocusEvent aE) {
			//	System.out.println("lost");
			saveChanges();
		}

	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {
		LOM theL1 = new LOM(LOM.class
				.getResource("resources/test/TestLOM1.xml"));

		JFrame theFrame = new JFrame();
		theFrame.setSize(200, 200);

		theFrame.getContentPane().add(
				new LOMAttributeTextUI(theL1, LOMAttribute
						.getLOMAttribute("general/keyword")));
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.pack();
		theFrame.setVisible(true);
		System.out.println(new VMID().toString().trim());
		System.out.println(new VMID().toString().trim());
	}

}
