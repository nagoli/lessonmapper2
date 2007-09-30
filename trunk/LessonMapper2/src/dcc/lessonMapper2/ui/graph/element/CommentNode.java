package dcc.lessonMapper2.ui.graph.element;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;

import javax.swing.JTextArea;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import zz.utils.ui.popup.RelativePopup;
import dcc.lessonMapper2.LessonMapper2;
import dcc.lessonMapper2.ui.HelperSupport;
import dcc.lessonMapper2.ui.LMUI;
import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.event.PInputEvent;
import edu.umd.cs.piccolo.event.PInputEventListener;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.nodes.PText;
import edu.umd.cs.piccolo.util.PBounds;
import edu.umd.cs.piccolo.util.PPaintContext;

public class CommentNode extends PText implements SelectionableNode,
		HelperSupport, PInputEventListener {

	public Font ITSFont = new Font("SanSerif", Font.PLAIN, 9);
	public Font ITSEditFont = new Font("SanSerif", Font.PLAIN, 11);
	public int ITSResizeNodeY = 5;
	public int ITSMinResizeNodeX = 5;
	ResizeNode itsResizeNode;
	boolean isEdited = false;

	public CommentNode() {
		super(LessonMapper2.getInstance().getLangComment("commentNodeInit"));
		setFont(ITSFont);
		setConstrainHeightToTextHeight(true);
		setConstrainWidthToTextWidth(false);

		itsResizeNode = new ResizeNode(this);
		addChild(itsResizeNode);
		itsResizeNode.setOffset(getWidth(), ITSResizeNodeY);
		itsResizeNode.setPickable(true);
		setTextPaint(LMUI.ITSBlueColor);
		addInputEventListener(this);
	}

	public CommentNode(CommentNode aCommentNode) {
		this();
		setText(aCommentNode.getText());
		setWidth(aCommentNode.getWidth());
	}

	public void processEvent(PInputEvent aEvent, int aType) {
		if (aType == MouseEvent.MOUSE_CLICKED && aEvent.getClickCount() == 2) {
			setEdited(true);
			CommentPopup theCommentPopup = new CommentPopup(this, aEvent);
			theCommentPopup.show();
			theCommentPopup.revalidatePopup();
		}
	}

	public void setEdited(boolean aIsEdited) {
		isEdited = aIsEdited;
		repaint();
	}

	/**
	 * paint the border
	 */
	@Override
	public void paint(PPaintContext aPaintContext) {
		super.paint(aPaintContext);
		Graphics2D theGraphics2D = aPaintContext.getGraphics();
		theGraphics2D.setPaint(LMUI.ITSDarkSandColor);
		theGraphics2D.setStroke(new BasicStroke(0.1f));
		theGraphics2D.drawRoundRect(-2, -2, (int) getWidth() + 4,
				(int) getHeight() + 4, 6, 6);
		if (isEdited) {
			theGraphics2D.setPaint(LMUI.ITSUNActiveColor);
			theGraphics2D.fillRoundRect(-2, -2, (int) getWidth() + 4,
					(int) getHeight() + 4, 6, 6);
		}
	}

	public void detach() {
		removeFromParent();
	}

	@Override
	public boolean setWidth(double aWidth) {
		if (aWidth < ITSMinResizeNodeX)
			aWidth = ITSMinResizeNodeX;
		itsResizeNode.setOffset(aWidth, ITSResizeNodeY);
		return super.setWidth(aWidth);
	}

	public PNode getPNode() {
		return this;
	}

	@Override
	public void repaintFrom(PBounds aLocalBounds, PNode aChildOrThis) {
		super.repaintFrom(aLocalBounds, aChildOrThis);
	}

	public String getHelp() {
		return LessonMapper2.getInstance().getLangComment("commentNodeHelp");
	}

	public class ResizeNode extends PPath implements PInputEventListener,
			HelperSupport {
		PNode itsAssociatedNode;

		public ResizeNode(PNode aAssociatedNode) {
			itsAssociatedNode = aAssociatedNode;
			addInputEventListener(this);
			setPaint(LMUI.ITSDarkSandColor);
			setStrokePaint(null);
			setPathToPolyline(new Point2D[] { new Point2D.Double(0, 2),
					new Point2D.Double(4, 0), new Point2D.Double(8, 2),
					new Point2D.Double(4, 4) });
		}

		public void processEvent(PInputEvent aEvent, int aType) {
			if (aType == MouseEvent.MOUSE_DRAGGED) {
				LessonMapper2.getInstance().getSelectionHandler().unselectAll();
				Point2D thePositionRelative = aEvent
						.getPositionRelativeTo(itsAssociatedNode);
				itsAssociatedNode
						.setWidth(thePositionRelative.getX() < ITSMinResizeNodeX ? ITSMinResizeNodeX
								: thePositionRelative.getX());
			}
		}

		public String getHelp() {
			return LessonMapper2.getInstance().getLangComment(
					"commentNodeSizer");
		}

	}

	class CommentPopup extends RelativePopup implements DocumentListener {

		CommentNode itsCommentNode;
		JTextArea itsTextField;

		public CommentPopup(CommentNode aNode, PInputEvent aEvent) {
			super(null, LessonMapper2.getInstance().getUI().getMainCanvas(),
					aEvent.getCanvasPosition());
			itsTextField = new JTextArea(getText());
			setContent(itsTextField);
			itsCommentNode = aNode;

			itsTextField.setFont(ITSEditFont);
			itsTextField.setBackground(LMUI.ITSSandColor);
			itsTextField.getDocument().addDocumentListener(this);
			itsTextField.setEditable(true);
			itsTextField.setWrapStyleWord(true);
			itsTextField.setLineWrap(true);
			itsTextField.setMinimumSize(new Dimension(300,20));
			// Rectangle2D theGlobalBounds =
			// aCamera.localToView(aCamera.globalToLocal(aNode.localToGlobal(itsCommentNode.getBounds())));
			//itsTextField.setMaximumSize(new Dimension(100,300));
			// itsTextField.setPreferredSize(new
			// Dimension((int)theGlobalBounds.getWidth(),(int)theGlobalBounds.getHeight()));
			setAutoHide(true);
		}

		@Override
		public void hide() {
			itsCommentNode.setText(itsTextField.getText());
			itsCommentNode.setEdited(false);
			super.hide();

		}

		public void changedUpdate(DocumentEvent aE) {

		}

		public void insertUpdate(DocumentEvent aE) {
			revalidatePopup();
		}

		public void removeUpdate(DocumentEvent aE) {
		}

	}

}
