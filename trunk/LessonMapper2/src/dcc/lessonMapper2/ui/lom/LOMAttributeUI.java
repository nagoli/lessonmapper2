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
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseWheelEvent;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.TransferHandler;

import lessonMapper.diffusion.LOMRestrictionSet;
import lessonMapper.diffusion.LOMRestrictionValue;
import lessonMapper.diffusion.fixpoint.SugDifValue;
import lessonMapper.diffusion.fixpoint.SugDifValueHolder;
import lessonMapper.diffusion.fixpoint.SugDifValue.SugValue;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMValue;
import lessonMapper.lom.LOMValueSet;
import lessonMapper.lom.LOMValueVocabularySet;
import lessonMapper.lom.LOMVocabulary;
import util.ui.StyledLabel;
import zz.utils.ui.popup.StickyPopup;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.SuggestionCloudTest;
import diffuse.models.res.DownResCMV;
import diffuse.models.res.DownResCMVHolderStorage;
import diffuse.models.res.UpResCMVHolderStorage;
import diffuse.models.sug.SugCMV;
import diffuse.models.sug.SugCMVHolderStorage;
import diffuse.models.res.UpResCMV;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * LOMAttributeUI class should implement the method saveChange and cancel which
 * respectively save the mofication done the the attribute value and cancel the
 * modification done on the attribute value.
 * 
 * @author omotelet
 */
public abstract class LOMAttributeUI extends JPanel {

	/** The ITS minimimum suggestion score for diplaying. */
	public static double ITSMinimimumSuggestionScoreForDiplaying = 0.1;

	/** The Constant ITSApplyIcon. */
	public final static String ITSApplyIcon = "resources/apply.gif";

	/** The Constant ITSCancelIcon. */
	public final static String ITSCancelIcon = "resources/cancel.gif";

	/** The Constant ITSDiffusionIcon. */
	public final static String ITSDiffusionIcon = "resources/magic.gif";

	/** The its LOM. */
	protected LOM itsLOM;

	/** The its LOM attribute. */
	protected LOMAttribute itsLOMAttribute;

	/** The its diffusion button. */
	protected JButton itsCancelButton, itsApplyButton, itsDiffusionButton;

	/** The is modified. */
	protected boolean isModified = true;

	/** The its popup. */
	static protected StickyPopup itsPopup;

	protected boolean isCloud = Math.random() > 0.5;

	private static LOMGlassPane itsGlassPane;

	/**
	 * The Constructor.
	 */
	public LOMAttributeUI() {
		super();
		setBorder(BorderFactory.createLineBorder(LMUI.ITSSandColor, 1));
		itsDiffusionButton = new JButton(new ImageIcon(LOMAttributeUI.class
				.getResource(LOMAttributeUI.ITSDiffusionIcon)));
		itsApplyButton = new JButton(new ImageIcon(LOMAttributeUI.class
				.getResource(LOMAttributeUI.ITSApplyIcon)));
		itsCancelButton = new JButton(new ImageIcon(LOMAttributeUI.class
				.getResource(LOMAttributeUI.ITSCancelIcon)));
		setButtonSizeToIconSize(itsDiffusionButton, 4);
		setButtonSizeToIconSize(itsApplyButton, 4);
		setButtonSizeToIconSize(itsCancelButton, 4);
		itsDiffusionButton.setBackground(null);
		itsApplyButton.setBackground(null);
		itsCancelButton.setBackground(null);
		itsApplyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aE) {
				saveChanges();
			}
		});
		itsCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aE) {
				cancel();
			}
		});
		itsDiffusionButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent aE) {
				if (itsPopup != null && itsPopup.isPopupShown())
					itsPopup.hide();
				showDiffusionResults();
			}
		});
		// setMaximumSize(new Dimension(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width,
		// 500));
	}

	private void setButtonSizeToIconSize(JButton aButton, int inset) {
		aButton.setPreferredSize(new Dimension(aButton.getIcon().getIconWidth()
				+ inset, aButton.getIcon().getIconHeight() + inset));
	}

	/**
	 * return true if canceled properly.
	 * 
	 * @return true, if cancel
	 */
	public abstract boolean cancel();

	/**
	 * return true if saved properly.
	 * 
	 * @return true, if save changes
	 */
	public abstract boolean saveChanges();

	/**
	 * Checks if is modified.
	 * 
	 * @return true, if is modified
	 */
	public boolean isModified() {
		return isModified;
	}

	/**
	 * show the diffusion result in a popup windows next to this component.
	 */
	public void showDiffusionResults() {
		// System.out.println("show diffucsion result is called");
		final JComponent theBox = new JPanel();
		int theSugTokenNb = 0;
		theBox.setLayout(new BoxLayout(theBox, BoxLayout.Y_AXIS));
		theBox.setAlignmentX(0f);
		theBox.setBorder(BorderFactory.createLineBorder(LMUI.ITSDarkSandColor,
				1));

		DownResCMV theDownResCMV = (DownResCMV) DownResCMVHolderStorage
				.getInstance().getHolderFor(itsLOMAttribute).getUpdater(itsLOM)
				.getDiffusionWithoutOriginal();
		UpResCMV theUpResCMV = (UpResCMV) UpResCMVHolderStorage.getInstance()
				.getHolderFor(itsLOMAttribute).getUpdater(itsLOM)
				.getDiffusionWithoutOriginal();

		List<SugCMV> theSugCMVs = SugCMVHolderStorage.getInstance()
				.getCMVCollectionWithoutOriginalFor(itsLOMAttribute, itsLOM);
		// put collection wihout original
		if (!theSugCMVs.isEmpty()) {
			Box theSubBox = Box.createHorizontalBox();
			double theMax, theMin;
			theMax = 1;
			theMin = ITSMinimimumSuggestionScoreForDiplaying;
			int theTokenNb = 0, theMaxToken = theSugCMVs.size();
			for (SugCMV theSugCMV : theSugCMVs) {
				double theVal = theSugCMV.getWeight();
				boolean isNotAllowed;
				isNotAllowed = (theDownResCMV != null && !theDownResCMV
						.comply(theSugCMV.getValue()))
						|| (theUpResCMV != null && !theUpResCMV
								.comply(theSugCMV.getValue()));
				if (theVal < ITSMinimimumSuggestionScoreForDiplaying) {
					theMaxToken--;
					continue;
				}
				// suggestion is shown between 15 and 7 point
				float theDisplaySize = new Float(
						7 + ((15 - 7) * theVal / (theMax - theMin)))
						.floatValue();
				if (SuggestionCloudTest.ISACTIVE) {

					if (!isCloud)
						theDisplaySize = 10f;
				}
				String theLabel = theSugCMV.getValue().getLabel();
				// debug print
				/*
				 * Set<MetadataSet> theSet = theSugCMV.getModifierSet();
				 * theLabel += "("+theVal+","+theSet+")" ;
				 */
				theSubBox.add(new LOMLabel(theLabel, isNotAllowed ? Color.red
						: Color.blue.darker(), theDisplaySize, null));

				theTokenNb++;
				if (theTokenNb < theMaxToken) {
					theSubBox.add(new JLabel(", "));
					if (theTokenNb % 5 == 0) {
						theBox.add(theSubBox);
						theSubBox = Box.createHorizontalBox();
					}
				}

			}
			theSugTokenNb = theTokenNb;
			theBox.add(theSubBox);

		}

		/*
		 * LOMRestrictionSet theRestrictions = (LOMRestrictionSet)
		 * ResDifValueHolder
		 * .getInstance(itsLOMAttribute).getValue(itsLOM).getValue();
		 * addFixPointRestrictions(theBox, theRestrictions, theVocabulary);
		 */

		// add restrictions
		Box theSubBox = Box.createHorizontalBox();
		LOMValue theValue = itsLOMAttribute.getValueIn(itsLOM);
		if (theDownResCMV != null)
			theSubBox
					.add(new StyledLabel(theDownResCMV.prettyPrint(),
							(theDownResCMV.comply(theValue) ? Color.BLACK
									: Color.red)));
		if (theUpResCMV != null)
			theSubBox.add(new StyledLabel(theUpResCMV.prettyPrint(),
					(theUpResCMV.comply(theValue) ? Color.BLACK : Color.red)));
		// debug print
		/*
		 * theSubBox.add(new
		 * StyledLabel("["+(theDownResCMV==null?"nullCMV":(theDownResCMV.getValue()+","+theDownResCMV.getModifierSet()))+
		 * ","+(theUpResCMV==null?"nullCMV":(theUpResCMV.getValue()+","+theUpResCMV.getModifierSet()))+"]",Color.black));
		 */
		theBox.add(theSubBox);

		if (theBox.getComponentCount() == 0) {
			theBox.add(new JLabel(LessonMapper2.getInstance().getLangComment(
					"noHelp1")));
			theBox.add(new JLabel(LessonMapper2.getInstance().getLangComment(
					"noHelp2")));
		}
		if (itsPopup == null) {
			itsPopup = new StickyPopup(theBox, this);
			itsGlassPane = new LOMGlassPane(itsPopup);
			itsPopup.setGlassPane(itsGlassPane);
			itsPopup.setPreferredDirection(JLabel.LEFT);
			itsPopup.setAutoHide(false);
			LessonMapper2.getInstance().getUI().getMainCanvas()
					.addInputEventListener(new PInputEventListener() {
						public void processEvent(PInputEvent aEvent, int aType) {
							if (aType == MouseEvent.MOUSE_PRESSED)
								itsPopup.hide();
						}
					});
		}
		itsGlassPane.itsSugTokenNb = theSugTokenNb;
		itsPopup.setTriggerComponent(this);
		itsPopup.setContent(theBox);
		itsPopup.show();
		itsGlassPane.setVisible(true);

	}

	protected void addFixPointRestrictions(final JComponent theBox,
			LOMRestrictionSet theRestrictions, LOMVocabulary theVocabulary) {
		List<LOMRestrictionValue> theUnsatisfiedRestrictions = theRestrictions
				.restrictionsNotSatisfiedBy(itsLOMAttribute.getValueIn(itsLOM));
		if (theRestrictions != null && !theRestrictions.isEmpty()
		/* && !theUnsatisfiedRestrictions.isEmpty() */) {
			StyledLabel theStyledLabel = new StyledLabel("_______",
					Color.ORANGE.darker(), Font.ITALIC + Font.BOLD);
			// theStyledLabel.setAlignmentX(0f);
			theBox.add(theStyledLabel);
			for (LOMRestrictionValue theRestrictionValue : theRestrictions
					.getLOMRestrictionValues()) {
				boolean isUnsatisfied = theUnsatisfiedRestrictions
						.contains(theRestrictionValue);
				Box theSubBox = Box.createHorizontalBox();
				theSubBox.add(new StyledLabel(""
						+ theRestrictionValue.getOperator() + ": ",
						isUnsatisfied ? Color.RED : Color.ORANGE.darker()));
				StringTokenizer theTokenizer = new StringTokenizer(
						theRestrictionValue.getValue().getValue(),
						LOM.ITSTokenizerLimits);// ., \t\n\r\f
				// make a new line each 4 tokens
				int theTokenNb = 0;
				for (; theTokenizer.hasMoreTokens();) {
					if (theVocabulary.isEmpty())
						theSubBox.add(new LOMLabel(theTokenizer.nextToken()
								.trim(), isUnsatisfied ? Color.RED
								: Color.ORANGE.darker()));
					else {
						String theID = theTokenizer.nextToken().trim();
						String theLabel = theVocabulary.getVocabularyElement(
								theID).toString();
						theSubBox.add(new LOMLabel(theLabel, theID,
								isUnsatisfied ? Color.RED : Color.ORANGE
										.darker(), null));
					}
					theTokenNb++;
					if (theTokenizer.hasMoreTokens()) {
						theSubBox.add(new JLabel(", "));
						if (theTokenNb % 4 == 0) {
							theBox.add(theSubBox);
							theSubBox = Box.createHorizontalBox();
						}
					}
				}
				theBox.add(theSubBox);
			}
		}
	}

	protected int printFixPointSug(final JComponent theBox, int theSugTokenNb,
			LOMRestrictionSet theRestrictions, LOMVocabulary theVocabulary) {
		SugDifValue theSugValue = SugDifValueHolder
				.getInstance(itsLOMAttribute).getValue(itsLOM);

		if (theSugValue != null) {
			Map<String, Double> theScores = ((SugValue) theSugValue.getValue())
					.getMap();
			Box theSubBox = Box.createHorizontalBox();
			double theMax, theMin;
			theMax = 1;
			theMin = ITSMinimimumSuggestionScoreForDiplaying;
			int theTokenNb = 0, theMaxToken = theScores.size();
			for (Entry<String, Double> theEntry : theScores.entrySet()) {
				double theVal = theEntry.getValue();
				boolean isNotAllowed;
				if (itsLOMAttribute.getLOMVocabulary().isEmpty())
					isNotAllowed = theRestrictions != null
							&& !theRestrictions.restrictionsNotSatisfiedBy(
									new LOMValueSet(theEntry.getKey(),
											itsLOMAttribute)).isEmpty();
				else
					isNotAllowed = theRestrictions != null
							&& !theRestrictions
									.restrictionsNotSatisfiedBy(
											new LOMValueVocabularySet(theEntry
													.getKey(), itsLOMAttribute))
									.isEmpty();

				if (theVal < ITSMinimimumSuggestionScoreForDiplaying) {
					theMaxToken--;
					continue;
				}
				// suggestion is shown between 15 and 7 point
				float theDisplaySize = new Float(
						7 + ((15 - 7) * theVal / (theMax - theMin)))
						.floatValue();
				if (SuggestionCloudTest.ISACTIVE) {

					if (!isCloud)
						theDisplaySize = 10f;
				}
				if (theVocabulary.isEmpty())
					theSubBox.add(new LOMLabel(
							theEntry.getKey()/* +"("+theVal+")" */,
							isNotAllowed ? Color.red : Color.BLUE.darker(),
							theDisplaySize, null));
				else {
					String theID = theEntry.getKey();
					String theLabel = theVocabulary.getVocabularyElement(theID)
							.toString();
					theSubBox.add(new LOMLabel(theLabel/* +"("+theVal+")" */,
							theID, isNotAllowed ? Color.red : Color.BLUE
									.darker(), theDisplaySize, null));
				}
				theTokenNb++;
				if (theTokenNb < theMaxToken) {
					theSubBox.add(new JLabel(", "));
					if (theTokenNb % 5 == 0) {
						theBox.add(theSubBox);
						theSubBox = Box.createHorizontalBox();
					}
				}

			}
			theSubBox.add(new LOMLabel("--OLDSUG", Color.black));
			theSugTokenNb = theTokenNb;
			theBox.add(theSubBox);

		}
		return theSugTokenNb;
	}

	// int a=0;
	/**
	 * Sets the modified.
	 * 
	 * @param aIsModified
	 *            the modified
	 */
	public void setModified(boolean aIsModified) {
		// System.out.println(aIsModified+""+a++);
		if (aIsModified != isModified) {
			isModified = aIsModified;
			if (!isModified) {
				itsApplyButton.setEnabled(false);
				itsCancelButton.setEnabled(false);
			} else {
				itsApplyButton.setEnabled(true);
				itsCancelButton.setEnabled(true);
			}
		}
	}

	@Override
	protected void processMouseEvent(MouseEvent aE) {
		super.processMouseEvent(aE);
		processEventForShowingSuggestions(aE);
	}

	public void processEventForShowingSuggestions(MouseEvent aE) {
		if (aE.getID() == MouseEvent.MOUSE_CLICKED) {
			if (itsPopup == null || !itsPopup.isPopupShown())
				showDiffusionResults();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see javax.swing.JComponent#setBackground(java.awt.Color)
	 */
	@Override
	public void setBackground(Color aBg) {
		super.setBackground(aBg);

		if (itsCancelButton != null)
			itsCancelButton.setBackground(aBg);
		if (itsApplyButton != null)
			itsApplyButton.setBackground(aBg);
		if (itsDiffusionButton != null)
			itsDiffusionButton.setBackground(aBg);
	}

	@Override
	public void setToolTipText(String aText) {
		super.setToolTipText(aText);
		//getBorder().setToolTipText(aText);
		/*for (int i = 0; i < getComponentCount(); i++) {
			Component theChild = getComponents()[i];
			if (theChild instanceof JComponent
					&& !(theChild instanceof JButton)) {
				JComponent theJChild = (JComponent) theChild;
				theJChild.setToolTipText(aText);
			}
		}*/
	}

	/**
	 * Gets the LOM.
	 * 
	 * @return the LOM
	 */
	public LOM getLOM() {
		return itsLOM;
	}

	/**
	 * Gets the LOM attribute.
	 * 
	 * @return the LOM attribute
	 */
	public LOMAttribute getLOMAttribute() {
		return itsLOMAttribute;
	}

	/**
	 * 
	 */
	public class LOMGlassPane extends JComponent implements MouseListener {

		protected boolean isEntered = false;
		StickyPopup itsPopup;
		int itsSugTokenNb;

		public LOMGlassPane(StickyPopup aPopup) {
			super();
			itsPopup = aPopup;
			addMouseListener(this);
			setBackground(null);
		}

		@Override
		protected void processMouseEvent(MouseEvent aE) {
			super.processMouseEvent(aE);
			/*
			 * System.out.println("hola"); if (SuggestionCloudTest.ISACTIVE) {
			 * if (aE.getID() == MouseEvent.MOUSE_ENTERED) {// && // !isEntered) { }
			 * if (aE.getID() == MouseEvent.MOUSE_EXITED) {// && //
			 * !getBounds().contains(aE.getPoint())) } } redispatch(aE);
			 */
			redispatch(aE);
		}

		@Override
		protected void processMouseMotionEvent(MouseEvent aE) {
			redispatch(aE);
		}

		@Override
		protected void processMouseWheelEvent(MouseWheelEvent aE) {
			redispatch(aE);
		}

		public void redispatch(MouseEvent aE) {
			Component theComponent = SwingUtilities.getDeepestComponentAt(
					itsPopup.getContent(), aE.getX(), aE.getY());
			if (theComponent != this && theComponent != null)
				theComponent.dispatchEvent(SwingUtilities.convertMouseEvent(
						this, aE, theComponent));
		}

		public void mouseClicked(MouseEvent aE) {
			// TODO Auto-generated method stub

		}

		public void mouseEntered(MouseEvent aE) {
			if (SuggestionCloudTest.ISACTIVE) {
				// System.out.println("enter");
				isEntered = true;
				SuggestionCloudTest.getInstance().takeFocus(itsLOMAttribute,
						itsSugTokenNb);
			}
		}

		public void mouseExited(MouseEvent aE) {
			if (SuggestionCloudTest.ISACTIVE) {
				// System.out.println("exit");
				isEntered = false;
				SuggestionCloudTest.getInstance().looseFocus(itsLOMAttribute,
						isCloud);
			}

		}

		public void mousePressed(MouseEvent aE) {
			// TODO Auto-generated method stub

		}

		public void mouseReleased(MouseEvent aE) {
			// TODO Auto-generated method stub

		}

	}

	/**
	 * this implementation of JLabel authorized a special type of drag and drop
	 * based on the property "LOMValue".
	 * 
	 * @author omotelet
	 */
	public class LOMLabel extends StyledLabel {

		/** The its ID. */
		String itsID;
		LOMGlassPane itsParentToDispachEvent;

		/**
		 * The Constructor.
		 * 
		 * @param aLabel
		 *            the a label
		 * @param aID
		 *            the a ID
		 * @param aColor
		 *            the a color
		 */
		public LOMLabel(String aLabel, String aID, Color aColor,
				LOMGlassPane aParentToDispatchEvent) {
			super(aLabel, aColor);
			itsID = aID;
			itsParentToDispachEvent = aParentToDispatchEvent;
			setTransferHandler(new TransferHandler("lomvalue"));
			addMouseListener(new DragMouseAdapter());
		}

		/**
		 * The Constructor.
		 * 
		 * @param aSize
		 *            the a size
		 * @param aLabel
		 *            the a label
		 * @param aID
		 *            the a ID
		 * @param aColor
		 *            the a color
		 */
		public LOMLabel(String aLabel, String aID, Color aColor, float aSize,
				LOMGlassPane aParentToDispatchEvent) {
			super(aLabel, aColor, aSize);
			itsParentToDispachEvent = aParentToDispatchEvent;
			itsID = aID;
			setTransferHandler(new TransferHandler("lomvalue"));
			addMouseListener(new DragMouseAdapter());
		}

		/**
		 * The Constructor.
		 * 
		 * @param aLabel
		 *            the a label
		 * @param aColor
		 *            the a color
		 */
		public LOMLabel(String aLabel, Color aColor) {
			this(aLabel, aLabel, aColor, null);
		}

		/**
		 * The Constructor.
		 * 
		 * @param aSize
		 *            the a size
		 * @param aLabel
		 *            the a label
		 * @param aColor
		 *            the a color
		 */
		public LOMLabel(String aLabel, Color aColor, float aSize,
				LOMGlassPane aParentToDispatchEvent) {
			this(aLabel, aLabel, aColor, aSize, aParentToDispatchEvent);
		}

		/**
		 * return the selected element as a String.
		 * 
		 * @return the lomvalue
		 */
		public String getLomvalue() {
			// System.out.println("drag is called");
			return itsID;
		}

		/**
		 * set the selection to the vocabulary element corresponding to the
		 * parameter aString if not regular element do nothing.
		 * 
		 * @param aString
		 *            the lomvalue
		 */
		public void setLomvalue(String aString) {
		}

		@Override
		protected void processMouseEvent(MouseEvent aE) {
			/*
			 * itsParentToDispachEvent.processMouseEvent(SwingUtilities
			 * .convertMouseEvent(this, aE, itsParentToDispachEvent));
			 */
			super.processMouseEvent(aE);
		}

	}

	/**
	 * enable to start drag movement from the source component implementing that
	 * component.
	 * 
	 * @author omotelet
	 */
	public class DragMouseAdapter extends MouseAdapter {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.awt.event.MouseAdapter#mousePressed(java.awt.event.MouseEvent)
		 */
		public void mousePressed(MouseEvent e) {
			JComponent c = (JComponent) e.getSource();
			TransferHandler handler = c.getTransferHandler();
			handler.exportAsDrag(c, e, TransferHandler.COPY);
			// System.out.println("Begin drag");
			if (SuggestionCloudTest.ISACTIVE) {
				SuggestionCloudTest.getInstance().beginDragEvent(
						itsLOMAttribute, isCloud);
			}
		}
	}

}
