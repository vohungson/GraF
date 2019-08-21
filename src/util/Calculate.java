package util;

import java.awt.geom.Point2D;

import com.sun.javafx.geom.Line2D;

public class Calculate {
	/*
	 * Calculate the height of the Triangle when we know 3 vertices of the triangle
	 * A(x1, y1); B(x2, y2); C(x3, y3) -> Calculate the height from Vertice A to the
	 * edge BC
	 * 		A
	 *a(AB)/|\ 
	 *    / | \ b(AC)
	 *   /  |  \  
	 * B/___|___\ C 
	 * 		H c(BC) => h(AH)
	 */
	public static int heightOfTriangle(double x1, double y1, double x2, double y2, double x3, double y3) {
		double a = Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2));
		double b = Math.sqrt((x1 - x3) * (x1 - x3) + (y1 - y3) * (y1 - y3));
		double c = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
		// Apply Heron's Formula for Area
		double S = (a + b + c) / 2;
		double Area = Math.sqrt(S * (S - a) * (S - b) * (S - c));
		double h = 2 * Area / c;
		//calculate the corner of B and C
		double B = (Math.pow(a, 2) + Math.pow(c, 2) - Math.pow(b, 2))/(2*a*c);
		double C = (Math.pow(b, 2) + Math.pow(c, 2) - Math.pow(a, 2))/(2*b*c);
		if (B < 0 || C < 0) { //the coner of B or C > 90 degree, we don't get this distance
			h = 100;
		}
		return (int) h;
	}
	
	public static Point2D getPointOnEdge (double x1, double y1, double x2, double y2, double x3, double y3) {
		Point2D point = new Point2D.Double();
		double a = Math.sqrt((x1 - x2)*(x1 - x2) + (y1 - y2)*(y1 - y2));
		double b = Math.sqrt((x1 - x3)*(x1 - x3) + (y1 - y3)*(y1 - y3));
		if (a == 0 && b == 0) {
			point.setLocation(x1, y1);
		} else {
			point.setLocation((b*x2 + a*x3)/(a + b), (b*y2 + a*y3)/(a + b));
		}
		return point;
	}

	/*
	 * There are two points: A(x1,y1) and B(x2,y2) with R is radius of B Calculate
	 * thePoint cross the line AB with circle of B
	 */
	public static Point2D getPoint(double x1, double y1, double x2, double y2, int R) {
		Point2D thePoint = new Point2D.Double();
		double x3 = (x1 + x2) / 2;
		double y3 = (y1 + y2) / 2;
		double d = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
		int times = 0;
		while ((int) d > R) {
			x1 = x3;
			y1 = y3;
			x3 = (x3 + x2) / 2;
			y3 = (y3 + y2) / 2;
			d = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
			while ((int) d < R && times < 10) {// set times to catch error here.
				x3 = (x3 + x1) / 2;
				y3 = (y3 + y1) / 2;
				d = Math.sqrt((x3 - x2) * (x3 - x2) + (y3 - y2) * (y3 - y2));
				times++;
			}
		}
		thePoint.setLocation(x3, y3);
		return thePoint;
	}

	public static Line2D getNewArc(double x1, double y1, double x2, double y2, double length1, double length2, String bendTikz) {
		Line2D newArc = new Line2D();
		Point2D p1, p2;
		if (bendTikz == "left") {
			p1 = getNewPoint(x1, y1, x2, y2, true, length1);
			p2 = getNewPoint(x2, y2, x1, y1, false, length2);
		} else {//bendTikz = right
			p1 = getNewPoint(x1, y1, x2, y2, false, length1);
			p2 = getNewPoint(x2, y2, x1, y1, true, length2);
		}
		newArc.setLine((float)p2.getX(), (float)p2.getY(), (float)p1.getX(), (float)p1.getY());
		return newArc;
	}
	
	private static Point2D getNewPoint(double x1, double y1, double x2, double y2, boolean whichPoint, double length) {
		Point2D newPoint1 = new Point2D.Double();
		Point2D newPoint2 = new Point2D.Double();
		double arrowLength = length;
		double arrowWidth = length/3;
		double sx = x1; // x1
		double sy = y1; // y1
		double ex = x2; // x2
		double ey = y2; // y2


			if (ex == sx && ey == sy) {// arrow parts of length 0
				newPoint1.setLocation(ex, ey);
				newPoint2.setLocation(ex, ey);
			} else {
				double factor = arrowLength / Math.hypot(sx - ex, sy - ey);
				double factorO = arrowWidth / Math.hypot(sx - ex, sy - ey);

				// part in direction of main line
				double dx = (sx - ex) * factor;
				double dy = (sy - ey) * factor;

				// part ortogonal to main line
				double ox = (sx - ex) * factorO;
				double oy = (sy - ey) * factorO;

				newPoint1.setLocation(ex + dx - oy, ey + dy + ox);
				newPoint2.setLocation(ex + dx + oy, ey + dy - ox);
			}

		return whichPoint ? newPoint1 : newPoint2;
	}
}
