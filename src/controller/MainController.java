package controller;

import java.awt.geom.Point2D;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.ResourceBundle;

import javax.imageio.ImageIO;

import application.Main;
import enums.StateOnLeftPane;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.MenuBar;
import javafx.scene.control.RadioButton;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.*;
import util.Calculate;
import util.TikZData;
import util.ValidateInput;

public class MainController extends AbstractController implements Initializable {

	@FXML
	private AnchorPane parentPane;
	@FXML
	private SplitPane splitPane;
	@FXML
	private AnchorPane leftPane;
	@FXML
	private AnchorPane rightPane;
	@FXML
	private RadioButton rbVertex;
	@FXML
	private RadioButton rbVertexCustomText;
	@FXML
	private RadioButton rbEdge;
	@FXML
	private RadioButton rbRemoveObj;
	@FXML
	private RadioButton rbChangeLbl;
	@FXML
	private RadioButton rbMoveLbl;
	@FXML
	private RadioButton formEdge;
	@FXML
	private RadioButton curveEdge;
	@FXML
	private RadioButton rbVertexIcon;
	@FXML
	private Button btnRemoveAll;
	@FXML
	private MenuBar menuBar;
	@FXML
	private ImageView imageView;

	private List<Vertex> vertices = new ArrayList<>();
	private List<Edge> edges = new ArrayList<>();
	private StateOnLeftPane eventOnLeftPane = StateOnLeftPane.VERTEX;
	private boolean isClickedInsideVertex = false;
	private boolean isMovingLabel = false;
	private Vertex firstVertex = null;
	private Vertex currentVertex = null;
	private Edge currentEdge = null;
	private Edge existEdge = null;
	private int indexVertex = -1;
	private int distanceToDeleteEdge = 15;// the limit of distance when click to delete the edge
	private boolean isDrawingEdge = false;
	private double deltaX, deltaY;// use to move the Vertex
	private double firstX, firstY;// save the first position of the Vertex before moving the Vertex
	private List<Integer> hasPoints = new ArrayList<>();
	private int radius = 20;// radius of Vertex
	HashMap<Vertex, Edge> parentEdge = new HashMap<Vertex, Edge>();// save the parent Edge
	private List<Edge> invisibleEdges = new ArrayList<>();// TODO tomorrow
	String typeEdge = "";
	String weightEdge = "";
	HashMap<String, Object> resultMap = null;
	Image imageRemove;
	Image imageCross;
	Image imageVertex;
	Image imageEdge;
	Image imageLabel;
	private boolean displayCurveEdgePoints = false;
	private Circle currentCurvePoint = null;
	private boolean isClickedInsideCurvePoint = false;

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		rbVertex.setSelected(true);
		// fix width left pane when resize window
		menuBar.prefWidthProperty().bind(parentPane.widthProperty());
		SplitPane.setResizableWithParent(leftPane, Boolean.FALSE);
		imageRemove = new Image("/death_head.png", 25, 25, false, false);
		imageCross = new Image("/cross.jpg", 25, 25, false, false);
		imageVertex = new Image("/vertexIcon.jpg", 25, 25, false, false);
		imageEdge = new Image("/edgeIcon.png", 25, 25, false, false);
		imageLabel = new Image("/labelIcon.png", 25, 25, false, false);

		rbVertex.setGraphic(new ImageView(imageVertex));
		rbEdge.setGraphic(new ImageView(imageEdge));
		rbRemoveObj.setGraphic(new ImageView(imageRemove));
		rbChangeLbl.setGraphic(new ImageView(imageLabel));
		rbMoveLbl.setGraphic(new ImageView(imageLabel));
		rbVertexIcon.setGraphic(new ImageView(imageCross));
	}

	public void handleFormEdgePress() {
		System.out.println("\nForm of Edge is pressed");
		eventOnLeftPane = StateOnLeftPane.FORM_EDGE;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleVertexPress() {
		System.out.println("\nVertex is pressed");
		eventOnLeftPane = StateOnLeftPane.VERTEX;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleVertexCustomTextPress() {
		System.out.println("\nCreate vertex custom text");
		eventOnLeftPane = StateOnLeftPane.VERTEX_CUSTOM_TEXT;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleEdgePress() {
		System.out.println("\nEdge is pressed");
		eventOnLeftPane = StateOnLeftPane.EDGE;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleRemoveObjPress() {
		System.out.println("\nRemove Object is pressed");
		eventOnLeftPane = StateOnLeftPane.REMOVE;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleChangeLabelPress() {
		System.out.println("\nChange Label is pressed");
		eventOnLeftPane = StateOnLeftPane.CHANGE_LABEL;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleMoveLabelPress() {
		System.out.println("\nMove Label is pressed");
		eventOnLeftPane = StateOnLeftPane.MOVE_LABEL;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleRemoveIconPress() {
		System.out.println("\nRemoveIconPress");
		eventOnLeftPane = StateOnLeftPane.REMOVE_ICON;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleVertexIconPress() {
		System.out.println("\nVertexIconPress");
		eventOnLeftPane = StateOnLeftPane.VERTEX_ICON;
		if (displayCurveEdgePoints) {
			displayCurveEdgePoints = false;
			hideCurveEdgePoints();
		}
	}

	public void handleCurveEdgePress() {
		System.out.println("\nhandle Curve Edge Press");
		eventOnLeftPane = StateOnLeftPane.CURVE_EDGE;
		displayCurveEdgePoints = true;
		// set visible for curve edge points
		for (Edge edge : edges) {
			if (edge.isCurveEdge()) {
				edge.setCurveEdgePoints(true);
				edge.setCurvePoint1();
				edge.setCurvePoint2();
				rightPane.getChildren().add(edge.getCurvePoint1());
				rightPane.getChildren().add(edge.getCurvePoint2());
			}
		}

	}

	public void hideCurveEdgePoints() {
		System.out.println("\nHide Curve Edge Points ");
		// set invisible for curve edge points
		for (Edge edge : edges) {
			if (edge.isCurveEdge()) {
				edge.setCurveEdgePoints(false);
				rightPane.getChildren().remove(edge.getCurvePoint1());
				rightPane.getChildren().remove(edge.getCurvePoint2());
			}
		}
	}

	@FXML
	public void clickMouse(MouseEvent event) {

	}

	@FXML
	public void pressMouse(MouseEvent event) {
		if (eventOnLeftPane == StateOnLeftPane.VERTEX || eventOnLeftPane == StateOnLeftPane.VERTEX_CUSTOM_TEXT) {// draw
			Vertex vertex = null; // vertice
			if (isOnAVertex(event.getX(), event.getY())) {// if mouse click inside the vertice then move when drag else
															// create a new vertice
				isClickedInsideVertex = true;
				deltaX = event.getX() - currentVertex.getX();
				deltaY = event.getY() - currentVertex.getY();
				firstX = currentVertex.getX();// save the first position of Vertex before moving
				firstY = currentVertex.getY();
			} else {
				if (eventOnLeftPane == StateOnLeftPane.VERTEX) {
					vertex = new Vertex(event.getX(), event.getY(), radius, Color.CADETBLUE);
					vertices.add(vertex);
					System.out.println("Size of vertices: " + vertices.size());
					vertex.setLabel(event.getX(), event.getY(), ++indexVertex, "-fx-fill: yellow");
					vertex.setIndex(indexVertex);
					// Setting the stroke width of the circle
					rightPane.getChildren().add(vertex);
					rightPane.getChildren().add(vertex.getLabel());

				} else if (eventOnLeftPane == StateOnLeftPane.VERTEX_CUSTOM_TEXT) {
					String valueText = showAddLabelPopupVertex();
					if (valueText == "") {
						System.out.println("You just closed Popup");
					} else {
						vertex = new Vertex(event.getX(), event.getY(), radius, Color.CADETBLUE);
						vertices.add(vertex);
						indexVertex++;
						System.out.println("Size of vertices: " + vertices.size());
						vertex.setLabel(event.getX(), event.getY(), valueText, "-fx-fill: yellow");
						vertex.setIndex(indexVertex);
						// Setting the stroke width of the circle
						rightPane.getChildren().add(vertex);
						rightPane.getChildren().add(vertex.getLabel());
					}

				} else {
					// do nothing
				}
			}

		} else if (eventOnLeftPane == StateOnLeftPane.EDGE) {// get starting point
			if (isOnAVertex(event.getX(), event.getY()) && !currentVertex.isIntermediatePoint()) {
				// if mouse click inside the vertice then move when drag else
				// create a new vertice
				Edge edge = new Edge(currentVertex.getX(), currentVertex.getY(), event.getX(), event.getY(),
						Color.BLUEVIOLET);
				edge.setStrokeWidth(2);
				System.out.println("Size of edges: " + edges.size());
				currentEdge = edge;
				rightPane.getChildren().add(currentEdge);
				isDrawingEdge = true;// this one,we have to put here in the end of this function because we want to
										// save FirstVertex
			} else {
				System.out.println("You should click inside the Vertex for drawing the line");
			}
		}

		else if (eventOnLeftPane == StateOnLeftPane.REMOVE) {// remove all
			removeObject(event.getX(), event.getY());
			System.out.println("\nSize of parentEdge: " + parentEdge.size());
			System.out.println("Size of invisibleEdges: " + invisibleEdges.size());
			System.out.println("Size of vertices: " + vertices.size());
			System.out.println("Size of edges: " + edges.size());
			System.out.println("Size of haspoints: " + hasPoints.size());

		} else if (eventOnLeftPane == StateOnLeftPane.CHANGE_LABEL) {
			if (isOnALabel(event.getX(), event.getY())) {
				showChangeLabelPopupEdge(currentEdge);
				System.out.println("New value of label: " + currentEdge.getTextWeight().getText());
				String textWeight = currentEdge.getTextWeight().getText();
				// do more code here
				if (parentEdge.containsValue(currentEdge)) {
					System.out.println("Update text weight to the intermediate edge! ");
					for (Entry<Vertex, Edge> map : parentEdge.entrySet()) {
						if (map.getValue() == currentEdge) {// get map.getKey();
							for (Edge edge : edges) {
								if (edge.getV1() == map.getKey()) {
									System.out.println("This is edge with value: " + edge.getTextWeight().getText());
									edge.setTextWeight(textWeight);
								}
							}
						}
					}
				}
			}
		} else if (eventOnLeftPane == StateOnLeftPane.MOVE_LABEL) {
			if (isOnALabel(event.getX(), event.getY())) {
				isMovingLabel = true;
				System.out.println("You just clicked inside the Label!");
			}
		} else if (eventOnLeftPane == StateOnLeftPane.VERTEX_ICON) {// INTERMEDIATE POINT
			if (isOnAVertex(event.getX(), event.getY())) {// move vertices (include intermediate point)
				isClickedInsideVertex = true;
				deltaX = event.getX() - currentVertex.getX();
				deltaY = event.getY() - currentVertex.getY();
				firstX = currentVertex.getX();// save the first position of Vertex before moving
				firstY = currentVertex.getY();
			} else { // create the intermediate point.
				Edge e = null;
				int smallestDistance = 1000;
				for (Edge edge : edges) {
					if (invisibleEdges.contains(edge) == false && !edge.isCurveEdge()) {
						int distance = Calculate.heightOfTriangle(event.getX(), event.getY(), edge.getX1(),
								edge.getY1(), edge.getX2(), edge.getY2());
						if (edge.getCircle() == null && distance <= distanceToDeleteEdge) {
							if (distance < smallestDistance) {
								e = edge;
								smallestDistance = distance;
								System.out.println("Smallest distance: " + distance);
							}
						}
					}
				}

				if (e != null) {// find out the intermediate point
					Point2D point = Calculate.getPointOnEdge(event.getX(), event.getY(), e.getX1(), e.getY1(),
							e.getX2(), e.getY2());
					if (!e.getV1().contains(point.getX(), point.getY())
							&& !e.getV2().contains(point.getX(), point.getY())) {
						Vertex vertex = new Vertex(point.getX(), point.getY());
						vertices.add(vertex);
						indexVertex++;
						vertex.setIndex(indexVertex);
						rightPane.getChildren().add(vertex);

						// map points: list intermediate points of the edge
						if (!e.getV1().isIntermediatePoint() && !e.getV2().isIntermediatePoint()) {
							parentEdge.put(vertex, e);
							if (!invisibleEdges.contains(e)) {
								invisibleEdges.add(e);
							}
						} else {
							Edge fatherEdge = new Edge();
							for (Entry<Vertex, Edge> map : parentEdge.entrySet()) { //
								if (e.getV1().isIntermediatePoint() && map.getKey() == e.getV1()) {
									fatherEdge = map.getValue();
									break;
								} else if (e.getV2().isIntermediatePoint() && map.getKey() == e.getV2()) {
									fatherEdge = map.getValue();
									break;
								}
							}
							parentEdge.put(vertex, fatherEdge);
						}
						System.out.println("One more intermediate point \nSize of vertices: " + vertices.size());

						// add the first edge to intermediate point
						Edge edge1 = new Edge(e.getX1(), e.getY1(), vertex.getX(), vertex.getY(), Color.BLUEVIOLET);
						edge1.setDirection(false);// no direction for first edge
						edge1.setIntermediateEdge(true);
						edge1.setStrokeWidth(2);
						edge1.setV1(e.getV1());
						edge1.setV2(vertex);
						edges.add(edge1);
						rightPane.getChildren().add(edge1);
						edge1.updateEdge();

						// add the second edge to intermediate point
						Edge edge2 = new Edge(vertex.getX(), vertex.getY(), e.getX2(), e.getY2(), Color.BLUEVIOLET);
						if (e.getDirection()) {
							edge2.setDirection(true);
						}
						edge2.setIntermediateEdge(true);
						edge2.setStrokeWidth(2);
						edge2.setV1(vertex);// set Vertex for starting point
						edge2.setV2(e.getV2());// set Vertex for ending point
						edge2.setTextWeight(parentEdge.get(edge2.getV1()).getTextWeight().getText());
						edges.add(edge2);
						rightPane.getChildren().add(edge2);
						rightPane.getChildren().add(edge2.getArrow1());
						rightPane.getChildren().add(edge2.getArrow2());
						edge2.updateEdge();

						// remove edge on the rightPane.
						rightPane.getChildren().remove(e);
						rightPane.getChildren().remove(e.getArrow1());
						rightPane.getChildren().remove(e.getArrow2());
						if (e.getV1().isIntermediatePoint() || e.getV2().isIntermediatePoint()) {// remove intermediate
																									// edge
							edges.remove(e);
						}
					}
				}

			} //// end creating the intermediate point.
		} else if (eventOnLeftPane == StateOnLeftPane.FORM_EDGE) {
			System.out.println("We will change the form of edge here!");
			Edge e = null;
			int smallestDistance = 1000;
			for (Edge edge : edges) {
				if (!invisibleEdges.contains(edge)) {
					int distance;
					if (edge.getCurve() == null) {// Calculate distance of Main Edge
						distance = Calculate.heightOfTriangle(event.getX(), event.getY(), edge.getX1(), edge.getY1(),
								edge.getX2(), edge.getY2());
					} else {// Calculate distance of curve edge
						distance = Calculate.heightOfTriangle(event.getX(), event.getY(),
								edge.getCurve().getControlX1(), edge.getCurve().getControlY1(),
								edge.getCurve().getControlX2(), edge.getCurve().getControlY2());
						if(edge.getCurve().contains(event.getX(), event.getY())) {
							distance = 0;
						}
					}
					if (edge.getCircle() == null && distance <= distanceToDeleteEdge) {
						if (distance < smallestDistance) {
							e = edge;
							smallestDistance = distance;
							System.out.println("Smallest distance: " + distance);
						}
					}
				}
			}
			if (e != null) {
				if (!e.isCurveEdge()) {// if straight edge -> set curve(right)
					e.setCurve("right");
					rightPane.getChildren().add(e.getCurve());
					e.setVisibleMainEdge(false);
				} else if (e.getBendOfCurveEdgeInTikZData() == "right") {// if curve(right) -> set curve(left)
					rightPane.getChildren().remove(e.getCurve());
					e.setCurve("left");
					rightPane.getChildren().add(e.getCurve());
				} else {// if curve(left) -> set straight edge
					rightPane.getChildren().remove(e.getCurve());
					e.setVisibleMainEdge(true);
					e.setNullCurveEdge();
				}
				e.updateEdge();
			}
		} else if (eventOnLeftPane == StateOnLeftPane.CURVE_EDGE) {
			if (isOnCurvePoint(event.getX(), event.getY())) {// move curve points
				isClickedInsideCurvePoint = true;
				System.out.println("is Clicked Inside Curve Point");
				deltaX = event.getX() - currentCurvePoint.getCenterX();
				deltaY = event.getY() - currentCurvePoint.getCenterY();
			}

		}

		else {
			// do nothing
		}
	}

	public boolean isOnCurvePoint(double xM, double yM) {
		boolean flag = false;
		if (edges.size() > 0) {
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getCurvePoint1().contains(xM, yM)) {
					currentCurvePoint = edges.get(i).getCurvePoint1();
					currentEdge = edges.get(i);
					flag = true;
					break;
				} else if (edges.get(i).getCurvePoint2().contains(xM, yM)) {
					currentCurvePoint = edges.get(i).getCurvePoint2();
					currentEdge = edges.get(i);
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	public boolean isOnAVertex(double xM, double yM) {
		boolean flag = false;
		if (vertices.size() > 0) {
			for (int i = 0; i < vertices.size(); i++) {
				if (vertices.get(i).contains(xM, yM)) {
					currentVertex = vertices.get(i);
					flag = true;
					break;
				}
			}
		}
		if (!isDrawingEdge && flag) {// get the first vertex when we haven't drawn the edge
			firstVertex = currentVertex;
		}
		return flag;
	}

	public boolean isOnALabel(double xL, double yL) {
		boolean flag = false;
		if (edges.size() > 0) {
			for (int i = 0; i < edges.size(); i++) {
				if (edges.get(i).getCircleLable().contains(xL, yL)) {
					currentEdge = edges.get(i);
					flag = true;
					break;
				}
			}
		}
		return flag;
	}

	@FXML
	public void moveMouse(MouseEvent event) {
		if (isClickedInsideCurvePoint) {
			double x = event.getX() - deltaX;
			double y = event.getY() - deltaY;
			currentCurvePoint.setCenterX(x);
			currentCurvePoint.setCenterY(y);
			currentEdge.updateEdge();

		}
		if (isClickedInsideVertex) {
			double x = event.getX() - deltaX;
			double y = event.getY() - deltaY;
			currentVertex.setX(x);
			currentVertex.setY(y);
			currentVertex.getLabel().setX(x);
			currentVertex.getLabel().setY(y);
			movingTheEdges(x, y);
		}
		if (isDrawingEdge) {
			currentEdge.setX2(event.getX());
			currentEdge.SetY2(event.getY());
		}
		if (isMovingLabel) {
			currentEdge.setDeltaText(event.getX(), event.getY());
		}

	}

	public Edge getExistEdge(Edge E) {
		Edge edge = null;
		existEdge = null;// we use this one to save opposite edge.
		for (Edge e : edges) {
			if (e.getX1() == E.getX1() && e.getY1() == E.getY1() && e.getX2() == E.getX2() && e.getY2() == E.getY2()) {
				edge = e;
				break;
			} else if (e.getX1() == E.getX2() && e.getY1() == E.getY2() && e.getX2() == E.getX1()
					&& e.getY2() == E.getY1()) {
				existEdge = e;// save opposite edge
				break;
			}
		}
		return edge;
	}

	public void releaseMouse(MouseEvent event) {
		isClickedInsideVertex = false;
		isClickedInsideCurvePoint = false;

		if (eventOnLeftPane == StateOnLeftPane.VERTEX) {

		} else if (eventOnLeftPane == StateOnLeftPane.EDGE) {
			if (isOnAVertex(event.getX(), event.getY()) && !currentVertex.isIntermediatePoint()) {
				// && currentVertex != firstVertex ->cancel this is on a vertex
				// -> set the ending point of edge is the central of Vertex
				resultMap = showPopupEdge();
				typeEdge = (String) resultMap.get("typeEdge");
				weightEdge = (String) resultMap.get("weight");
				if (ValidateInput.userWantToCreate(typeEdge, weightEdge)) {
					if (weightEdge != null && !weightEdge.contentEquals("")) {
						currentEdge.setTextWeight(weightEdge);// add weight to the edge
					}

					if (weightEdge.contentEquals("")) {
						currentEdge.setTextWeight("0");
					}

					if (typeEdge != null) {
						if (typeEdge.equalsIgnoreCase("directed")) {
							currentEdge.setDirection(true);
						} else {
							currentEdge.setDirection(false);
						}
					}
					// add the edge and its feature
					currentEdge.setPoint2(currentVertex.getX(), currentVertex.getY());
					rightPane.getChildren().add(currentEdge.getTextWeight());
					// draw a curve
					if (currentEdge.getX1() == currentEdge.getX2() && currentEdge.getY1() == currentEdge.getY2()) {
						System.out.println("This is a point, not an edge! Draw a curve");
						// if(currentEdge.getCircle() == null) {
						currentEdge.setCircle(radius);
						rightPane.getChildren().add(currentEdge.getCircle());
						currentEdge.getCircle().toBack();
						rightPane.getChildren().add(currentEdge.getArrow1());
						rightPane.getChildren().add(currentEdge.getArrow2());
						currentEdge.updateEdge();
						// }
					} else {// draw an edge
						rightPane.getChildren().add(currentEdge.getArrow1());
						rightPane.getChildren().add(currentEdge.getArrow2());
						currentEdge.updateEdge();
					}

					// add vertex v1 and vertex v2 to the Edge
					currentEdge.setV1(firstVertex);
					currentEdge.setV2(currentVertex);

					// update the edge
					Edge edge = getExistEdge(currentEdge);
					System.out.println("existEdge" + existEdge);
					if (edge != null) {
						rightPane.getChildren().remove(edge.getCircle());// remove the last circle
						System.out.println("This edge is existed! Update the new edge!");
						// set the form of currentEdge similar to existEdge
						if (edge.isCurveEdge()) {
							System.out.println("Set this edge is curve edge");
							currentEdge.setCurve("left");
							rightPane.getChildren().add(currentEdge.getCurve());
							currentEdge.updateEdge();
							currentEdge.setVisibleMainEdge(false);// set invisible
						}
						if (parentEdge.containsValue(edge)) {
							System.out.println("Update the text weight for intermediate edge");
							edge.setTextWeight(currentEdge.getTextWeight().getText());
							edge.updateEdge();
							removeEdge(currentEdge);
						} else {
							removeEdge(edge);
						}
					}

					if (edge == null && existEdge != null) {
						if (existEdge.getDirection() && currentEdge.getDirection()) {
							System.out.println("=> two edges between two vertices");
							System.out.println("=> Change the form of edges to curve edges!");
							// Change the form of exist edge
							existEdge.setCurve("left");
							rightPane.getChildren().add(existEdge.getCurve());
							existEdge.updateEdge();
							existEdge.setVisibleMainEdge(false);// set invisible
							// Change the form of current edge
							currentEdge.setCurve("left");
							rightPane.getChildren().add(currentEdge.getCurve());
							currentEdge.updateEdge();
							currentEdge.setVisibleMainEdge(false);// set invisible
						} else {
							System.out.println(
									"Can't exist directed edge and undirected edge between two edges!\n Update the new edge!");
							removeEdge(existEdge);
						}
					}
					edges.add(currentEdge);
					currentEdge = null;// remove it after we don't use it anymore.

				} else {
					rightPane.getChildren().remove(currentEdge);
				}
			} else { // mouse release not on a vertex -> remove current Edge
				rightPane.getChildren().remove(currentEdge);
			}
			isDrawingEdge = false;
		} else if (eventOnLeftPane == StateOnLeftPane.MOVE_LABEL) {
			if (isMovingLabel) {
				System.out.println("You just released the Label!");
				currentEdge.setDeltaText(event.getX(), event.getY());
				isMovingLabel = false;
			}
			currentEdge = null; // remove it after we don't use it anymore.

		} else if (eventOnLeftPane == StateOnLeftPane.CURVE_EDGE) {

		}

		else {
			// do nothing
		}
	}

	public void movingTheEdges(double newX, double newY) {
		for (Edge edge : edges) {
			if (firstX == edge.getX1() && firstY == edge.getY1()) {
				edge.setEdge(newX, newY, edge.getX2(), edge.getY2());
				edge.updateEdge();
			} else if (firstX == edge.getX2() && firstY == edge.getY2()) {
				edge.setEdge(edge.getX1(), edge.getY1(), newX, newY);
				edge.updateEdge();
			}
		}
		firstX = newX;
		firstY = newY;
	}

	public void removeObject(double cX, double cY) {
		if (isOnAVertex(cX, cY)) {// delete Vertex
			System.out.println("Delete Vertex!");

			for (Vertex v : vertices) {
				if (v.contains(cX, cY)) {
					if (v.isIntermediatePoint()) {
						Edge e1 = new Edge();
						Edge e2 = new Edge();
						// Make the father edge for intermediate point before remove this point
						for (Edge edge : edges) {
							if (edge.getV1() == v) {
								e2 = edge;
							} else if (edge.getV2() == v) {
								e1 = edge;
							}
						}
						if (!e1.getV1().isIntermediatePoint() && !e2.getV2().isIntermediatePoint()) {
							removeIntermediatePoint(v);// remove it when father edge is normal edge
						} else {
							// set right edge to the father edge
							e2.setX1(e1.getX1());
							e2.setY1(e1.getY1());
							e2.setV1(e1.getV1());
							// remove left edge (e1)
							rightPane.getChildren().remove(e1);
							edges.remove(e1);
							rightPane.getChildren().remove(v);
							vertices.remove(v);
							parentEdge.remove(v);// remove the parent of point v out of the HashMap
							e2.updateEdge();
						}
					} else {
						removeVertex(v);
					}
					break;
				}
			}
		} else {// delete Edge
			for (Edge edge : edges) {
				int distance;
				if (edge.getCurve() == null) {// Calculate distance of Main Edge
					distance = Calculate.heightOfTriangle(cX, cY, edge.getX1(), edge.getY1(), edge.getX2(),
							edge.getY2());
				} else {// Calculate distance of curve edge
					distance = Calculate.heightOfTriangle(cX, cY, edge.getCurve().getControlX1(),
							edge.getCurve().getControlY1(), edge.getCurve().getControlX2(),
							edge.getCurve().getControlY2());
				}

				if ((edge.getCircle() == null && distance <= distanceToDeleteEdge)
						|| ((edge.getCurve()!= null) && (edge.getCurve().contains(cX, cY)))) {
					// check normal edge and cubic edge between two vertices is parallel or not?
					double corner1, corner2;
					corner1 = 0;
					corner2 = 0;
					int corner = 0;
					if ((edge.getCurve()!= null) && (!edge.getCurve().contains(cX, cY))) {
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
					}
					if (corner == 0) {
						System.out.println("You select the line with the distance to the edge is: " + distance);
						hasPoints.add(edges.indexOf(edge));// save index of edge in edges
						currentEdge = edge;
					}
				}
				if (edge.getCircle() != null && edge.getCircle().contains(cX, cY)) {
					System.out.println("Delete the circle Edge!");
					hasPoints.add(edges.indexOf(edge));// save index of edge in edges
				}
			}

			if (currentEdge != null) {
				if (currentEdge.getV1().isIntermediatePoint()) {
					removeIntermediatePoint(currentEdge.getV1());
					System.out.println("Delete intermediate point v1");
					hasPoints.clear();
				} else if (currentEdge.getV2().isIntermediatePoint()) {
					removeIntermediatePoint(currentEdge.getV2());
					System.out.println("Delete intermediate point v2");
					hasPoints.clear();
				} else {
					// delete the points in the list of lines from highest index to lowest index
					for (int i = hasPoints.size() - 1; i >= 0; i--) {// we have to delete from highest index to lowest
																		// index
						System.out.println("-> Delete line at index: " + hasPoints.get(i));
						removeEdge(edges.get(hasPoints.get(i)));

					}
				}
			}

			hasPoints.clear();// clear hasPoints (*_*)
		}
	}

	private void removeIntermediatePoint(Vertex point) {// return the father edge.
		List<Vertex> points = new ArrayList<>();
		Edge fatherEdge = parentEdge.get(point);
		for (Entry<Vertex, Edge> map : parentEdge.entrySet()) {
			if (map.getValue() == fatherEdge) {
				points.add(map.getKey());
			}
		}
		System.out.println("=> Number of points in this edge: " + points.size());
		for (Vertex v : points) {
			removeVertex(v);
			parentEdge.remove(v);
		}
		points.clear();// delete memory of the list of points
		rightPane.getChildren().add(fatherEdge);
		rightPane.getChildren().add(fatherEdge.getArrow1());
		rightPane.getChildren().add(fatherEdge.getArrow2());
		invisibleEdges.remove(fatherEdge);// remove father edge from the list of invisible edges
	}

	private void removeVertex(Vertex v) {
		// Before remove the normal vertex, we find intermediate point & remove it first
		if (!v.isIntermediatePoint()) {
			boolean isIntermediateEdge;
			do {
				isIntermediateEdge = false; // remove all intermediate edges that related
				for (Entry<Vertex, Edge> map : parentEdge.entrySet()) {
					if (map.getValue().getV1() == v || map.getValue().getV2() == v) {
						// here we find out the father edge and intermediate point.
						removeIntermediatePoint(map.getKey());
						hasPoints.clear();
						isIntermediateEdge = true;
						break;
					}
				}
			} while (isIntermediateEdge);
		}
		// Now we can remove the vertex
		Point2D point = new Point2D.Double(v.getCenterX(), v.getCenterY());
		hasPoints.clear();
		for (Edge edge : edges) {
			if (point.getX() == edge.getX1() && point.getY() == edge.getY1()) {
				// convert the line that has started point to Point
				edge.setEdge(point.getX(), point.getY(), point.getX(), point.getY());
				hasPoints.add(edges.indexOf(edge));
				System.out.println("add to hasPoints at index: " + edges.indexOf(edge));
			} else if (point.getX() == edge.getX2() && point.getY() == edge.getY2()) {
				// convert the line that has ended point to Point
				edge.setEdge(point.getX(), point.getY(), point.getX(), point.getY());
				hasPoints.add(edges.indexOf(edge));
				System.out.println("add to hasPoints at index: " + edges.indexOf(edge));
			}
		}
		// delete the points in the list of lines from highest index to lowest index
		for (int i = hasPoints.size() - 1; i >= 0; i--) {
			System.out.println("-> Delete edge at index: " + hasPoints.get(i) + " of size: " + hasPoints.size());
			removeEdge(edges.get(hasPoints.get(i)));
		}
		hasPoints.clear();// clear hasPoints (*_*)

		// delete Vertex on the list and rightPane
		vertices.remove(v);// remove Vertex on the list (note: Vertex include label inside)
		rightPane.getChildren().remove(v);// remove Vertex on the rightPane
		rightPane.getChildren().remove(v.getLabel());// remove Label of Vertex on rightPane
	}

	private void removeEdge(Edge edge) {
		System.out.println("Deleted the edge!");
		if (edge.getCircle() != null) {
			rightPane.getChildren().remove(edge.getCircle());
		}
		if (edge.getCurve() != null) {
			rightPane.getChildren().remove(edge.getCurve());
		}
		edges.remove(edge);//
		rightPane.getChildren().remove(edge.getTextWeight());
		rightPane.getChildren().remove(edge.getArrow1());
		rightPane.getChildren().remove(edge.getArrow2());
		rightPane.getChildren().remove(edge);
	}

	private HashMap<String, Object> showPopupEdge() {
		// HashMap<String, Object> resultMap = new HashMap<String, Object>();
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/ui/Popup.fxml"));
		// initializing the controller
		PopupEdgeController popupEdgeController = new PopupEdgeController();
		loader.setController(popupEdgeController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// Giving the popup controller access to the popup stage (to allow the
			// controller to close the stage)
			popupEdgeController.setStage(popupStage);
			if (this.main != null) {
				popupStage.initOwner(main.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return popupEdgeController.getResult();
	}

	@FXML
	public void removeAll() {
		System.out.println("remove all");
		rightPane.getChildren().clear();
		edges.clear();
		vertices.clear();
		indexVertex = -1;// reset index vertex
		hasPoints.clear();
		parentEdge.clear();
		invisibleEdges.clear();
		resultMap.clear();
	}

	private Edge showChangeLabelPopupEdge(Edge edge) {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/ui/ChangeLabelPopup.fxml"));
		// initializing the controller
		ChangeLabelPopupController changeLabelController = new ChangeLabelPopupController(edge);
		loader.setController(changeLabelController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// Giving the popup controller access to the popup stage (to allow the
			// controller to close the stage)
			changeLabelController.setStage(popupStage);
			if (this.main != null) {
				popupStage.initOwner(main.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return changeLabelController.getResult();
	}

	public void onNewPage() {
		System.out.println("open new page");

		Main main = new Main();
		Stage primaryStage = new Stage();
		main.start(primaryStage);

	}

	public void onClosePage() {
		System.out.println("close page");
	}

	public void onExit() {
		System.out.println("exit");
		Platform.exit();
		System.exit(0);
	}

	public void onDirectorChooserImage() throws IOException {
		System.out.println("Open Director Chooser Window Save Image.");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("png files (*.png)", "*.png"));
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("jpeg files (*.jpeg)", "*.jpeg"));
		configuringDirectoryChooser(fileChooser);
		// Show save file dialog
		File file = fileChooser.showSaveDialog(main.getPrimaryStage());
		if (file != null) {
			System.out.println("Save Image Start");
			saveImageToFile(file);

		}
	}

	public void addImageToSnapshotArea(Node root, File file) {
		SnapshotParameters params = new SnapshotParameters();
		WritableImage image = rightPane.snapshot(params, null);
		try {
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public void saveImageToFile(File file) {
		// Pad the capture area
		addImageToSnapshotArea(parentPane, file);

	}

	private void configuringDirectoryChooser(FileChooser fileChooser) {
		fileChooser.setTitle("Select Some Directories");
		fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
	}

	String inputTestTikZ = "tikiZ command line";

	public void onDirectorChooserTikZ() {
		System.out.println("Open Dialog File chooser Export TikiZ");
		FileChooser fileChooser = new FileChooser();
		// Set extension filter
		fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("tikz files (*.tex)", "*.tex"));
		configuringDirectoryChooser(fileChooser);
		// Show save file dialog
		File file = fileChooser.showSaveDialog(main.getPrimaryStage());
		if (file != null) {
			System.out.println("Start Save text to file tiKZ");
			updatePositionOfLabelToSegmentEdge();
			this.saveTextToFile(TikZData.handlerData(vertices, edges, invisibleEdges), file);
			System.out.println("End Save text to file tiKZ");
		}

	}
	
	public void updatePositionOfLabelToSegmentEdge() {//update position of Label to the segment edge
		for (Edge edge : edges) {
			if(edge.getV1().isIntermediatePoint()) {
				currentEdge = parentEdge.get(edge.getV1());
				edge.setPositionOfTextWeight(currentEdge.getTextWeight().getX(), currentEdge.getTextWeight().getY());
			} else if (edge.getV2().isIntermediatePoint()) {
				currentEdge = parentEdge.get(edge.getV2());
				edge.setPositionOfTextWeight(currentEdge.getTextWeight().getX(), currentEdge.getTextWeight().getY());
			}
		}
	}

	private String showAddLabelPopupVertex() {
		FXMLLoader loader = new FXMLLoader();
		loader.setLocation(getClass().getResource("/ui/AddLabelToVertexPopup.fxml"));
		// initializing the controller
		AddLabelToVertexPopupController addLabelToVertexPopupController = new AddLabelToVertexPopupController();
		loader.setController(addLabelToVertexPopupController);
		Parent layout;
		try {
			layout = loader.load();
			Scene scene = new Scene(layout);
			// this is the popup stage
			Stage popupStage = new Stage();
			// Giving the popup controller access to the popup stage (to allow the
			// controller to close the stage)
			addLabelToVertexPopupController.setStage(popupStage);
			if (this.main != null) {
				popupStage.initOwner(main.getPrimaryStage());
			}
			popupStage.initModality(Modality.WINDOW_MODAL);
			popupStage.setScene(scene);
			popupStage.showAndWait();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return addLabelToVertexPopupController.getResult();
	}

	private void saveTextToFile(StringBuilder content, File file) {
		try {
			PrintWriter writer;
			writer = new PrintWriter(file);
			writer.println(content);
			writer.close();
		} catch (IOException ex) {
			ex.printStackTrace();

		}
	}

}