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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeSelectionModel;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMCategory;
import util.Couple;
import util.ui.DependentWindow;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.SelectionBorderManager;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;

/**
 * This class holds the management of the LOM attributes shown. Its is composed
 * of a tree containing the attribute list and a JWindow containg the selected
 * attributes.
 * 
 * @author omotelet
 */
public class LOMAttributeTree extends JPanel implements TreeSelectionListener,
		ActionListener {

	/** The Constant ITSLOMAttributeList. */
	public static  URL ITSLOMAttributeList = LessonMapper2.class
	.getResource("resources/LOMProfile.xml");
	
	static  Collection<LOMAttribute> ITSAttributeList;
	public static Collection<LOMAttribute> getAttibuteList(){
		if (ITSAttributeList== null){
			ITSAttributeList =  LOMAttribute.getAttributeList(ITSLOMAttributeList);
		}
		 return ITSAttributeList;
	}
	

	public static final ImageIcon ITSCategoryIcon;
	public static final ImageIcon ITSCheckedIcon;
	public static final ImageIcon ITSUnCheckedIcon;
	static {
		URL theURL = LOMAttributeTree.class
				.getResource("resources/categoryIcon.gif");
		ITSCategoryIcon = new ImageIcon(theURL);
		theURL = LOMAttributeTree.class
				.getResource("resources/checkedIcon.gif");
		ITSCheckedIcon = new ImageIcon(theURL);
		theURL = LOMAttributeTree.class
				.getResource("resources/unCheckedIcon.gif");
		ITSUnCheckedIcon = new ImageIcon(theURL);

	}
	/** The Constant ITSDefaultAttributeView. */
	public static final JLabel ITSDefaultAttributeView = new JLabel(
			LessonMapper2.getInstance().getLangComment("treeDefault"));

	public static int ITSHeight = 280;

	/** The its attribute list. */
	protected Collection<LOMAttribute> itsAttributeList;

	/** The its selected attributes. */
	protected Vector<LOMAttribute> itsSelectedAttributes = new Vector<LOMAttribute>();

	/** The its attribute panels. */
	protected Vector<LOMAttributePanel> itsAttributePanels = new Vector<LOMAttributePanel>();

	/** The its tree. */
	protected JTree itsTree;

	/** The its root node. */
	protected TreeNode itsRootNode;

	/** The its button pane. */
	protected Box itsButtonPane;
	protected Box itsControlButtonPane;
	
	
	/** The its attributes pane. */
	protected Box itsAttributesPane;

	/** The its attribute scroll. */
	protected JScrollPane itsAttributeScroll;

	/** The its show attribute button. */
	protected JButton itsCategoryDownButton, itsCategoryUpButton,
			itsSelectionDownButton, itsSelectionUpButton,
			itsShowAttributeButton;
	protected JToggleButton itsShowValidationButton;
	/** The its attribute window. */
	final protected DependentWindow itsAttributeWindow;

	/**
	 * The Constructor.
	 */
	public LOMAttributeTree() {
		super();

		itsAttributeList = getAttibuteList();
		itsRootNode = getRootTreeNode();
		itsTree = new JTree(itsRootNode);
		itsTree.setCellRenderer(new AttributeRenderer());
		itsTree.getSelectionModel().setSelectionMode(
				TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
		itsTree.addTreeSelectionListener(this);
		itsTree.setRootVisible(false);
		itsTree.setExpandsSelectedPaths(true);
		// expand all
		for (int i = 0; i < itsTree.getRowCount(); i++) {
			itsTree.expandRow(i);
		}
		itsTree.setBackground(LMUI.ITSSandColor);
		// putClientProperty("JTree.lineStyle", "Horizontal");
		JScrollPane theTreeScroll = new JScrollPane(itsTree);

		itsAttributesPane = new Box(BoxLayout.Y_AXIS);
		itsAttributesPane.setBackground(null);
		itsAttributesPane.setMaximumSize(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE);

		itsAttributeScroll = new JScrollPane(itsAttributesPane);
		itsAttributeScroll.setBackground(null);
		itsAttributeScroll.getViewport().setBackground(null);
		itsAttributeWindow = new DependentWindow(LessonMapper2.getInstance()
				.getUI(), LessonMapper2.getInstance().getLangComment(
				"attributeWindow"));

		itsAttributeWindow.add(itsAttributeScroll);
		updateAttributePanel();
		/*itsAttributeWindow.setMinimumSize(new Dimension(
				(int) LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.getWidth(), 30));
		itsAttributeWindow.setMaximumSize(new Dimension(
				(int) LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.getWidth(), 500));
		*/
		itsAttributeWindow.setLocation(400, 400);
		
		
		itsControlButtonPane = Box.createHorizontalBox();
		itsCategoryDownButton = new JButton(new ImageIcon(getClass()
				.getResource("resources/Cleft.gif")));
		itsCategoryDownButton.addActionListener(this);
		itsCategoryUpButton = new JButton(new ImageIcon(getClass().getResource(
				"resources/Cright.gif")));
		itsCategoryUpButton.addActionListener(this);
		itsSelectionDownButton = new JButton(new ImageIcon(getClass()
				.getResource("resources/left.gif")));
		itsSelectionDownButton.addActionListener(this);
		itsSelectionUpButton = new JButton(new ImageIcon(getClass()
				.getResource("resources/right.gif")));
		itsSelectionUpButton.addActionListener(this);
		itsControlButtonPane.add(Box.createVerticalGlue());
		itsControlButtonPane.add(itsCategoryDownButton);
		itsControlButtonPane.add(itsSelectionDownButton);
		itsControlButtonPane.add(itsSelectionUpButton);
		itsControlButtonPane.add(itsCategoryUpButton);
		itsControlButtonPane.add(Box.createVerticalGlue());
		
		
		itsButtonPane = Box.createVerticalBox();
		itsShowAttributeButton = new JButton(LessonMapper2.getInstance()
				.getLangComment("showAttribute"));
		itsShowAttributeButton.addActionListener(this);
		itsShowValidationButton = new JToggleButton(LessonMapper2.getInstance()
				.getLangComment("showValidation"));
		itsShowValidationButton.addActionListener(this);
		itsShowValidationButton.setSelected(LessonMapper2.getInstance().isShownValidationState);
		itsButtonPane.add(itsShowAttributeButton);
		itsButtonPane.add(Box.createHorizontalGlue());
		itsButtonPane.add(itsShowValidationButton);
		
		

		// itsButtonPane.setMaximumSize(new Dimension(80,50));

		// Box theTreePane = Box.createVerticalBox();
		itsShowAttributeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
		itsButtonPane.setAlignmentX(Component.CENTER_ALIGNMENT);
		// theTreePane.add(itsShowAttributeButton);
		setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

		add(itsButtonPane);
		add(theTreeScroll);
		add(itsControlButtonPane);
		setPreferredSize(new Dimension(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width,
				ITSHeight));

		// register to be notified of LOM selection Events
		PNotificationCenter.defaultCenter().addListener(this,
				"LOMSelectionChanged",
				SelectionBorderManager.LOM_SELECTION_CHANGED_EVENT,
				SelectionBorderManager.ITSInstance);

	}

	public Box getButtonPane() {
		return itsButtonPane;
	}

	/**
	 * update the LOMPanel with the new Selection of LOM elements.
	 * 
	 * @param aNotification
	 *            the a notification
	 */
	public void LOMSelectionChanged(PNotification aNotification) {
		for (LOMAttributePanel thePanel : itsAttributePanels) {
			Collection<Couple<LOM, Color>> theSelectedLOMMap = SelectionBorderManager.ITSInstance
					.getSelectedLOMMap();
			thePanel.setLOM(theSelectedLOMMap);
			// thePanel.validate();
		}
		
		packAttributeWindows();
	}

	public void packAttributeWindows(){
		if (itsAttributeWindow != null){
			itsAttributeScroll.validate();
			itsAttributeWindow.setSize(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width+10, 
				itsAttributeWindow.getPreferredSize().height>600?600:itsAttributeWindow.getPreferredSize().height);
		}
	}
	
	
	/**
	 * create a node tree with the attribute list.
	 * 
	 * @return the root tree node
	 */
	protected TreeNode getRootTreeNode() {
		MutableTreeNode theRoot = new DefaultMutableTreeNode("LOM Profile");
		HashMap<LOMCategory, MutableTreeNode> theCategoryMap = new HashMap<LOMCategory, MutableTreeNode>();
		for (LOMAttribute theAttribute : itsAttributeList) {
			LOMCategory theCategory = theAttribute.getLOMCategory();
			MutableTreeNode theCategoryNode;
			if (theCategoryMap.containsKey(theCategory))
				theCategoryNode = theCategoryMap.get(theCategory);
			else {
				theCategoryNode = new DefaultMutableTreeNode("<html><i>"
						+ theCategory.getLabel() + "</i></html>");
				theCategoryMap.put(theCategory, theCategoryNode);
				theRoot.insert(theCategoryNode, theRoot.getChildCount());
			}
			theCategoryNode
					.insert(new AttributeTreeNode(theAttribute.getLabel(),
							theAttribute), theCategoryNode.getChildCount());
		}
		return theRoot;
	}

	/**
	 * when the tree selection is changed the displayed attributes are changed.
	 * 
	 * @param aE
	 *            the a e
	 */
	public void valueChanged(TreeSelectionEvent aE) {
		itsSelectedAttributes.clear();
		for (int i = 0; i < itsTree.getSelectionPaths().length; i++) {
			TreePath thePath = itsTree.getSelectionPaths()[i];
			Object theComponent = thePath.getLastPathComponent();
			if (theComponent instanceof AttributeTreeNode)
				itsSelectedAttributes.add(((AttributeTreeNode) theComponent)
						.getAttribute());
		}
		updateAttributePanel();
	}

	/**
	 * Update attribute panel.
	 */
	private void updateAttributePanel() {
		itsAttributesPane.removeAll();
		itsAttributePanels.clear();
		if (itsSelectedAttributes.isEmpty()) {
			itsAttributesPane.add(ITSDefaultAttributeView);
			ITSDefaultAttributeView.setAlignmentX(Component.LEFT_ALIGNMENT);

		} else {
			for (LOMAttribute theAttribute : itsSelectedAttributes) {
				LOMAttributePanel theAttPane = LOMAttributePanel
						.getLOMAttributePanel(theAttribute);
				itsAttributePanels.add(theAttPane);
				itsAttributesPane.add(theAttPane);
				theAttPane.setAlignmentX(Component.LEFT_ALIGNMENT);
				LOMSelectionChanged(null);
			}
		}
		itsAttributesPane.add(Box.createVerticalGlue());
	//	itsAttributesPane.invalidate();
		packAttributeWindows();
	}

	/**
	 * select the next/previous category select the next/previous element.
	 * 
	 * @param aE
	 *            the a e
	 */
	public void actionPerformed(ActionEvent aE) {
		TreePath[] theSelections = itsTree.getSelectionPaths();
		TreePath theFirstSelection = null, theLastSelection = null;
		if (theSelections != null && theSelections.length != 0) {
			theLastSelection = theSelections[theSelections.length - 1];
			theFirstSelection = theSelections[0];
		}
		if (aE.getSource() == itsCategoryUpButton) {
			int theIndex = 0;
			if (theLastSelection != null) {
				theIndex = itsRootNode.getIndex((TreeNode) theLastSelection
						.getPathComponent(1));
				if (++theIndex == itsRootNode.getChildCount())
					theIndex = 0;
			}
			TreePath theNewPath = new TreePath(new Object[] { itsRootNode,
					itsRootNode.getChildAt(theIndex) });
			itsTree.setSelectionPath(theNewPath);
			itsTree.scrollPathToVisible(theNewPath);
		}
		if (aE.getSource() == itsCategoryDownButton) {
			int theIndex = itsRootNode.getChildCount() - 1;
			if (theLastSelection != null) {
				theIndex = itsRootNode.getIndex((TreeNode) theFirstSelection
						.getPathComponent(1));
				if (--theIndex == -1)
					theIndex = itsRootNode.getChildCount() - 1;
			}
			TreePath theNewPath = new TreePath(new Object[] { itsRootNode,
					itsRootNode.getChildAt(theIndex) });
			itsTree.setSelectionPath(theNewPath);
			itsTree.scrollPathToVisible(theNewPath);
		}
		if (aE.getSource() == itsSelectionDownButton) {
			int theFirstIndex = 0, theLastIndex = 0;
			TreeNode theFirstCategory = null, theLastCategory = null;
			TreePath[] theNewSelections;
			TreePath theNewPath;

			if (theSelections != null && theSelections.length != 0) {
				// get the firstindex
				theFirstCategory = (TreeNode) theFirstSelection
						.getPathComponent(1);
				if (theFirstSelection.getPathCount() > 2) {
					theFirstIndex = theFirstCategory
							.getIndex((TreeNode) theFirstSelection
									.getPathComponent(2));
				}
				if (--theFirstIndex < 0) {
					// jump to the last element of the above category
					int theIndex = itsRootNode
							.getIndex((TreeNode) theFirstSelection
									.getPathComponent(1));
					if (--theIndex < 0)
						theIndex = itsRootNode.getChildCount() - 1;
					theFirstCategory = itsRootNode.getChildAt(theIndex);
					theFirstIndex = theFirstCategory.getChildCount() - 1;
				}
				theNewPath = new TreePath(new Object[] { itsRootNode,
						theFirstCategory,
						theFirstCategory.getChildAt(theFirstIndex) });
				// remove the last selection and add the previous to the first
				// one
				int newSelectionLength = theSelections.length;
				theNewSelections = new TreePath[newSelectionLength];
				theNewSelections[0] = theNewPath;
				for (int i = 0; i < theSelections.length - 1; i++) {
					theNewSelections[i + 1] = theSelections[i];
				}
			} else {
				// jump to the last element of the last category
				int theIndex = itsRootNode.getChildCount() - 1;
				theLastCategory = itsRootNode.getChildAt(theIndex);
				theLastIndex = theLastCategory.getChildCount() - 1;
				theNewPath = new TreePath(new Object[] { itsRootNode,
						theLastCategory,
						theLastCategory.getChildAt(theLastIndex) });
				// select this element
				theNewSelections = new TreePath[] { theNewPath };
			}
			itsTree.setSelectionPaths(theNewSelections);
			itsTree.scrollPathToVisible(theNewPath);
		}
		if (aE.getSource() == itsSelectionUpButton) {
			int theLastIndex = 0;
			TreeNode theFirstCategory = null, theLastCategory = null;
			TreePath[] theNewSelections;
			TreePath theNewPath;

			if (theSelections != null && theSelections.length != 0) {
				// get the firstindex
				theLastCategory = (TreeNode) theLastSelection
						.getPathComponent(1);
				if (theLastSelection.getPathCount() > 2) {
					theLastIndex = theLastCategory
							.getIndex((TreeNode) theLastSelection
									.getPathComponent(2));
				}
				if (++theLastIndex > theLastCategory.getChildCount() - 1) {
					// jump to the first element of the next category
					int theIndex = itsRootNode
							.getIndex((TreeNode) theLastSelection
									.getPathComponent(1));
					if (++theIndex > itsRootNode.getChildCount() - 1)
						theIndex = 0;
					theLastCategory = itsRootNode.getChildAt(theIndex);
					theLastIndex = 0;
				}
				theNewPath = new TreePath(new Object[] { itsRootNode,
						theLastCategory,
						theLastCategory.getChildAt(theLastIndex) });
				// remove the last selection and add the previous to the first
				// one
				int newSelectionLength = theSelections.length;
				theNewSelections = new TreePath[newSelectionLength];
				for (int i = 1; i < theSelections.length; i++) {
					theNewSelections[i - 1] = theSelections[i];
				}
				theNewSelections[newSelectionLength - 1] = theNewPath;
			} else {
				// jump to the first element of the first category

				theFirstCategory = itsRootNode.getChildAt(0);
				theNewPath = new TreePath(new Object[] { itsRootNode,
						theFirstCategory, theFirstCategory.getChildAt(0) });
				// select this element
				theNewSelections = new TreePath[] { theNewPath };
			}
			itsTree.setSelectionPaths(theNewSelections);
			itsTree.scrollPathToVisible(theNewPath);
		}
		if (aE.getSource() == itsShowAttributeButton) {
			packAttributeWindows();
			itsAttributeWindow.setVisible(true);
			// if (itsAttributeDialog.is)
		}
		if (aE.getSource() == itsShowAttributeButton) {
			packAttributeWindows();
			itsAttributeWindow.setVisible(true);
			// if (itsAttributeDialog.is)
		}
		if (aE.getSource() == itsShowValidationButton) {
			LessonMapper2.getInstance().isShownValidationState = itsShowValidationButton.isSelected();
			LessonMapper2.getInstance().getActiveLayer().repaint();
		}
		
		
	}

	/**
	 * The Class AttributeTreeNode.
	 */
	protected class AttributeTreeNode extends DefaultMutableTreeNode {

		/** The its attribute. */
		protected LOMAttribute itsAttribute;

		/**
		 * The Constructor.
		 * 
		 * @param aName
		 *            the a name
		 * @param aAttribute
		 *            the a attribute
		 */
		public AttributeTreeNode(String aName, LOMAttribute aAttribute) {
			super(aName);
			itsAttribute = aAttribute;
		}

		/**
		 * Gets the attribute.
		 * 
		 * @return the attribute
		 */
		public LOMAttribute getAttribute() {
			return itsAttribute;
		}

	}

	class AttributeRenderer extends DefaultTreeCellRenderer {

		public AttributeRenderer() {
			super();
			setBackgroundNonSelectionColor(LMUI.ITSSandColor);
			setBackgroundSelectionColor(LMUI.ITSSandColor);
			setLeafIcon(ITSUnCheckedIcon);
			// System.out.println(ITSCategoryIcon);
			setOpenIcon(ITSCategoryIcon);
			setClosedIcon(ITSCategoryIcon);
		}

		public Component getTreeCellRendererComponent(JTree tree, Object value,
				boolean sel, boolean expanded, boolean leaf, int row,
				boolean hasFocus) {
			if (sel)
				setLeafIcon(ITSCheckedIcon);
			else
				setLeafIcon(ITSUnCheckedIcon);
			super.getTreeCellRendererComponent(tree, value, sel, expanded,
					leaf, row, hasFocus);
			return this;
		}

	}

	/*
	 * @Override public void setPreferredSize(Dimension aPreferredSize) {
	 * super.setPreferredSize(aPreferredSize);
	 * itsTree.setSize(aPreferredSize.width,aPreferredSize.height-itsButtonPane.getHeight()); }
	 */

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {
		JFrame theFrame = new JFrame();
		// theFrame.setSize(200,200);
		theFrame.getContentPane().setLayout(
				new BoxLayout(theFrame.getContentPane(), BoxLayout.Y_AXIS));

		theFrame.getContentPane().add(new LOMAttributeTree());
		theFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		theFrame.pack();
		theFrame.setVisible(true);
	}

}
