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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import graph.DirectedEdge;
import graph.Graph;
import graph.MultiGraph;

import static graph.Graph.Edge;

public class Conjecture_1_2_3 {

	public static final boolean DEBUG = false;

	/**
	 * Make a coloring of a graph such that for v1 and v2 two neighboring
	 * vertices, the sum w1 of each weight of incident edge of v1 and the the
	 * sum w2 of each weight of incident edge of v2 are different. The coloring
	 * will use only a set of five colors.
	 * 
	 * @param g
	 *            graph to color
	 * @return
	 */
	public static Map<Edge<String>, Integer> five_coloring(
			final Graph<String, Edge<String>> g) {
		Map<Edge<String>, Integer> coloring = new HashMap<Edge<String>, Integer>();
		for (Edge<String> e : g.edges()) {
			coloring.put(e, 3);
		}
		Map<String, Integer> setColor = new HashMap<String, Integer>();

		List<String> verticesArray = getListOrdonnedVertices(g);

		for (int k = 0; k < verticesArray.size() - 1; k++) {
			String vk = verticesArray.get(k);

			boolean properColoring = false;
			List<String[]> listIVertex = new ArrayList<String[]>();
			boolean completedList = false;

			String vj = null;
			for (int j = k + 1; j < verticesArray.size(); j++) {
				vj = verticesArray.get(j);
				if (g.areNeighbors(vk, vj)) {
					break;
				}
			}
			if (DEBUG) {
				System.out.println(vk + "------>" + vj);
			}

			while (!properColoring) {
				int weight = getWeight(g, coloring, vk);
				int wk = 0;
				if (weight % 4 > 1) {
					wk = 2;
				}

				properColoring = true;

				for (int i = 0; i < k; i++) {
					String vi = verticesArray.get(i);
					if (g.areNeighbors(vk, vi)) {
						if ((setColor.get(vi) == 0 && weight - wk == getWeight(
								g, coloring, vi))
								|| (setColor.get(vi) == 2 && weight - wk == getWeight(
										g, coloring, vi) - 2)) {
							properColoring = false;

						}
						if (!completedList) {
							String[] vertexArray = new String[2];
							vertexArray[0] = vi;
							vertexArray[1] = "0";
							listIVertex.add(vertexArray);
						}
					}

				}
				for (Edge<String> e : g.incidentEdges(vk, vj)) {
					if (coloring.get(e) == 2) {
						if (weight % 4 == 0 || weight % 4 == 1) {
							properColoring = false;
						}
					} else if (coloring.get(e) == 4) {
						if (weight == 3 || weight % 4 == 2) {
							properColoring = false;
						}
					}
				}
				if (!properColoring) {
					nextState(g, coloring, listIVertex, setColor, vk, vj);
				}
				completedList = true;
			}
			int weight = getWeight(g, coloring, vk);
			if (weight % 4 == 1 || weight % 4 == 0) {
				setColor.put(vk, 0);
			} else
				setColor.put(vk, 2);

		}
		if (checkWeightColoring(g, coloring)) {
			return coloring;
		}
		if (DEBUG) {
			System.out.println("last : "
					+ verticesArray.get(verticesArray.size() - 1));
			displayColoredGraph(g, coloring);
		}
		String vn = verticesArray.get(verticesArray.size() - 1);
		int nbVerticestoW2 = 0;
		for (String vi : g.neighbors(vn)) {
			if (setColor.get(vi) == 2) {
				nbVerticestoW2++;
			}

		}
		int a = getWeight(g, coloring, vn) - 2 * nbVerticestoW2;
		if (a % 4 == 2 || a % 4 == 3) {
			if (DEBUG) {
				System.out.println("case 1");
			}
			for (String vi : g.neighbors(vn)) {
				if (setColor.get(vi) == 2) {
					for (Edge<String> e : g.incidentEdges(vn, vi)) {
						changeSetColor(g, coloring, setColor, vi, e);
					}
				}
			}
			return coloring;
		}

		String vertexWitheWdifferentOfA = null;
		for (String vi : g.neighbors(vn)) {
			if (g.degree(vi) != a) {
				vertexWitheWdifferentOfA = vi;
				break;
			}
		}
		if (vertexWitheWdifferentOfA != null) {
			if (DEBUG) {
				System.out.println("\na : " + a + " case 2 ->"
						+ vertexWitheWdifferentOfA);
			}
			for (String vi : g.neighbors(vn)) {
				if (setColor.get(vi) == 2 && vi != vertexWitheWdifferentOfA) {
					for (Edge<String> e : g.incidentEdges(vn, vi)) {
						changeSetColor(g, coloring, setColor, vi, e);
					}
				}
			}
			if (setColor.get(vertexWitheWdifferentOfA) == 0) {
				for (Edge<String> e : g.incidentEdges(vn,
						vertexWitheWdifferentOfA)) {
					changeSetColor(g, coloring, setColor,
							vertexWitheWdifferentOfA, e);
				}
			}
			return coloring;
		}
		if (DEBUG) {
			System.out.println("case 3");
		}
		int i = 0;
		for (String vi : g.neighbors(vn)) {
			if (i != 2) {
				if (setColor.get(vi) == 0) {
					for (Edge<String> e : g.incidentEdges(vn, vi)) {
						changeSetColor(g, coloring, setColor, vi, e);
					}
				}
				i++;
			} else {
				if (setColor.get(vi) == 2) {
					for (Edge<String> e : g.incidentEdges(vn, vi)) {
						changeSetColor(g, coloring, setColor, vi, e);
					}
				}
				i++;
			}
		}

		return coloring;
	}

	private static List<String> getListOrdonnedVertices(
			final Graph<String, Edge<String>> g) {
		List<String> verticesArray = new ArrayList<String>();
		List<String> queue = new ArrayList<String>();

		for (String v : g.vertices()) {
			if (g.degree(v) >= 2) {
				queue.add(v);
				if (DEBUG) {
					System.out.println("add " + v);
				}
				break;
			}
		}
		while (queue.size() > 0) {
			String v = queue.get(0);
			queue.remove(v);
			for (String neighbor : g.neighbors(v)) {
				if (!queue.contains(neighbor) && !verticesArray.contains(neighbor)) {
					queue.add(neighbor);
				}
			}
			verticesArray.add(0, v);
		}

		if (DEBUG) {
			System.out.print("List :");
			for (String v : verticesArray) {
				System.out.print(v + " ");
			}
			System.out.println("");
		}

		return verticesArray;
	}

	private static int getWeight(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, String vertex) {

		int weight = 0;
		for (Edge<String> e : g.incidentEdges(vertex)) {
			weight += coloring.get(e);
		}

		return weight;
	}

	private static void changeSetColor(final Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring,
			Map<String, Integer> setColor, String vertex, Edge<String> e) {
		if (DEBUG) {
			System.out.println("change : " + vertex);
		}
		if (setColor.get(vertex) == 0) {
			setColor.replace(vertex, 2);

			coloring.replace(e, coloring.get(e) + 2);
		} else {
			setColor.replace(vertex, 0);
			coloring.replace(e, coloring.get(e) - 2);
		}
	}

	private static void nextState(final Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring,
			List<String[]> listIVertex, Map<String, Integer> setColor,
			String vertex, String vj) {
		boolean loopStop = false;
		Edge<String> linkWithJ = null;
		for (Edge<String> e : g.incidentEdges(vertex, vj)) {
			linkWithJ = e;
		}
		if (DEBUG) {
			System.out.print(vj + " edge : " + linkWithJ);
			displayListColor(g, coloring, vertex);
		}

		for (String[] array : listIVertex) {
			for (Edge<String> e : g.incidentEdges(vertex, array[0])) {
				changeSetColor(g, coloring, setColor, array[0], e);
			}
			if (array[1].equals("0")) {
				array[1] = "1";
				loopStop = true;
				break;

			} else {
				array[1] = "0";
			}

		}
		if (!loopStop) {
			if (coloring.get(linkWithJ) == 3) {
				coloring.replace(linkWithJ, 2);
			} else if (coloring.get(linkWithJ) == 2) {
				coloring.replace(linkWithJ, 4);
			} else if (DEBUG) {
				System.out.println(vertex + " Error : no combination works");
				displayColoredGraph(g, coloring);
				System.exit(0);
			}
		}

		if (DEBUG) {
			for (String[] array : listIVertex) {
				System.out.print(array[1]);
			}
			System.out.println("");
			System.out.print(coloring.get(linkWithJ));
		}

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
				{ { S, _2 }, { "13" } }, { { _1, _4 }, { "10" } },
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

		Map<Edge<String>, Integer> color = five_coloring(g4);
		displayColoredGraph(g4, color);
		if (!checkWeightColoring(g4, color)) {
			System.out.println("error");
			System.exit(0);
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
				{ { "11", "15" } }, { { "12", "10" } }, { { "12", "14" } },
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

		Map<Edge<String>, Integer> color2 = five_coloring(g2);
		displayColoredGraph(g2, color2);
		if (!checkWeightColoring(g2, color2)) {
			System.out.println("error");
			System.exit(0);
		}

	}

	private static void displayListColor(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, String vertex) {

		System.out.print("\n" + vertex + "(" + getWeight(g, coloring, vertex)
				+ ") : ");

		for (Edge<String> edge : g.incidentEdges(vertex)) {
			if (coloring.containsKey(edge)) {

				System.out.print(edge.getOpposite(vertex) + "(c"
						+ coloring.get(edge) + "-"
						+ getWeight(g, coloring, edge.getOpposite(vertex))
						+ ") ");

			}
		}
	}

	/**
	 * Display a colored graph with adjacent lists, with the weight of the
	 * vertex in brackets
	 * 
	 * @param g
	 *            graph to display
	 * @param coloring
	 *            contains color of the edge of g
	 */
	public static void displayColoredGraph(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring) {
		for (String vertex : g.vertices()) {
			displayListColor(g, coloring, vertex);
		}
	}

	/**
	 * Check if the coloring of a graph is proper
	 * 
	 * @param g
	 *            graph to check
	 * @param coloring
	 *            contains color of the edges of g
	 * @return true if the coloring is proper, false otherwise
	 */
	public static boolean checkWeightColoring(
			final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring) {
		for (String vertex : g.vertices()) {
			int weight = getWeight(g, coloring, vertex);
			for (String neighbor : g.neighbors(vertex)) {
				if (getWeight(g, coloring, neighbor) == weight) {
					if (DEBUG) {
						System.out.print("\nThe vertex " + vertex
								+ " has the same weight as " + neighbor);
						displayListColor(g, coloring, vertex);
					}
					return false;
				}
			}
		}
		return true;
	}

}
