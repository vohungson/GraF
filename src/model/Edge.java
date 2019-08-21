package model;

import java.awt.geom.Point2D;

import com.sun.javafx.geom.Line2D;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Line;
import javafx.scene.text.Text;
import util.Calculate;

public class Edge extends Line {
	private Vertex v1 = new Vertex();
	private Vertex v2 = new Vertex();
	private double x1;
	private double y1;
	private double x2;
	private double y2;
	private Paint color;
	private Text textWeight = new Text();
	private Point2D deltaText = new Point2D.Double(0, 0);
	private Circle circleLabel = new Circle();// is the new position of the Label
	private boolean directed = false;
	private Line arrow1 = new Line();
	private Line arrow2 = new Line();
	private Point2D point1 = new Point2D.Double();// point 1,2 is intersected by edge and vertex
	private Point2D point2 = new Point2D.Double();//
	// declare the edge by circle.
	private Circle circle = null;
	private double r = 20;// radius of the circle
	private boolean isIntermediateEdge = false;
	// declare the edge by Arc (we will use CubicCurve)
	private CubicCurve curve = null;
	private boolean isCurveEdge = false;
	private Line2D arc1 = new Line2D();
	private Line2D arc2 = new Line2D();
	private String bendOfCurveEdgeInTikZData = "";
	private boolean isCurveEdgePoints = false;
	private Circle curvePoint1 = new Circle();//save curve edge points to these circles
	private Circle curvePoint2 = new Circle();
	private Line2D curveLine = null;

	public Edge() {

	}

	public Edge(double x1, double y1, double x2, double y2, Paint color) {
		super();
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		this.color = color;
		super.setStartX(x1);
		super.setStartY(y1);
		super.setEndX(x2);
		super.setEndY(y2);
		super.setStroke(color);
	}

	public void setCircle(double r) {
		this.circle = new Circle(x1 - r, y1 - r, r);
		this.r = r;
		this.circle.setStroke(Color.BLUEVIOLET);
		this.circle.setFill(Color.TRANSPARENT);
		this.circle.setStrokeWidth(2);
		super.setStroke(Color.TRANSPARENT);//set the color is invisible -> use it later
	}

	public Circle getCircle() {
		return circle;
	}

	public void updateEdge() {
		if (circle == null && curve == null) {// update the edge
			point1 = Calculate.getPoint(x2, y2, x1, y1, v1.getR());
			point2 = Calculate.getPoint(x1, y1, x2, y2, v2.getR());
			// update line
			super.setStartX(point1.getX());
			super.setStartY(point1.getY());
			super.setEndX(point2.getX());
			super.setEndY(point2.getY());
			updatePositionOfTextWeight();
			calculateArrow();
		} else if (curve == null) { // System.out.println("Update the circle!");
			if (circle.getCenterX() + r == x1 && circle.getCenterY() + r == y1) {// move circle follow to new position
				x1 = x2;
				y1 = y2;
				circle.setCenterX(x2 - r);
				circle.setCenterY(y2 - r);
			} else if (circle.getCenterX() + r == x2 && circle.getCenterY() + r == y2) {
				x2 = x1;
				y2 = y1;
				circle.setCenterX(x1 - r);
				circle.setCenterY(y1 - r);
			}
			updatePositionOfTextWeight();
			calculateArrow();
		} else {//update the cubic curve edge
			double length1 = 20;
			if(v2.isIntermediatePoint()) {
				length1 = length1/2;
			}
			double length2 = 20;
			if(v1.isIntermediatePoint()) {
				length2 = length2/2;
			}
			double length = Math.max(length1, length2)*3;
			arc1 = Calculate.getNewArc(x1, y1, x2, y2, length1, length2, this.bendOfCurveEdgeInTikZData);
			arc2 = Calculate.getNewArc(arc1.x1, arc1.y1, arc1.x2, arc1.y2, length, length, this.bendOfCurveEdgeInTikZData);
			if(curveLine == null) {//initialize it for the first time and save it
				curveLine = arc2;
				curvePoint1.setCenterX(arc2.x1);
				curvePoint1.setCenterY(arc2.y1);
				curvePoint2.setCenterX(arc2.x2);
				curvePoint2.setCenterY(arc2.y2);
				System.out.println("Only run this step one times");
			}
			if(isCurveEdgePoints) {
				curveLine.setLine(arc2.x1, arc2.y1, arc2.x2, arc2.y2);
				arc2.setLine((float)curvePoint1.getCenterX(), (float)curvePoint1.getCenterY(), (float)curvePoint2.getCenterX(), (float)curvePoint2.getCenterY());
			} else {
				float deltaX1 = arc2.x1 - curveLine.x1;
				float deltaY1 = arc2.y1 - curveLine.y1;
				float deltaX2 = arc2.x2 - curveLine.x2;
				float deltaY2 = arc2.y2 - curveLine.y2;
				arc2.setLine((float)curvePoint1.getCenterX() + deltaX1, (float)curvePoint1.getCenterY() + deltaY1, (float)curvePoint2.getCenterX() + deltaX2, (float)curvePoint2.getCenterY() + deltaY2);
			}
			//calculate again position for arc1(x1, y1)&(x2, y2) because when we move the curve edge so this point should move around the circle of the vertex
			Point2D arcP1 = Calculate.getPoint(arc2.x1, arc2.y1, x1, y1, v1.getR());
			Point2D arcP2 = Calculate.getPoint(arc2.x2, arc2.y2, x2, y2, v2.getR());
			arc1.setLine((float)arcP1.getX(), (float)arcP1.getY(), (float)arcP2.getX(), (float)arcP2.getY());
			//draw the curve edge
			curve.setStartX(arc1.x1);
			curve.setStartY(arc1.y1);
			curve.setControlX1(arc2.x1);
			curve.setControlY1(arc2.y1);
			curve.setControlX2(arc2.x2);
			curve.setControlY2(arc2.y2);
			curve.setEndX(arc1.x2);
			curve.setEndY(arc1.y2);
			//set new line for the super of curve
			super.setStartX(arc2.x1);
			super.setStartY(arc2.y1);
			super.setEndX(arc2.x2);
			super.setEndY(arc2.y2);
			updatePositionOfTextWeight();
			//update point1 and point2 for calculating the arrow
			point1.setLocation(arc2.x2, arc2.y2);
			point2.setLocation(arc1.x2, arc1.y2);
			calculateArrow();
		}
	}

	public void setEdge(double x1, double y1, double x2, double y2) {
		this.x1 = x1;
		this.y1 = y1;
		this.x2 = x2;
		this.y2 = y2;
		super.setStartX(x1);
		super.setStartY(y1);
		super.setEndX(x2);
		super.setEndY(y2);
	}

	public void setDeltaText(double x, double y) {
		double xDeltaText = 0, yDeltaText = 0;
		if (circle == null && curve == null) {// update for line
			xDeltaText = x - (x1 + x2) / 2;
			yDeltaText = y - (y1 + y2) / 2;
		} else if(curve == null) {// update for circle edge
			xDeltaText = x - circle.getCenterX() + r;
			yDeltaText = y - circle.getCenterY() + r;
		} else {//update for cubic curve edge
			xDeltaText = x - ((arc2.x1 + arc2.x2) / 2);
			yDeltaText = y - ((arc2.y1 + arc2.y2) / 2);
		}
		this.deltaText.setLocation(xDeltaText, yDeltaText);
		updatePositionOfTextWeight();
	}

	private void updatePositionOfTextWeight() {// update position of Text Weight when edge moving
		double x, y;
		if (circle == null && curve == null) {// update for line
			x = ((x1 + x2) / 2) + deltaText.getX();
			y = ((y1 + y2) / 2) + deltaText.getY();
		} else if(curve == null) {// update for circle edge
			x = circle.getCenterX() - r + deltaText.getX();
			y = circle.getCenterY() - r + deltaText.getY();
		} else {
			x = ((arc2.x1 + arc2.x2) / 2) + deltaText.getX();
			y = ((arc2.y1 + arc2.y2) / 2) + deltaText.getY();
		}
		this.textWeight.setX(x);
		this.textWeight.setY(y);
		this.circleLabel.setCenterX(x);
		this.circleLabel.setCenterY(y);
		this.circleLabel.setRadius(r);
	}

	public Circle getCircleLable() {// get the position of the Label
		return circleLabel;
	}

	public void setTextWeight(String text) {
		this.textWeight.setText(text);
		this.textWeight.setStyle("-fx-fill: red; -fx-font-weight: bold;");
	}
	
	public void setPositionOfTextWeight(double x, double y) {
		this.textWeight.setX(x);
		this.textWeight.setY(y);
	}

	public Text getTextWeight() {
		return textWeight;
	}

	public void setDirection(boolean directed) {
		this.directed = directed;
	}

	public boolean getDirection() {
		return directed;
	}

	private void calculateArrow() {
		double arrowLength = 20;
		double arrowWidth = 7;
		double sx = point1.getX(); // x1
		double sy = point1.getY(); // y1
		double ex = point2.getX(); // x2
		double ey = point2.getY(); // y2
		if (circle != null) {
			arrowLength = 10;
			arrowWidth = 3;
			double cx = circle.getCenterX();
			double cy = circle.getCenterY();
			sx = cx + r - 5;
			sy = cy - r;
			ex = cx + r;
			ey = cy;
		}

		arrow1.setEndX(ex);
		arrow1.setEndY(ey);
		arrow2.setEndX(ex);
		arrow2.setEndY(ey);

		if (directed) {
			arrow1.setStrokeWidth(2);
			arrow1.setStroke(Color.BLUEVIOLET);
			arrow2.setStrokeWidth(2);
			arrow2.setStroke(Color.BLUEVIOLET);

			if (ex == sx && ey == sy) {// arrow parts of length 0
				arrow1.setStartX(ex);
				arrow1.setStartY(ey);
				arrow2.setStartX(ex);
				arrow2.setStartY(ey);

			} else {
				double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
				double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

				// part in direction of main line
				double dx = (sx - ex) * factor;
				double dy = (sy - ey) * factor;

				// part ortogonal to main line
				double ox = (sx - ex) * factorO;
				double oy = (sy - ey) * factorO;

				arrow1.setStartX(ex + dx - oy);
				arrow1.setStartY(ey + dy + ox);
				arrow2.setStartX(ex + dx + oy);
				arrow2.setStartY(ey + dy - ox);
			}
		} else { // undirected, we don't draw an arrow, just draw a point
			arrow1.setStartX(ex);
			arrow1.setStartY(ey);
			arrow2.setStartX(ex);
			arrow2.setStartY(ey);
		}
	}

	public Line getArrow1() {
		return arrow1;
	}

	public Line getArrow2() {
		return arrow2;
	}

	public void setX1(double x1) {
		this.x1 = x1;
		super.setStartX(x1);
	}

	public void setY1(double y1) {
		this.y1 = y1;
		super.setStartY(y1);
	}

	public void setX2(double x2) {
		this.x2 = x2;
		super.setEndX(x2);
	}

	public void SetY2(double y2) {
		this.y2 = y2;
		super.setEndY(y2);
	}

	public void setPoint2(double x2, double y2) {
		this.x2 = x2;
		super.setEndX(x2);
		this.y2 = y2;
		super.setEndY(y2);
		updatePositionOfTextWeight();
	}

	public double getX1() {
		return x1;// line.getStartX();
	}

	public double getY1() {
		return y1;// line.getStartY();
	}

	public double getX2() {
		return x2;// line.getEndX();
	}

	public double getY2() {
		return y2;// line.getEndY();
	}

	public Paint getColor() {
		return color;
	}

	public void setColor(Paint color) {
		this.color = color;
		super.setStroke(color);
	}

	public Vertex getV1() {
		return v1;
	}

	public void setV1(Vertex v1) {
		this.v1 = v1;
	}

	public Vertex getV2() {
		return v2;
	}

	public void setV2(Vertex v2) {
		this.v2 = v2;
	}

	public boolean isIntermediateEdge() {
		return isIntermediateEdge;
	}

	public void setIntermediateEdge(boolean isIntermediateEdge) {
		this.isIntermediateEdge = isIntermediateEdge;
	}

	public boolean isCurveEdge() {
		return isCurveEdge;
	}

	public void setCurveEdge(boolean isCurveEdge) {
		this.isCurveEdge = isCurveEdge;
	}

	public CubicCurve getCurve() {
		return curve;
	}
	
	public void setNullCurveEdge() {//we use it for change the form of the edge, use later
		this.isCurveEdge = false;
		this.curve = null;
		this.bendOfCurveEdgeInTikZData = "";
	}

	public void setCurve(String bendTikz) {
		this.bendOfCurveEdgeInTikZData = bendTikz;//update the bend of Curve Edge is left or right
		double length1 = 20;
		if(v2.isIntermediatePoint()) {
			length1 = length1/2;
		}
		double length2 = 20;
		if(v1.isIntermediatePoint()) {
			length2 = length2/2;
		}
		double length = Math.max(length1, length2)*3;
		arc1 = Calculate.getNewArc(x1, y1, x2, y2, length1, length2, bendTikz);
		arc2 = Calculate.getNewArc(arc1.x1, arc1.y1, arc1.x2, arc1.y2, length, length, bendTikz);
		this.curve = new CubicCurve( arc1.x1, arc1.y1, arc2.x1, arc2.y1, arc2.x2, arc2.y2, arc1.x2, arc1.y2);
		this.curve.setStroke(Color.BLUEVIOLET);
        this.curve.setStrokeWidth(2);
        this.curve.setFill( null);
        setCurveEdge(true);
        
	}

	public void setVisibleMainEdge (boolean visible) {
		if(visible) {
			super.setStroke(Color.BLUEVIOLET);
		}else {//invisible
			super.setStroke(Color.TRANSPARENT);//set the color is invisible
		}
	}

	public String getBendOfCurveEdgeInTikZData() {
		return bendOfCurveEdgeInTikZData;
	}

	public void setBendOfCurveEdgeInTikZData(String bendOfCurveEdgeInTikZData) {
		this.bendOfCurveEdgeInTikZData = bendOfCurveEdgeInTikZData;
	}

	public boolean isCurveEdgePoints() {
		return isCurveEdgePoints;
	}

	public void setCurveEdgePoints(boolean isCurveEdgePoints) {
		this.isCurveEdgePoints = isCurveEdgePoints;
	}

	public Circle getCurvePoint1() {
		return curvePoint1;
	}

	public void setCurvePoint1() {//arc2.(x1,y1)
		this.curvePoint1.setCenterX(arc2.x1);
		this.curvePoint1.setCenterY(arc2.y1);
		this.curvePoint1.setRadius(5);
		this.curvePoint1.setStroke(Color.BLUEVIOLET);
		this.curvePoint1.setFill(Color.HOTPINK);
		this.curvePoint1.setStrokeWidth(2);
	}

	public Circle getCurvePoint2() {
		return curvePoint2;
	}

	public void setCurvePoint2() {//arc2.(x2,y2)
		this.curvePoint2.setCenterX(arc2.x2);
		this.curvePoint2.setCenterY(arc2.y2);
		this.curvePoint2.setRadius(5);
		this.curvePoint2.setStroke(Color.BLUEVIOLET);
		this.curvePoint2.setFill(Color.HOTPINK);
		this.curvePoint2.setStrokeWidth(2);
	}
}
