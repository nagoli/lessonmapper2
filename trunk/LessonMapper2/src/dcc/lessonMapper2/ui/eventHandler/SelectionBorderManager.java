/*
 * Created on Aug 10, 2005
 * 
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import lessonMapper.lom.LOM;
import util.Couple;
import util.ui.ColorMosaic;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.lom.LOMTitleUI;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolox.event.PNotification;
import edu.umd.cs.piccolox.event.PNotificationCenter;

/**
 * The Class SelectionBorderManager.
 * 
 * @author omotelet
 * 
 * aspect to manage selection border it decorate nodes when they are selected by
 * the selection handler or by the insertEdgeHandler Only PNodes implementing
 * the SelectionableNode interface may be decorated SelectionableLOM are
 * selectioned with different colors so that multiple instances of LOM can be
 * compared.
 */
public class SelectionBorderManager {

	/** The LO m_ SELECTIO n_ CHANGE d_ EVENT. */
	public static String LOM_SELECTION_CHANGED_EVENT = "LOM_SELECTION_CHANGED_EVENT";

	/** The its stroke size. */
	public static int itsStrokeSize = 2;

	/** The its insets. */
	public static int itsInsets = 8;

	/** The ITS instance. */
	public static SelectionBorderManager ITSInstance = new SelectionBorderManager();

	/** The its selections. */
	protected Set<SelectionableNode> itsSelections;

	/** The its active LOM map. */
	protected Set<Couple<LOM, Color>> itsActiveLOMMap;

	/** The its LOM map. */
	protected HashMap<LOM, Couple<LOM,Color>> itsLOMMap;

	/**
	 * Gets the instance.
	 * 
	 * @return the instance
	 */
	public static SelectionBorderManager getInstance() {
		return ITSInstance;
	}

	/**
	 * The Constructor.
	 */
	protected SelectionBorderManager() {
		itsSelections = new HashSet<SelectionableNode>();
		itsActiveLOMMap = new HashSet<Couple<LOM, Color>>();
		itsLOMMap = new HashMap<LOM, Couple<LOM,Color>>();
	}

	/**
	 * this method updates the itsSelections and itsActiveLOMMap to match with
	 * the current selection
	 * 
	 * when there is no selection setDefaultActiveLOMMap is called.
	 * 
	 * @param aNotification
	 *            the a notification
	 */
	public void selectionChanged(PNotification aNotification) {
 
		if (aNotification != null) {
			SelectionHandler theHandler = (SelectionHandler) aNotification
					.getObject();
			Collection theSelectionCollection = theHandler.getSelection();
			List<SelectionableNode> theNewSelections = new ArrayList<SelectionableNode>();
			for (Iterator iter = theSelectionCollection.iterator(); iter
					.hasNext();) {
				Object theElement = iter.next();
				if (theElement instanceof SelectionableNode) {
					theNewSelections.add((SelectionableNode) theElement);
				}
			}
			Set<SelectionableNode> theRemovedSelections = new HashSet<SelectionableNode>(
					itsSelections);
			theRemovedSelections.removeAll(theNewSelections);

			Set<SelectionableNode> theAddedSelections = new HashSet<SelectionableNode>(
					theNewSelections);
			theAddedSelections.removeAll(itsSelections);

			// remove the obsolete nodes
			itsSelections.removeAll(theRemovedSelections);
			for (SelectionableNode theNode : theRemovedSelections) {
				if (theNode instanceof SelectionableLOM) {
					SelectionableLOM theLOM = (SelectionableLOM) theNode;
					itsActiveLOMMap.remove(getLOMColor(theLOM
							.getLOM()));
				}
			}

			// add the new selected nodes
			itsSelections.addAll(theAddedSelections);
			for (SelectionableNode theNode : theAddedSelections) {
				if (theNode instanceof SelectionableLOM) {
					SelectionableLOM theLOM = (SelectionableLOM) theNode;
					itsActiveLOMMap.add(getLOMColor(theLOM
							.getLOM()));
				}
			}
		}
		else {
			itsActiveLOMMap.clear();
			setDefaultActiveLOMMap();
		}
		//if (itsActiveLOMMap.isEmpty())
			//setDefaultActiveLOMMap();
		//repaint the title of the active layer for enuring repaint consistency
		LOMTitleUI theTitleUI = LessonMapper2.getInstance().getActiveLayerTitleUI();
		if (theTitleUI != null) theTitleUI.repaint();
		
		PNotificationCenter.defaultCenter().postNotification(
				LOM_SELECTION_CHANGED_EVENT, this);
	}

	/**
	 * default LOM Map contains the current active layer.
	 */
	public void setDefaultActiveLOMMap() {
		PLayer theActiveLayer = LessonMapper2.getInstance().getActiveLayer();
		if (theActiveLayer instanceof SelectionableLOM) {
			SelectionableLOM theLOMNode = (SelectionableLOM) theActiveLayer;
			itsActiveLOMMap.add( getLOMColor(theLOMNode
					.getLOM()));
		}
	}

	/**
	 * return the list of selected LOM and an associated color.
	 * 
	 * @return the selected LOM map
	 */
	public Set<Couple<LOM, Color>> getSelectedLOMMap() {
		return itsActiveLOMMap;
	}

	/**
	 * return the color to associate with a LOM object take form LOMMap or
	 * create it if not yet created.
	 * 
	 * @param aLOM
	 *            the a LOM
	 * 
	 * @return the LOM color
	 */
	public Couple<LOM,Color> getLOMColor(LOM aLOM) {
		if (!itsLOMMap.containsKey(aLOM)) {
			Color theColor = ColorMosaic.nextRandomColor();
			itsLOMMap.put(aLOM, new Couple<LOM,Color>(aLOM,theColor));
		}
		return itsLOMMap.get(aLOM);
	}

	/**
	 * return true of this Node is selected or not.
	 * 
	 * @param aNode
	 *            the a node
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected(PNode aNode) {
		return itsSelections.contains(aNode);
	}

	public boolean isSelected(SelectionableLOM aNode) {
		return itsSelections.contains(aNode);
	}
	
	
}
