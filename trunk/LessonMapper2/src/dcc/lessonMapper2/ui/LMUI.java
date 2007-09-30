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
package dcc.lessonMapper2.ui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.WindowEvent;
import java.util.HashSet;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.border.LineBorder;

import util.ui.ModelGroup;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.action.InsertCommentMode;
import dcc.lessonMapper2.ui.action.InsertEdgeMode;
import dcc.lessonMapper2.ui.action.InsertGenericActivityMode;
import dcc.lessonMapper2.ui.action.InsertGenericMaterialMode;
import dcc.lessonMapper2.ui.action.SelectionMode;
import dcc.lessonMapper2.ui.eventHandler.ExternalDropManager;
import dcc.lessonMapper2.ui.eventHandler.GoUpperLayerHandler;
import dcc.lessonMapper2.ui.eventHandler.ZoomToActiveLayer;
import dcc.lessonMapper2.ui.lom.LOMAttributeBaseSet;
import dcc.lessonMapper2.ui.lom.LOMAttributeTree;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;

/**
 * The Class LMUI.
 */
public class LMUI extends JFrame {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The Constant ITSQueryIcon. */
	public final static String ITSQueryIcon = "resources/query.gif";

	/** The Constant ITSCopyIcon. */
	public final static String ITSCopyIcon = "resources/copy.gif";

	/** The Constant ITSCutIcon. */
	public final static String ITSCutIcon = "resources/cut.gif";

	/** The Constant ITSPasteIcon. */
	public final static String ITSPasteIcon = "resources/paste.gif";

	/** The Constant ITSSaveIcon. */
	public final static String ITSSaveIcon = "resources/save.gif";

	/** The Constant ITSSaveAsIcon. */
	public final static String ITSSaveAsIcon = "resources/saveAs.gif";

	/** The Constant ITSSaveDBIcon. */
	public final static String ITSSaveDBIcon = "resources/saveDB.gif";

	/** The Constant ITSOpenIcon. */
	public final static String ITSOpenIcon = "resources/open.gif";

	/** The Constant ITSNewIcon. */
	public final static String ITSNewIcon = "resources/new.gif";

	/** The Constant ITSSelectionIcon. */
	public final static String ITSSelectionIcon = "resources/selection.gif";

	/** The Constant ITSNewActivityIcon. */
	public final static String ITSNewActivityIcon = "resources/newActivity.gif";

	/** The Constant ITSNewMaterialIcon. */
	public final static String ITSNewMaterialIcon = "resources/newMaterial.gif";
	
	public final static String ITSNewCommentIcon = "resources/newComment.gif";

	/** The Constant ITSNewRelationIcon. */
	public final static String ITSNewRelationIcon = "resources/newRelation.gif";

	/** The Constant ITSZoomInIcon. */
	public final static String ITSZoomInIcon = "resources/zoomIN.gif";

	/** The Constant ITSZoomOutIcon. */
	public final static String ITSZoomOutIcon = "resources/zoomOUT.gif";

	/** The Constant ITSZoomIcon. */
	public final static String ITSZoomIcon = "resources/zoom.gif";

	/** The Constant ITSTrashIcon. */
	public final static String ITSTrashIcon = "resources/trash.gif";
	public final static String ITSTutorialIcon = "resources/tutorial.gif";

	public static Color ITSDarkSandColor = new Color(161, 161, 161);
	public static Color ITSSandColor = new Color(244, 244, 244);
	public static Color ITSActiveColor = ITSSandColor;
	public static Color ITSUNActiveColor = new Color(222, 222, 222);
	public static Color ITSBlueColor = new Color(96, 125, 191);
	public static Color ITSPurple = new Color(162, 38, 207);
	public static Color ITSPurpleTransparent = new Color(162, 38, 207, 100);
	/** The MAXIMU m_ LO m_ DESCRIPTIO n_ SIZE. */
	public static Dimension MAXIMUM_LOM_DESCRIPTION_SIZE = new Dimension(250,
			2000);

	/** The its tool bar. */
	protected JToolBar itsToolBar;

	/** The its main canvas. */
	protected PCanvas itsMainCanvas;

	/** The its preview canvas. */
	protected PCanvas itsPreviewCanvas;

	/** The its zoom panel. */
	protected Box itsZoomPanel;

	/** The its model group. */
	protected ModelGroup itsModelGroup;

	/** The its info pane. */
	protected JSplitPane itsInfoPane;

	/** The its preview localizator layer. */
	protected PLayer itsPreviewLocalizatorLayer;

	/** The its helper display. */
	protected HelperDisplay itsHelperDisplay;

	/** The its activity state. */
	protected Box itsActivityState;

	/** The its attribute tree. */
	protected LOMAttributeTree itsAttributeTree;

	/** The its selection mode. */
	protected SelectionMode itsSelectionMode;

	/** The its insert generic activity mode. */
	protected InsertGenericActivityMode itsInsertGenericActivityMode;

	/** The its insert generic material mode. */
	protected InsertGenericMaterialMode itsInsertGenericMaterialMode;

	protected InsertCommentMode itsInsertCommentMode;
	
	/** The its insert edge mode. */
	protected InsertEdgeMode itsInsertEdgeMode;

	/** The its go upper button. */
	protected JButton itsGoUpperButton;

	/** keep trace of the objects asking for activity state. */
	protected Set<Runnable> itsActivityLocks = new HashSet<Runnable>();

	/**
	 * method init may be called after construction.
	 */
	public LMUI() {
		super("LessonMapper2");
		initMainCanvas();
		initPreviewCanvas();
		initZoomPanel();
		initActivityState();
		itsHelperDisplay = HelperDisplay.getInstance();
	}

	/**
	 * this method may be called after construction.
	 */
	public void init() {
		initInfoPane();
		resetMainCanvas();
		resetPreviewCanvas();
		initConstantInteractionModes();
		initExclusiveActions();
		initToolBar();
		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(itsToolBar, BorderLayout.WEST);
		getContentPane().add(itsMainCanvas, BorderLayout.CENTER);
		JPanel thePane = new JPanel();
		thePane.setLayout(new BorderLayout());
		thePane.setBackground(null);
		thePane.add(itsInfoPane, BorderLayout.CENTER);
		Box theZoomBox = Box.createHorizontalBox();
		theZoomBox.add(itsZoomPanel);
		theZoomBox.add(itsPreviewCanvas);
		thePane.add(theZoomBox, BorderLayout.NORTH);
		thePane.setMaximumSize(MAXIMUM_LOM_DESCRIPTION_SIZE);
		thePane.setPreferredSize(MAXIMUM_LOM_DESCRIPTION_SIZE);
		getContentPane().add(thePane, BorderLayout.EAST);
		Box theStatusBox = Box.createHorizontalBox();
		theStatusBox.add(itsHelperDisplay);
		theStatusBox.add(Box.createHorizontalGlue());
		theStatusBox.add(itsActivityState);
		getContentPane().add(theStatusBox, BorderLayout.SOUTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		pack();

		setSize(1000, 800);
		setLocationByPlatform(true);
		setVisible(true);

		itsInfoPane.setDividerLocation(itsInfoPane.getHeight()
				- LOMAttributeTree.ITSHeight + itsInfoPane.getDividerSize());

		// itsAttributeTree.setDividerLocation(0.1);

		// itsInfoPane.setDividerLocation(0.15);

	}

	/**
	 * this method init the main canvas.
	 */
	public void initMainCanvas() {
		itsMainCanvas = new PCanvasWithHelper(LessonMapper2.getInstance()
				.getLangComment("mainCanvas"));
		itsMainCanvas.setBackground(ITSDarkSandColor);
		itsMainCanvas.setBorder(new LineBorder(Color.black));
		itsMainCanvas.setPreferredSize(new Dimension(700, 700));
		itsMainCanvas.setDropTarget(new ExternalDropManager(itsMainCanvas));
	}

	/**
	 * Reset main canvas.
	 */
	public void resetMainCanvas() {
		for (int i = 0; i < itsMainCanvas.getCamera().getLayerCount(); i++) {
			itsMainCanvas.getCamera().removeLayer(i);
		}
		if (LessonMapper2.getInstance().getActiveProject() != null)
			itsMainCanvas.getCamera().addLayer(
					LessonMapper2.getInstance().getUpperActivity());
	}

	/**
	 * This method init the previewcanvas. Since the preview canvas depends on
	 * the main canvas main canvas should be initialized before preview canvas
	 */
	public void initPreviewCanvas() {
		itsPreviewCanvas = new PCanvasWithHelper(LessonMapper2.getInstance()
				.getLangComment("previewCanvas"));
		itsPreviewLocalizatorLayer = itsPreviewCanvas.getLayer();
		// add the manin canvas layer bottom in the hierarchy
		itsPreviewCanvas.setBorder(new LineBorder(Color.black));
		itsPreviewCanvas.setBackground(ITSDarkSandColor);
		itsPreviewCanvas.setPreferredSize(new Dimension(150, 150));
		itsPreviewLocalizatorLayer.addChild(new PreviewLocalizer(itsMainCanvas
				.getCamera()));
		itsPreviewCanvas.setPanEventHandler(null);
		itsPreviewCanvas.setZoomEventHandler(null);
		itsPreviewCanvas.addInputEventListener(new PInputEventListener() {
			public void processEvent(PInputEvent aEvent, int aType) {
				if (aType == MouseEvent.MOUSE_CLICKED) {
					LessonMapper2.getInstance().goUpperLayer();
				}
			}
		});
		itsPreviewCanvas.setMaximumSize(MAXIMUM_LOM_DESCRIPTION_SIZE);

	}

	/**
	 * Reset preview canvas.
	 */
	public void resetPreviewCanvas() {
		PCamera theCamera = itsPreviewCanvas.getCamera();
		for (int i = 0; i < theCamera.getLayerCount(); i++) {
			if (theCamera.getLayer(i) != itsPreviewLocalizatorLayer)
				theCamera.removeLayer(i);
		}
		if (LessonMapper2.getInstance().getActiveProject() != null)
			theCamera.addLayer(0, LessonMapper2.getInstance()
					.getUpperActivity());
	}

	/**
	 * Inits the zoom panel.
	 */
	public void initZoomPanel() {
		itsZoomPanel = Box.createVerticalBox();
		// itsZoomPanel.setBackground(ITSSandColor);
		JButton theZoom = new JButton(new ImageIcon(LMUI.class
				.getResource(ITSZoomIcon)));
		theZoom.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoom();
			}
		});
		JButton theZoomIn = new JButton(new ImageIcon(LMUI.class
				.getResource(ITSZoomInIcon)));
		theZoomIn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomIn();
			}
		});
		JButton theZoomOut = new JButton(new ImageIcon(LMUI.class
				.getResource(ITSZoomOutIcon)));
		theZoomOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				zoomOut();
			}
		});
		itsZoomPanel.add(theZoom);
		itsZoomPanel.add(theZoomIn);
		itsZoomPanel.add(theZoomOut);
	}

	/**
	 * this method init the Info pane.
	 */
	public void initInfoPane() {
		itsInfoPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);/*
																 * {
																 * //setDivider
																 * locaation is
																 * @Override
																 * public void
																 * setDividerLocation(int
																 * aLocation) {
																 * super.setDividerLocation(aLocation);
																 * itsAttributeTree.setPreferredSize(new
																 * Dimension(this.getWidth(),this.getHeight()-aLocation)); } };
																 */
		itsInfoPane.setBackground(ITSSandColor);
		LOMAttributeBaseSet theBaseSet = new LOMAttributeBaseSet();
		// Dimension theDimension = new
		// Dimension(LMUI.MAXIMUM_LOM_DESCRIPTION_SIZE.width, 350);
		// theBaseSet.setPreferredSize(theDimension);
		itsInfoPane.add(theBaseSet);
		itsAttributeTree = new LOMAttributeTree();
		itsInfoPane.add(itsAttributeTree);

	}

	/**
	 * this method init theBasic interactionMode of the main Canvas.
	 */
	public void initConstantInteractionModes() {
		itsMainCanvas.addInputEventListener(new ZoomToActiveLayer());
		itsMainCanvas.addInputEventListener(new GoUpperLayerHandler());
	}

	/**
	 * this method inits the exclusive actions i.e. aggregate them to a
	 * modelGroup
	 */
	protected void initExclusiveActions() {
		itsSelectionMode = SelectionMode.ITSInstance;
		itsInsertGenericMaterialMode = new InsertGenericMaterialMode();
		itsInsertGenericActivityMode = new InsertGenericActivityMode();
		itsInsertCommentMode = new InsertCommentMode();
		itsInsertEdgeMode = InsertEdgeMode.ITSInstance;

		itsModelGroup = new ModelGroup();
		itsModelGroup.add(itsSelectionMode);
		itsModelGroup.add(itsInsertGenericMaterialMode);
		itsModelGroup.add(itsInsertGenericActivityMode);
		itsModelGroup.add(itsInsertEdgeMode);
		itsModelGroup.add(itsInsertCommentMode);
	}

	/**
	 * Inits the tool bar.
	 */
	private void initToolBar() {
		itsToolBar = new JToolBar(JToolBar.VERTICAL);
		itsToolBar.add(itsSelectionMode.createToggleButton());
		itsToolBar.add(Box.createVerticalStrut(25));
		itsToolBar.add(itsInsertGenericMaterialMode.createToggleButton());
		itsToolBar.add(itsInsertGenericActivityMode.createToggleButton());
		itsToolBar.add(itsInsertEdgeMode.createToggleButton());
		itsToolBar.add(itsInsertCommentMode.createToggleButton());
		itsToolBar.add(Box.createVerticalStrut(25));

		itsSelectionMode.getModel().setSelected(true);

		itsToolBar.add(Box.createVerticalStrut(25));

		JButton theCopyButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSCopyIcon)), LessonMapper2.getInstance()
				.getLangComment("copy"));
		theCopyButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().copy();
			};
		});
		itsToolBar.add(theCopyButton);

		JButton theCutButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSCutIcon)), LessonMapper2.getInstance()
				.getLangComment("cut"));
		theCutButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().cut();
			};
		});
		itsToolBar.add(theCutButton);

		JButton thePasteButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSPasteIcon)), LessonMapper2.getInstance()
				.getLangComment("paste"));
		thePasteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().paste();
			};
		});
		itsToolBar.add(thePasteButton);

		itsToolBar.add(Box.createVerticalStrut(25));

		JButton theTrashButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSTrashIcon)), LessonMapper2.getInstance()
				.getLangComment("delete"));
		theTrashButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int isOK = JOptionPane.showConfirmDialog(LMUI.this,
						LessonMapper2.getInstance().getLangComment(
								"removeWarning"), "Warning",
						JOptionPane.YES_NO_OPTION);
				if (isOK == JOptionPane.YES_OPTION)
					LessonMapper2.getInstance().removeSelection();
			};
		});
		itsToolBar.add(theTrashButton);

		itsToolBar.add(Box.createVerticalStrut(25));

		JButton theNewButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSNewIcon)), LessonMapper2.getInstance()
				.getLangComment("new"));
		theNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().newProject();
			};
		});
		itsToolBar.add(theNewButton);

		JButton theLoadButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSOpenIcon)), LessonMapper2.getInstance()
				.getLangComment("load"));
		theLoadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().loadProject();
			};
		});
		itsToolBar.add(theLoadButton);

		JButton theSaveButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSSaveIcon)), LessonMapper2.getInstance()
				.getLangComment("save"));
		theSaveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().saveProject();
			};
		});
		itsToolBar.add(theSaveButton);

		JButton theSaveAsButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSSaveAsIcon)), LessonMapper2.getInstance()
				.getLangComment("saveAs"));
		theSaveAsButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().saveProjectAs();
			};
		});
		itsToolBar.add(theSaveAsButton);

		JButton theSaveOnDBButton = new ButtonWithHelper(new ImageIcon(
				LMUI.class.getResource(ITSSaveDBIcon)), LessonMapper2
				.getInstance().getLangComment("saveOnDB"));
		theSaveOnDBButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().saveProjectOnDB();
			};
		});
		itsToolBar.add(theSaveOnDBButton);

	/*	JButton theQueryButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSQueryIcon)), LessonMapper2.getInstance()
				.getLangComment("query"));
		theQueryButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().queryRepositoryWithSelection();
			};
		});

		itsToolBar.add(Box.createVerticalStrut(25));
		itsToolBar.add(theQueryButton);
*/
		JButton theTutorialButton = new ButtonWithHelper(new ImageIcon(LMUI.class
				.getResource(ITSTutorialIcon)), LessonMapper2.getInstance()
				.getLangComment("tutorial"));
		theTutorialButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				LessonMapper2.getInstance().loadTutorial();
			};
		});

		itsToolBar.add(Box.createVerticalStrut(25));
		itsToolBar.add(theTutorialButton);

		
		
		if (false &&SuggestionCloudTest.ISACTIVE){
			final JButton theToggleButton = new JButton("C");
			theToggleButton.addActionListener(new ActionListener(){
				public void actionPerformed(ActionEvent aE) {
					theToggleButton.setText(""+SuggestionCloudTest.getInstance().printToggleCloudSession());
				}
			});
			theToggleButton.setText(""+SuggestionCloudTest.getInstance().printToggleCloudSession());
			itsToolBar.add(theToggleButton);
		}
		/*
		 * itsGoUpperButton = new JButton("go upper");
		 * itsGoUpperButton.addActionListener(this);
		 * itsToolBar.add(itsGoUpperButton);
		 */

	}

	/**
	 * Inits the activity state.
	 */
	public void initActivityState() {
		itsActivityState = Box.createHorizontalBox();

		JProgressBar theProgressBar = new JProgressBar();
		theProgressBar.setIndeterminate(true);
		itsActivityState.add(theProgressBar);
		// JLabel theStateLabel = new
		// JLabel(LessonMapper2.getInstance().getLangComment("activityState"));
		// theStateLabel.setBackground(Color.WHITE);
		// itsActivityState.add(theStateLabel);
	}

	/**
	 * Show activity state.
	 * 
	 * @param aLock
	 *            the a lock
	 * @param isShown
	 *            the is shown
	 */
	public void showActivityState(boolean isShown, Runnable aLock) {
		if (isShown) {
			itsActivityLocks.add(aLock);
			itsActivityState.setVisible(true);
		} else {
			itsActivityLocks.remove(aLock);
			if (itsActivityLocks.isEmpty())
				itsActivityState.setVisible(false);
		}
	}

	/**
	 * Switch to selection mode.
	 */
	public void switchToSelectionMode() {
		itsSelectionMode.getModel().setSelected(true);
	}

	/**
	 * Zoom in.
	 */
	public void zoomIn() {
		ZoomToActiveLayer.zoom(itsMainCanvas.getCamera(),
				1 + LessonMapper2.ITSZoomIncrement);
		// ZoomToActiveLayer.zoom(itsPreviewCanvas.getCamera(),LessonMapper2.ITSZoomIncrement);
	}

	/**
	 * Zoom out.
	 */
	public void zoomOut() {
		ZoomToActiveLayer.zoom(itsMainCanvas.getCamera(),
				1 - LessonMapper2.ITSZoomIncrement);
		// ZoomToActiveLayer.zoom(itsPreviewCanvas.getCamera(),-LessonMapper2.ITSZoomIncrement);
	}

	/**
	 * Zoom.
	 */
	public void zoom() {
		ZoomToActiveLayer.zoomtoActiveFor(itsMainCanvas.getCamera());
		// ZoomToActiveLayer.zoom(itsPreviewCanvas.getCamera(),-LessonMapper2.ITSZoomIncrement);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */

	/**
	 * Gets the tool bar.
	 * 
	 * @return the tool bar
	 */
	public JToolBar getToolBar() {
		return itsToolBar;
	}

	/**
	 * Gets the main canvas.
	 * 
	 * @return the main canvas
	 */
	public PCanvas getMainCanvas() {
		return itsMainCanvas;
	}

	/**
	 * Gets the preview canvas.
	 * 
	 * @return the preview canvas
	 */
	public PCanvas getPreviewCanvas() {
		return itsPreviewCanvas;
	}

	/**
	 * The Class ButtonWithHelper.
	 */
	public class ButtonWithHelper extends JButton implements HelperSupport {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1789804302067612256L;

		/** The its help text. */
		protected String itsHelpText;

		/**
		 * The Constructor.
		 * 
		 * @param aIcon
		 *            the a icon
		 * @param aHelpText
		 *            the a help text
		 */
		public ButtonWithHelper(Icon aIcon, String aHelpText) {
			super(aIcon);
			itsHelpText = aHelpText;
			setToolTipText(itsHelpText);
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
		 */
		public String getHelp() {
			return itsHelpText;
		}

	}

	/**
	 * The Class PCanvasWithHelper.
	 */
	public class PCanvasWithHelper extends PCanvas implements HelperSupport {

		/** The Constant serialVersionUID. */
		private static final long serialVersionUID = 1L;

		/** The its help text. */
		protected String itsHelpText;

		/**
		 * The Constructor.
		 * 
		 * @param aHelpText
		 *            the a help text
		 */
		public PCanvasWithHelper(String aHelpText) {
			itsHelpText = aHelpText;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see dcc.lessonMapper2.ui.HelperSupport#getHelp()
		 */
		public String getHelp() {
			return itsHelpText;
		}

	}

	protected void processWindowEvent(WindowEvent e) {
		if (e.getID() == WindowEvent.WINDOW_CLOSING) {
				LessonMapper2.getInstance().close();
			}
	}

	public void showTerminationFrame() {
		JFrame thePresentation = new JFrame();
		Box theBox = Box.createVerticalBox();
		JLabel theLabel = new JLabel("<html><b><i>"+LessonMapper2.getInstance()
				.getLangComment("closingLM")+"...</i></b></html>");
		theLabel.setBackground(ITSSandColor);
		theBox.add(Box.createVerticalGlue());
		theBox.add(theLabel);
		theBox.add(
				Box.createVerticalStrut(50));
		theBox.add(itsActivityState);
		theBox.add(Box.createVerticalGlue());
		theBox.setBorder(BorderFactory.createEtchedBorder(ITSBlueColor, ITSDarkSandColor));
		thePresentation.getContentPane().add(theBox);
		itsActivityState.setVisible(true);
		thePresentation.setBackground(ITSSandColor);
		thePresentation.setResizable(false);
		thePresentation.setUndecorated(true);
		thePresentation.pack();
		Dimension theScreenSize = Toolkit.getDefaultToolkit()
				.getScreenSize();
		int size = 200;
		thePresentation.setSize(size, size);
		thePresentation.setLocation(
				(int) (theScreenSize.getWidth() - size) / 2,
				(int) (theScreenSize.getHeight() - size) / 2);
		thePresentation.setVisible(true);
	}

	public static void main(String[] args) {
		new LMUI().showTerminationFrame();
	}
	
	
}
