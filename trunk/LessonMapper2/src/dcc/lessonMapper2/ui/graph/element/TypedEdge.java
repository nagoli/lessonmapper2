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
package dcc.lessonMapper2.ui.graph.element;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Shape;
import java.awt.geom.GeneralPath;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import com.graphbuilder.curve.CardinalSpline;
import com.graphbuilder.curve.ControlPath;
import com.graphbuilder.curve.GroupIterator;
import com.graphbuilder.curve.Point;
import com.graphbuilder.curve.ShapeMultiPath;

import edu.umd.cs.piccolo.PNode;
import edu.umd.cs.piccolo.nodes.PPath;
import edu.umd.cs.piccolo.util.PPaintContext;

/**
 * The Class TypedEdge.
 * 
 * @author omotelet
 * 
 * this class defines a non-pickable node drawing a edge between two nodes and
 * aggregating a third node used to apply the motion mode An edge manage scale
 * transformation due to multiple container in a customized manner: scaling is
 * applied on the edge stroke not on the distance between the points
 */

public class TypedEdge extends PPath implements PropertyChangeListener {

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;

	/** The ITS tip angle. */
	public static double ITSTipAngle = Math.toRadians(45);

	/** The ITS tip size. */
	public static int ITSTipSize = 9;

	public static float ITSstrokeSize = 1.6f;

	/** The its node1. */
	protected PNode itsNode1;

	/** The its node2. */
	protected PNode itsNode2;

	/** The its motion node. */
	protected MotionNode itsMotionNode;

	/** The its multi path. */
	protected ShapeMultiPath itsMultiPath;

	/** The its cardinal spline. */
	protected CardinalSpline itsCardinalSpline;

	/** The its control path. */
	protected ControlPath itsControlPath;

	/** The its point2. */
	protected com.graphbuilder.curve.Point itsPoint1, itsPointC, itsPointC2,
			itsPoint2;

	/** The its line c2. */
	protected Line2D itsLineC2;

	/** The its point factory. */
	protected PointFactory itsPointFactory = new PointFactory();

	/** The its tip. */
	protected Shape itsTip;

	/** The its scale. */
	protected double itsScale = 1;

	/** The is motion node positioned. */
	protected boolean isMotionNodePositioned = false;

	protected boolean isInit =false;
	
	
	/**
	 * Build a new typed edge between these two nodes warning : method init may
	 * be called after construction.
	 * 
	 * @param aNode2
	 *            the a node2
	 * @param aNode1
	 *            the a node1
	 */
	public TypedEdge(PNode aNode1, PNode aNode2) {
		this(aNode1, aNode2, new MotionNode());
	}

	/**
	 * build a new typed edge between between these 2 nodes and with this
	 * specific motion node warning : method init may be called after
	 * construction.
	 * 
	 * @param aNode2
	 *            the a node2
	 * @param aMotionNode
	 *            the a motion node
	 * @param aNode1
	 *            the a node1
	 */
	public TypedEdge(PNode aNode1, PNode aNode2, MotionNode aMotionNode) {
		super();
		setNode1(aNode1);
		setNode2(aNode2);
		itsMotionNode = aMotionNode;
		itsMotionNode.setTypedEdge(this);
		setStroke(new BasicStroke(ITSstrokeSize * (float) itsScale));
		// setPaint(Color.black);
		setPickable(false);

	}

	/**
	 * set node1 if isInit is false, this node  is not yet listen by this edge
	 * @param aNode1
	 */
	protected void setNode1(PNode aNode1) {
		if (itsNode1!=null) {
			itsNode1.removePropertyChangeListener(this);
		}
		itsNode1 = aNode1;
		if (isInit ) aNode1.addPropertyChangeListener(this);
	}

	/**
	 * set node2 if isInit is false, this node  is not yet listen by this edge
	 * @param aNode2
	 */
	protected void setNode2(PNode aNode2) {
		if (itsNode2!=null) {
			itsNode2.removePropertyChangeListener(this);
		}
		itsNode2 = aNode2;
		if (isInit) aNode2.addPropertyChangeListener(this);
	}

	/**
	 * This method is overriden in order to limit scaling to stroke size not
	 * distance between points.
	 * 
	 * @param aScale
	 *            the scale
	 */
	public void setScale(double aScale) {
		itsScale = aScale;
		setStroke(new BasicStroke(ITSstrokeSize * (float) itsScale));
	}

	/**
	 * position the motion node.
	 * 
	 * @param theOffSet
	 *            the motion node offset
	 */
	public void setMotionNodeOffset(Point2D theOffSet) {
		itsMotionNode.setOffset(theOffSet);
		isMotionNodePositioned = true;
	}

	public PNode getSource() {
		return itsNode1;
	}

	public PNode getTarget() {
		return itsNode2;
	}

	/**
	 * return MotionNode.
	 * 
	 * @return the motion node
	 */
	public MotionNode getMotionNode() {
		return itsMotionNode;
	}

	/**
	 * an edge cannot be pickable. This method is overriden in order to forbit
	 * this state
	 * 
	 * @param aBool
	 *            the pickable
	 */
	public void setPickable(boolean aBool) {
		super.setPickable(false);
	}

	/**
	 * Init.
	 */
	public void init() {
		Point2D theP1 = itsNode1.localToParent(itsNode1.getBounds()
				.getCenter2D());
		Point2D theP2 = itsNode2.localToParent(itsNode2.getBounds()
				.getCenter2D());
		if (!isMotionNodePositioned)
			itsMotionNode.setOffset((theP1.getX() + theP2.getX()) / 2, (theP1
					.getY() + theP2.getY()) / 2);
		getParent().addChild(itsMotionNode);
		moveInBackOf(itsNode1);
		itsMultiPath = new ShapeMultiPath();
		itsMultiPath.setFlatness(1 * itsScale);
		itsControlPath = new ControlPath();

		itsPoint1 = itsPointFactory.createPoint(0, 0);
		itsPointC = itsPointFactory.createPoint(0, 0);
		itsPoint2 = itsPointFactory.createPoint(0, 0);
		itsPointC2 = itsPointFactory.createPoint(0, 0);
		// test changes

		itsControlPath.addPoint(itsPoint1);
		itsControlPath.addPoint(itsPointC);
		itsControlPath.addPoint(itsPointC2);
		itsControlPath.addPoint(itsPoint2);

		itsCardinalSpline = new CardinalSpline(itsControlPath,
				new GroupIterator("0,0:n-1", itsControlPath.numPoints()));
		itsControlPath.addCurve(itsCardinalSpline);
		itsMotionNode.addPropertyChangeListener(this);
		itsNode1.addPropertyChangeListener(this);
		itsNode2.addPropertyChangeListener(this);
		isInit =true;
		updateCurve();
	}

	/**
	 * Move point to.
	 * 
	 * @param aPoint1
	 *            the a point1
	 * @param aPoint2
	 *            the a point2
	 */
	protected void movePointTo(com.graphbuilder.curve.Point aPoint1,
			com.graphbuilder.curve.Point aPoint2) {
		aPoint1.setLocation(new double[] { aPoint2.getLocation()[0],
				aPoint2.getLocation()[1] });
	}

	protected void movePointTo(com.graphbuilder.curve.Point aPoint1,
			Point2D aPoint2) {
		aPoint1.setLocation(new double[] { aPoint2.getX(), aPoint2.getY() });
	}

	/**
	 * Creates the line with points.
	 * 
	 * @param aPoint1
	 *            the a point1
	 * @param aPoint2
	 *            the a point2
	 * 
	 * @return the line2 d
	 */
	protected Line2D createLineWithPoints(com.graphbuilder.curve.Point aPoint1,
			com.graphbuilder.curve.Point aPoint2) {
		return new Line2D.Double(aPoint1.getLocation()[0], aPoint1
				.getLocation()[1], aPoint2.getLocation()[0], aPoint2
				.getLocation()[1]);
	}

	/**
	 * Gets the line rect intersection.
	 * 
	 * @param r
	 *            the r
	 * @param l
	 *            the l
	 * 
	 * @return the line rect intersection
	 */
	protected Point2D getLineRectIntersection(Line2D l, Rectangle2D r) {
		double dx, dy; // distance between centers
		double x, y; // final endpoints
		dx = l.getX2() - l.getX1();
		dy = l.getY2() - l.getY1();
		if (l.intersectsLine(r.getX(), r.getY(), r.getX(), r.getMaxY())) {
			x = r.getX();
			y = l.getY2() - r.getWidth() * dy / (2 * dx);
		} else if (l.intersectsLine(r.getX(), r.getY(), r.getMaxX(), r.getY())) {
			y = r.getY();
			x = l.getX2() - r.getHeight() * dx / (2 * dy);
		} else if (l.intersectsLine(r.getMaxX(), r.getY(), r.getMaxX(), r
				.getMaxY())) {
			x = r.getMaxX();
			y = l.getY2() - r.getWidth() * dy / (-2 * dx);
		} else {
			y = r.getMaxY();
			x = l.getX2() - r.getHeight() * dx / (-2 * dy);
		}
		return new Point2D.Double(x, y);
	}

	/**
	 * this method update the point of the curve to match the current positions
	 * of the nodes.
	 */
	public void updateCurve() {
		itsMultiPath.setNumPoints(0);
		itsCardinalSpline.appendTo(itsMultiPath);
		movePointTo(itsPoint1, getCenterforParent(itsNode1));
		movePointTo(itsPointC, getCenterforParent(itsMotionNode));
		movePointTo(itsPoint2, getCenterforParent(itsNode2));
		// itsLine1C = createLineWithPoints(itsPoint1,itsPointC);
		itsLineC2 = createLineWithPoints(itsPointC, itsPoint2);
		// movePointTo(itsPoint1C, getLineRectIntersection(itsLine1C,
		// itsNode1.getFullBounds()));
		movePointTo(itsPointC2, getLineRectIntersection(itsLineC2, itsNode2
				.localToParent(itsNode2.getBounds().inset(-4, -4))));
		setPathTo(itsMultiPath);
		itsTip = getTip(itsPointC, itsPointC2);
		append(itsTip, false);
	}

	/**
	 * Gets the centerfor parent.
	 * 
	 * @param aNode
	 *            the a node
	 * 
	 * @return the centerfor parent
	 */
	public Point2D getCenterforParent(PNode aNode) {
		return aNode.localToParent(aNode.getBounds().getCenter2D());
	}

	/**
	 * define the center as the intersection between the first compionent bounds
	 * and the line between the bounds.
	 * 
	 * @param aNode
	 * @return
	 */
	public Point2D getCenterforParent(PNode aNode, PNode aMiddleNode) {
		Rectangle2D theFirstBounds = aNode.localToParent(aNode.getBounds());
		System.out.println(theFirstBounds);
		Rectangle2D theSecondBounds = aMiddleNode.localToParent(aMiddleNode
				.getBounds());
		System.out.println(theSecondBounds);
		Point2D theLineRectIntersection = getLineRectIntersection(
				new Line2D.Double(theFirstBounds.getCenterX(), theFirstBounds
						.getCenterY(), theSecondBounds.getCenterX(),
						theSecondBounds.getCenterY()), theFirstBounds);
		System.out.println(theLineRectIntersection);
		return theLineRectIntersection;

	}

	/**
	 * Gets the tip.
	 * 
	 * @param p1
	 *            the p1
	 * @param p2
	 *            the p2
	 * 
	 * @return the tip
	 */
	public Shape getTip(com.graphbuilder.curve.Point p1,
			com.graphbuilder.curve.Point p2) {
		double size = ITSTipSize * itsScale;
		double a = Math.atan2(p2.getLocation()[1] - p1.getLocation()[1], p1
				.getLocation()[0]
				- p2.getLocation()[0]);
		// System.out.println("-------------");
		double x2 = p2.getLocation()[0];
		double y2 = p2.getLocation()[1];
		double x3 = p2.getLocation()[0] + size * Math.cos(a - ITSTipAngle / 2);
		// System.out.println("x+"+Math.cos(a - angle / 2) );
		double y3 = p2.getLocation()[1] - Math.sin(a - ITSTipAngle / 2) * size;
		// System.out.println("y-"+ Math.sin(a - angle / 2) );
		double x4 = p2.getLocation()[0] + Math.cos(a + ITSTipAngle / 2) * size;
		// System.out.println("x+"+Math.cos(a + angle / 2) );
		double y4 = p2.getLocation()[1] - Math.sin(a + ITSTipAngle / 2) * size;
		// System.out.println("y+"+Math.sin(a + angle / 2) );
		GeneralPath thePath = new GeneralPath();
		thePath.moveTo((float) x3, (float) y3);
		thePath.lineTo((float) x2, (float) y2);
		thePath.lineTo((float) x4, (float) y4);

		// thePath.

		// new int[] { x2, x3, x4 }, new int[] { y2, y3, y4 }, 3);
		return thePath;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see edu.umd.cs.piccolo.nodes.PPath#paint(edu.umd.cs.piccolo.util.PPaintContext)
	 */
	protected void paint(PPaintContext aPaintContext) {
		super.paint(aPaintContext);
		// aPaintContext.setRenderQuality(PPaintContext.HIGH_QUALITY_RENDERING);
		Graphics2D g = aPaintContext.getGraphics();
		g.fill(itsTip);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.beans.PropertyChangeListener#propertyChange(java.beans.PropertyChangeEvent)
	 */
	public void propertyChange(PropertyChangeEvent arg0) {
		updateCurve();
		invalidatePaint();
		repaint();
	}

	/**
	 * Detach.
	 */
	public void detach() {
		removeFromParent();
		itsMotionNode.removeFromParent();
	}

	/**
	 * The Class PointFactory.
	 */
	class PointFactory {

		/**
		 * Creates the point.
		 * 
		 * @param aPoint
		 *            the a point
		 * 
		 * @return the point
		 */
		public Point createPoint(Point2D aPoint) {
			return createPoint(aPoint.getX(), aPoint.getY());
		}

		/**
		 * Creates the point.
		 * 
		 * @param y
		 *            the y
		 * @param x
		 *            the x
		 * 
		 * @return the point
		 */
		public Point createPoint(double x, double y) {
			final double[] arr = new double[] { x, y };

			return new Point() {
				public double[] getLocation() {
					return arr;
				}

				public void setLocation(double[] loc) {
					arr[0] = loc[0];
					arr[1] = loc[1];
				}
			};
		}
	}

}
