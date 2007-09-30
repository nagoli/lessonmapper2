package dcc.lessonMapper2.ui.query;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.query.LOMQuery;
import lor.LOR;

import org.jdom.Element;
import org.jdom.Document;

import util.system.FileManagement;
import util.ui.ImageScaling;
import util.ui.StyledLabel;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import util.Couple;



public class ResultPane extends JFrame {

	GenericMaterial itsNode;

	Vector<LOM> itsLOMs;

	JList itsList;

	Box itsMainBox, itsButtonBox;

	Map<LOM, Double> itsScoreMap = new HashMap<LOM, Double>();

	//int itsMaximumScore;
	boolean isNodeTemporary;
	LOMQuery itsQuery;
	String itsXQuery;
	
	/**
	 * create a new frame with the results given in parameter
	 * for a query that was generated for giving a value to aNode
	 *  aNode will not be remove if no result is chosen
	 * @param aNode
	 * @param aResultVector
	 */
	public ResultPane(GenericMaterial aNode, LOMQuery aQuery){
		this(aNode,aQuery,false);
	}
	
/**
 * create a new frame with the results given in parameter
 * for a query that was generated for giving a value to aNode
 * if isNodeTemporary aNode will be remove if no result is chosen
 * @param aNode
 * @param aResultVector
 * @param isNodeTemporary
 */
	public ResultPane(GenericMaterial aNode, LOMQuery aQuery, boolean isNodeTemporary) {
		super();
		setMaximumSize(new Dimension(800,600));
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		this.isNodeTemporary=isNodeTemporary;
		itsMainBox = Box.createHorizontalBox();
		itsNode = aNode;
		itsQuery = aQuery;
		List<String> theResults = itsQuery.getResults();
		//LOMRanking theRanking = new LOMRanking(itsNode.getLOM(),"");
		//itsMaximumScore = theRanking.getMaximumScore();
		itsLOMs = new Vector<LOM>();
		for (String element : theResults) {
			if (!element.equals("")) {
				LOM theLOM = new LOM(element);
				itsLOMs.add(theLOM);
				//itsScoreMap.put(theLOM, 0.0); //todo give the score
			}
		}
		
		//theList is already ordered
		
		//comparator compare the score and define the best score 
		//as the first element of the list
		/*Comparator<LOM> theComparator = new Comparator<LOM>() {
			public int compare(LOM o1, LOM o2) {
				double theScore1 = itsScoreMap.get(o1);
				double theScore2 = itsScoreMap.get(o2);
				if (theScore1 < theScore2)
					return 1;
				if (theScore1 == theScore2)
					return 0;
				else
					return -1;
			};
		};*/
		//Collections.sort(itsLOMs, theComparator);
		initList();
		JTabbedPane theTab = new JTabbedPane();
		theTab.addTab(LessonMapper2.getInstance().getLangComment("results"),new JScrollPane(itsList));
		theTab.addTab(LessonMapper2.getInstance().getLangComment("xquery"), new JScrollPane(new JTextArea(itsXQuery)));
		itsMainBox.add(theTab);
		initButtons();
		itsMainBox.add(itsButtonBox);
		getContentPane().add(itsMainBox);
		pack();
		setSize(800,600);
		setVisible(true);
	}

	public void initList() {

		itsList = new JList(itsLOMs);
		itsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		itsList.setCellRenderer(new ListCellRenderer() {
			Map<LOM,JComponent> itsComponentMap= new HashMap<LOM,JComponent>();
			
			public Component getListCellRendererComponent(JList aList,
					Object aValue, int aIndex, boolean aIsSelected,
					boolean aCellHasFocus) {
				LOM theLOM = (LOM) aValue;
				if (!itsComponentMap.containsKey(theLOM)){
				Box theBaseBox = Box.createHorizontalBox();
				String theMaterialID = LOMAttribute.getLOMAttribute("general/identifier").getValueIn(theLOM).getValue();
				JLabel theImage=null;
				try {
					ImageIcon theIcon= new ImageIcon(new URL(LOR.INSTANCE.getThumbLocationWithMaterialID(theMaterialID)));
					theImage = new JLabel(ImageScaling.getInstance().scaleImageIconUpTo(theIcon,60,60));		
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}
				Box theMainBox = Box.createVerticalBox();
				Box theTitleBox = Box.createHorizontalBox();
				theTitleBox.add(new StyledLabel(LessonMapper2.getInstance().getLangComment("resultTitle"), Font.ITALIC));
				theTitleBox.add(new JLabel(LOMAttribute.getLOMAttribute(
						"general/title").getValueIn(theLOM).getValue()));
				theTitleBox.add(Box.createHorizontalGlue());
				theMainBox.add(theTitleBox);
				Box theKeywordBox = Box.createHorizontalBox();
				theKeywordBox.add(new StyledLabel(LessonMapper2.getInstance().getLangComment("resultKeywords"), Font.ITALIC));
				theKeywordBox.add(new JLabel(LOMAttribute.getLOMAttribute(
						"general/keyword").getValueIn(theLOM).getValue()));
				theKeywordBox.add(Box.createHorizontalGlue());
				theMainBox.add(theKeywordBox);
				Box theDescriptionBox = Box.createHorizontalBox();
				theDescriptionBox.add(new StyledLabel(LessonMapper2.getInstance().getLangComment("resultDescription"),
						Font.ITALIC));
				theDescriptionBox.add(new JTextArea(LOMAttribute
						.getLOMAttribute("general/description").getValueIn(theLOM)
						.getValue()));
				theDescriptionBox.add(Box.createHorizontalGlue());
				theMainBox.add(theDescriptionBox);
				//theMainBox.add(new StyledLabel(LessonMapper2.getInstance().getLangComment("resultScore")
				//		+ itsScoreMap.get(theLOM) /*+ "/" + itsMaximumScore*/));
				if (theImage!= null) theBaseBox.add(theImage);
				theBaseBox.add(theMainBox);
				theBaseBox.setBorder(BorderFactory.createEtchedBorder());
				itsComponentMap.put(theLOM,theBaseBox);
				}
				JComponent theComponent = itsComponentMap.get(theLOM);
				theComponent.setBackground(aIsSelected?Color.DARK_GRAY:Color.LIGHT_GRAY);	
				
				return theComponent;
			}
		});
	}

	public void initButtons() {
		itsButtonBox = Box.createVerticalBox();
		JButton thePreviewButton = new JButton(LessonMapper2.getInstance().getLangComment("viewMaterial"));
		thePreviewButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (!itsList.isSelectionEmpty()) {
					LOM theLOM = (LOM) itsList.getSelectedValue();
					try {
						LOMAttribute theLocation = LOMAttribute
								.getLOMAttribute("technical/location");
						String theMaterialLocation = theLocation.getValueIn(
								theLOM).getValue();
						FileManagement.getFileManagement().openFile(
								theMaterialLocation,false);
					} catch (Exception ex) {
						ex.printStackTrace();
					}
				}
			};
		});
		itsButtonBox.add(thePreviewButton);
		JButton theSelectButton = new JButton(LessonMapper2.getInstance().getLangComment("adoptLOM"));
		theSelectButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (!itsList.isSelectionEmpty()) {
					LOM theLOM = (LOM) itsList.getSelectedValue();
					itsQuery.setChosenResult(theLOM.getID());
					LOMAttribute theLocation = LOMAttribute
					.getLOMAttribute("technical/location");
					String theMaterialLocation = ((Element)theLocation.getNodesIn(
					theLOM).get(0)).getTextTrim();
					//try to build an activity with this node
					//TODO check validity of the lmproject given in paramenter
					Couple<Document,GenericActivity> theActivityCouple = GenericActivity.getActivityFromURL(theMaterialLocation,theLOM, itsNode.getLMProject(),true);
					if (theActivityCouple == null){
						itsNode.setLOM(theLOM);
						itsNode.updateThumb();
					}
					else {
						GenericActivity theActivityNode = theActivityCouple.getRightElement();
						itsNode.getParent().addChild(theActivityNode);
						itsNode.detach();
						theActivityNode.setOffset(itsNode.getOffset());
						//add node
					}
					setVisible(false);
					dispose();
				}
			};
		});
		itsButtonBox.add(theSelectButton);
		JButton theCancelButton = new JButton(LessonMapper2.getInstance().getLangComment("refineQuery"));
		theCancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				itsQuery.releaseQuery();
				if (isNodeTemporary) itsNode.detach();	
				setVisible(false);
				dispose();
			};
		});
		itsButtonBox.add(theCancelButton);
	}

	
	
}
