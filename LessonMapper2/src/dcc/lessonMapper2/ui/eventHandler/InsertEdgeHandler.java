/*
 * Created on Aug 9, 2005
 *
 * LessonMapper2 Copyright DCC Universidad de Chile
 */
package dcc.lessonMapper2.ui.eventHandler;

import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.graph.element.TypedEdge;
import dcc.lessonMapper2.ui.graph.element.lom.LOMRelationUI;
import dcc.lessonMapper2.ui.graph.element.lom.SelectionableLOM;
import edu.umd.cs.piccolo.PLayer;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PBasicInputEventHandler;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.util.PPickPath;

/**
 * The Class InsertEdgeHandler.
 * 
 * @author omotelet
 * 
 * This class is responible for inserting a new node in the current ActiveLaeyer
 * of lessonMapper It is associated with a InsetNodeAction specifying the node
 * to insert with getNodeToInsert()
 */
public class InsertEdgeHandler extends PBasicInputEventHandler {

	/** The unique insert. */
	protected boolean uniqueInsert = true; // aUnique insertion

	/** The first selection. */
	protected PNode firstSelection;

	/** The second selection. */
	protected PNode secondSelection;

	/**
	 * The Constructor.
	 */
	public InsertEdgeHandler() {
		init();
	}

	/**
	 * Init.
	 */
	public void init() {
		if (firstSelection != null) {
			PNode theOldSelection = firstSelection;
			firstSelection = null;
			theOldSelection.repaint();
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.event.PBasicInputEventHandler#mousePressed(edu.umd.cs.piccolo.event.PInputEvent)
	 */
	public void mousePressed(PInputEvent aEvent) {
		selectSourceAndTarget(aEvent.getPath());
	}

	@Override
	public void mouseReleased(PInputEvent aEvent) {
		selectSourceAndTarget(aEvent.getInputManager().getMouseOver());
	}



	private void selectSourceAndTarget(PPickPath aPick) {
		PLayer theParent = LessonMapper2.getInstance().getActiveLayer();
		PNode thePickNode = aPick.getPickedNode();
		boolean isValidNode = (thePickNode instanceof SelectionableLOM && theParent == thePickNode
				.getParent());
		if (!isValidNode) {
			thePickNode = aPick.nextPickedNode();
			isValidNode = (thePickNode instanceof SelectionableLOM && theParent == thePickNode
					.getParent());
		}
		if (isValidNode) {
			if (firstSelection == null) {
				firstSelection = thePickNode;
				firstSelection.repaint();
			} else {
				if (firstSelection != thePickNode) {
					secondSelection = thePickNode;
					TypedEdge theEdge = new LOMRelationUI(
							(SelectionableLOM) firstSelection,
							(SelectionableLOM) secondSelection);
					theParent.addChild(theEdge);
					theEdge.init();
					theEdge.repaint();
					init();
					if (uniqueInsert)
						LessonMapper2.getInstance().getUI()
								.switchToSelectionMode();
				}
			}
		}
	}

	/**
	 * Checks if is first node.
	 * 
	 * @param aNode
	 *            the a node
	 * 
	 * @return true, if is first node
	 */
	public boolean isFirstNode(PNode aNode) {
		return aNode == firstSelection;
	}

}
