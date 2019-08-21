/*******************************************************************************
 * Copyright (C) 2018 Olivier Baudon
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package util;

import graph.DirectedEdge;
import graph.Graph;
import graph.MultiGraph;

import static graph.Graph.Edge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class K_Coloring {
	public static final boolean DEBUG = false;

	private static void getConnectedComponent(
			final Graph<String, Edge<String>> g, final String vertex,
			Graph<String, Edge<String>> connectedComponent,
			Edge<String> previousEdge) {
		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (e != previousEdge) {
				if (!connectedComponent.containsEdge(e)) {
					connectedComponent.addVertex(e.getOpposite(vertex));

					connectedComponent.addEdge(e);

					getConnectedComponent(g, e.getOpposite(vertex),
							connectedComponent, e);
				}
			}
		}
	}

	/**
	 * Return the connected component which contains the vertex vertex
	 * 
	 * @param g
	 *            graph which contains the connected component
	 * @param vertex
	 *            a vertex of the connected component
	 * @return connected component which contains the vertex vertex
	 */
	public static Graph<String, Edge<String>> getConnectedComponent(
			final Graph<String, Edge<String>> g, String vertex) {
		Graph<String, Edge<String>> connectedComponent = new MultiGraph<String, Edge<String>>();
		connectedComponent.addVertex(vertex);

		for (Edge<String> e : g.incidentEdges(vertex)) {
			connectedComponent.addVertex(e.getOpposite(vertex));
			connectedComponent.addEdge(e);
			getConnectedComponent(g, e.getOpposite(vertex), connectedComponent,
					e);
		}
		return connectedComponent;
	}

	private static boolean isOddCycle(Graph<String, Edge<String>> g) {
		for (String vertex : g.vertices()) {
			if (g.degree(vertex) != 2) {
				return false;
			}
		}
		if (g.size() % 2 == 0) {
			return false;
		}
		return true;
	}

	private static void recoloring(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, String x0, int k,
			List<String> path) {
		if (DEBUG) {
			System.out.println("\nrecoloring : "
					+ x0);
		}
		if (path == null) {
			path = new ArrayList<String>();
			if (DEBUG) {
				for (String v : path) {
					System.out.print(v + " ");
				}
			}
		}

		if (!path.contains(x0)) {
			int colorAlpha = 0;
			int listColor[] = new int[k];
			for (int i = 0; i < k; i++) {
				listColor[i] = 0;
			}
			for (Edge<String> e : g.incidentEdges(x0)) {
				listColor[coloring.get(e)]++;
			}
			for (colorAlpha = 0; colorAlpha < k; colorAlpha++) {
				if (listColor[colorAlpha] > 1) {
					break;
				}
			}

			int colorBeta = getMinorColor(g, coloring, x0, k);

			Set<Edge<String>> edgeH = new HashSet<Edge<String>>();
			Set<String> verticesH = new HashSet<String>();
			for (Edge<String> e : g.edges()) {
				if (coloring.get(e) == colorAlpha
						|| coloring.get(e) == colorBeta) {
					edgeH.add(e);
					verticesH.add(e.source());
					verticesH.add(e.target());
				}
			}

			Graph<String, Edge<String>> H = getConnectedComponent(
					g.subgraph(verticesH, edgeH), x0);

			if (!isOddCycle(H)) {
				if (DEBUG) {
					System.out.println("even Cycle ");
					Vizing.displayColoredGraph(H, coloring);
				}
				two_recoloring(H, coloring, colorAlpha, colorBeta);
			} else {

				for (String y : H.neighbors(x0)) {
					if (path.size() == 0 || !y.equals(path.get(0))) {
						int listColorY[] = new int[k];
						for (int i = 0; i < k; i++) {
							listColorY[i] = 0;
						}

						for (Edge<String> e : g.incidentEdges(y)) {
							if (coloring.containsKey(e)) {
								listColorY[coloring.get(e)]++;
							}
						}
						int colorAlpha2 = -1;
						for (int i = 1; i < k; i++) {
							if (listColorY[i] == 0) {
								colorAlpha2 = i;
								break;
							}
						}
						if (colorAlpha2 == -1) {
							for (Edge<String> xy : H.incidentEdges(x0, y)) {
								coloring.replace(xy, colorBeta);
							}
							path.add(0, x0);
							recoloring(g, coloring, y, k, path);
						} else {
							for (Edge<String> xy : H.incidentEdges(x0, y)) {
								coloring.replace(xy, colorAlpha2);
							}

							int nbColorAlpha2 = 0;
							for (Edge<String> e : g.incidentEdges(x0)) {
								if (coloring.get(e) == colorAlpha2) {
									nbColorAlpha2++;
								}
							}
							if (nbColorAlpha2 > 1) {
								recoloring(g, coloring, x0, k, path);
							}
						}
						break;
					}
				}
			}
		} else if (DEBUG) {
			System.out.println(x0 + " cycle");
		}
	}

	private static void recoloring(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, String x0, int k) {
		if (DEBUG) {
			System.out.println("recoloring : " + x0);
		}

		int colorAlpha = 0;
		int listColor[] = new int[k];
		for (int i = 0; i < k; i++) {
			listColor[i] = 0;
		}
		for (Edge<String> e : g.incidentEdges(x0)) {
			listColor[coloring.get(e)]++;
		}
		for (colorAlpha = 0; colorAlpha < k; colorAlpha++) {
			if (listColor[colorAlpha] > 1) {
				break;
			}
		}

		int colorBeta = getMinorColor(g, coloring, x0, k);

		Set<Edge<String>> edgeH = new HashSet<Edge<String>>();
		Set<String> verticesH = new HashSet<String>();
		for (Edge<String> e : g.edges()) {
			if (coloring.get(e) == colorAlpha || coloring.get(e) == colorBeta) {
				edgeH.add(e);
				verticesH.add(e.source());
				verticesH.add(e.target());
			}
		}

		Graph<String, Edge<String>> H = getConnectedComponent(
				g.subgraph(verticesH, edgeH), x0);

		if (!isOddCycle(H)) {
			if (DEBUG) {
				System.out.println("even Cycle ");
				Vizing.displayColoredGraph(H, coloring);
			}
			two_recoloring(H, coloring, colorAlpha, colorBeta);
		} else {

			for (String y : H.neighbors(x0)) {
				int listColorY[] = new int[k];
				for (int i = 0; i < k; i++) {
					listColorY[i] = 0;
				}

				for (Edge<String> e : g.incidentEdges(y)) {
					if (coloring.containsKey(e)) {
						listColorY[coloring.get(e)]++;
					}
				}
				int colorAlpha2 = -1;
				for (int i = 1; i < k; i++) {
					if (listColorY[i] == 0) {
						colorAlpha2 = i;
						break;
					}
				}
				if (colorAlpha2 == -1) {
					for (Edge<String> xy : H.incidentEdges(x0, y)) {

						coloring.replace(xy, colorBeta);
					}
				} else {
					for (Edge<String> xy : H.incidentEdges(x0, y)) {
						coloring.replace(xy, colorAlpha2);
					}
					int nbColorAlpha2 = 0;
					for (Edge<String> e : g.incidentEdges(x0)) {
						if (coloring.get(e) == colorAlpha2) {
							nbColorAlpha2++;
						}
					}
					if (nbColorAlpha2 > 1) {

						recoloring(g, coloring, x0, k);
					}
				}

				break;
			}
		}

	}

	/**
	 * Color a graph with delta color, the color deficiency of each vertex v of
	 * g will <= than 1 if d(v) = delta, = 0 otherwise
	 * 
	 * @param g
	 *            graph to color
	 * @return coloring of the graph
	 */
	public static Map<Edge<String>, Integer> h_coloring(
			Graph<String, Edge<String>> g) {

		int degreeMax = Vizing.degreeMax(g);
		Map<Edge<String>, Integer> coloring = naiveK_Coloration(g,
				degreeMax);
		while (!checkKColoring2(g, coloring, degreeMax)) {

			for (String vertex : g.vertices()) {
				if (g.degree(vertex) == degreeMax) {
					if (colorDeficiency(g, coloring, vertex, degreeMax) > 1) {
						recoloring(g, coloring, vertex, degreeMax);

					}
				} else {
					if (colorDeficiency(g, coloring, vertex, degreeMax) != 0) {

						recoloring(g, coloring, vertex, degreeMax);
					}
				}
			}

		}

		for (String vertex : g.vertices()) {
			if (g.degree(vertex) == degreeMax) {
				if (colorDeficiency(g, coloring, vertex, degreeMax) == 1) {
					recoloring(g, coloring, vertex, degreeMax, null);
				}
			}
		}

		return coloring;
	}

	/**
	 * Make a good 2_coloring (with a null deficiency) for a graph different of
	 * an odd cycle
	 * 
	 * @param g
	 *            graph to color, must be different of an odd cycle
	 * @return good 2_coloring of the graph
	 */
	public static Map<Edge<String>, Integer> two_recoloring(
			final Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, int colorAlpha,
			int colorBeta) {

		List<String> remainingOddVertices = new ArrayList<String>();
		String vertex2 = null;
		for (String v : g.vertices()) {
			vertex2 = v;
			break;
		}
		for (String v : g.vertices()) {
			if (g.degree(v) % 2 == 1) {
				remainingOddVertices.add(v);
			} else if (g.degree(v) > 2) {
				vertex2 = v;
			}
		}
		if (remainingOddVertices.size() == 0) {
			if (DEBUG) {
				System.out.println("Graph don't contain odd vertices");
			}
			List<String> eulerianCycle = eulerianCycle(g, vertex2);
			for (int i = 0; i < eulerianCycle.size() - 1; i++) {

				for (Edge<String> e : g.incidentEdges(eulerianCycle.get(i),
						eulerianCycle.get(i + 1))) {
					if (coloring.get(e) == colorAlpha
							|| coloring.get(e) == colorBeta) {
						if (i % 2 == 0) {
							if (DEBUG) {
								System.out.println(e + " " + coloring.get(e)
										+ "->" + colorAlpha);
							}
							coloring.replace(e, colorAlpha);
						} else {
							if (DEBUG) {
								System.out.println(e + " " + coloring.get(e)
										+ "->" + colorBeta);
							}
							coloring.replace(e, colorBeta);
						}

						break;
					}
				}
			}
			for (Edge<String> e : g.incidentEdges(eulerianCycle.get(0),
					eulerianCycle.get(eulerianCycle.size() - 1))) {
				if (!coloring.containsKey(e)) {
					if (eulerianCycle.size() == 0) {
						coloring.replace(e, colorAlpha);

					} else {
						coloring.replace(e, colorBeta);
					}
					break;
				}
			}

		} else {
			Graph<String, Edge<String>> g2 = g
					.subgraph(g.vertices(), g.edges());
			while (remainingOddVertices.size() > 0) {
				colorChain(
						g2,
						remainingOddVertices.get(0),
						coloring,
						getMinorColor(g, coloring, remainingOddVertices.get(0),
								colorAlpha, colorBeta), remainingOddVertices,
						colorAlpha, colorBeta);
			}
			while (g2.size() > 0) {
				for (Edge<String> remainingE : g2.edges()) {

					List<String> eulerianCycle = eulerianCycle(g2,
							remainingE.source());

					for (int i = 0; i < eulerianCycle.size() - 1; i++) {
						for (Edge<String> e : g.incidentEdges(
								eulerianCycle.get(i), eulerianCycle.get(i + 1))) {
							if (i % 2 == 0) {
								coloring.replace(e, colorAlpha);
							} else
								coloring.replace(e, colorBeta);
							g2.removeEdge(e);
							break;
						}
					}

					for (Edge<String> e : g.incidentEdges(eulerianCycle.get(0),
							eulerianCycle.get(eulerianCycle.size() - 1))) {
						if (eulerianCycle.size() == 0) {
							coloring.replace(e, colorAlpha);
						} else
							coloring.replace(e, colorBeta);
						g2.removeEdge(e);

						break;
					}
					break;
				}
			}
		}

		return coloring;
	}

	/**
	 * Make a good 2_coloring (with a null deficiency) for a graph different of
	 * an odd cycle
	 * 
	 * @param g
	 *            graph to color, must be different of an odd cycle
	 * @return good 2_coloring of the graph
	 */
	public static Map<Edge<String>, Integer> two_coloring(
			final Graph<String, Edge<String>> g) {
		Map<Edge<String>, Integer> coloring = new Hashtable<Edge<String>, Integer>();
		List<String> remainingOddVertices = new ArrayList<String>();
		String vertex2 = null;
		for (String v : g.vertices()) {
			vertex2 = v;
			break;
		}

		for (String v : g.vertices()) {
			if (g.degree(v) % 2 == 1) {
				remainingOddVertices.add(v);
			} else if (g.degree(v) > 2) {
				vertex2 = v;
			}
		}
		if (remainingOddVertices.size() == 0) {
			List<String> eulerianCycle = eulerianCycle(g, vertex2);

			for (int i = 0; i < eulerianCycle.size() - 1; i++) {
				for (Edge<String> e : g.incidentEdges(eulerianCycle.get(i),
						eulerianCycle.get(i + 1))) {
					if (!coloring.containsKey(e)) {
						coloring.put(e, i % 2);
						break;
					}
				}
			}
			for (Edge<String> e : g.incidentEdges(eulerianCycle.get(0),
					eulerianCycle.get(eulerianCycle.size() - 1))) {
				if (!coloring.containsKey(e)) {
					coloring.put(e, eulerianCycle.size() % 2);
					break;
				}
			}

		} else {
			Graph<String, Edge<String>> g2 = g
					.subgraph(g.vertices(), g.edges());
			while (remainingOddVertices.size() > 0) {
				colorChain(
						g2,
						remainingOddVertices.get(0),
						coloring,
						getMinorColor(g, coloring, remainingOddVertices.get(0),
								2), remainingOddVertices, 0, 1);
			}
			while (g2.size() > 0) {
				for (Edge<String> remainingE : g2.edges()) {
					List<String> eulerianCycle = eulerianCycle(g2,
							remainingE.source());
					for (int i = 0; i < eulerianCycle.size() - 1; i++) {
						for (Edge<String> e : g.incidentEdges(
								eulerianCycle.get(i), eulerianCycle.get(i + 1))) {
							if (!coloring.containsKey(e)) {
								coloring.put(e, i % 2);
								g2.removeEdge(e);
								break;
							}
						}
					}
					for (Edge<String> e : g.incidentEdges(eulerianCycle.get(0),
							eulerianCycle.get(eulerianCycle.size() - 1))) {
						if (!coloring.containsKey(e)) {
							coloring.put(e, eulerianCycle.size() % 2);
							g2.removeEdge(e);
							break;
						}
					}
					break;
				}
			}
		}
		if (DEBUG) {
			if (!checkKColoring(g, coloring, 2)) {
				System.out.println("Error the graph has not a good 2-coloring");
			}
		}

		return coloring;
	}

	public static void test() {
		System.out.println("------  begin testColor------");
		System.out.println("test 1");
		final String _1 = "1";
		final String _2 = "2";
		final String _3 = "3";
		final String _4 = "4";
		final String S = "S";
		final String T = "T";

		String[] vertices4 = { _1, _2, _3, _4, S, T };
		String[][][] edges4 = { { { S, _1 }, { "16" } },
				{ { S, _2 }, { "13" } }, { { _1, _2 }, { "10" } },
				{ { _1, _3 }, { "12" } }, { { _2, _1 }, { "4" } },
				{ { _2, _4 }, { "14" } }, { { _3, _2 }, { "9" } },
				{ { _3, T }, { "20" } }, { { _4, _3 }, { "7" } },
				{ { _4, T }, { "4" } } };
		Graph<String, Edge<String>> g4 = new MultiGraph<String, Edge<String>>();
		for (String v : vertices4)
			g4.addVertex(v);
		for (String[][] e : edges4) {
			Edge<String> edge = new DirectedEdge<String>(e[0][0], e[0][1]);
			g4.addEdge(edge);
		}

		Map<Edge<String>, Integer> color = h_coloring(g4);
		Vizing.displayColoredGraph(g4, color);
		if (!checkKColoring2(g4, color, Vizing.degreeMax(g4))) {
			System.out.println("Error : the graph has not a good "
					+ Vizing.degreeMax(g4) + "_coloring");

		}

		System.out.println("\n\ntest 2");
		String[] vertices2 = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
				"20" };
		String[][][] edges2 = { { { "1", "3" } }, { { "1", "4" } },
				{ { "2", "4" } }, { { "2", "5" } }, { { "3", "5" } },
				{ { "6", "8" } }, { { "6", "9" } }, { { "6", "10" } },
				{ { "7", "8" } }, { { "7", "9" } }, { { "7", "10" } },
				{ { "8", "10" } }, { { "11", "13" } }, { { "11", "14" } },
				{ { "11", "15" } }, { { "12", "13" } }, { { "12", "14" } },
				{ { "12", "15" } }, { { "13", "14" } }, { { "13", "15" } },
				{ { "14", "15" } }, { { "16", "17" } }, { { "16", "18" } },
				{ { "16", "19" } }, { { "16", "20" } }, { { "17", "18" } },
				{ { "17", "19" } }, { { "17", "20" } }, { { "18", "19" } },
				{ { "18", "20" } }, { { "19", "20" } }, { { "1", "20" } },
				{ { "6", "20" } } };
		Graph<String, Edge<String>> g2 = new MultiGraph<String, Edge<String>>();
		for (String v : vertices2)
			g2.addVertex(v);
		for (String[][] e : edges2) {
			Edge<String> edge = new DirectedEdge<String>(e[0][0], e[0][1]);
			g2.addEdge(edge);
		}

		Map<Edge<String>, Integer> color2 = h_coloring(g2);
		Vizing.displayColoredGraph(g2, color2);
		if (!checkKColoring2(g2, color2, Vizing.degreeMax(g2))) {
			System.out.println("Error : the graph has not a good "
					+ Vizing.degreeMax(g2) + "_coloring");

		}

		System.out.println("\n\ntest 3");
		String[] vertices3 = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
				"20" };
		String[][][] edges3 = { { { "1", "3" } }, { { "1", "4" } },
				{ { "2", "4" } }, { { "2", "5" } }, { { "3", "5" } },
				{ { "6", "8" } }, { { "6", "9" } }, { { "6", "10" } },
				{ { "7", "8" } }, { { "7", "9" } }, { { "7", "10" } },
				{ { "8", "10" } }, { { "11", "13" } }, { { "11", "14" } },
				{ { "11", "15" } }, { { "12", "13" } }, { { "12", "14" } },
				{ { "12", "15" } }, { { "13", "14" } }, { { "13", "15" } },
				{ { "14", "15" } }, { { "16", "17" } }, { { "16", "18" } },
				{ { "16", "19" } }, { { "16", "20" } }, { { "17", "18" } },
				{ { "17", "19" } }, { { "17", "20" } }, { { "18", "19" } },
				{ { "18", "20" } }, { { "19", "20" } }, { { "1", "2" } },
				{ { "6", "2" } } };
		Graph<String, Edge<String>> g3 = new MultiGraph<String, Edge<String>>();
		for (String v : vertices3)
			g3.addVertex(v);
		for (String[][] e : edges3) {
			Edge<String> edge = new DirectedEdge<String>(e[0][0], e[0][1]);
			g3.addEdge(edge);
		}

		Map<Edge<String>, Integer> color3 = h_coloring(g3);
		Vizing.displayColoredGraph(g3, color3);
		if (!checkKColoring2(g3, color3, Vizing.degreeMax(g3))) {
			System.out.println("Error : the graph has not a good "
					+ Vizing.degreeMax(g2) + "_coloring");

		}
	}

	private static void colorChain(Graph<String, Edge<String>> g,
			String currentVertex, Map<Edge<String>, Integer> coloring,
			int currentColor, List<String> remainingOddVertices,
			int colorAlpha, int colorBeta) {
		boolean lastVertex = true;
		for (Edge<String> e : g.incidentEdges(currentVertex)) {
			if (DEBUG) {
				System.out
						.print(" " + currentVertex + "(" + currentColor + ")");
			}
			if (coloring.containsKey(e)) {
				coloring.replace(e, currentColor);
			} else
				coloring.put(e, currentColor);
			g.removeEdge(e);
			if (currentColor == colorAlpha) {
				colorChain(g, e.getOpposite(currentVertex), coloring,
						colorBeta, remainingOddVertices, colorAlpha, colorBeta);
			} else
				colorChain(g, e.getOpposite(currentVertex), coloring,
						colorAlpha, remainingOddVertices, colorAlpha, colorBeta);

			lastVertex = false;
			break;

		}

		if (lastVertex) {
			if (DEBUG) {
				System.out.print("Last " + currentVertex + "(" + currentColor
						+ ")");
			}

			remainingOddVertices.remove(currentVertex);
		}
	}

	private static int getMinorColor(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, final String vertex,
			int... listColor) {
		int nbColor1[] = new int[listColor.length];
		for (int i = 0; i < listColor.length; i++) {
			nbColor1[i] = 0;
		}

		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (coloring.containsKey(e)) {

				for (int i = 0; i < listColor.length; i++) {
					if (coloring.get(e) == listColor[i]) {
						nbColor1[i]++;
						break;
					}
				}
			}
		}
		int colorMin = 0;
		for (int i = 1; i < listColor.length; i++) {
			if (nbColor1[i] < nbColor1[colorMin]) {
				colorMin = i;
			}
		}
		return listColor[colorMin];
	}

	private static int getMinorColor(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, final String vertex,
			int nbColor) {
		int nbColor1[] = new int[nbColor];
		for (int i = 0; i < nbColor; i++) {
			nbColor1[i] = 0;
		}

		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (coloring.containsKey(e)) {
				nbColor1[coloring.get(e)]++;
			}
		}
		int colorMin = 0;
		for (int i = 1; i < nbColor; i++) {
			if (nbColor1[i] < nbColor1[colorMin]) {
				colorMin = i;
			}
		}
		return colorMin;
	}

	private static List<String> eulerianCycle(Graph<String, Edge<String>> g,
			String firstVertex, List<String> eulerianCycle) {
		int position = eulerianCycle.indexOf(firstVertex) + 1;
		String currentVertex = firstVertex;
		for (Edge<String> e : g.incidentEdges(firstVertex)) {
			currentVertex = e.getOpposite(firstVertex);
			eulerianCycle.add(position, currentVertex);
			g.removeEdge(e);
			position++;
			break;
		}
		while (!currentVertex.equals(firstVertex)) {
			for (Edge<String> e : g.incidentEdges(currentVertex)) {
				currentVertex = e.getOpposite(currentVertex);
				eulerianCycle.add(position, currentVertex);
				g.removeEdge(e);
				position++;
				break;
			}
		}
		for (String vertex : eulerianCycle) {
			if (g.degree(vertex) != 0) {
				eulerianCycle(g, vertex, eulerianCycle);
				break;
			}
		}
		return eulerianCycle;
	}

	/**
	 * Create an eulerian cycle of a graph from a vertex
	 * 
	 * @param g
	 *            graph from which the cycle will be constructed
	 * @param firstVertex
	 *            first vertex of the cycle
	 * @return eulerian cycle
	 */
	public static List<String> eulerianCycle(
			final Graph<String, Edge<String>> g, String firstVertex) {
		List<String> eulerianCycle = new ArrayList<String>();
		Graph<String, Edge<String>> g2 = g.subgraph(g.vertices(), g.edges());
		String currentVertex = firstVertex;
		for (Edge<String> e : g2.incidentEdges(firstVertex)) {
			currentVertex = e.getOpposite(firstVertex);
			eulerianCycle.add(currentVertex);
			g2.removeEdge(e);
			break;
		}
		while (!currentVertex.equals(firstVertex)) {
			for (Edge<String> e : g2.incidentEdges(currentVertex)) {
				currentVertex = e.getOpposite(currentVertex);
				eulerianCycle.add(currentVertex);
				g2.removeEdge(e);
				break;
			}
		}
		for (String vertex : eulerianCycle) {
			if (g2.degree(vertex) != 0) {
				return eulerianCycle(g2, vertex, eulerianCycle);
			}
		}
		return eulerianCycle;

	}

	/**
	 * Return the color deficiency of a vertex
	 * 
	 * @param g
	 *            graph containing the vertex
	 * @param coloring
	 *            edge coloring of g
	 * @param vertex
	 *            vertex which we want the color deficiency
	 * @param k
	 *            number of different color of coloring
	 * @return color deficiency of vertex
	 */
	public static int colorDeficiency(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, final String vertex,
			int k) {
		int listColor[] = new int[k];
		for (int i = 0; i < k; i++) {
			listColor[i] = 0;
		}
		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (coloring.containsKey(e)) {
				if (listColor[coloring.get(e)] == 0) {
					listColor[coloring.get(e)] = 1;
				}
			}
		}
		int nbColor = 0;
		for (int i = 0; i < k; i++) {
			if (listColor[i] == 1) {
				nbColor++;
			}
		}
		return Math.min(k, g.degree(vertex)) - nbColor;
	}

	/**
	 * Check if all the vertices of the graph have a null deficiency, that means
	 * that for each vertices, the predicate min(degree, k) - |Cx| = 0 is
	 * checked where k is the number of colors allowed.
	 * 
	 * @param g
	 *            graph to check
	 * @param coloring
	 *            contains the color of vertices
	 * @param k
	 *            number of color
	 * @return true if the coloring is a good coloring, false otherwise
	 */
	public static boolean checkKColoring2(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, int k) {

		int degreeMax = Vizing.degreeMax(g);
		for (String vertex : g.vertices()) {
			int listColor[] = new int[k];
			for (int i = 0; i < k; i++) {
				listColor[i] = 0;
			}
			for (Edge<String> e : g.incidentEdges(vertex)) {
				if (coloring.containsKey(e)) {
					if (listColor[coloring.get(e)] == 0) {
						listColor[coloring.get(e)] = 1;
					}
				} else
					return false;
			}
			int nbColor = 0;
			for (int i = 0; i < k; i++) {
				if (listColor[i] == 1) {
					nbColor++;
				}
			}
			if (g.degree(vertex) == degreeMax) {
				if (Math.min(k, g.degree(vertex)) - nbColor > 1) {
					if (DEBUG) {
						System.out.println("The vertex " + vertex
								+ "has not a good deficiency of coloring");
					}
					return false;
				}
			} else if (Math.min(k, g.degree(vertex)) - nbColor != 0) {
				if (DEBUG) {
					System.out.println("The vertex " + vertex
							+ "has not a good deficiency of coloring");
				}
				return false;
			}

		}
		return true;
	}

	/**
	 * Check if all the vertices of the graph have a null deficiency, that means
	 * that for each vertices, the predicate min(degree, k) - |Cx| = 0 is
	 * checked where k is the number of colors allowed.
	 * 
	 * @param g
	 *            graph to check
	 * @param coloring
	 *            contains the color of vertices
	 * @param k
	 *            number of color
	 * @return true if the coloring is a good coloring, false otherwise
	 */
	public static boolean checkKColoring(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, int k) {
		for (String vertex : g.vertices()) {
			int listColor[] = new int[k];
			for (int i = 0; i < k; i++) {
				listColor[i] = 0;
			}
			for (Edge<String> e : g.incidentEdges(vertex)) {
				if (coloring.containsKey(e)) {
					if (listColor[coloring.get(e)] == 0) {
						listColor[coloring.get(e)] = 1;
					}
				} else
					return false;
			}
			int nbColor = 0;
			for (int i = 0; i < k; i++) {
				if (listColor[i] == 1) {
					nbColor++;
				}
			}
			if (Math.min(k, g.degree(vertex)) - nbColor != 0) {
				if (DEBUG) {
					System.out.println("The vertex " + vertex
							+ "has not a good deficiency of coloring");
				}
				return false;
			}

		}
		return true;
	}

	/**
	 * Make a naive coloration of the edges of the graph g
	 * 
	 * @param g
	 *            graph where the coloration will occur
	 * @param k
	 *            number of color
	 * @return Map which contains colors associated with edges. Uncolored edges
	 *         do not appear in the map.
	 */
	public static Map<Edge<String>, Integer> naiveK_Coloration(
			Graph<String, Edge<String>> g, int k) {
		Map<Edge<String>, Integer> coloring = new HashMap<Edge<String>, Integer>();
		for (Edge<String> currentEdge : g.edges()) {
			coloring.put(currentEdge,
					getMinorColor(g, coloring, currentEdge.source(), k));
		}
		return coloring;
	}
}
