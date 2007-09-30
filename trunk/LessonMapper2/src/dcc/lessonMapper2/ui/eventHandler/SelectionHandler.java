/*
 * Created on Aug 9, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

/**
 * @author omotelet
 * 
 * This code is based on the class PSelectionEventHandler of Piccolo It was
 * rewritten in order to assume the zoom level. // For usability reason the
 * marquee function was removed // and drag event match with
 * PDragEventHandlerExclusive
 * 
 */
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.MotionNode;
import dcc.lessonMapper2.ui.graph.element.SelectionableNode;
import dcc.lessonMapper2.ui.graph.element.lom.LOMTitleUI;
import edu.umd.cs.piccolo.PCamera;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PDragSequenceEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventFilter;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PDimension;
import edu.umd.cs.piccolo.util.PNodeFilter;
import edu.umd.cs.piccolox.event.PNotificationCenter;

/**
 * The Class SelectionHandler.
 */
@SuppressWarnings("all")
public class SelectionHandler extends PDragSequenceEventHandler{

	/** The Constant SELECTION_CHANGED_NOTIFICATION. */
	public static final String SELECTION_CHANGED_NOTIFICATION = "SELECTION_CHANGED_NOTIFICATION";

	/** The Constant DASH_WIDTH. */
	final static int DASH_WIDTH = 5;

	/** The Constant NUM_STROKES. */
	final static int NUM_STROKES = 10;

	/** The selection. */
	private HashMap selection = null; // The current selection

	/** The selectable parents. */
	private List selectableParents = null; // List of nodes whose children can
	// be selected

	/** The marquee. */
	private PPath marquee = null;

	/** The marquee parent. */
	private PNode marqueeParent = null; // Node that marquee is added to as a
	// child

	/** The presspt. */
	private Point2D presspt = null;

	/** The canvas press pt. */
	private Point2D canvasPressPt = null;

	/** The stroke num. */
	private float strokeNum = 0;

	/** The strokes. */
	private Stroke[] strokes = null;

	/** The all items. */
	private HashMap allItems = null; // Used within drag handler temporarily

	/** The unselect list. */
	private ArrayList unselectList = null; // Used within drag handler
	// temporarily

	/** The marquee map. */
	private HashMap marqueeMap = null;

	/** The press node. */
	private PNode pressNode = null; // Node pressed on (or null if none)

	/** The delete enabled. */
	private boolean deleteEnabled = true; // True if delete is enabled

	/** The marquee paint. */
	private Paint marqueePaint;

	/** The marquee paint transparency. */
	private float marqueePaintTransparency = 1.0f;

	/** The its camera. */
	protected PCamera itsCamera;

	/**
	 * Creates a selection event handler.
	 * 
	 * @param aCamera
	 *            the a camera
	 * @param selectableParent
	 *            The node whose children will be selected by this event
	 *            handler. The camera will define the size of the stroke being
	 *            drawn
	 * @param marqueeParent
	 *            The node to which the event handler dynamically adds a marquee
	 *            (temporarily) to represent the area being selected.
	 */
	public SelectionHandler(PNode marqueeParent, PNode selectableParent,
			PCamera aCamera) {
		this.marqueeParent = marqueeParent;
		itsCamera = aCamera;
		this.selectableParents = new ArrayList();
		this.selectableParents.add(selectableParent);
		init();
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
	}

	/**
	 * Creates a selection event handler.
	 * 
	 * @param selectableParents
	 *            A list of nodes whose children will be selected by this event
	 *            handler.
	 * @param aCamera
	 *            the a camera
	 * @param marqueeParent
	 *            The node to which the event handler dynamically adds a marquee
	 *            (temporarily) to represent the area being selected.
	 */
	public SelectionHandler(PNode marqueeParent, List selectableParents,
			PCamera aCamera) {
		itsCamera = aCamera;
		this.marqueeParent = marqueeParent;
		this.selectableParents = selectableParents;
		init();
		setEventFilter(new PInputEventFilter(InputEvent.BUTTON1_MASK));
	}

	/**
	 * reiinitialize this selectionHandler with new parameters.
	 * 
	 * @param selectableParent
	 *            the selectable parent
	 * @param marqueeParent
	 *            the marquee parent
	 */
	public void reinit(PNode marqueeParent, PNode selectableParent) {
		this.marqueeParent = marqueeParent;
		this.selectableParents = new ArrayList();
		this.selectableParents.add(selectableParent);
		init();
	}

	/**
	 * Init.
	 */
	protected void init() {
		selection = new HashMap();
		allItems = new HashMap();
		unselectList = new ArrayList();
		marqueeMap = new HashMap();
	}

	/**
	 * Inits the stroke.
	 */
	public void initStroke() {
		float theScaleFactor = 1 / (float) itsCamera.getViewScale();
		float[] dash = { DASH_WIDTH * theScaleFactor,
				DASH_WIDTH * theScaleFactor };
		strokes = new Stroke[NUM_STROKES];
		for (int i = 0; i < NUM_STROKES; i++) {
			strokes[i] = new BasicStroke(1 * theScaleFactor,
					BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1, dash, i);
		}
	}

	// /////////////////////////////////////////////////////
	// Public static methods for manipulating the selection
	// /////////////////////////////////////////////////////

	/**
	 * Select.
	 * 
	 * @param items
	 *            the items
	 */
	public void select(Collection items) {
		boolean changes = false;
		Iterator itemIt = items.iterator();
		while (itemIt.hasNext()) {
			PNode node = (PNode) itemIt.next();
			changes |= internalSelect(node);
		}
		if (changes) {
			postSelectionChanged();
		}
	}

	/**
	 * Select.
	 * 
	 * @param items
	 *            the items
	 */
	public void select(Map items) {
		select(items.keySet());
	}

	/**
	 * Internal select.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return true, if internal select
	 */
	private boolean internalSelect(PNode node) {
		

		
		selection.put(node, Boolean.TRUE);
	    //depreacated see selectionbordermanager.aj instead
		//decorateSelectedNode(node);
		return true;
	}

	/**
	 * Post selection changed.
	 */
	private void postSelectionChanged() {
		PNotificationCenter.defaultCenter().postNotification(
				SELECTION_CHANGED_NOTIFICATION, this);
	}

	/**
	 * Select.
	 * 
	 * @param node
	 *            the node
	 */
	public void select(PNode node) {
		if (internalSelect(node)) {
			postSelectionChanged();
		}
	}

	/**
	 * Decorate selected node.
	 * 
	 * @param node
	 *            the node
	 */
	public void decorateSelectedNode(PNode node) {
		// PBoundsHandle.addBoundsHandlesTo(node);
	}

	/**
	 * Unselect.
	 * 
	 * @param items
	 *            the items
	 */
	public void unselect(Collection items) {
		boolean changes = false;
		Iterator itemIt = items.iterator();
		while (itemIt.hasNext()) {
			PNode node = (PNode) itemIt.next();
			changes |= internalUnselect(node);
		}
		if (changes) {
			postSelectionChanged();
		}
	}

	/**
	 * Internal unselect.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return true, if internal unselect
	 */
	private boolean internalUnselect(PNode node) {
		
		selection.remove(node);
	    //depreacated see selectionbordermanager.aj instead
		// undecorateSelectedNode(node);
		
		return true;
	}

	/**
	 * Unselect.
	 * 
	 * @param node
	 *            the node
	 */
	public void unselect(PNode node) {
		if (internalUnselect(node)) {
			postSelectionChanged();
		}
	}

	/**
	 * Undecorate selected node.
	 * 
	 * @param node
	 *            the node
	 */
	public void undecorateSelectedNode(PNode node) {
		// PBoundsHandle.removeBoundsHandlesFrom(node);
	}

	/**
	 * Unselect all.
	 */
	public void unselectAll() {
		// Because unselect() removes from selection, we need to
		// take a copy of it first so it isn't changed while we're iterating
		ArrayList sel = new ArrayList(selection.keySet());
		unselect(sel);
	}

	/**
	 * Checks if is selected.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return true, if is selected
	 */
	public boolean isSelected(PNode node) {
		if ((node != null) && (selection.containsKey(node))) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Returns a copy of the currently selected nodes.
	 * 
	 * @return the selection
	 */
	public Collection getSelection() {
		ArrayList sel = new ArrayList(selection.keySet());
		return sel;
	}

	/**
	 * Gets a reference to the currently selected nodes. You should not modify
	 * or store this collection.
	 * 
	 * @return the selection reference
	 */
	public Collection getSelectionReference() {
		return Collections.unmodifiableCollection(selection.keySet());
	}

	/**
	 * Determine if the specified node is selectable (i.e., if it is a child of
	 * the one the list of selectable parents.
	 * 
	 * @param node
	 *            the node
	 * 
	 * @return true, if is selectable
	 */
	protected boolean isSelectable(PNode node) {
		return (node instanceof SelectionableNode);
		/*boolean selectable = false;
		
		Iterator parentsIt = selectableParents.iterator();
		while (parentsIt.hasNext()) {
			PNode parent = (PNode) parentsIt.next();
			if (parent.getChildrenReference().contains(node)) {
				selectable = true;
				break;
			} else if (parent instanceof PCamera) {
				for (int i = 0; i < ((PCamera) parent).getLayerCount(); i++) {
					PLayer layer = ((PCamera) parent).getLayer(i);
					if (layer.getChildrenReference().contains(node)) {
						selectable = true;
						break;
					}
				}
			}
		}

		return selectable;*/
	}

	// ////////////////////////////////////////////////////
	// Methods for modifying the set of selectable parents
	// ////////////////////////////////////////////////////

	/**
	 * Adds the selectable parent.
	 * 
	 * @param node
	 *            the node
	 */
	public void addSelectableParent(PNode node) {
		selectableParents.add(node);
	}

	/**
	 * Removes the selectable parent.
	 * 
	 * @param node
	 *            the node
	 */
	public void removeSelectableParent(PNode node) {
		selectableParents.remove(node);
	}

	/**
	 * Sets the selectable parent.
	 * 
	 * @param node
	 *            the selectable parent
	 */
	public void setSelectableParent(PNode node) {
		selectableParents.clear();
		selectableParents.add(node);
	}

	/**
	 * Sets the selectable parents.
	 * 
	 * @param c
	 *            the selectable parents
	 */
	public void setSelectableParents(Collection c) {
		selectableParents.clear();
		selectableParents.addAll(c);
	}

	/**
	 * Gets the selectable parents.
	 * 
	 * @return the selectable parents
	 */
	public Collection getSelectableParents() {
		return new ArrayList(selectableParents);
	}

	// //////////////////////////////////////////////////////
	// The overridden methods from PDragSequenceEventHandler
	// For usability reason the marquee function was removed
	// and drag event match with PDragEventHandlerExclusive
	// //////////////////////////////////////////////////////

	
	@Override
	public void mouseClicked(PInputEvent aEvent) {
		super.mouseClicked(aEvent);
		processSelectionEvent(aEvent);
	}
	
	/**
	 * process the selection event. select if needed or unselect if cliked with shift key
	 */
	public void processSelectionEvent(PInputEvent aEvent){
		initializeSelection(aEvent);
		if (!isOptionSelection(aEvent)) {
			startStandardSelection(aEvent);
		} else {
			startStandardOptionSelection(aEvent);
		}
		if (pressNode != null)
			aEvent.setHandled(true);
	}
	
	/**
	 * Start drag.
	 * 
	 * @param e
	 *            the e
	 */
	
	
	protected void startDrag(PInputEvent e) {
		super.startDrag(e);
		processSelectionEvent(e);
		if (pressNode != null)
			e.setHandled(true);
		
	}

	/**
	 * Drag.
	 * 
	 * @param e
	 *            the e
	 */
	protected void drag(PInputEvent e) {
		super.drag(e);
		/*
		 * MARQUEE is canceled if (isMarqueeSelection(e)) { updateMarquee(e);
		 * 
		 * if (!isOptionSelection(e)) { computeMarqueeSelection(e); } else {
		 * computeOptionMarqueeSelection(e); } } else {
		 */
		if (pressNode != null) {
			dragStandardSelection(e);
			e.setHandled(true);
		}
		// }
	}

	/**
	 * End drag.
	 * 
	 * @param e
	 *            the e
	 */
	protected void endDrag(PInputEvent e) {
		super.endDrag(e);
		processSelectionEvent(e);
	}

	// //////////////////////////
	// Additional methods
	// //////////////////////////

	/**
	 * Checks if is option selection.
	 * 
	 * @param pie
	 *            the pie
	 * 
	 * @return true, if is option selection
	 */
	public boolean isOptionSelection(PInputEvent pie) {
		return pie.isShiftDown();
	}

	/**
	 * Checks if is marquee selection.
	 * 
	 * @param pie
	 *            the pie
	 * 
	 * @return true, if is marquee selection
	 */
	protected boolean isMarqueeSelection(PInputEvent pie) {
		return (pressNode == null);
	}

	/**
	 * Initialize selection.
	 * define which node should be taken into account
	 * In particular, it consider the active layer when its title its selected
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void initializeSelection(PInputEvent pie) {
		canvasPressPt = pie.getCanvasPosition();
		presspt = pie.getPosition();
		pressNode = pie.getPath().getPickedNode();
		if (pressNode instanceof LOMTitleUI) {
			LOMTitleUI theTitle = (LOMTitleUI) pressNode;
			//if (theTitle.isTitleOfActiveLayer()) 
			pressNode = theTitle.getSelectionableLOM().getPNode();
			//else pressNode=null;
		}else if (pressNode instanceof PCamera) {
			pressNode = null;
		}
	}

	/**
	 * Initialize marquee.
	 * 
	 * @param e
	 *            the e
	 */
	protected void initializeMarquee(PInputEvent e) {
		initStroke();
		marquee = PPath.createRectangle((float) presspt.getX(), (float) presspt
				.getY(), 0, 0);
		marquee.setPaint(marqueePaint);
		marquee.setTransparency(marqueePaintTransparency);
		marquee.setStrokePaint(Color.black);
		marquee.setStroke(strokes[0]);
		marqueeParent.addChild(marquee);

		marqueeMap.clear();
	}

	/**
	 * Start option marquee selection.
	 * 
	 * @param e
	 *            the e
	 */
	protected void startOptionMarqueeSelection(PInputEvent e) {
	}

	/**
	 * Start marquee selection.
	 * 
	 * @param e
	 *            the e
	 */
	protected void startMarqueeSelection(PInputEvent e) {
		// unselectAll();
	}

	/**
	 * Start standard selection.
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void startStandardSelection(PInputEvent pie) {
		// Option indicator not down - clear selection, and start fresh
		if (!isSelected(pressNode) && isSelectable(pressNode)) {
			unselectAll();
			select(pressNode);
		}
	}

	/**
	 * Start standard option selection.
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void startStandardOptionSelection(PInputEvent pie) {
		// Option indicator is down, toggle selection
		if (isSelectable(pressNode)) {
		
			if (isSelected(pressNode)) {
				unselect(pressNode);
			} else {
				select(pressNode);
			}
		}
	}

	/**
	 * Update marquee.
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void updateMarquee(PInputEvent pie) {
		PBounds b = new PBounds();

		if (marqueeParent instanceof PCamera) {
			b.add(canvasPressPt);
			b.add(pie.getCanvasPosition());
		} else {
			b.add(presspt);
			b.add(pie.getPosition());
		}

		marquee.setPathToRectangle((float) b.x, (float) b.y, (float) b.width,
				(float) b.height);
		b.reset();
		b.add(presspt);
		b.add(pie.getPosition());

		allItems.clear();
		PNodeFilter filter = createNodeFilter(b);
		Iterator parentsIt = selectableParents.iterator();
		while (parentsIt.hasNext()) {
			PNode parent = (PNode) parentsIt.next();

			Collection items;
			if (parent instanceof PCamera) {
				items = new ArrayList();
				for (int i = 0; i < ((PCamera) parent).getLayerCount(); i++) {
					((PCamera) parent).getLayer(i).getAllNodes(filter, items);
				}
			} else {
				items = parent.getAllNodes(filter, null);
			}

			Iterator itemsIt = items.iterator();
			while (itemsIt.hasNext()) {
				allItems.put(itemsIt.next(), Boolean.TRUE);
			}
		}
	}

	/**
	 * Compute marquee selection.
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void computeMarqueeSelection(PInputEvent pie) {
		unselectList.clear();
		// Make just the items in the list selected
		// Do this efficiently by first unselecting things not in the list
		Iterator selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			if (!allItems.containsKey(node)) {
				unselectList.add(node);
			}
		}
		unselect(unselectList);

		// Then select the rest
		selectionEn = allItems.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			if (!selection.containsKey(node) && !marqueeMap.containsKey(node)
					&& isSelectable(node)) {
				marqueeMap.put(node, Boolean.TRUE);
			} else if (!isSelectable(node)) {
				allItems.remove(node);
			}
		}

		select(allItems);
	}

	/**
	 * Compute option marquee selection.
	 * 
	 * @param pie
	 *            the pie
	 */
	protected void computeOptionMarqueeSelection(PInputEvent pie) {
		unselectList.clear();
		Iterator selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			if (!allItems.containsKey(node) && marqueeMap.containsKey(node)) {
				marqueeMap.remove(node);
				unselectList.add(node);
			}
		}
		unselect(unselectList);

		// Then select the rest
		selectionEn = allItems.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			if (!selection.containsKey(node) && !marqueeMap.containsKey(node)
					&& isSelectable(node)) {
				marqueeMap.put(node, Boolean.TRUE);
			} else if (!isSelectable(node)) {
				allItems.remove(node);
			}
		}

		select(allItems);
	}

	/**
	 * Creates the node filter.
	 * 
	 * @param bounds
	 *            the bounds
	 * 
	 * @return the p node filter
	 */
	protected PNodeFilter createNodeFilter(PBounds bounds) {
		return new BoundsFilter(bounds);
	}

	/**
	 * Gets the marquee bounds.
	 * 
	 * @return the marquee bounds
	 */
	protected PBounds getMarqueeBounds() {
		if (marquee != null) {
			return marquee.getBounds();
		}
		return new PBounds();
	}

	/**
	 * Drag standard selection.
	 * 
	 * @param e
	 *            the e
	 */
	protected void dragStandardSelection(PInputEvent e) {
		// There was a press node, so drag selection
		PDimension d = e.getCanvasDelta();
		e.getTopCamera().localToView(d);

		Iterator selectionEn = selection.keySet().iterator();

		while (selectionEn.hasNext()) {
			PDimension gDist = new PDimension();
			PNode node = (PNode) selectionEn.next();
			if (node != LessonMapper2.getInstance().getActiveLayer()) {
				gDist.setSize(d);
				node.getParent().globalToLocal(gDist);
				node.offset(gDist.getWidth(), gDist.getHeight());
			}
		}
	}

	/**
	 * End marquee selection.
	 * 
	 * @param e
	 *            the e
	 */
	protected void endMarqueeSelection(PInputEvent e) {
		// Remove marquee
		marquee.removeFromParent();
		marquee = null;
	}

	/**
	 * End standard selection.
	 * 
	 * @param e
	 *            the e
	 */
	protected void endStandardSelection(PInputEvent e) {
		pressNode = null;
	}

	/**
	 * This gets called continuously during the drag, and is used to animate the
	 * marquee.
	 * 
	 * @param aEvent
	 *            the a event
	 */
	protected void dragActivityStep(PInputEvent aEvent) {
		if (marquee != null) {
			float origStrokeNum = strokeNum;
			strokeNum = (strokeNum + 0.5f) % NUM_STROKES; // Increment by
			// partial steps to
			// slow down
			// animation
			if ((int) strokeNum != (int) origStrokeNum) {
				marquee.setStroke(strokes[(int) strokeNum]);
			}
		}
	}

	/**
	 * Delete selection when delete key is pressed (if enabled).
	 * 
	 * @param e
	 *            the e
	 */
	public void keyPressed(PInputEvent e) {
		// System.out.println("iscalled");
		switch (e.getKeyCode()) {
		case KeyEvent.VK_DELETE:

		}
	}

	/** The its copy vector. */
	protected Vector itsCopyVector;

	/** The is cut mode. */
	protected boolean isCutMode = false;

	/**
	 * Copy.
	 * 
	 * @return true, if copy
	 */
	public boolean copy() {
		isCutMode = false;
		itsCopyVector = new Vector();
		Iterator selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			itsCopyVector.add(node);
		}
		return true;
	}

	/**
	 * Cut.
	 * 
	 * @return true, if cut
	 */
	public boolean cut() {
		isCutMode = true;
		itsCopyVector = new Vector();
		Iterator selectionEn = selection.keySet().iterator();
		while (selectionEn.hasNext()) {
			PNode node = (PNode) selectionEn.next();
			itsCopyVector.add(node);
		}
		return true;
	}

	/**
	 * paste the copy or cut elements the edges having source and destination
	 * node also in the paste vector are paste. Others are not. in cut mode the
	 * pasted elements are deleted form the origin
	 * 
	 * @return true, if paste
	 */
	public boolean paste() {
		if (!isCutMode) {
			Vector theMotionNodes = new Vector();
			Vector theOtherNode = new Vector();
			for (Iterator iter = itsCopyVector.iterator(); iter.hasNext();) {
				PNode element = (PNode) iter.next();
				if (element instanceof MotionNode)
					theMotionNodes.add(element);
				else
					theOtherNode.add(element);
			}
		}
		return false;
	}

	/**
	 * this method remove the selected nodes return false id remove is disabled
	 * otherwise true.
	 * 
	 * @return true, if remove
	 */
	public boolean remove() {
		if (deleteEnabled) {
			Iterator selectionEn = selection.keySet().iterator();
			while (selectionEn.hasNext()) {
				PNode node = (PNode) selectionEn.next();
				node.removeFromParent();
			}
			selection.clear();
			return true;
		}
		return false;
	}

	/**
	 * Gets the support delete key.
	 * 
	 * @return the support delete key
	 */
	public boolean getSupportDeleteKey() {
		return deleteEnabled;
	}

	/**
	 * Checks if is delete enabled.
	 * 
	 * @return true, if is delete enabled
	 */
	public boolean isDeleteEnabled() {
		return deleteEnabled;
	}

	/**
	 * Specifies if the DELETE key should delete the selection.
	 * 
	 * @param deleteKeyActive
	 *            the delete enabled
	 */
	public void setDeleteEnabled(boolean deleteKeyActive) {
		this.deleteEnabled = deleteKeyActive;
	}

	// ////////////////////
	// Inner classes
	// ////////////////////

	/**
	 * The Class BoundsFilter.
	 */
	protected class BoundsFilter implements PNodeFilter {

		/** The local bounds. */
		PBounds localBounds = new PBounds();

		/** The bounds. */
		PBounds bounds;

		/**
		 * The Constructor.
		 * 
		 * @param bounds
		 *            the bounds
		 */
		protected BoundsFilter(PBounds bounds) {
			this.bounds = bounds;
		}

		/**
		 * Accept.
		 * 
		 * @param node
		 *            the node
		 * 
		 * @return true, if accept
		 */
		public boolean accept(PNode node) {
			localBounds.setRect(bounds);
			node.globalToLocal(localBounds);

			boolean boundsIntersects = node.intersects(localBounds);
			boolean isMarquee = (node == marquee);
			return (node.getPickable() && boundsIntersects && !isMarquee
					&& !selectableParents.contains(node) && !isCameraLayer(node));
		}

		/**
		 * Accept children of.
		 * 
		 * @param node
		 *            the node
		 * 
		 * @return true, if accept children of
		 */
		public boolean acceptChildrenOf(PNode node) {
			return selectableParents.contains(node) || isCameraLayer(node);
		}

		/**
		 * Checks if is camera layer.
		 * 
		 * @param node
		 *            the node
		 * 
		 * @return true, if is camera layer
		 */
		public boolean isCameraLayer(PNode node) {
			if (node instanceof PLayer) {
				for (Iterator i = selectableParents.iterator(); i.hasNext();) {
					PNode parent = (PNode) i.next();
					if (parent instanceof PCamera) {
						if (((PCamera) parent).indexOfLayer((PLayer) node) != -1) {
							return true;
						}
					}
				}
			}
			return false;
		}
	}

	/**
	 * Indicates the color used to paint the marquee.
	 * 
	 * @return the paint for interior of the marquee
	 */
	public Paint getMarqueePaint() {
		return marqueePaint;
	}

	/**
	 * Sets the color used to paint the marquee.
	 * 
	 * @param paint
	 *            the paint color
	 */
	public void setMarqueePaint(Paint paint) {
		this.marqueePaint = paint;
	}

	/**
	 * Indicates the transparency level for the interior of the marquee.
	 * 
	 * @return Returns the marquee paint transparency, zero to one
	 */
	public float getMarqueePaintTransparency() {
		return marqueePaintTransparency;
	}

	/**
	 * Sets the transparency level for the interior of the marquee.
	 * 
	 * @param marqueePaintTransparency
	 *            The marquee paint transparency to set.
	 */
	public void setMarqueePaintTransparency(float marqueePaintTransparency) {
		this.marqueePaintTransparency = marqueePaintTransparency;
	}
}