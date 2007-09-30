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
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.List;
import java.util.Vector;

import javax.swing.BoxLayout;
import javax.swing.CellRendererPane;
import javax.swing.JComboBox;
import javax.swing.JList;
import javax.swing.TransferHandler;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMVocabulary;
import lessonMapper.lom.LOMVocabularyElement;

import org.jdom.Element;

import dcc.lessonMapper2.ui.SuggestionCloudTest;

import util.ui.ImageScaling;

/**
 * The Class LOMAttributeComboUI.
 */
public class LOMAttributeComboUI extends LOMAttributeUI implements
		ActionListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The its LOM vocabulary. */
	LOMVocabulary itsLOMVocabulary;

	/** The its node. */
	Element itsNode;

	/** The its combo box. */
	LOMComboBox itsComboBox;

	/** The its element. */
	LOMVocabularyElement itsElement;

	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aLOMAttribute
	 *            the a LOM attribute
	 * @param aLOMVocabulary
	 *            the a LOM vocabulary
	 */
	public LOMAttributeComboUI(LOM aLOM, LOMAttribute aLOMAttribute,
			LOMVocabulary aLOMVocabulary) {
		super();
		itsComboBox = new LOMComboBox(aLOMVocabulary
				.getVocabularyOrderedVector());
		itsComboBox.getComponent(0).addMouseListener(new MouseListener() {
			public void mouseClicked(MouseEvent aE) {
				
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
		itsComboBox.addActionListener(this);
		itsLOMAttribute = aLOMAttribute;
		itsLOM = aLOM;
		itsLOMVocabulary = aLOMVocabulary;
		List theList = itsLOMAttribute.getNodesIn(itsLOM);
		itsNode = (Element) theList.get(0);

		setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
		add(itsComboBox);
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
		itsElement = itsLOMVocabulary.getVocabularyElement(itsNode.getText());
		itsComboBox.setSelectedItem(itsElement);
		setModified(false);
		return true;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.lom.LOMAttributeUI#saveChanges()
	 */
	public boolean saveChanges() {
		itsElement = (LOMVocabularyElement) itsComboBox.getSelectedItem();
		itsNode.setText((itsElement).getName());
		setModified(false);
		return true;
	}

	/**
	 * define the state of isChanged when action was performed on comboBox.
	 * 
	 * @param aE
	 *            the a e
	 */
	public void actionPerformed(ActionEvent aE) {
		setModified(!itsElement.equals(itsComboBox.getSelectedItem()));
		saveChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.lom.LOMAttributeUI#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color aBg) {
		super.setBackground(aBg);
		if (itsComboBox != null)
			itsComboBox.setBackground(aBg);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setFont(java.awt.Font)
	 */
	@Override
	public void setFont(Font aFont) {
		if (itsComboBox != null)
			itsComboBox.setFont(aFont);
		super.setFont(aFont);
	}

	/**
	 * this implementation of JComboBox authorized a special type of drag and
	 * drop based on the property "LOMValue".
	 * 
	 * @author omotelet
	 */
	protected class LOMComboBox extends JComboBox {

		/**
		 * The Constructor.
		 * 
		 * @param items
		 *            the items
		 */
		public LOMComboBox(Vector<LOMVocabularyElement> items) {
			super(items);
			setTransferHandler(new TransferHandler("lomvalue"));
			Component[] theComps = getComponents();
			for (int i = 0; i < theComps.length; i++) {
				Component theComponent = theComps[i];
				if (!(theComponent instanceof CellRendererPane)) {
					theComponent.addMouseListener(new MouseAdapter() {
						public void mousePressed(MouseEvent e) {
							getTransferHandler().exportAsDrag(LOMComboBox.this,
									e, TransferHandler.COPY);
						}
					});
				}
			}
			setRenderer(new BasicComboBoxRenderer() {
				public Component getListCellRendererComponent(JList list,
						Object value, int index, boolean isSelected,
						boolean cellHasFocus) {
					// Get the selected index. (The index param isn't
					// always valid, so just use the value.)
					// theLabel.setHorizontalAlignment(JLabel.CENTER);
					// theLabel.setVerticalAlignment(JLabel.CENTER);

					if (isSelected) {
						setBackground(list.getSelectionBackground());
						setForeground(list.getSelectionForeground());
					} else {
						setBackground(list.getBackground());
						setForeground(list.getForeground());
					}
					LOMVocabularyElement theElement = (LOMVocabularyElement) value;
					setIcon(ImageScaling.getInstance().scaleImageIconUpTo(
							theElement.getIcon(), 25, 40));
					setText(theElement.getLabel());
					setFont(list.getFont());

					return this;
				}
			});
			// addMouseListener(new DragMouseAdapter());
		}

		/**
		 * return the selected element as a String.
		 * 
		 * @return the lomvalue
		 */
		public String getLomvalue() {
			LOMVocabularyElement theElement = (LOMVocabularyElement) getSelectedItem();
			return theElement.getName();
		}

		/**
		 * set the selection to the vocabulary element corresponding to the
		 * parameter aString if not regular element do nothing.
		 * 
		 * @param aString
		 *            the lomvalue
		 */
		public void setLomvalue(String aString) {
			LOMVocabularyElement theElement = itsLOMVocabulary
					.getVocabularyElement(aString);
			// if (theElement.getName().equals("")) System.out.println(aString
			// +" is not regular vocabulary for "+itsLOMAttribute.getName());
			// else
			setSelectedItem(theElement);
			if (SuggestionCloudTest.ISACTIVE) {
				SuggestionCloudTest.getInstance().endDragEvent(itsLOMAttribute);
			}
		}

	}

}
