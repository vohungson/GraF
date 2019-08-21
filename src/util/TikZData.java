package util;

import java.util.List;

import javafx.scene.shape.CubicCurve;
import javafx.scene.text.Text;
import model.Edge;
import model.Vertex;

public class TikZData {
	private static StringBuilder sb = new StringBuilder();

	public static StringBuilder handlerData(List<Vertex> vertices, List<Edge> edges, List<Edge> invisibleEdges) {
		sb.delete(0, sb.length());
		sb.append("\\documentclass{article} \n");
		sb.append("\\usepackage[utf8]{inputenc} \n");
		sb.append("\\usepackage{tikz} \n");
		sb.append("\\usetikzlibrary{positioning} \n");
		sb.append("\\begin{document} \n");
		sb.append("\\begin{figure} \n");
		sb.append("\\begin{tikzpicture} \n");
		// START THE CODE FOR EXPORT GRAPH
		sb.append(
				"[bigNode/.style={circle, draw=green!60, fill=green!5, thick, minimum size=6mm}, smallNode/.style={circle, draw=red!20, fill=red!20, thick, minimum size=3mm}, squareNode/.style={rectangle, draw=blue!20, fill=blue!15, thick, minimum size=3mm}] \n");

		System.out.println("Size of vertices: " + vertices.size());
		for (Vertex v : vertices) {
			if (v.isIntermediatePoint()) {
				smallNode(v.getIndex(), v.getX(), v.getY());
			} else {
				bigNode(v.getIndex(), v.getX(), v.getY());
			}
		}

		System.out.println("Size of edges when export tkz: " + edges.size());
		for (Edge e : edges) {
			if (e.getCircle() == null && e.getCurve() == null) {// draw the normal edge & segment edge
				if (e.getV2().isIntermediatePoint() || e.getV2().isIntermediatePoint()) {//draw the segment edge
					if(!invisibleEdges.contains(e)) {
						if(!e.getV2().isIntermediatePoint()) {
							drawNormalEdge(e.getDirection(), e.getV1().getIndex(), e.getV2().getIndex(), e.getTextWeight());
						} else {
							drawSegmentEdge(e.getV1().getIndex(), e.getV2().getIndex());
						}
					}
				} else {// draw normal edge and only invisible father edge that have intermediate points
					if(!invisibleEdges.contains(e)) {
						drawNormalEdge(e.getDirection(), e.getV1().getIndex(), e.getV2().getIndex(), e.getTextWeight());
					}
				}
			} else if (e.getCurve() == null) {// draw the circle (loop edge)
				drawLoopEdge(e.getDirection(), e.getV1().getIndex(), e.getTextWeight());
			} else { // draw the curve edge
				if(isCubicEdge(e)) {
					drawCubicEdge(e.getDirection(), e.getCurve(), e.getTextWeight());
				} else {
					drawCurveEdge(e.getDirection(), e.getV1().getIndex(), e.getV2().getIndex(), e.getTextWeight(), e.getBendOfCurveEdgeInTikZData());
				}
			}
		}

		// END THE CODE
		sb.append("\\end{tikzpicture} \n");
		sb.append("\\end{figure} \n");
		sb.append("\\end{document} \n");
		return sb;

	}

	//\node[squarednode] at ( 5.9875, 4.25) {1}; 
	private static void squareNode(Text textWeight, double x, double y) {
		x = x/80;
		y = 10 - y/80;
		sb.append("		\\node[squareNode] at ( " + x + ", " + y + ")" + " {" + textWeight.getText() + "}; \n");
	}
	
	private static void bigNode(int index, double x, double y) {
		x = x/80;
		y = 10 - y/80;
		sb.append("		\\node[bigNode] (" + index + ") at ( " + x + ", " + y + ")" + " {" + index + "}; \n");
	}

	private static void smallNode(int index, double x, double y) {
		x = x/80;
		y = 10 - y/80;
		sb.append("		\\node[smallNode] (" + index + ") at ( " + x + ", " + y + ")" + " {}; \n");
	}

	// \draw[->] (e) to [loop above] node [midway,fill=red!20] {5} (e);
	private static void drawLoopEdge(boolean directed, int indexV1, Text textWeight) {
		if (directed) {
			sb.append("		\\draw [->] [line width=0.8pt] (" + indexV1 + ") to [loop above] (" + indexV1 + "); \n");
			squareNode(textWeight, textWeight.getX(), textWeight.getY());
		} else {
			sb.append("		\\draw [-] [line width=0.8pt] (" + indexV1 + ") to [loop above] (" + indexV1 + "); \n");
			squareNode(textWeight, textWeight.getX(), textWeight.getY());
		}
	}
	
	private static boolean isCubicEdge (Edge edge) {//define: after moving the curve edge will become cubic edge
		boolean cubic = true;
		double corner1, corner2;
		corner1 = 0;
		corner2 = 0;
		int corner = 0;
		if (edge.getY1() != edge.getY2()) {
			corner1 = ((edge.getX2() - edge.getX1()) / (edge.getY2() - edge.getY1()));
			System.out.println("Corner of original edge: " + corner1);
		}
		if (edge.getCurve().getControlY1() != edge.getCurve().getControlY2()) {
			corner2 = ((edge.getCurve().getControlX2() - edge.getCurve().getControlX1())
					/ (edge.getCurve().getControlY2() - edge.getCurve().getControlY1()));
			System.out.println("Corner of curve edge:" + corner2);
		}
		corner = (int) (corner2 - corner1);
		System.out.println("Compare two corners: " + corner);	
		if (corner == 0) {
			cubic = false;
		}
		return cubic;
	}

	// \draw [->] (3,3) .. controls (3.17,3.67) and (3.83,3.67) .. (4,4);
	private static void drawCubicEdge(boolean directed,CubicCurve curve, Text textWeight) {
		double sx = curve.getStartX()/80;
		double sy = 10 - curve.getStartY()/80;
		double cx1 = curve.getControlX1()/80;
		double cy1 = 10 - curve.getControlY1()/80;
		double cx2 = curve.getControlX2()/80;
		double cy2 = 10 - curve.getControlY2()/80;
		double ex = curve.getEndX()/80;
		double ey = 10 - curve.getEndY()/80;
		if (directed) {
			sb.append("		\\draw [->] [line width=0.8pt] (" + sx + "," + sy 
			+ ") .. controls (" + cx1 + "," + cy1
			+ ") and (" + cx2 + "," + cy2
			+ ") .. (" + ex + "," + ey + "); \n");
			squareNode(textWeight, textWeight.getX(), textWeight.getY());	
		} else {	
			sb.append("		\\draw [-] [line width=0.8pt] (" + sx + "," + sy 
					+ ") .. controls (" + cx1 + "," + cy1
					+ ") and (" + cx2 + "," + cy2
					+ ") .. (" + ex + "," + ey + "); \n");
				squareNode(textWeight, textWeight.getX(), textWeight.getY());
		}
	}
	
	// \draw[->] (g) to [bend left] node [midway,fill=red!20] {8} (h);
	private static void drawCurveEdge(boolean directed, int indexV1, int indexV2, Text textWeight, String bend) {
		if (directed) {
			if(bend == "right") {
				sb.append("		\\draw [->] [line width=0.8pt] (" + indexV1 + ") to [bend right] (" + indexV2 + "); \n");
				squareNode(textWeight, textWeight.getX(), textWeight.getY());
			} else {
				sb.append("		\\draw [->] [line width=0.8pt] (" + indexV1 + ") to [bend left] (" + indexV2 + "); \n");
				squareNode(textWeight, textWeight.getX(), textWeight.getY());
			}
		} else {	
			if(bend == "right") {
				sb.append("		\\draw [-] [line width=0.8pt] (" + indexV1 + ") to [bend right] (" + indexV2 + "); \n");
				squareNode(textWeight, textWeight.getX(), textWeight.getY());
			} else {
				sb.append("		\\draw [-] [line width=0.8pt] (" + indexV1 + ") to [bend left] (" + indexV2 + "); \n");
				squareNode(textWeight, textWeight.getX(), textWeight.getY());
			}
		}
	}

	private static void drawNormalEdge(boolean directed, int indexV1, int indexV2, Text textWeight) {
		if (directed) {
			sb.append("		\\draw [->] [line width=0.8pt] (" + indexV1 + ") -- (" + indexV2 + "); \n");
			squareNode(textWeight, textWeight.getX(), textWeight.getY());
		} else {
			sb.append("		\\draw [-] [line width=0.8pt] (" + indexV1 + ") -- (" + indexV2 + "); \n");
			squareNode(textWeight, textWeight.getX(), textWeight.getY());
		}
	}

	private static void drawSegmentEdge(int indexV1, int indexV2) {
		sb.append("		\\draw [-] [line width=0.8pt] (" + indexV1 + ") -- (" + indexV2 + "); \n");
	}
}
