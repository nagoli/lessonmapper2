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
package dcc.lessonMapper2.ui.graph.element.lom;

import java.awt.Color;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.lom.LOMRelation;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import util.Couple;
import util.system.FileManagement;
import dcc.lessonMapper2.LMProject;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.eventHandler.ZoomTemporaryToPickedNode;
import dcc.lessonMapper2.ui.graph.element.CommentNode;
import dcc.lessonMapper2.ui.graph.element.ContainerNode;
import dcc.lessonMapper2.ui.lom.LOMValidityUI;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * this node is a container described by LOM it may contained material or other
 * activities Its LOM description contains the relation hasPart on his part
 * 
 * GenericActivities are xml files containing position information.
 * 
 * @author omotelet
 */

public class GenericActivity extends ContainerNode implements SelectionableLOM {

	/** The Constant ITSActivityFileName. */
	public static final String ITSActivityFileName = "lessonMapperActivity.xml";

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The ITSSAX builder. */
	static SAXBuilder ITSSAXBuilder;

	static JMenuItem ITSPresentationModeItem, ITSQueryModeItem;
	static {
		ITSPresentationModeItem = new JCheckBoxMenuItem(LessonMapper2
				.getInstance().getLangComment("presentationMode"));
		ITSPresentationModeItem.setActionCommand("presentationMode");
		ITSPresentationModeItem.setSelected(LessonMapper2.isPresentationMode);

		ITSQueryModeItem = new JCheckBoxMenuItem(LessonMapper2.getInstance()
				.getLangComment("queryMode"));
		ITSQueryModeItem.setActionCommand("queryMode");
		ITSQueryModeItem.setSelected(LessonMapper2.isQueryMode);

		ActionListener theMenuListener = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getActionCommand().equals("presentationMode"))
					LessonMapper2.isPresentationMode = ITSPresentationModeItem
							.isSelected();
				if (e.getActionCommand().equals("queryMode"))
					LessonMapper2.isQueryMode = ITSQueryModeItem.isSelected();
			}
		};
		ITSPresentationModeItem.addActionListener(theMenuListener);
		ITSQueryModeItem.addActionListener(theMenuListener);

	}

	// public static double ITSTitleScale = 0.8

	/**
	 * Gets the activity from XML.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aProject
	 *            the a project
	 * @param aActivity
	 *            the a activity
	 * 
	 * @return the activity from XML
	 */
	public static GenericActivity getActivityFromXML(Document aActivity,
			LOM aLOM, LMProject aProject, boolean isInit) {
		GenericActivity theActivity;
		try {
			theActivity = (GenericActivity) (Class.forName(
					aActivity.getRootElement().getName()).getConstructor(
					new Class[] { LOM.class })
					.newInstance(new Object[] { aLOM }));

		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		if (theActivity != null) {
			theActivity.setLMProject(aProject);
			// should be call after loading all the loms!
			if (isInit)
				theActivity.buildFromXML(aActivity.getRootElement());
		}
		return theActivity;
	}

	/**
	 * Gets the activity from URL. isInit is true if this method must init the
	 * activity (i.e. load the xml content ) otherwise
	 * theActivity.buldFormXML(theDocument) should be called
	 * 
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aLMProject
	 *            the a LM project
	 * @param aURL
	 *            the a URL
	 * 
	 * @return the document form URL and the activity object
	 */
	public static Couple<Document, GenericActivity> getActivityFromURL(
			String aURL, LOM aLOM, LMProject aLMProject, boolean isInit) {
		Document theActivityXML = null;
		try {
			theActivityXML = getSaxBuilder().build(aURL);
		} catch (JDOMException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (theActivityXML != null) {
			return new Couple<Document, GenericActivity>(
					theActivityXML,
					getActivityFromXML(theActivityXML, aLOM, aLMProject, isInit));
		} else
			return null;
	}

	/** The its selectionable LO mto relation map. */
	protected Map<SelectionableLOM, LOMRelation> itsSelectionableLOMtoRelationMap = new HashMap<SelectionableLOM, LOMRelation>();

	/** The its LOM. */
	LOM itsLOM;

	/** The its location element. */
	Element itsLocationElement;

	/** The its title node. */
	public LOMTitleUI itsTitleNode;

	/** The its validity UI. */
	LOMValidityUI itsValidityUI;

	/** The its LM project. */
	LMProject itsLMProject;

	JPopupMenu itsMenu;

	String itsDefaultMaster;

	/**
	 * The Constructor.
	 */
	public GenericActivity() {
		super();
		init();
		setLOM(new LOM());
	}

	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 */
	public GenericActivity(LOM aLOM) {
		super();
		init();
		setLOM(aLOM);
	}

	public GenericActivity(GenericMaterial aMaterial) {
		super();
		init();
		setLOM(aMaterial.getLOM());
		setOffset(aMaterial.getOffset());
	}

	/**
	 * Init.
	 */
	private void init() {
		itsTitleNode = new LOMTitleUI(this, LessonMapper2.getInstance()
				.getLangComment("notitle"));
		addChildWithoutScaling(itsTitleNode);
		addAlwaysPickableNode(itsTitleNode);

		itsValidityUI = new LOMValidityUI(this);
		addChildWithoutScaling(itsValidityUI);
		itsValidityUI.setPickable(true);
		addAlwaysPickableNode(itsValidityUI);
		addInputEventListener(ZoomTemporaryToPickedNode.ITSInstance);
		// initMenu(); cannot be call because it needs LMProject.getRoot being
		// assigned
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#setLOM(lessonMapper.lom.LOM)
	 */
	public void setLOM(LOM aLOM) {
		itsLOM = aLOM;
		itsTitleNode.setLOM(itsLOM);
		itsValidityUI.setLOM(itsLOM);
		LOMAttribute theLocation = LOMAttribute
				.getLOMAttribute("technical/location");
		itsLocationElement = (Element) theLocation.getNodesIn(itsLOM).get(0);
		itsLocationElement.setText(ITSActivityFileName);
		updateView();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#getLOM()
	 */
	public LOM getLOM() {
		return itsLOM;
	}

	public LOMTitleUI getTitleUI() {
		return itsTitleNode;
	}

	/**
	 * Gets the all children LOM.
	 * 
	 * @return the all children LOM
	 */
	public Set<LOM> getAllChildrenLOM() {
		Set<LOM> theSet = new LinkedHashSet<LOM>();
		theSet.add(getLOM());
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof GenericActivity)
				theSet.addAll(((GenericActivity) element).getAllChildrenLOM());
			if (element instanceof GenericMaterial)
				theSet.add(((GenericMaterial) element).getLOM());
		}
		return theSet;
	}

	/**
	 * return a List of relationTitleUI related to aSelectionableLOM
	 * 
	 * @param aSelectionableLOM
	 * @return
	 */
	public Collection<LOMRelationTitleUI> getVisibleRelationRelatedTo(
			SelectionableLOM aSelectionableLOM) {
		List<LOMRelationTitleUI> theList = new ArrayList<LOMRelationTitleUI>();
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof LOMRelationTitleUI) {
				LOMRelationTitleUI theRelation = (LOMRelationTitleUI) element;
				if (theRelation.concerns(aSelectionableLOM))
					theList.add(theRelation);
			}
		}
		return theList;
	}

	/**
	 * change the association of visible edge form aSelecetionableLOM to
	 * aNewSelectionableLOM
	 * 
	 * @param aSelectionableLOM
	 * @param aNewSelectionableLOM
	 */
	public void changeVisibleRelationFor(SelectionableLOM aSelectionableLOM,
			SelectionableLOM aNewSelectionableLOM) {
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof LOMRelationTitleUI) {
				LOMRelationUI theRelation = ((LOMRelationTitleUI) element)
						.getRelationUI();
				if (theRelation.concerns(aSelectionableLOM))
					theRelation
							.replace(aSelectionableLOM, aNewSelectionableLOM);
			}
		}
	}

	/**
	 * Gets the displayed title.
	 * 
	 * @return the displayed title
	 */
	public String getDisplayedTitle() {
		return itsTitleNode.getText();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#updateView()
	 */
	public void updateView() {
		String theTitle = LOMAttribute.getLOMAttribute("general/title")
				.getValueIn(itsLOM).getValue();
		itsTitleNode.setText(theTitle);

		itsValidityUI.update();
		itsValidityUI.invalidatePaint();
		validateFullPaint();
	}

	/**
	 * Update all view.
	 */
	public void updateAllView() {
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof GenericActivity)
				((GenericActivity) element).updateAllView();
			if (element instanceof GenericMaterial)
				((GenericMaterial) element).updateView();
		}
		updateView();
	}

	/**
	 * Gets the ID.
	 * 
	 * @return the ID
	 */
	public String getID() {
		return itsLOM.getID();
	}

	/**
	 * manage the title size at different zoom level repaint loop is manage by
	 * itsCameraScaleTable which keep a scale value for each camera painting
	 * this component.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	@Override
	public void paint(PPaintContext aPaintContext) {
		super.paint(aPaintContext);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.PNode#fullPaint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	@Override
	public void fullPaint(PPaintContext aPaintContext) {
		super.fullPaint(aPaintContext);
	}

	@Override
	public void processEvent(PInputEvent aEvent, int aType) {
		//System.out.println("hola");
		super.processEvent(aEvent, aType);
		if (!aEvent.isHandled() && aType == MouseEvent.MOUSE_CLICKED
				&& aEvent.isRightMouseButton()) {
			PCanvas theCanvas = LessonMapper2.getInstance().getMainCanvas();
			PCamera theTopCamera = theCanvas.getCamera();
			Point2D theNewLocation = aEvent.getPositionRelativeTo(theTopCamera);
			if (itsMenu == null)
				initMenu();
			itsMenu.show(theCanvas, (int) theNewLocation.getX(),
					(int) theNewLocation.getY());
			aEvent.setHandled(true);
		}
	}

	/**
	 * Inits the menu.
	 */
	protected void initMenu() {
		itsMenu = new JPopupMenu();
		JMenuItem theDefaultMasterItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialDefaultMaster"));
		theDefaultMasterItem.setActionCommand("defaultMaster");
		itsMenu.add(theDefaultMasterItem);

		JMenuItem theModifyMaterialItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuModifyMaterial"));
		theModifyMaterialItem.setActionCommand("modifymaterial");
		itsMenu.add(theModifyMaterialItem);
		JMenuItem theModifyContextItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuModifyContext"));
		theModifyContextItem.setActionCommand("modifycontext");
		itsMenu.add(theModifyContextItem);
		if (LessonMapper2.getInstance().getActiveProject().getUpperActivity() == this) {
			itsMenu.addSeparator();
			itsMenu.add(ITSPresentationModeItem);
			itsMenu.add(ITSQueryModeItem);
		}
		ActionListener theMenuListener = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getActionCommand().equals("defaultMaster"))
					setDefaultMaster();
				if (e.getActionCommand().equals("modifymaterial"))
					modifyMaterial();
				if (e.getActionCommand().equals("modifycontext"))
					modifyContext();

			}
		};

		theModifyMaterialItem.addActionListener(theMenuListener);
		theModifyContextItem.addActionListener(theMenuListener);
		theDefaultMasterItem.addActionListener(theMenuListener);
	}

	/**
	 * Sets the default master.
	 */
	protected void setDefaultMaster() {
		File theFile = LessonMapper2.getInstance().getFileChooser()
				.showAsOpenDialog(
						LessonMapper2.getInstance().getLangComment(
								"loadMaterialDialog"), "*.*");
		if (theFile != null)
			setDefaultMaster(theFile);

	}

	/**
	 * set the default material for the children of this activity return true if
	 * master was successfullyt setted.
	 */
	public boolean setDefaultMaster(File aFile) {
		boolean isCopied = false;
		String theMaterialLocation = aFile.getAbsolutePath();
		String[] theLocationList;
		if (System.getProperty("os.name").startsWith("Win")
				&& !theMaterialLocation.startsWith("http://")) {
			theLocationList = theMaterialLocation.split("\\\\");
		} else
			theLocationList = theMaterialLocation.split("/");
		String theFileName = theLocationList[theLocationList.length - 1];
		String theNewMaterialLocation = itsLMProject.getRepository()
				.getMaterialLocationForID(getLOM().getMaterialID())
				+ theFileName;
		if (!theMaterialLocation.equals(theNewMaterialLocation)) {
			try {
				if (theMaterialLocation.startsWith("file:")
						|| theMaterialLocation.startsWith("http:")
						|| theMaterialLocation.startsWith("ftp:"))
					isCopied = FileManagement.getFileManagement().copy(
							new URL(theMaterialLocation),
							new File(theNewMaterialLocation));
				else if (theFileName.equals(theMaterialLocation))
					isCopied = FileManagement.getFileManagement().copy(aFile,
							new File(theNewMaterialLocation));
				else
					isCopied = FileManagement.getFileManagement().copy(aFile,
							new File(theNewMaterialLocation));
			} catch (Exception e) {
				System.out.println("Error when copying file "
						+ theMaterialLocation);
				// e.printStackTrace();

				isCopied = false;
			}
		}
		if (isCopied)
			itsDefaultMaster = theFileName;
		return isCopied;
	}

	/**
	 * return the default master location for this activity return the master of
	 * its parent if null
	 * 
	 * default master are stored in
	 * projectFolder/masters/activityID/mastername.ppt
	 * 
	 */
	public String getDefaultMaster() {
		return getDefaultMaster(itsLMProject.getRepository().getArchivePath());
	}

	public String getDefaultMaster(String aArchivePath) {
		if (itsDefaultMaster != null)
			return itsLMProject.getRepository().getMaterialLocationForID(
					getLOM().getMaterialID(), aArchivePath)
					+ itsDefaultMaster;
		else if (getParent() instanceof GenericActivity) {
			GenericActivity theParentActivity = (GenericActivity) getParent();
			return theParentActivity.getDefaultMaster();
		}
		return null;
	}

	/**
	 * change the id of the associated material (basically set the current
	 * material as new material and make local copy)
	 */
	public void modifyMaterial() {
		setLOM(new LOM(itsLOM, true, true));
		updateParentRelation();
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof SelectionableLOM) {
				// TODO manage relations !!!!!
				// ((SelectionableLOM) element).detach();
				((SelectionableLOM) element).modifyMaterial();

			}
		}
	}

	private void updateParentRelation() {
		// update relation with parent
		PNode theNode = getParent();
		if (theNode != null && theNode instanceof GenericActivity) {
			((GenericActivity) theNode).associate(this, true);
		}
	}

	/**
	 * change the id of the associated material (basically set the current
	 * material as new material and make local copy)
	 */
	public void modifyContext() {
		setLOM(new LOM(itsLOM, true, false));
		updateParentRelation();
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof SelectionableLOM) {
				// TODO manage relations !!!!!
				// ((SelectionableLOM) element).detach();
				((SelectionableLOM) element).modifyMaterial();

			}
		}
	}

	/*
	 * 
	 * hooked by aspect LOMIConUIPlug for painting icons
	 */
	@Override
	public void paintAfterChildren(PPaintContext aPaintContext) {
		super.paintAfterChildren(aPaintContext);
	}

	/**
	 * override the computation of the union of children in order to exclude the
	 * titleNodes
	 */
	public PBounds getUnionOfChildrenBounds(PBounds dstBounds) {
		if (dstBounds == null) {
			dstBounds = new PBounds();
		} else {
			dstBounds.resetToZero();
		}

		int count = getChildrenCount();
		for (int i = 0; i < count; i++) {
			PNode each = (PNode) getChildrenReference().get(i);
			if (each != itsTitleNode)
				dstBounds.add(each.getFullBoundsReference());
		}

		return dstBounds;
	}

	/**
	 * return an XML specification of the container node children id and positon
	 * as relations id and position are referenced.
	 * 
	 * @return the XML serialization
	 */
	public Element getXMLSerialization() {
		String theNameSpace = LessonMapper2.ITSNameSpace;
		Element theRoot = new Element(getClass().getName(), theNameSpace);
		Element theID = new Element("ID", theNameSpace);
		theID.setText(getID());
		theRoot.addContent(theID);
		if (itsDefaultMaster != null) {
			Element theMasterFile = new Element("MasterFile", theNameSpace);
			theMasterFile.setText(itsDefaultMaster);
			theRoot.addContent(theMasterFile);
		}
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof GenericActivity) {
				GenericActivity theActivity = (GenericActivity) element;
				Element theActivityElement = new Element("Activity",
						theNameSpace);
				Element theIDElement = new Element("ID", theNameSpace);
				theIDElement.setText(theActivity.getID());
				theActivityElement.addContent(theIDElement);
				// id for haspart relation
				Element theRelationIDElement = new Element("HasPartRelationID",
						theNameSpace);
				theRelationIDElement.setText(itsSelectionableLOMtoRelationMap
						.get(theActivity).getID());
				theActivityElement.addContent(theRelationIDElement);
				buildPositionElement(theNameSpace, theActivity, theActivityElement);
				theRoot.addContent(theActivityElement);
			}
			if (element instanceof GenericMaterial) {
				GenericMaterial theMaterial = (GenericMaterial) element;
				Element theMaterialElement = new Element("Material",
						theNameSpace);
				Element theIDElement = new Element("ID", theNameSpace);
				theIDElement.setText(theMaterial.getID());
				theMaterialElement.addContent(theIDElement);
				Element theRelationIDElement = new Element("HasPartRelationID",
						theNameSpace);
				theRelationIDElement.setText(itsSelectionableLOMtoRelationMap
						.get(theMaterial).getID());
				theMaterialElement.addContent(theRelationIDElement);
				buildPositionElement(theNameSpace, theMaterial, theMaterialElement);
				theRoot.addContent(theMaterialElement);
			}
			if (element instanceof LOMRelationUI) {
				LOMRelationUI theRelation = (LOMRelationUI) element;
				Element theRelationElement = new Element("Relation",
						theNameSpace);
				Element theIDElement = new Element("ID", theNameSpace);
				theIDElement.setText(theRelation.getLOMRelation().getID());
				theRelationElement.addContent(theIDElement);
				Element theSourceIDElement = new Element("SourceID",
						theNameSpace);
				theSourceIDElement.setText(theRelation.getLOMRelation()
						.getSourceLOMId());
				theRelationElement.addContent(theSourceIDElement);
				buildPositionElement(theNameSpace, theRelation.getRelationTitleUI(), theRelationElement);
				theRoot.addContent(theRelationElement);
			}
			if (element instanceof CommentNode) {
				CommentNode theCommentNode = (CommentNode) element;
				Element theCommentElement = new Element("CommentNode",
						theNameSpace);
				Element theTextElement = new Element("Text", theNameSpace);
				theTextElement.setText(theCommentNode.getText());
				theCommentElement.addContent(theTextElement);
				Element theWidthElement = new Element("Width",
						theNameSpace);
				theWidthElement.setText(""+theCommentNode.getWidth());
				theCommentElement.addContent(theWidthElement);
				buildPositionElement(theNameSpace, theCommentNode,
						theCommentElement);
				theRoot.addContent(theCommentElement);
			}
			
			
		}
		return theRoot;
	}

	private void buildPositionElement(String theNameSpace,
			PNode theNode, Element theElement) {
		Element thePositionElement = new Element("Position",
				theNameSpace);
		Element thePositionX = new Element("X", theNameSpace);
		Element thePositionY = new Element("Y", theNameSpace);
		thePositionX.setText(""
				+ theNode.getOffset().getX());
		thePositionY.setText(""
				+ theNode.getOffset().getY());
		thePositionElement.addContent(thePositionX);
		thePositionElement.addContent(thePositionY);
		theElement.addContent(thePositionElement);
	}

	/**
	 * build the chidren of this container as specified in the XML document
	 * passed as parameter regiserted relations are also displayed.
	 * 
	 * @param aRoot
	 *            the a root
	 */
	public void buildFromXML(Element aRoot) {
		Namespace theNameSpace = Namespace
				.getNamespace(LessonMapper2.ITSNameSpace);
		Map<String, SelectionableLOM> theLOMNodes = new HashMap<String, SelectionableLOM>();

		// build lom
		List theActivityElements = aRoot.getChildren("Activity", theNameSpace);
		List<Couple<Element, GenericActivity>> theActivitiess = new ArrayList<Couple<Element, GenericActivity>>();
		List<Couple<Document, GenericActivity>> theActivitiesToInit = new ArrayList<Couple<Document, GenericActivity>>();
		for (Iterator iter = theActivityElements.iterator(); iter.hasNext();) {
			Element theActivity = (Element) iter.next();
			String theActivityID = theActivity.getChildTextTrim("ID",
					theNameSpace);
			LOM theActivityLOM = itsLMProject.getRepository().loadLOM(
					theActivityID);
			String theActivityLocation = itsLMProject.getRepository()
					.getMaterialLocationForID(theActivityLOM.getMaterialID())
					+ GenericActivity.ITSActivityFileName;
			Couple<Document, GenericActivity> theDocActivityCouple = getActivityFromURL(
					theActivityLocation, theActivityLOM, getLMProject(), false);
			if (theDocActivityCouple != null) {
				theActivitiesToInit.add(theDocActivityCouple);
				theActivitiess.add(new Couple<Element, GenericActivity>(
						theActivity, theDocActivityCouple.getRightElement()));
			}
		}
		List theMaterialElements = aRoot.getChildren("Material", theNameSpace);
		List<Couple<Element, GenericMaterial>> theMaterialss = new ArrayList<Couple<Element, GenericMaterial>>();
		for (Iterator iter = theMaterialElements.iterator(); iter.hasNext();) {
			Element theMaterial = (Element) iter.next();
			String theMaterialID = theMaterial.getChildTextTrim("ID",
					theNameSpace);
			LOM theMaterialLOM = itsLMProject.getRepository().loadLOM(
					theMaterialID);
			if (theMaterialLOM != null)
				theMaterialss.add(new Couple<Element, GenericMaterial>(
						theMaterial, new GenericMaterial(theMaterialLOM,
								getLMProject())));
		}

		// then build the nested activities
		for (Couple<Document, GenericActivity> theDocActivityCouple : theActivitiesToInit) {
			theDocActivityCouple.getRightElement().buildFromXML(
					theDocActivityCouple.getLeftElement().getRootElement());
		}

		// build relations
		for (Couple<Element, GenericActivity> theCouple : theActivitiess) {
			// register the associated "hasChild" Relation
			String theHasPartRelationID = theCouple.getLeftElement()
					.getChildTextTrim("HasPartRelationID", theNameSpace);
			LOMRelation theLOMRelation = LOMRelation.getRelation(
					theHasPartRelationID, getLOM());
			if (theLOMRelation == null) {
				theLOMRelation = new LOMRelation(getLOM(), theCouple
						.getRightElement().getLOM());
				theLOMRelation.setLOMRelationType("hasPart");
				System.out.println("Relation hasPart built between "
						+ getLOM().getValue("general/title")
						+ " and "
						+ theCouple.getRightElement().getLOM().getValue(
								"general/title"));
			}
			// rebuild haspart/ispart relation for overpassing
			// previous consistency prblm
			else
				theLOMRelation.setLOMRelationType("hasPart");
			itsSelectionableLOMtoRelationMap.put(theCouple.getRightElement(),
					theLOMRelation);
			addChild(theCouple.getRightElement());
			theLOMNodes.put(theCouple.getRightElement().getID(), theCouple
					.getRightElement());
			setPosition(theCouple.getRightElement(),
					theCouple.getLeftElement(), theNameSpace);
		}
		for (Couple<Element, GenericMaterial> theCouple : theMaterialss) {
			// register the associated "hasChild" Relation
			String theHasPartRelationID = theCouple.getLeftElement()
					.getChildTextTrim("HasPartRelationID", theNameSpace);
			LOMRelation theLOMRelation = LOMRelation.getRelation(
					theHasPartRelationID, getLOM());
			if (theLOMRelation == null) {
				theLOMRelation = new LOMRelation(getLOM(), theCouple
						.getRightElement().getLOM());
				theLOMRelation.setLOMRelationType("hasPart");
				System.out.println("Relation hasPart built between "
						+ getLOM().getValue("general/title")
						+ " and "
						+ theCouple.getRightElement().getLOM().getValue(
								"general/title"));
				
			}
			// rebuild haspart/ispart relation for overpassing
			// previous consistency prblm
			else
				theLOMRelation.setLOMRelationType("hasPart");
			itsSelectionableLOMtoRelationMap.put(theCouple.getRightElement(),
					theLOMRelation);
			addChild(theCouple.getRightElement());
			theLOMNodes.put(theCouple.getRightElement().getID(), theCouple
					.getRightElement());
			setPosition(theCouple.getRightElement(),
					theCouple.getLeftElement(), theNameSpace);
		}
		List theRelations = aRoot.getChildren("Relation", theNameSpace);
		for (Iterator iter = theRelations.iterator(); iter.hasNext();) {
			Element theRelation = (Element) iter.next();
			String theRelationID = theRelation.getChildTextTrim("ID",
					theNameSpace);
			String theRelationSourceID = theRelation.getChildTextTrim(
					"SourceID", theNameSpace);
			LOM theSourceLOM = LOM.getLOM(theRelationSourceID);
			LOMRelation theLOMRelation = LOMRelation.getRelation(theRelationID,
					theSourceLOM);
			if (theLOMRelation != null) {
				SelectionableLOM theSource = theLOMNodes
						.get(theRelationSourceID);
				SelectionableLOM theDestination = theLOMNodes
						.get(theLOMRelation.getTargetLOMId());
				if (theSource != null && theDestination != null) {
					LOMRelationUI theUI = new LOMRelationUI(theSource,
							theDestination, theLOMRelation);
					addChild(theUI);
					theUI.init();
					setPosition(theUI.getMotionNode(), theRelation,
							theNameSpace);
				}

			}
		}
		Element theMaster = aRoot.getChild("MasterFile", theNameSpace);
		if (theMaster != null) {
			itsDefaultMaster = theMaster.getTextTrim();
		}
		
		List theCommentNodes = aRoot.getChildren("CommentNode", theNameSpace);
		for (Iterator iter = theCommentNodes.iterator(); iter.hasNext();) {
			Element theComment = (Element) iter.next();
			CommentNode theCommentNode = new CommentNode();
			theCommentNode.setText(theComment.getChildTextTrim("Text",theNameSpace));
			setPosition(theCommentNode, theComment, theNameSpace);
			double theWidth = Double.parseDouble(theComment.getChildTextTrim("Width",
					theNameSpace));
			theCommentNode.setWidth(theWidth);
			addChild(theCommentNode);
		}
		

	}

	/**
	 * Sets the position.
	 * 
	 * @param theNameSpace
	 *            the name space
	 * @param theNode
	 *            the node
	 * @param theElement
	 *            the element
	 */
	private void setPosition(PNode theNode, Element theElement,
			Namespace theNameSpace) {
		Element thePosition = theElement.getChild("Position", theNameSpace);
		double theX = Double.parseDouble(thePosition.getChildTextTrim("X",
				theNameSpace));
		double theY = Double.parseDouble(thePosition.getChildTextTrim("Y",
				theNameSpace));
		theNode.setOffset(theX, theY);
	}

	/**
	 * Gets the sax builder.
	 * 
	 * @return the sax builder
	 */
	public static SAXBuilder getSaxBuilder() {
		if (ITSSAXBuilder == null) {
			ITSSAXBuilder = new SAXBuilder();
			ITSSAXBuilder.setValidation(false);
			ITSSAXBuilder.setIgnoringElementContentWhitespace(true);
		}
		return ITSSAXBuilder;
	}

	/**
	 * Gets the file location.
	 * 
	 * @return the file location
	 */
	public String getFileLocation() {
		return itsLMProject.getRepository().getMaterialLocationForID(
				getLOM().getMaterialID())
				+ itsLocationElement.getTextTrim();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#getMaterialURL()
	 */
	public String getMaterialURL() {
		return getFileLocation();
	}

	/**
	 * consider to build "hasPart" relation between this container and this
	 * material if it is not already done.
	 * 
	 * @param aChild
	 *            the a child
	 * @param aIndex
	 *            the a index
	 */
	@Override
	public void addChild(int aIndex, PNode aChild) {
		super.addChild(aIndex, aChild);
		if (aChild instanceof SelectionableLOM) {
			SelectionableLOM theLOMNode = (SelectionableLOM) aChild;
			if (theLOMNode.getLMProject() != getLMProject())
				theLOMNode.setLMProject(getLMProject());
			associate(theLOMNode, false);
		}
		if (aChild != itsTitleNode && itsTitleNode != null) {
			// let the title node on the top (movetofront is not used since we
			// do not want title scaling
			removeChild(itsTitleNode);
			addChildWithoutScaling(itsTitleNode);
		}
	}

	/**
	 * associate a selectionableLOM as child of this graph by creating the
	 * corresponding hasPart relation if isRmoveExistingRelation remove it else
	 * do nothing if the relation exists.
	 */
	public void associate(SelectionableLOM aLOMNode,
			boolean isRemoveExistingRelation) {
		if (itsSelectionableLOMtoRelationMap.containsKey(aLOMNode))
			if (isRemoveExistingRelation) {
				LOMRelation theRelation = itsSelectionableLOMtoRelationMap
						.get(aLOMNode);
				theRelation.detach();
			} else
				return;
		LOMRelation theRelation = new LOMRelation(getLOM(), aLOMNode.getLOM());
		theRelation.setLOMRelationType("hasPart");
		itsSelectionableLOMtoRelationMap.put(aLOMNode, theRelation);
	}

	/**
	 * this method is called when a child call the removefromparent method
	 * 
	 * manage the list of SelectionableLOM with relations.
	 * 
	 * @param aIndex
	 *            the a index
	 * 
	 * @return the p node
	 */
	@Override
	public PNode removeChild(int aIndex) {
		PNode thePNode = super.removeChild(aIndex);
		if (thePNode instanceof SelectionableLOM) {
			// remove the haspart relation
			LOMRelation theLOMRelation = itsSelectionableLOMtoRelationMap
					.get(thePNode);
			if (theLOMRelation != null)
				theLOMRelation.detach();
			// check the current relation UI and remove those related with
			// theSelectionableLOM
			Set<LOMRelationTitleUI> theRelationsToRemove = new HashSet<LOMRelationTitleUI>();
			for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
				PNode element = (PNode) iter.next();
				if (element instanceof LOMRelationTitleUI) {
					LOMRelationTitleUI theRelationTitleUI = (LOMRelationTitleUI) element;
					if (theRelationTitleUI
							.concerns((SelectionableLOM) thePNode)) {
						theRelationsToRemove.add(theRelationTitleUI);
					}
				}
			}
			// relation are removed out of the loop to avoid concurrent access
			// to childrenIterator
			for (LOMRelationTitleUI theTitleUI : theRelationsToRemove) {
				theTitleUI.detach();
			}
		}
		return thePNode;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#setTitleColor(java.awt.Color)
	 */
	public void setTitleColor(Color aColor) {
		// itsTitleNode.setTextPaint(aColor);
	}

	/**
	 * this method is overriden so that the returned image is not transparent.
	 * 
	 * @return the image
	 */
	public Image toImage() {
		PBounds imageBounds = getFullBounds();
		parentToLocal(imageBounds);
		GraphicsConfiguration graphicsConfiguration = GraphicsEnvironment
				.getLocalGraphicsEnvironment().getDefaultScreenDevice()
				.getDefaultConfiguration();
		BufferedImage result = graphicsConfiguration.createCompatibleImage(
				(int) imageBounds.width, (int) imageBounds.height,
				Transparency.OPAQUE);
		return toImage(result, null);
	}

	/*
	 * @Override public void processEvent(PInputEvent aEvent, int aType) {
	 * 
	 * if (!isActive) { if (aEvent.getClickCount() == 1){ System.out.println(new
	 * XMLOutputter().outputString(getXMLDocument())); } }
	 * 
	 * super.processEvent(aEvent, aType); }
	 */
	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.ContainerNode#getHelp()
	 */
	@Override
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("genericActivity");
	}

	/**
	 * set a project associated with this activity and set it to all
	 * SelectionableLOM children.
	 * 
	 * @param aProject
	 *            the LM project
	 */
	public void setLMProject(LMProject aProject) {

		if (itsLMProject != null) {
			String theMaterial;
			if (aProject != null && (aProject.isArchivePathChanging()))
				theMaterial = getDefaultMaster(aProject.getOldArchivePath());
			else
				theMaterial = getDefaultMaster();
			itsLMProject = aProject;
			if (theMaterial != null)
				setDefaultMaster(new File(theMaterial));
		} else
			itsLMProject = aProject;
		for (Iterator iter = getChildrenIterator(); iter.hasNext();) {
			PNode element = (PNode) iter.next();
			if (element instanceof SelectionableLOM) {
				((SelectionableLOM) element).setLMProject(itsLMProject);
			}
		}
	}

	/**
	 * Gets the LM project.
	 * 
	 * @return the LM project
	 */
	public LMProject getLMProject() {
		return itsLMProject;
	}

	public String getMaterialName() {
		return ITSActivityFileName; //removed toLowerCase
	}

}
