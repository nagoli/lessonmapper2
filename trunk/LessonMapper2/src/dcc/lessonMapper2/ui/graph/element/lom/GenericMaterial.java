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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FileDialog;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URL;
import java.util.StringTokenizer;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;

import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMAttribute;
import lessonMapper.query.LOMQuery;
import lor.LOR;

import org.jdom.Element;

import util.image.ImageUtil;
import util.system.FileManagement;
import util.system.thumb.ThumbMaker;
import util.ui.ShadowMaker;
import dcc.lessonMapper2.LMProject;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.eventHandler.ZoomTemporaryToPickedNode;
import dcc.lessonMapper2.ui.graph.element.LinkNode;
import dcc.lessonMapper2.ui.lom.LOMValidityUI;
import dcc.lessonMapper2.ui.query.ResultPane;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.nodes.PImage;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * Generic Material references a external didactic multimedia document.
 * 
 * @author omotelet
 */
public class GenericMaterial extends LinkNode implements SelectionableLOM {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The ITS high quality scale. */
	public static double ITSHighQualityScale = 3;

	public static int ITSInsets = 3;

	/** The ITS default width. */
	public static int ITSDefaultWidth = 70;

	/** The ITS default height. */
	public static int ITSDefaultHeight = 55;

	public float ITSStrokeSize = 2;

	public Color ITSStrokeColor = LMUI.ITSDarkSandColor.darker();

	// public static double ITSIconScale = 0.25;

	/** The ITS query icon. */
	public static Image ITSQueryIcon = new ImageIcon(LMUI.class
			.getResource("resources/queryIcon.png")).getImage();

	/** The its semantic density icon. */
	PImage itsThumb, itsLowThumb;

	/** The its file chooser. */
	static FileDialog ITSMacFileChooser;

	/** The its menu. */
	JPopupMenu itsMenu;

	/** The its LOM. */
	LOM itsLOM;

	/** The its location element. */
	Element itsLocationElement;

	/** The its title node. */
	LOMTitleUI itsTitleNode;

	/** The its LM project. */
	LMProject itsLMProject;

	/** The is query. */
	boolean isQuery = true;

	/** The its validity UI. */
	LOMValidityUI itsValidityUI;

	/**
	 * The Constructor.
	 */
	public GenericMaterial() {
		super();
		init();
		setLOM(new LOM());
	}

	/**
	 * The Constructor.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * @param aProject
	 *            the a project
	 */
	public GenericMaterial(LOM aLOM, LMProject aProject) {
		super();
		init();
		setLMProject(aProject);
		setLOM(aLOM);
		if (aProject != null)
			updateThumb();
	}

	/**
	 * make a copy of aMaterial.
	 * 
	 * @param aMaterial
	 *            the a material
	 */
	public GenericMaterial(GenericMaterial aMaterial) {
		super();
		init();
		setLMProject(aMaterial);
		setLOM(aMaterial.getLOM());
		getThumbFromMaterial(aMaterial);
	}

	/**
	 * Init.
	 */
	private void init() {
		setPathToRectangle(0, 0, ITSDefaultWidth * 2, ITSDefaultHeight);

		initThumb();

		itsTitleNode = new LOMTitleUI(this, null);
		addChild(itsTitleNode);
		// itsTitleNode.setWidth(ITSDefaultWidth);

		itsValidityUI = new LOMValidityUI(this);
		addChild(itsValidityUI);
		itsValidityUI.setPickable(true);
		// setPaint(Color.WHITE);
		addInputEventListener(ZoomTemporaryToPickedNode.ITSInstance);

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
		updateView();
	}

	/**
	 * Sets the query shape.
	 */
	public void setQueryShape() {
		/*
		 * Point[] thePoints = new Point[6]; thePoints[0] = new
		 * Point(ITSDefaultWidth, 3); thePoints[1] = new Point(ITSDefaultWidth,
		 * 0); thePoints[2] = new Point(0, 0); thePoints[3] = new Point(0,
		 * ITSDefaultHeight); thePoints[4] = new Point(ITSDefaultWidth,
		 * ITSDefaultHeight); thePoints[5] = new Point(ITSDefaultWidth,
		 * ITSDefaultHeight - 3); setPathToPolyline(thePoints);
		 */
		setPathTo(new RoundRectangle2D.Double(0, 0, ITSDefaultWidth,
				ITSDefaultHeight, 5, 5));

	}

	/**
	 * Sets the rectangle shape.
	 */
	public void setRectangleShape() {
		setPathTo(new RoundRectangle2D.Double(0, 0, ITSDefaultWidth,
				ITSDefaultHeight, 5, 5));
		// setPathToRectangle(0, 0, ITSDefaultWidth, ITSDefaultHeight);
		if (itsThumb != null) {
			itsThumb.setVisible(true);
			// itsTextNode.setVisible(false);
		} else {
			// itsTextNode.setVisible(true);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#getLOM()
	 */
	public LOM getLOM() {
		return itsLOM;
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
	 * method to update the node view when LOM values changed.
	 */
	public void updateView() {
		// System.out.println("called");
		String theTitle = LOMAttribute.getLOMAttribute("general/title")
				.getValueIn(itsLOM).getValue();
		itsTitleNode.setText(theTitle);

		setIsQuery((itsLocationElement == null)
				|| (itsLocationElement.getTextTrim().equals("")));
		if (isQuery)
			setQueryShape();
		else
			setRectangleShape();
		itsValidityUI.update();
		itsValidityUI.invalidatePaint();
		validateFullPaint();

	}

	public GenericActivity setAsActivityNode() {
		GenericActivity theActivity = new GenericActivity(this);
		GenericActivity theParent = (GenericActivity) getParent();
		theParent.addChild(theActivity);
		theParent.changeVisibleRelationFor(this, theActivity);
		detach();
		LessonMapper2.getInstance().getSelectionHandler().unselectAll();
		return theActivity;
	}

	/**
	 * Sets the is query.
	 * 
	 * @param aBool
	 *            the is query
	 */
	public void setIsQuery(boolean aBool) {
		if (isQuery != aBool) {
			isQuery = aBool;
			initMenu();
		}
	}

	/**
	 * draw the nodes (LOM values may be cached in instance variable and updated
	 * when updateView() is called.
	 * 
	 * @param aPaintContext
	 *            the a paint context
	 */
	public void paint(PPaintContext aPaintContext) {
		// if
		// (LessonMapper2.getInstance().getSelectionHandler().isSelected(this))
		// System.out.println(aPaintContext.getScale());
		if (aPaintContext.getScale() > ITSHighQualityScale)
			useHighThumb();
		else
			useLowThumb();
		Graphics2D theGraphics2D = aPaintContext.getGraphics();
		PBounds theBounds = getBounds();
		theBounds.inset(-ITSInsets, -ITSInsets);
		RoundRectangle2D.Double theShape = new RoundRectangle2D.Double(
				theBounds.x, theBounds.y, theBounds.width, theBounds.height,
				10, 10);
		ShadowMaker.paintShadow(theGraphics2D, theShape, 0.8f, ITSStrokeSize,
				ITSStrokeColor, LMUI.ITSSandColor);
		theGraphics2D.setPaint(getPaint());
		theGraphics2D.fill(theShape);
		theGraphics2D.setStroke(new BasicStroke(ITSStrokeSize));
		theGraphics2D.setPaint(ITSStrokeColor);
		theGraphics2D.draw(theShape);

		if (isQuery) {
			aPaintContext.getGraphics().drawImage(ITSQueryIcon,
					ITSDefaultWidth - 20, ITSDefaultHeight / 4, null);
		}
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

	/**
	 * Gets the displayed title.
	 * 
	 * @return the displayed title
	 */
	public String getDisplayedTitle() {
		return itsTitleNode.getText();
	}

	/**
	 * if the material is not a web URL (i.e. if the material is not accessible
	 * from outside the repository) the material is saved in the local
	 * repository if not the reference is the web URL
	 * 
	 * @param theMaterialLocation
	 *            the material
	 */
	public void setMaterial(String theMaterialLocation) {
		setMaterial(theMaterialLocation, false);
	}

	/**
	 * if the material is not a web URL (i.e. if the material is not accessible
	 * from outside the repository) the material is saved in the local
	 * repository if it is a web URL the material is saved locally if
	 * makeLocalCopy is true or the reference is kept unchanged if makeLocalCopy
	 * is false
	 * 
	 * @param makeLocalCopy
	 *            the make local copy
	 * @param theMaterialLocation
	 *            the material location
	 */
	public void setMaterial(String theMaterialLocation, boolean makeLocalCopy) {
		if (theMaterialLocation == null || theMaterialLocation.equals(""))
			return;
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

			if ((theMaterialLocation.startsWith("http://") || theMaterialLocation
					.startsWith("ftp:"))
					&& (!makeLocalCopy)) {
				itsLocationElement.setText(theMaterialLocation);
				updateThumb();
			} else {
				// copy the material in the repository
				boolean isCopied;
				try {
					if (theMaterialLocation.startsWith("file:")
							|| theMaterialLocation.startsWith("http:")
							|| theMaterialLocation.startsWith("ftp:"))
						isCopied = FileManagement.getFileManagement().copy(
								new URL(theMaterialLocation),
								new File(theNewMaterialLocation));
					else if (theFileName.equals(theMaterialLocation))
						isCopied = FileManagement.getFileManagement().copy(
								new File(theMaterialLocation),
								new File(theNewMaterialLocation));
					else
						isCopied = FileManagement.getFileManagement().copy(
								new File(theMaterialLocation),
								new File(theNewMaterialLocation));
				} catch (Exception e) {
					System.out.println("Error when copying file "
							+ theMaterialLocation);
					e.printStackTrace();

					isCopied = false;
				}
				if (isCopied)
					itsLocationElement.setText(theFileName);// only the fileName
				// is given
				else
					itsLocationElement.setText(theMaterialLocation);
				updateThumb();
			}
			Element theTitle = (Element) LOMAttribute.getLOMAttribute(
					"general/title").getNodesIn(itsLOM).get(0);
			if (theTitle.getTextTrim().equals("")) {
				theTitle.setText(theFileName.substring(0,
						theFileName.length() > 20 ? 20 : theFileName.length()));
			}
		}

	}

	/**
	 * Gets the material.
	 * 
	 * @return the material
	 */
	public String getMaterial() {
		return getMaterial(itsLMProject.getRepository().getArchivePath());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM#getMaterialURL()
	 */
	public String getMaterialURL() {
		return getMaterial();
	}

	/**
	 * Gets the material.
	 * 
	 * @param aArchivePath
	 *            the a archive path
	 * 
	 * @return the material
	 */
	public String getMaterial(String aArchivePath) {
		if (itsLocationElement == null)
			return null;
		String theLocation = itsLocationElement.getTextTrim();
		if (theLocation.equals(""))
			return null;
		if (!(theLocation.contains(File.separator) || theLocation.contains("/")))
			return itsLMProject.getRepository().getMaterialLocationForID(
					getLOM().getMaterialID(), aArchivePath)
					+ itsLocationElement.getTextTrim();
		else
			return itsLocationElement.getTextTrim();
	}

	/**
	 * return the name of the material (as the end of the materialLocation)
	 */
	public String getMaterialName() {
		StringTokenizer theTokenizer = new StringTokenizer(itsLocationElement
				.getTextTrim(), "/" + File.separator);
		String theName = "";
		while (theTokenizer.hasMoreTokens()) {
			theName = theTokenizer.nextToken();
		}
		return theName; //removed tolowercase
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
	 * when double click open the associated material if no material exist a
	 * query for this node is generated keyword query will be based on the
	 * content of the fields title, keyword and description.
	 * 
	 * @param aEvent
	 *            the a event
	 * @param aType
	 *            the a type
	 */
	public void processEvent(PInputEvent aEvent, int aType) {
		if (!aEvent.isHandled()) {
			if (aType == MouseEvent.MOUSE_CLICKED && aEvent.isLeftMouseButton()
					&& aEvent.getClickCount() == 2) {
				if (isQuery && LessonMapper2.isQueryMode) {
					queryRepository();
				} else {
					openMaterial();
				}
			} else if (aType == MouseEvent.MOUSE_CLICKED
					&& aEvent.isRightMouseButton()) {
				PCanvas theCanvas = LessonMapper2.getInstance().getMainCanvas();
				PCamera theTopCamera = theCanvas.getCamera();
				Point2D theNewLocation = aEvent
						.getPositionRelativeTo(theTopCamera);
				if (itsMenu == null)
					initMenu();
				itsMenu.show(theCanvas, (int) theNewLocation.getX(),
						(int) theNewLocation.getY());
				aEvent.setHandled(true);
			}
		}
	}

	/**
	 * Inits the menu.
	 */
	protected void initMenu() {
		itsMenu = new JPopupMenu();
		JMenuItem theQueryItem = new JMenuItem(LessonMapper2.getInstance()
				.getLangComment("materialMenuQuery"));
		theQueryItem.setActionCommand("query");
		JMenuItem theOpenItem = new JMenuItem(LessonMapper2.getInstance()
				.getLangComment("materialMenuOpen"));
		theOpenItem.setActionCommand("open");
		// if (isQuery)
		itsMenu.add(theQueryItem);
		// else
		itsMenu.add(theOpenItem);
		JMenuItem theSetAsActivityItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("setMaterialAsActivity"));
		theSetAsActivityItem.setActionCommand("setAsActivity");
		itsMenu.add(theSetAsActivityItem);
		JMenuItem theSetMaterialItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuSet"));
		theSetMaterialItem.setActionCommand("setmaterial");
		itsMenu.add(theSetMaterialItem);
		JMenuItem theModifyMaterialItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuModifyMaterial"));
		theModifyMaterialItem.setActionCommand("modifymaterial");
		itsMenu.add(theModifyMaterialItem);
		JMenuItem theModifyContextItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuModifyContext"));
		theModifyContextItem.setActionCommand("modifycontext");
		itsMenu.add(theModifyContextItem);
		// JMenuItem theAutomaticThumbnailItem = new JMenuItem(LessonMapper2
		// .getInstance().getLangComment("materialMenuAutomaticThumb"));
		// theAutomaticThumbnailItem.setActionCommand("makeAutomaticThumbnail");
		// itsMenu.add(theAutomaticThumbnailItem);
		JMenuItem theInteractiveThumbnailItem = new JMenuItem(LessonMapper2
				.getInstance().getLangComment("materialMenuInteractiveThumb"));
		theInteractiveThumbnailItem
				.setActionCommand("makeInteractiveThumbnail");
		itsMenu.add(theInteractiveThumbnailItem);
		ActionListener theMenuListener = new ActionListener() {
			public void actionPerformed(java.awt.event.ActionEvent e) {
				if (e.getActionCommand().equals("query"))
					// testImage();
					queryRepository();
				if (e.getActionCommand().equals("open"))
					openMaterial();
				if (e.getActionCommand().equals("setAsActivity")) {
					if (getMaterial() != null) {
						if (JOptionPane.showConfirmDialog(LessonMapper2.getInstance().getUI(), LessonMapper2.getInstance().getLangComment(
								"exisitingMaterialWarning"), "", JOptionPane.YES_NO_OPTION)==JOptionPane.YES_OPTION)
							setAsActivityNode();
					}else setAsActivityNode();
				}
				if (e.getActionCommand().equals("setmaterial"))
					setMaterial();
				if (e.getActionCommand().equals("modifymaterial"))
					modifyMaterial();
				if (e.getActionCommand().equals("modifycontext"))
					modifyContext();
				if (e.getActionCommand().equals("makeInteractiveThumbnail"))
					makeInteractiveThumb();
				if (e.getActionCommand().equals("makeAutomaticThumbnail"))
					makeAutomaticThumb();
			}
		};
		theQueryItem.addActionListener(theMenuListener);
		theOpenItem.addActionListener(theMenuListener);
		theSetMaterialItem.addActionListener(theMenuListener);
		theModifyMaterialItem.addActionListener(theMenuListener);
		theModifyContextItem.addActionListener(theMenuListener);
		// theAutomaticThumbnailItem.addActionListener(theMenuListener);
		theInteractiveThumbnailItem.addActionListener(theMenuListener);
		theSetAsActivityItem.addActionListener(theMenuListener);
	}

	/**
	 * Open material.
	 */
	protected void openMaterial() {
		try {
			String theMaterial = getMaterial();
			if (theMaterial == null) {
				Object[] options = {
						LessonMapper2.getInstance().getLangComment(
								"associateActionTemplate"),
						LessonMapper2.getInstance().getLangComment(
								"associateActionActivity"), "Cancel" };
				int theChoice = JOptionPane
						.showOptionDialog(LessonMapper2.getInstance().getUI(),
								LessonMapper2.getInstance().getLangComment(
										"associateAction"), "",
								JOptionPane.YES_NO_CANCEL_OPTION,
								JOptionPane.QUESTION_MESSAGE, null, options,
								options[0]);
				if (theChoice == JOptionPane.CANCEL_OPTION) return;
				if (theChoice == JOptionPane.NO_OPTION) {
					setAsActivityNode();
				} else {
					if (getParent() instanceof GenericActivity) {
						String theDefaultMaster = ((GenericActivity) getParent())
								.getDefaultMaster();
						if (theDefaultMaster == null) {
							System.out
									.println("No material is associated with this node and no default master has been declared");
							return;
						}
						setMaterial(theDefaultMaster);
						System.out.println("set default master as material");
						theMaterial = getMaterial();
					}
				}
			}
			System.out.println("open " + theMaterial);
			FileManagement.getFileManagement().openFile(theMaterial,
					LessonMapper2.isPresentationMode);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Query repository.
	 */
	protected void queryRepository() {
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		LOMQuery theQuery = new LOMQuery(itsLOM);
		new ResultPane(this, theQuery);
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Sets the material.
	 */
	protected void setMaterial() {
		File theFile = LessonMapper2.getInstance().getFileChooser()
				.showAsOpenDialog(
						LessonMapper2.getInstance().getLangComment(
								"loadMaterialDialog"), "*.*");
		if (theFile != null)
			setMaterial(theFile.getAbsolutePath());
	}

	/**
	 * init itsThumbF.
	 */
	public void initThumb() {
		itsThumb = new PImage();
		itsThumb.setPickable(false);
		addChild(0, itsThumb); // put the thumb on the back
		itsLowThumb = new PImage();
		itsLowThumb.setPickable(false);
		addChild(0, itsLowThumb); // put the thumb on the back
		setThumbVisible(true);
	}

	/**
	 * set the low thumb visible and thumb not visible if aBool is true set both
	 * to false of aBool is false.
	 * 
	 * @param aBool
	 *            the thumb visible
	 */
	public void setThumbVisible(boolean aBool) {
		if (!aBool) {
			itsLowThumb.setVisible(false);
			itsThumb.setVisible(false);
		}
		useLowThumb();
	}

	/**
	 * Checks if is low thumb used.
	 * 
	 * @return true, if is low thumb used
	 */
	public boolean isLowThumbUsed() {
		return itsLowThumb.getVisible();
	}

	/**
	 * Use low thumb.
	 */
	public void useLowThumb() {
		itsLowThumb.setVisible(true);
		itsThumb.setVisible(false);
	}

	/**
	 * Use high thumb.
	 */
	public void useHighThumb() {
		itsLowThumb.setVisible(false);
		itsThumb.setVisible(true);
	}

	/**
	 * take the thumb from aMaterial.
	 * 
	 * @param aMaterial
	 *            the a material
	 */
	public void getThumbFromMaterial(GenericMaterial aMaterial) {
		if (aMaterial.itsThumb != null) {
			setThumb(aMaterial.itsThumb.getImage());
		}
	}

	/**
	 * first look on the LOR with the material ID next make default thumb if
	 * first step failed.
	 */
	public void updateThumb() {
		if ((itsLocationElement != null)
				&& (!itsLocationElement.getTextTrim().equals(""))
				&& itsThumb.getImage() == null) {
			try {
				String theThumbURL = itsLMProject.getRepository()
						.getMaterialThumbForID(itsLOM.getMaterialID());
				File theThumb = new File(theThumbURL);
				if (theThumb.exists()) {
					setThumb(theThumbURL);
				} else
					throw new Exception("no local thumb available");
			} catch (Exception e) {
				// try to find the thumb on distant repository
				try {
					String theThumbURL = LOR.INSTANCE
							.getThumbLocationWithMaterialID(itsLOM
									.getMaterialID());
					@SuppressWarnings("unused")
					Object theContent = new URL(theThumbURL).getContent();
					// check if a connection can be done with this URL if not it
					// returns IOException
					setThumb(theThumbURL);
				} catch (Exception e2) {
					System.out.println("thumb not found make default thumb");
					makeDefaultThumb();
				}
			}
		}
	}

	/**
	 * set the thumb image as child of this node the image is scaled to match
	 * default width and height of the node.
	 * 
	 * @param theThumbURL
	 *            the thumb
	 */
	public void setThumb(String theThumbURL) {
		System.out.println("new thumb setted");
		if (itsThumb == null) {
			initThumb();
		}
		BufferedImage theImage = null;
		if (theThumbURL.startsWith("http://"))
			try {
				theImage = ImageIO.read(new URL(theThumbURL));
			} catch (Exception e) {
				e.printStackTrace();

			}
		else
			try {
				theImage = ImageIO.read(new File(theThumbURL));
			} catch (Exception e) {
				e.printStackTrace();
			}
		ImageUtil.createCompatibleImage(theImage, Transparency.OPAQUE);
		setThumb(theImage);
	}

	/**
	 * Sets the thumb.
	 * 
	 * @param theImage
	 *            the thumb
	 */
	public void setThumb(Image theImage) {
		System.out.println("setThumb for " + itsTitleNode.getText());
		if (theImage != null) {
			theImage.flush();
			// TODO create compatible image
			// theImage =
			// ImageUtil.createCompatibleImage(theImage,Transparency.OPAQUE);
			itsThumb.setImage(theImage);
			if (itsThumb.getImage() != null) {
				itsThumb.getImage().flush();
				double theWidthRate = ((double) ITSDefaultWidth - 1)
						/ ((double) theImage.getWidth(null));
				double theHeightRate = ((double) ITSDefaultHeight - 1)
						/ ((double) theImage.getHeight(null));
				double theScale = Math.min(theWidthRate, theHeightRate);
				Image theScaledImage = theImage.getScaledInstance(
						(int) (theImage.getWidth(null) * theScale),
						(int) (theImage.getHeight(null) * theScale),
						Image.SCALE_SMOOTH);
				itsLowThumb.setImage(theScaledImage);
				itsThumb.setScale(theScale);
				Point2D theOffset = new Point(
						(int) ((ITSDefaultWidth - (theImage.getWidth(null) * theScale)) / 2),
						(int) ((ITSDefaultHeight - (theImage.getHeight(null) * theScale)) / 2));
				itsLowThumb.setOffset(theOffset);
				itsThumb.setOffset(theOffset);
				itsLowThumb.invalidatePaint();
				itsThumb.invalidatePaint();
				if (getParent() != null)
					getParent().validateFullPaint();
				else
					validateFullPaint();
			}
		}
	}

	/**
	 * call thumbmaker to nuild the thumb associated with the material if it
	 * fails, it associates a thumb considering its extension.
	 */
	public void makeAutomaticThumb() {
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String theThumbURL = itsLMProject.getRepository()
				.getMaterialThumbForID(itsLOM.getMaterialID());
		if ((itsLocationElement != null)
				&& (!itsLocationElement.getTextTrim().equals(""))) {
			try {
				ThumbMaker theThumbMaker = ThumbMaker.getThumbMaker();
				// try {
				theThumbMaker.makeAutomaticThumb(itsLocationElement
						.getTextTrim(), theThumbURL);

				/*
				 * }catch (ThumbNotBuildException e) { //e.printStackTrace();
				 * System.out.println("thumb creation did not succeed. Default
				 * application icon instead.");
				 * theThumbMaker.makeApplicationThumb(itsLocationElement.getTextTrim(),
				 * theThumbURL); }
				 */
			} catch (Exception e) {
				System.out.println("thumb creation not supported on this OS");
			}
			File theThumb = new File(theThumbURL);
			if (theThumb.exists()) {
				setThumb(theThumbURL);
			}
		}
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Make default thumb.
	 */
	public void makeDefaultThumb() {
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
		String theThumbURL = itsLMProject.getRepository()
				.getMaterialThumbForID(itsLOM.getMaterialID());
		if ((itsLocationElement != null)
				&& (!itsLocationElement.getTextTrim().equals(""))) {
			try {
				ThumbMaker theThumbMaker = ThumbMaker.getThumbMaker();
				// try {
				theThumbMaker.makeDefaultThumb(
						itsLocationElement.getTextTrim(), theThumbURL);

				/*
				 * }catch (ThumbNotBuildException e) { //e.printStackTrace();
				 * System.out.println("thumb creation did not succeed. Default
				 * application icon instead.");
				 * theThumbMaker.makeApplicationThumb(itsLocationElement.getTextTrim(),
				 * theThumbURL); }
				 */
			} catch (Exception e) {
				System.out.println("thumb creation not supported on this OS");
			}
			File theThumb = new File(theThumbURL);
			if (theThumb.exists()) {
				setThumb(theThumbURL);
			}
		}
		LessonMapper2.getInstance().getMainCanvas().setCursor(
				Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
	}

	/**
	 * Make interactive thumb.
	 */
	public void makeInteractiveThumb() {
		(new Thread() {
			public void run() {
				String theThumbURL = itsLMProject.getRepository()
						.getMaterialThumbForID(itsLOM.getMaterialID());
				if ((itsLocationElement != null)
						&& (!itsLocationElement.getTextTrim().equals(""))) {
					try {
						ThumbMaker theThumbMaker = ThumbMaker.getThumbMaker();
						theThumbMaker.makeInteractiveThumb(getMaterial(),
								theThumbURL);
					} catch (Exception e) {
						System.out
								.println("thumb creation not supported on this OS");
					}
					File theThumb = new File(theThumbURL);
					if (theThumb.exists()) {
						setThumb(theThumbURL);
					}
				}
			}
		}).start();
	}

	/**
	 * change the id of the associated material (basically set the current
	 * material as new material and make local copy)
	 */
	public void modifyMaterial() {
		String theMaterial = getMaterial();
		setLOM(new LOM(itsLOM, true, true));
		// TODO manage relation with neighbors
		updateParentRelation();
		setMaterial(theMaterial, true);
	}

	private void updateParentRelation() {
		// update relation with parent
		PNode theNode = getParent();
		if (theNode != null && theNode instanceof GenericActivity) {
			((GenericActivity) theNode).associate(this, true);
		}
	}

	/**
	 * copy the LOM object and make the current LOMID but keep the material ID
	 * 
	 */
	public void modifyContext() {
		setLOM(new LOM(itsLOM, true, false));
		// TODO manage relation with neighbors
		updateParentRelation();
	}

	@Override
	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("genericMaterial");

	}

	public class PTextWithHelper extends PText implements HelperSupport {
		public PTextWithHelper(String aString) {
			super(aString);
		}

		public String getHelp() {
			return LessonMapper2.getInstance().getLangComment("titleNode");
		}

	}

	/**
	 * set the LMProject of an other material do not change the material
	 */
	protected void setLMProject(GenericMaterial aMaterial) {
		itsLMProject = aMaterial.getLMProject();
	}

	/**
	 * set a new LMProject and copy the material to the new location
	 */
	public void setLMProject(LMProject aProject) {
		if (itsLMProject != null) {
			String theMaterial;
			if (aProject != null && (aProject.isArchivePathChanging()))
				theMaterial = getMaterial(aProject.getOldArchivePath());
			else
				theMaterial = getMaterial();
			itsLMProject = aProject;
			setMaterial(theMaterial);
		} else
			itsLMProject = aProject;
	}

	public LMProject getLMProject() {
		return itsLMProject;
	}

	public Image getThumbImage() {
		return itsThumb.getImage();
	}

	/**
	 * hooked by aspect LOMIConUIPlug for painting icons
	 */
	@Override
	public void paintAfterChildren(PPaintContext aPaintContext) {
		// TODO Auto-generated method stub
		super.paintAfterChildren(aPaintContext);
	}

	@Override
	public void repaintFrom(PBounds aLocalBounds, PNode aChildOrThis) {
		// TODO Auto-generated method stub
		super.repaintFrom(aLocalBounds, aChildOrThis);
	}

}
