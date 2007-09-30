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

import java.util.Stack;

import dcc.lessonMapper2.fileManager.LocalRepository;
import dcc.lessonMapper2.ui.eventHandler.ZoomToActiveLayer;
import dcc.lessonMapper2.ui.graph.element.ContainerNode;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import edu.umd.cs.piccolo.PLayer;

/**
 * The Class LMProject.
 */
public class LMProject {

	/** The its previous active layers. */
	protected Stack<ContainerNode> itsPreviousActiveLayers = new Stack<ContainerNode>();

	/** The its active layer. */
	protected ContainerNode itsActiveLayer;

	/** The its upper activity. */
	protected GenericActivity itsUpperActivity;

	/** The its archive path. */
	protected String itsArchivePath = LessonMapper2.ITSDefaultTempDirectory;

	protected boolean isDefaultArchivePath = true;
	
	/** The its old archive path. */
	protected String itsOldArchivePath;

	/** The its repository. */
	protected LocalRepository itsRepository;

	/** The is archive path changing. */
	protected boolean isArchivePathChanging = false;

	protected boolean isTutorial =false;
	
	/**
	 * The Constructor.
	 * 
	 * @param aUpperActivity
	 *            the a upper activity
	 */
	public LMProject(GenericActivity aUpperActivity) {
	
		itsUpperActivity = aUpperActivity;
		itsUpperActivity.setLMProject(this);
		itsRepository = new LocalRepository(this);
	}

	public LMProject(GenericActivity aUpperActivity, boolean isTutorial) {
		this(aUpperActivity);
		this.isTutorial =isTutorial;
	}
	
	
	/**
	 * The Constructor.
	 */
	public LMProject() {
		itsRepository = new LocalRepository(this);
	}

	public LMProject(boolean isTutorial) {
		itsRepository = new LocalRepository(this);
		this.isTutorial = isTutorial;
	}
	
	
	/**
	 * Sets the upper activity.
	 * 
	 * @param aUpperActivity
	 *            the upper activity
	 */
	public void setUpperActivity(GenericActivity aUpperActivity) {
		itsUpperActivity = aUpperActivity;
		itsUpperActivity.setLMProject(this);
	}

	/**
	 * Reset layers.
	 */
	public void resetLayers() {
		itsPreviousActiveLayers = new Stack<ContainerNode>();
		itsActiveLayer = null;
	}

	/**
	 * Enter layer.
	 * 
	 * @param aLayer
	 *            the a layer
	 */
	public void enterLayer(PLayer aLayer) {
		if (aLayer instanceof ContainerNode) {
			if (itsActiveLayer != null)
				itsPreviousActiveLayers.push(itsActiveLayer);
			itsActiveLayer = (ContainerNode) aLayer;
			itsActiveLayer.setActive(true);
			updateActiveLayer();
			LessonMapper2.getInstance().getSelectionHandler().unselectAll();
			LessonMapper2.getInstance().getSelectionHandler().select(aLayer);
		}
	}

	/**
	 * Go upper layer.
	 */
	public void goUpperLayer() {
		if (!itsPreviousActiveLayers.isEmpty()) {
			ContainerNode theContainerNode = itsActiveLayer;
			itsActiveLayer.setActive(false);
			itsActiveLayer = itsPreviousActiveLayers.pop();
			updateActiveLayer();
			LessonMapper2.getInstance().getSelectionHandler().unselectAll();
			LessonMapper2.getInstance().getSelectionHandler().select(theContainerNode);
			
		}
		// if theActiveLayer is not a container node it means that it reached
		// the upperlevel is activeLayer
		else
			return;
	}

	/**
	 * Update active layer.
	 */
	public void updateActiveLayer() {
		ZoomToActiveLayer.zoomtoActiveFor(LessonMapper2.getInstance()
				.getMainCanvas().getCamera());
		ZoomToActiveLayer.zoomtoParentActiveFor(LessonMapper2.getInstance()
				.getPreviewCanvas().getCamera());
		
		//SelectionBorderManager.getInstance().selectionChanged(null);
	}

	/**
	 * Gets the active layer.
	 * 
	 * @return Returns the activeLayer.
	 */
	public ContainerNode getActiveLayer() {
		return itsActiveLayer;
	}

	/**
	 * Gets the previous active layers.
	 * 
	 * @return Returns the previousActiveLayers.
	 */
	public Stack getPreviousActiveLayers() {
		return itsPreviousActiveLayers;
	}

	/**
	 * Gets the upper activity.
	 * 
	 * @return the upper activity
	 */
	public GenericActivity getUpperActivity() {
		if (itsUpperActivity == null) {
			itsUpperActivity = new GenericActivity();
		}
		return itsUpperActivity;
	}

	/**
	 * Gets the archive path.
	 * 
	 * @return the archive path
	 */
	public String getArchivePath() {
		return itsArchivePath;
	}

	/**
	 * method for changing the archive path of a lmproject
	 * isArchivePathChanging() and getOldArchivePath may be use to manage
	 * changes due to modifying the archive path.
	 * 
	 * @param aArchivePath
	 *            the archive path
	 */
	public void setArchivePath(String aArchivePath) {
		if (!itsArchivePath.equals(aArchivePath))
			itsOldArchivePath = itsArchivePath;
		itsArchivePath = aArchivePath;
		// launch checking of material and thumb location
		isArchivePathChanging = true;
		if (itsUpperActivity != null)
			itsUpperActivity.setLMProject(this);
		isArchivePathChanging = false;
		isDefaultArchivePath=false;
		itsOldArchivePath = null;
	}

	public boolean isDefaultArchivePath() {
		return isDefaultArchivePath;
	} 
	
	
	/**
	 * Checks if is archive path changing.
	 * 
	 * @return true, if is archive path changing
	 */
	public boolean isArchivePathChanging() {
		return isArchivePathChanging;
	}

	/**
	 * Gets the old archive path.
	 * 
	 * @return the old archive path
	 */
	public String getOldArchivePath() {
		return itsOldArchivePath;
	}

	/**
	 * Gets the repository.
	 * 
	 * @return the repository
	 */
	public LocalRepository getRepository() {
		return itsRepository;
	}
}