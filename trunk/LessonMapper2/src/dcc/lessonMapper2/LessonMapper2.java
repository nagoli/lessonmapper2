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
package dcc.lessonMapper2;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;

import lessonMapper.LangManager;
import lessonMapper.diffusion.DiffusionCache;
import lessonMapper.lom.LOM;
import lessonMapper.lom.LOMRelation;
import lessonMapper.lom.diffuse.LOMResRuleBuilder;
import lessonMapper.lom.diffuse.LOMSuggestionProbability;
import lessonMapper.lom.util.LOMRelationBuffer;
import lessonMapper.query.LOMQuery;
import lessonMapper.query.ProbabilityBuilder;
import lessonMapper.validation.CMV.ValidationCache;

import org.apache.commons.io.FileUtils;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;

import util.concurrency.SequentialWorker;
import util.system.thumb.ThumbMaker;
import util.ui.fileDialog.OSOrientedFileDialog;
import dcc.lessonMapper2.ui.LMUI;
import dcc.lessonMapper2.ui.action.SelectionMode;
import dcc.lessonMapper2.ui.eventHandler.SelectionHandler;
import dcc.lessonMapper2.ui.graph.element.CommentNode;
import dcc.lessonMapper2.ui.graph.element.ContainerNode;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import dcc.lessonMapper2.ui.graph.element.lom.LOMRelationTitleUI;
import dcc.lessonMapper2.ui.graph.element.lom.LOMRelationUI;
import dcc.lessonMapper2.ui.graph.element.lom.LOMTitleUI;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import dcc.lessonMapper2.ui.query.ResultPane;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;

/**
 * The Class LessonMapper2.
 */
public class LessonMapper2 {

	public static final String VERSION = "Version 1.0.0b";

	/** The ITS instance. */
	protected static LessonMapper2 ITSInstance = new LessonMapper2();

	public static boolean isPresentationMode = false;
	public static boolean isQueryMode = false;

	public boolean isShownValidationState = false;

	public static String ITSTutorialName = "Tutorial";
	public static String ITSDefaultTempDirectory = LMInitializer.iniFilesDirectory
			+ File.separator + "temp";

	/** The Constant ITSNameSpace. */
	public static final String ITSNameSpace = "http://www.dcc.uchile.cl/lessonMapper";

	/** The Constant ITSLangResource. */
	public static final String ITSLangResource = "resources/translation.xml";

	/** The Constant ITSPresentationImage. */
	public static final String ITSPresentationImage = "resources/Presentation.jpg";

	/** The Constant ITSNameSpaces. */
	static final String[][] ITSNameSpaces = new String[][] {
			{ "rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#" }, // shoudl
			// remain
			// in
			// first
			// position
			{ "rdfs", "http://www.w3.org/2000/01/rdf-schema#" } };

	/** The Constant ITSZoomIncrement. */
	public final static double ITSZoomIncrement = 0.3;

	/** The Constant ITSlevelZoomFactor. */
	public static final double ITSlevelZoomFactor = 0.15;

	/** The ITS view insets. */
	public static Dimension ITSViewInsets = new Dimension(0, 0);

	/** The its UI. */
	protected LMUI itsUI;

	/** The its active project. */
	protected LMProject itsActiveProject;

	/** The its lang properties. */
	protected Hashtable<String, String> itsLangProperties;

	/** The its clipboard. */
	protected LessonMapperClipboard itsClipboard = new LessonMapperClipboard();

	/** The its file chooser. */
	protected OSOrientedFileDialog itsFileChooser;

	/**
	 * The worker is responsible for doing tasks like loading/saviing project
	 * and updating the cache and the view Worker acts in a secondary thread but
	 * executes the tasks in order (FIFO) and sequencially.
	 */
	protected LMSequentialWorker itsWorker = new LMSequentialWorker();

	/** The its presentation. */
	public final JFrame itsPresentation = new JFrame();

	public boolean isFirstTimeOpen = false;

	/**
	 * Init.
	 */
	public void init() {
		// clean temp
		
		//build the diffusion rules
		LOMSuggestionProbability.init();
		LOMResRuleBuilder.parseRules();
		
		
		
		try {
			FileUtils.cleanDirectory(new File(ITSDefaultTempDirectory));
		} catch (Exception e) {
			System.out.println("Problem while cleaning temp directory");
		}
		new LMInitializer().init();
		ImageIcon theIcon = new ImageIcon(LessonMapper2.class
				.getResource(ITSPresentationImage));
		itsPresentation.getContentPane().add(new JLabel(theIcon) {
			@Override
			protected void processMouseEvent(MouseEvent aE) {
				if (aE.getID() == MouseEvent.MOUSE_CLICKED)
					itsPresentation.setVisible(false);
			}
			@Override
			public void paint(Graphics aG) {
				super.paint(aG);
				aG.drawString(VERSION, 480, 187);
			}
		});
		
		// itsPresentation.setAlwaysOnTop(true);
		itsPresentation.setResizable(false);
		itsPresentation.setUndecorated(true);
		itsPresentation.pack();
		
		Dimension theScreenSize = Toolkit.getDefaultToolkit().getScreenSize();
		itsPresentation
				.setSize(theIcon.getIconWidth(), theIcon.getIconHeight());
		itsPresentation
				.setLocation((int) (theScreenSize.getWidth() - theIcon
						.getIconWidth()) / 2,
						(int) (theScreenSize.getHeight() - theIcon
								.getIconHeight()) / 2);
		itsPresentation.setVisible(true);

		DiffusionCache.setActive(true);

		
		
		itsActiveProject = new LMProject();
		itsUI = new LMUI();
		itsUI.init();

		try {
			ThumbMaker theThumbMaker = ThumbMaker.getThumbMaker();
			theThumbMaker.setExplanations(getLangComment("screenshot"),
					getLangComment("screenshot2"));
			theThumbMaker.addWindowToHide(itsUI);
		} catch (Exception e) {
			e.printStackTrace();
		}
		itsFileChooser = OSOrientedFileDialog.create(itsUI);
		if (isFirstTimeOpen)
			loadTutorialDirect();
		else
			giveToWorker(new NewProjectTask());
		itsPresentation.setVisible(false);

		// itsWorker.addTask(new ClosePresentationTask());
	}

	/**
	 * The Class ClosePresentationTask.
	 */
	public class ClosePresentationTask implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			itsPresentation.setVisible(false);
		}
	}

	/**
	 * The main method.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {
		if (args.length == 0) {
			// manage mac menu
			if (System.getProperty("os.name").startsWith("Mac")) {
				try {
					Class.forName(
							"dcc.lessonMapper2.LMApplicationHandlerForMac")
							.newInstance();
				} catch (Exception e) {
					System.out
							.println("mac application handler not initialized");
				}
			}
			ITSInstance.init();
		} else if (args[0].equals("buildProbabilities"))
			if (args.length > 1)
				ProbabilityBuilder.buildProba(args[1]);
			else
				System.out.println("Output probability file is missing");
		else
			System.out.println("Invalid Option");
	}

	/**
	 * Reset layers.
	 */
	public void resetLayers() {
		itsActiveProject.resetLayers();
	}

	/**
	 * Enter layer.
	 * 
	 * @param aLayer
	 *            the a layer
	 */
	public void enterLayer(PLayer aLayer) {
		itsActiveProject.enterLayer(aLayer);
	}

	public OSOrientedFileDialog getFileChooser() {
		return itsFileChooser;
	}

	/**
	 * Go upper layer.
	 */
	public void goUpperLayer() {
		itsActiveProject.goUpperLayer();
	}

	/**
	 * Gets the instance.
	 * 
	 * @return Returns the instance.
	 */
	public static LessonMapper2 getInstance() {
		return ITSInstance;
	}

	/**
	 * Gets the active layer.
	 * 
	 * @return Returns the activeLayer.
	 */
	public ContainerNode getActiveLayer() {
		return itsActiveProject.getActiveLayer();
	}

	/**
	 * Gets the main canvas.
	 * 
	 * @return Returns the mainCanvas.
	 */
	public PCanvas getMainCanvas() {
		return itsUI.getMainCanvas();
	}

	/**
	 * Gets the preview canvas.
	 * 
	 * @return returns the PreviewCanvas
	 */
	public PCanvas getPreviewCanvas() {
		return itsUI.getPreviewCanvas();
	}

	/**
	 * Gets the previous active layers.
	 * 
	 * @return Returns the previousActiveLayers.
	 */
	public Stack getPreviousActiveLayers() {
		return itsActiveProject.getPreviousActiveLayers();
	}

	/**
	 * Gets the UI.
	 * 
	 * @return Returns the uI.
	 */
	public LMUI getUI() {
		return itsUI;
	}

	/**
	 * Gets the upper activity.
	 * 
	 * @return the upper activity
	 */
	public GenericActivity getUpperActivity() {
		return itsActiveProject.getUpperActivity();
	}

	/**
	 * load a new project.
	 */

	public void newProject() {
		int theChoice = JOptionPane.showConfirmDialog(itsUI, LessonMapper2
				.getInstance().getLangComment("newProjectConfirmation"),
				LessonMapper2.getInstance().getLangComment("newProject"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		boolean isCancel = false;
		if (theChoice == JOptionPane.CANCEL_OPTION)
			isCancel = true;
		else if (theChoice == JOptionPane.YES_OPTION) {
			if (LessonMapper2.getInstance().getActiveProject()
					.isDefaultArchivePath())
				isCancel = !LessonMapper2.getInstance().saveProjectAs();
			LessonMapper2.getInstance().saveProject();
		}
		if (!isCancel)
			giveToWorker(new NewProjectTask());
	}

	/**
	 * The Class NewProjectTask.
	 */
	public class NewProjectTask implements Runnable {

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			
			itsActiveProject = new LMProject(new GenericActivity());
			itsUI.resetMainCanvas();
			itsUI.resetPreviewCanvas();
			resetCaches();
			resetLayers();
			enterLayer(getUpperActivity());
		}
	}

	/**
	 * load a new upperActivity from exisiting activities from recently used
	 * ones or init with a new one. if the action is cancelled do not change the
	 * present one
	 */
	public void loadProject() {
		int theChoice = JOptionPane.showConfirmDialog(itsUI, LessonMapper2
				.getInstance().getLangComment("loadProjectConfirmation"),
				LessonMapper2.getInstance().getLangComment("loadProject"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		boolean isCancel = false;
		if (theChoice == JOptionPane.CANCEL_OPTION)
			isCancel = true;
		else if (theChoice == JOptionPane.YES_OPTION) {
			if (LessonMapper2.getInstance().getActiveProject()
					.isDefaultArchivePath())
				isCancel = !LessonMapper2.getInstance().saveProjectAs();
			LessonMapper2.getInstance().saveProject();
		}
		if (!isCancel) {
			File file = itsFileChooser.showAsOpenDialog(
					getLangComment("loadDialog"), null);
			if (file != null)
				giveToWorker(new LoadProjectTask(file));
		}
	}

	/**
	 * load a the tutorial. Ask for confirmation before closing opened projects
	 */
	public void loadTutorial() {
		int theChoice = JOptionPane.showConfirmDialog(itsUI, LessonMapper2
				.getInstance().getLangComment("loadProjectConfirmation"),
				LessonMapper2.getInstance().getLangComment("loadProject"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		boolean isCancel = false;
		if (theChoice == JOptionPane.CANCEL_OPTION)
			isCancel = true;
		else if (theChoice == JOptionPane.YES_OPTION) {
			if (LessonMapper2.getInstance().getActiveProject()
					.isDefaultArchivePath())
				isCancel = !LessonMapper2.getInstance().saveProjectAs();
			LessonMapper2.getInstance().saveProject();
		}
		if (!isCancel) {
			loadTutorialDirect();
		}
	}

	/**
	 * load the tutorial
	 */
	private void loadTutorialDirect() {
		try {
			
			File theTutorial = new File(LMInitializer.iniFilesDirectory
					+ File.separator + ITSTutorialName);
			giveToWorker(new LoadProjectTask(theTutorial, true));
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * The Class LoadProjectTask.
	 */
	public class LoadProjectTask implements Runnable {
		/** The its file. */
		File itsFile;

		boolean isTutorial = false;

		/**
		 * The Constructor.
		 * 
		 * @param aFile
		 *            the a file
		 */
		public LoadProjectTask(File aFile) {
			itsFile = aFile;
		}

		public LoadProjectTask(File aFile, boolean isTutorial) {
			this(aFile);
			this.isTutorial = isTutorial;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			LMProject theExActiveProject = itsActiveProject;
			itsActiveProject = new LMProject(isTutorial);
			itsActiveProject.setArchivePath(itsFile.getAbsolutePath());
			resetCaches();
			GenericActivity theActivity = itsActiveProject.getRepository()
					.loadUpperActivity();
			if (theActivity != null) {
				itsActiveProject.setUpperActivity(theActivity);
				itsUI.resetMainCanvas();
				itsUI.resetPreviewCanvas();
				resetLayers();
				enterLayer(getUpperActivity());
			} else {
				itsActiveProject = theExActiveProject;
				if (itsActiveProject == null)
					System.exit(0);
			}
		}
	}

	/**
	 * reset the caches for validation and restrictions values.
	 */
	public void resetCaches() {
		LOMRelationBuffer.resetRelationBuffer();
		ValidationCache.getInstance().reset();
		DiffusionCache.getInstance().reset();
	}

	/**
	 * save the currentSession and ask for a location for saving it. return
	 * false if the save dialog was cancel
	 */
	public boolean saveProjectAs() {
		File file = itsFileChooser.showAsSaveDialog(
				getLangComment("saveDialog"), null);
		if (file != null) {
			giveToWorker(new SaveProjectAsTask(file));
			return true;
		}
		return false;
	}

	/**
	 * The Class SaveProjectAsTask.
	 */
	public class SaveProjectAsTask implements Runnable {

		/** The its file. */
		File itsFile;

		/**
		 * The Constructor.
		 * 
		 * @param aFile
		 *            the a file
		 */
		public SaveProjectAsTask(File aFile) {
			itsFile = aFile;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see java.lang.Runnable#run()
		 */
		public void run() {
			// setArchivePath diffuse the archive path change to the nodes
			itsActiveProject.setArchivePath(itsFile.getAbsolutePath());
			itsActiveProject.getRepository().saveActivity(getUpperActivity(),
					true);
			itsActiveProject.getRepository().savePropertyAndCacheFiles();
		}
	}

	/**
	 * save the currentSession.
	 */
	public void saveProject() {
		giveToWorker(new Runnable() {
			public void run() {
				if (!itsActiveProject.isTutorial) {
					itsActiveProject.getRepository().saveActivity(
							getUpperActivity(), true);
					itsActiveProject.getRepository()
							.savePropertyAndCacheFiles();
				}
			}
		});
	}

	/**
	 * save the currentSession on database.
	 */
	public void saveProjectOnDB() {
		giveToWorker(new Runnable() {
			public void run() {
				itsActiveProject.getRepository().saveOnRepository(
						getUpperActivity());
				itsActiveProject.getRepository().savePropertyAndCacheFiles();
			}
		});
	}

	/**
	 * return the current selction handler sleectionHandler is held by the
	 * SlectionMode class.
	 * 
	 * @return the selection handler
	 */
	public SelectionHandler getSelectionHandler() {
		return SelectionMode.getSelectionHandler();
	}

	/**
	 * Gets the active project.
	 * 
	 * @return the active project
	 */
	public LMProject getActiveProject() {
		return itsActiveProject;
	}

	/**
	 * Removes the selection.
	 */
	public void removeSelection() {
		SelectionHandler theHandler = getSelectionHandler();
		if (theHandler != null) {
			for (Iterator iter = theHandler.getSelection().iterator(); iter
					.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof SelectionableNode) {
					SelectionableNode theSelection = (SelectionableNode) element;
					theSelection.detach();
				}
			}
			theHandler.unselectAll();
		}
	}

	/**
	 * create a query using the values of the LOM of the current to search in
	 * the repository.
	 */
	public void queryRepositoryWithSelection() {
		SelectionHandler theHandler = getSelectionHandler();
		if (theHandler != null) {
			for (Iterator iter = theHandler.getSelection().iterator(); iter
					.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof SelectionableLOM) {
					itsUI.getMainCanvas().setCursor(
							Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
					SelectionableLOM theSelection = (SelectionableLOM) element;
					LOMQuery theQuery = new LOMQuery(theSelection.getLOM());
					GenericMaterial theNewMaterial = new GenericMaterial();
					getActiveLayer().addChild(theNewMaterial);
					new ResultPane(theNewMaterial, theQuery, true);
					itsUI.getMainCanvas().setCursor(
							Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));

				}
			}
		}
	}

	/**
	 * return the comments associated with aPropertyName For that purpose the
	 * file "resources/translation.xml" is parsed and the comment in the
	 * language associated with LangManager is extracted.
	 * 
	 * @param aPropertyName
	 *            the a property name
	 * 
	 * @return the lang comment
	 */
	public String getLangComment(String aPropertyName) {
		if (itsLangProperties == null) {
			itsLangProperties = new Hashtable<String, String>();
			URL theURL = getClass().getResource(ITSLangResource);
			List theResults = null;
			try {
				Document theRDFModel = (new SAXBuilder()).build(theURL);
				JDOMXPath myXPath = new JDOMXPath("rdf:RDF/rdf:Property");
				for (int i = 0; i < ITSNameSpaces.length; i++) {
					String[] theNameSpace = ITSNameSpaces[i];
					myXPath.addNamespace(theNameSpace[0], theNameSpace[1]);
				}
				theResults = myXPath.selectNodes(theRDFModel);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (theResults != null)
				for (Iterator iter = theResults.iterator(); iter.hasNext();) {
					Element theElement = (Element) iter.next();
					String theID = theElement.getAttribute(
							"ID",
							Namespace.getNamespace(ITSNameSpaces[0][0],
									ITSNameSpaces[0][1])).getValue();
					Element theLabel = theElement.getChild("label", Namespace
							.getNamespace(ITSNameSpaces[1][0],
									ITSNameSpaces[1][1]));
					Element theAlt = null;
					if (theLabel != null)
						theAlt = theLabel.getChild("Alt", Namespace
								.getNamespace(ITSNameSpaces[0][0],
										ITSNameSpaces[0][1]));
					String theLangString = LangManager.getInstance()
							.getLangString(theAlt);
					if (theLangString == null)
						theLangString = theID + "(not labeled)";
					itsLangProperties.put(theID, theLangString);
				}
		}
		if (itsLangProperties.containsKey(aPropertyName))
			return itsLangProperties.get(aPropertyName);
		else
			return "not defined: " + aPropertyName;
	}

	/**
	 * turn on or off the display of an infinite progressbar.
	 * 
	 * @param aLock
	 *            the a lock
	 * @param isShown
	 *            the is shown
	 */
	public void showActivityState(boolean isShown, Runnable aLock) {
		itsUI.showActivityState(isShown, aLock);
	}

	/**
	 * Copy the current selection basically it removes the content of the
	 * clipboard then it aggregates - the LOM of the copied nodes - the offset
	 * of the copied nodes - the LOMRelation if the concerned nodes are also
	 * copied and the offset of the RelationTitle node
	 * 
	 * if selection is null do nothing. TODO copy upperlevel...
	 */
	public void copy() {

		Set<SelectionableNode> theCopiedNodes = new HashSet<SelectionableNode>();
		SelectionHandler theHandler = getSelectionHandler();
		if (theHandler != null) {
			for (Iterator iter = theHandler.getSelection().iterator(); iter
					.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof SelectionableNode) {
					SelectionableNode theSelection = (SelectionableNode) element;
					theCopiedNodes.add(theSelection);
				}
			}
			if (!theCopiedNodes.isEmpty()) {
				itsClipboard.setContent(theCopiedNodes, false);
			}

			theHandler.unselectAll();
		}
	}

	/**
	 * cut the current selection it is igual to a copy but the copied elements
	 * will be cut when.
	 */
	public void cut() {
		Set<SelectionableNode> theCopiedNodes = new HashSet<SelectionableNode>();
		SelectionHandler theHandler = getSelectionHandler();
		if (theHandler != null) {
			for (Iterator iter = theHandler.getSelection().iterator(); iter
					.hasNext();) {
				Object element = (Object) iter.next();
				if (element instanceof SelectionableNode) {
					SelectionableNode theSelection = (SelectionableNode) element;
					theCopiedNodes.add(theSelection);
				}
			}
			if (!theCopiedNodes.isEmpty()) {
				itsClipboard.setContent(theCopiedNodes, true);
			}
			theHandler.unselectAll();
		}
	}

	/**
	 * paste the content of the clipboard in the active layer.
	 */
	public void paste() {
		Set<PNode> theNodes = itsClipboard.getContent();
		for (PNode theNode : theNodes) {
			getActiveLayer().addChild(theNode);
		}
		// init the relations
		for (PNode theNode : theNodes) {
			if (theNode instanceof LOMRelationUI) {
				LOMRelationUI theUI = (LOMRelationUI) theNode;
				theUI.init();
			}
		}
	}

	/**
	 * The Class LessonMapperClipboard.
	 */
	public class LessonMapperClipboard {

		/** The its activities. */
		protected Map<GenericActivity, Point2D> itsActivities = new HashMap<GenericActivity, Point2D>();

		protected Map<GenericActivity, LessonMapperClipboard> itsActivityClipboards =  new HashMap<GenericActivity, LessonMapperClipboard>();
		
		
		/** The its materials. */
		protected Map<GenericMaterial, Point2D> itsMaterials = new HashMap<GenericMaterial, Point2D>();

		protected Map<CommentNode, Point2D> itsComments = new HashMap<CommentNode, Point2D>();

		/** The its relations. */
		protected Map<LOMRelation, Point2D> itsRelations = new HashMap<LOMRelation, Point2D>();

		
		protected LMProject itsClipboardProject;
		/** The is cut clipboard. */
		protected boolean isCutClipboard = false;

		/** The its nodes to be cut. */
		protected Set<SelectionableNode> itsNodesToBeCut;

		/**
		 * The Constructor.
		 */
		public LessonMapperClipboard() {

		}

		/**
		 * Reset.
		 */
		public void reset() {
			itsActivities.clear();
			itsActivityClipboards.clear();
			itsMaterials.clear();
			itsComments.clear();
			itsRelations.clear();
		}

		/**
		 * add the nodes and the relations contained in the set to the clipboard
		 * excepting if the relations do not have their targets also in the set.
		 * it also keep a reference of theNodes in itsNodesToBeCut if
		 * isCutClipBoard is true
		 * 
		 * @param aIsCutClipboard
		 *            the a is cut clipboard
		 * @param theNodes
		 *            the nodes
		 */
		public void setContent(Set<SelectionableNode> theNodes,
				boolean aIsCutClipboard) {
			itsClipboardProject = getActiveProject();
			isCutClipboard = aIsCutClipboard;
			if (isCutClipboard)
				itsNodesToBeCut = theNodes;
			reset();
			for (SelectionableNode theNode : theNodes) {
				if (theNode instanceof GenericActivity) {
					GenericActivity theActivity = (GenericActivity) theNode;
					itsActivities.put(theActivity, theActivity.getOffset());
					Set<SelectionableNode> theChildren = new HashSet<SelectionableNode>();
					for (Iterator iter = theActivity.getChildrenIterator(); iter
							.hasNext();) {
						PNode element = (PNode) iter.next();
						if (element instanceof SelectionableNode)
							theChildren.add((SelectionableNode) element);
					}
					LessonMapperClipboard theClipboard = new LessonMapperClipboard();
					theClipboard.setContent(theChildren, false);
					itsActivityClipboards.put(theActivity, theClipboard);
				}
				if (theNode instanceof GenericMaterial) {
					GenericMaterial theMaterial = (GenericMaterial) theNode;
					itsMaterials.put(theMaterial, theMaterial.getOffset());
					
					
				}
				if (theNode instanceof CommentNode) {
					CommentNode theComment = (CommentNode) theNode;
					itsComments.put(theComment, theComment.getOffset());
				}
			}

			// select the relations to copy i.e. the relation which link copied
			// nodes
			Set<LOMRelation> theRelationsToCopy = new HashSet<LOMRelation>();
			Set<LOM> theLOMs = new HashSet<LOM>();
			for (SelectionableLOM theLom : itsMaterials.keySet())
				theLOMs.add(theLom.getLOM());
			for (SelectionableLOM theLom : itsActivities.keySet())
				theLOMs.add(theLom.getLOM());
			for (LOM theLOM : theLOMs) {
				Set<LOMRelation> theRelations = LOMRelationBuffer
						.getRelationsIn(theLOM);
				for (LOMRelation theRelation : theRelations)
					if (theLOMs.contains(theRelation.getTargetLOM())) // check
						// for a
						// lom
						// in
						// the
						// lom
						// list
						theRelationsToCopy.add(theRelation);
			}
			for (Object theChild : LessonMapper2.getInstance().getActiveLayer()
					.getChildrenReference()) {
				if (theChild instanceof LOMRelationTitleUI) {
					LOMRelationTitleUI theRelationTitle = (LOMRelationTitleUI) theChild;
					if (theRelationsToCopy.contains(theRelationTitle
							.getRelationUI().getLOMRelation()))
						itsRelations
								.put(theRelationTitle.getRelationUI()
										.getLOMRelation(), theRelationTitle
										.getOffset());
				}
			}
		}

		/**
		 * return a set of Nodes to be paste it also rremoved the nodesToBeCut
		 * in case isCutClipboard is true.
		 * 
		 * @return the content
		 */
		public Set<PNode> getContent() {

			if (itsClipboardProject!=getActiveProject()) isCutClipboard = false;
			Set<PNode> theNodes = new HashSet<PNode>();
			Map<LOM, SelectionableLOM> theLOMNodeMap = new HashMap<LOM, SelectionableLOM>();
			// WARN that activity nodes cannot be copied
			if (!isCutClipboard  && !itsActivities.isEmpty() && itsClipboardProject==getActiveProject()) {
				JOptionPane.showMessageDialog(itsUI,
						getLangComment("copyActivityWarning"));
				itsActivities.clear();
			}
			for (GenericActivity theActivity : itsActivities.keySet()) {
				LOM theLOM = theActivity.getLOM();
				GenericActivity theNewActivity = new GenericActivity(theLOM);
				theNewActivity.setLMProject(itsActiveProject);
				// copy the children of theActivity into the new activity
//				Set<SelectionableNode> theChildren = new HashSet<SelectionableNode>();
//				for (Iterator iter = theActivity.getChildrenIterator(); iter
//						.hasNext();) {
//					PNode element = (PNode) iter.next();
//					if (element instanceof SelectionableNode)
//						theChildren.add((SelectionableNode) element);
//				}
		//		LessonMapperClipboard theClipboard = new LessonMapperClipboard();
		//		theClipboard.setContent(theChildren, false);
				Set<PNode> theCopiedChildren = itsActivityClipboards.get(theActivity).getContent();
				for (PNode theNode : theCopiedChildren) {
					theNewActivity.addChild(theNode);
				}
				for (PNode theNode : theCopiedChildren) {
					if (theNode instanceof LOMRelationUI) {
						LOMRelationUI theUI = (LOMRelationUI) theNode;
						theUI.init();
					}
				}
				theNewActivity.setOffset(itsActivities.get(theActivity));
				theNodes.add(theNewActivity);
				theLOMNodeMap.put(theLOM, theNewActivity);
			}
			for (GenericMaterial theMaterial : itsMaterials.keySet()) {
				GenericMaterial theNewMaterial = new GenericMaterial(
						theMaterial);
				theNewMaterial.setOffset(itsMaterials.get(theMaterial));
				theNodes.add(theNewMaterial);
				theLOMNodeMap.put(theMaterial.getLOM(), theNewMaterial);
			}
			for (CommentNode theComment : itsComments.keySet()) {
				CommentNode theNewComment = new CommentNode(theComment);
				theNewComment.setOffset(itsComments.get(theComment));
				theNodes.add(theNewComment);
			}

			if (isCutClipboard) {
				for (SelectionableNode theNodeToBeCut : itsNodesToBeCut) {
					if (theNodeToBeCut instanceof LOMRelationTitleUI) {
						LOMRelationTitleUI theRelationTitle = (LOMRelationTitleUI) theNodeToBeCut;
						// do not remove the concrete relation but
						// only its view
						theRelationTitle.detach(true);
					} else
						theNodeToBeCut.detach();
				}
				isCutClipboard = false; // nodes are detached only once
			}
			for (LOMRelation theRelation : itsRelations.keySet()) {
				theRelation.attach();
				LOMRelationUI theRelationUI = new LOMRelationUI(theLOMNodeMap
						.get(theRelation.getSourceLOM()), theLOMNodeMap
						.get(theRelation.getTargetLOM()), theRelation);
				theRelationUI
						.setMotionNodeOffset(itsRelations.get(theRelation));
				theNodes.add(theRelationUI);

			}

			return theNodes;
		}

	}

	/**
	 * exit the system
	 */
	public void close() {
		int theChoice = JOptionPane.showConfirmDialog(itsUI, LessonMapper2
				.getInstance().getLangComment("closingConfirmation"),
				LessonMapper2.getInstance().getLangComment("closingLM"),
				JOptionPane.YES_NO_CANCEL_OPTION);
		boolean isCancel = false;
		if (theChoice == JOptionPane.CANCEL_OPTION)
			isCancel = true;
		else if (theChoice == JOptionPane.YES_OPTION) {
			if (LessonMapper2.getInstance().getActiveProject()
					.isDefaultArchivePath())
				isCancel = !LessonMapper2.getInstance().saveProjectAs();
			LessonMapper2.getInstance().saveProject();
		}
		if (!isCancel) {
			itsUI.dispose();
			itsUI.showTerminationFrame();
			giveToWorker(new Runnable() {
				public void run() {
					try {
						FileUtils.cleanDirectory(new File(
								ITSDefaultTempDirectory));
					} catch (Exception e) {
						System.out
								.println("Problem while cleaning temp directory");
					}
					System.exit(0);
				}
			});
		}
	}

	/**
	 * return the title UI of the active layer if it exits else return null
	 */
	public LOMTitleUI getActiveLayerTitleUI() {
		if (getActiveLayer() instanceof GenericActivity) {
			GenericActivity theActiveLayer = (GenericActivity) getActiveLayer();
			return theActiveLayer.getTitleUI();
		} else
			return null;
	}

	/**
	 * Give to worker.
	 * 
	 * @param aTask
	 *            the a task
	 */
	public void giveToWorker(Runnable aTask) {
		itsWorker.addTask(aTask);
	}

	/**
	 * The Class LMSequentialWorker.
	 */
	public class LMSequentialWorker extends SequentialWorker {

		/**
		 * The Constructor.
		 */
		public LMSequentialWorker() {
			super(true);
		}

		/**
		 * Run.
		 */
		@Override
		public void run() {
			while (true) {
				Runnable theTask = pop();
				showActivityState(true, theTask);
				for (int i = 0; i < 5; i++) {
					try {
						System.out.println("started "
								+ theTask.getClass().getName());
						theTask.run();
						System.out.println("terminate "
								+ theTask.getClass().getName());
						break;
					} catch (Exception e) {
						System.out.println("exeption during "
								+ theTask.getClass().getName());
						e.printStackTrace();
						System.out.println("retry " + i);
					}
				}
				showActivityState(false, theTask);
			}
		}

	}

}
