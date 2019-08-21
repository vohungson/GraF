package test;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import controller.MainController;
import enums.StateOnLeftPane;
import javafx.scene.paint.Color;
import model.Edge;
import model.Vertex;

public class UITest {
	MainController controller;
	HashMap<Vertex, Edge> parentEdge = new HashMap<Vertex, Edge>();// save the
	String typeEdge = "";
	String weightEdge = "";

	@Before
	public void init() {
		controller = new MainController();
	}

	@Test
	public void createVerticeTest() {
		double x = 100;
		double y = 200;
		StateOnLeftPane state = StateOnLeftPane.VERTEX;

		assertTrue(createVertice(state, x, y) != null);

	}

	@Test
	public void createEdgeTest() {
		double x = 10;
		double y = 20;
		StateOnLeftPane state = StateOnLeftPane.VERTEX;

		assertTrue(createEdge(state, x, y) != null);

	}

	public Vertex createVertice(StateOnLeftPane state, double x, double y) {
		Vertex vertex = null; // vertice
		boolean isClickedInsideVertex = false;
		double deltaX;
		double deltaY;
		int radius = 20;
		double firstX;
		double firstY;
		List<Vertex> vertices = new ArrayList<>();
		List<Edge> edges = new ArrayList<>();
		int indexVertex = -1;
		if (controller.isOnAVertex(x, y)) {// if mouse click inside the vertice then move when drag else
											// create a new vertice
			isClickedInsideVertex = true;
			deltaX = x - 10;
			deltaY = y - 20;
			firstX = 10;// save the first position of Vertex before moving
			firstY = 20;
		} else {
			if (state == StateOnLeftPane.VERTEX) {
				vertex = new Vertex(x, y, radius, Color.CADETBLUE);
				vertices.add(vertex);
				System.out.println("Size of vertices: " + vertices.size());
				// vertex.setLabel(x, y, ++indexVertex, "-fx-fill: yellow");

			} else if (state == StateOnLeftPane.VERTEX_CUSTOM_TEXT) {
				// String valueText = controller.showAddLabelPopupVertex();
				vertex = new Vertex(x, y, radius, Color.CADETBLUE);
				vertices.add(vertex);
				indexVertex++;
				System.out.println("Size of vertices: " + vertices.size());
				vertex.setLabel(x, y, "test", "-fx-fill: yellow");
			} else {
				// do nothing
			}
		}

		return vertex;

	}

	public Edge createEdge(StateOnLeftPane state, double x, double y) {

		Edge edge = new Edge(15, 20, x, y, Color.BLUEVIOLET);
		edge.setStrokeWidth(2);

		return edge;

	}
}
