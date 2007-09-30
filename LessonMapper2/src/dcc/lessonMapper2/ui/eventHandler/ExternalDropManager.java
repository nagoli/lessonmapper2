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
package dcc.lessonMapper2.ui.eventHandler;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTarget;
import java.io.File;
import java.net.URL;
import java.util.List;

import javax.swing.JOptionPane;

import util.ui.PickPathUtil;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.lom.GenericActivity;
import dcc.lessonMapper2.ui.graph.element.lom.GenericMaterial;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PCanvas;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * The Class ExternalDropManager.
 */
public class ExternalDropManager extends DropTarget {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The its main canvas. */
	PCanvas itsMainCanvas;

	/**
	 * The Constructor.
	 * 
	 * @param aCanvas
	 *            the a canvas
	 */
	public ExternalDropManager(PCanvas aCanvas) {
		itsMainCanvas = aCanvas;
	}

	/**
	 * manage drop event on the PCanvas if the drop of a url is done over an
	 * existing material, the url is associated with this material if it is over
	 * the current container a new generic material is aggregated.
	 * 
	 * @param dtde
	 *            the dtde
	 */
	public synchronized void drop(java.awt.dnd.DropTargetDropEvent dtde) {
		dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE
				| DnDConstants.ACTION_LINK);
		DataFlavor[] flavors = dtde.getCurrentDataFlavors();
		String fileURL = null;
		DataFlavor urlFlavor = null;
		DataFlavor listFlavor = null;
		if (flavors != null) {
			for (int i = 0; i < flavors.length; i++) {
				if (flavors[i].getRepresentationClass() == java.net.URL.class)
					urlFlavor = flavors[i];
				else if (flavors[i].getRepresentationClass() == List.class)
					listFlavor = flavors[i];
			}
			// if listFlavor is not null then the link is local
			// else we use a url

			try {

				if (listFlavor == null) {
					Object obj = dtde.getTransferable().getTransferData(
							urlFlavor);
					if (obj instanceof URL)
						fileURL = ((URL) obj).toExternalForm();
				} else {
					Object obj = dtde.getTransferable().getTransferData(
							listFlavor);
					if (obj instanceof List) {
						Object theFile = ((List) obj).get(0);
						if (theFile instanceof File)
							fileURL = ((File) theFile).getAbsolutePath();
					}
				}

			} catch (Throwable t) {
				t.printStackTrace();
			}

			if (fileURL != null) {
				Point thePoint = dtde.getLocation();
				PCamera theCamera = itsMainCanvas.getCamera();
				PPickPath thePickPath = itsMainCanvas.getCamera().pick(
						thePoint.x, thePoint.y, 1);
				PNode thePNode = PickPathUtil.getFirstNodeOfType(thePickPath,
						GenericMaterial.class);
				if (thePNode != null) {
					GenericMaterial theMaterial = (GenericMaterial) thePNode;
					if (theMaterial.getMaterial() != null) {
						if (JOptionPane.showConfirmDialog(LessonMapper2
								.getInstance().getUI(), LessonMapper2
								.getInstance().getLangComment(
										"exisitingMaterialWarning"), "",
								JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION)
							theMaterial.setMaterial(fileURL);
					} else
						theMaterial.setMaterial(fileURL);
				} else {
					PLayer theLayer = LessonMapper2.getInstance()
							.getActiveLayer();
					if (theLayer instanceof GenericActivity) {
						GenericActivity theActivity = (GenericActivity) theLayer;
						GenericMaterial theMaterial = new GenericMaterial();
						theMaterial.setLMProject(theActivity.getLMProject());
						theMaterial.setMaterial(fileURL);
						theActivity.addChild(theMaterial);
						try {
							Point thePointCopy = thePoint;

							theCamera.getViewTransform().inverseTransform(
									thePointCopy, thePointCopy);
							theActivity.globalToLocal(thePointCopy);
							theMaterial.setOffset(thePointCopy);
						} catch (Exception e) {
							e.printStackTrace();
							theMaterial.setOffset(new Point(2, 2));
						}

					}
				}
			}
		}
		try {
			dtde.dropComplete(true);
		} catch (Throwable t) {
			System.out.println("???? " + t);
		}
	}

}
