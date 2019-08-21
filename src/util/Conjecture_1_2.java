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

public class Conjecture_1_2 {

	/**
	 * Make a proper k-total-weighting coloring of a graph. The coloring will
	 * use the set {1,2} to color the vertices and the set {1,2,3} to color the
	 * edges. We call total weight of a vertex v the sum of all weight of
	 * incident edge and the weight of v. The coloring is a proper
	 * k-total-weighting coloring if no two neighboring vertices have the same
	 * total weight.
	 * 
	 * @param g
	 *            graph to color
	 * @param verticesColoring
	 *            will contain the colors of the edges of g
	 * @param edgeColoring
	 *            will contain the colors of the vertices of g
	 */
	public static void coloring(final Graph<String, Edge<String>> g,
			Map<String, Integer> verticesColoring,
			Map<Edge<String>, Integer> edgeColoring) {
		Map<String, Integer> finalColor = new HashMap<String, Integer>();

		for (Edge<String> e : g.edges()) {
			edgeColoring.put(e, 2);
		}
		for (String vertex : g.vertices()) {
			verticesColoring.put(vertex, 1);
		}

		for (String vertex : g.vertices()) {
			boolean weightIsCorrect = false;
			List<String[]> listAlterableVertices = new ArrayList<String[]>();
			boolean completedList = false;

			while (!weightIsCorrect) {
				int weight = getWeight(g, verticesColoring, edgeColoring,
						vertex);
				weightIsCorrect = true;
				for (String neighbor : g.neighbors(vertex)) {
					if (finalColor.containsKey(neighbor)) {
						if (finalColor.get(neighbor) == weight) {
							weightIsCorrect = false;
						}
						if (!completedList) {
							String[] vertexArray = new String[2];
							vertexArray[0] = neighbor;
							vertexArray[1] = "0";
							listAlterableVertices.add(vertexArray);
						}

					}

				}
				completedList = true;
				if (!weightIsCorrect) {
					nextState(g, edgeColoring, verticesColoring, finalColor,
							listAlterableVertices, vertex);
				}
			}
			finalColor.put(vertex,
					getWeight(g, verticesColoring, edgeColoring, vertex));
		}
		for (String vertex : g.vertices()) {
			if (getWeight(g, verticesColoring, edgeColoring, vertex) != finalColor
					.get(vertex)) {
				verticesColoring.replace(vertex, 2);
			}
		}

	}

	/**
	 * Return the total weight of a vertex
	 * 
	 * @param g
	 *            graph which contains the vertex
	 * @param verticesColoring
	 *            will contain the colors of the edges of g
	 * @param edgeColoring
	 *            will contain the colors of the vertices of g
	 * @param vertex
	 *            vertex we want to know the total weight
	 * @return total weight of vertex
	 */
	public static int getWeight(final Graph<String, Edge<String>> g,
			final Map<String, Integer> verticesColoring,
			final Map<Edge<String>, Integer> edgeColoring, String vertex) {
		int weight = 0;
		for (Edge<String> e : g.incidentEdges(vertex)) {
			weight += edgeColoring.get(e);
		}
		weight += verticesColoring.get(vertex);

		return weight;
	}

	/**
	 * Check if a coloring of a graph is a proper k-total-weighting coloring
	 * 
	 * @param g
	 *            graph to check
	 * @param edgeColoring
	 *            will contain the colors of the vertices of g
	 * @param vertex
	 *            vertex we want to know the total weight
	 * @return true if the coloring is a proper k-total-weighting coloring
	 */
	public static boolean checkColoring(final Graph<String, Edge<String>> g,
			Map<String, Integer> verticesColoring,
			Map<Edge<String>, Integer> edgeColoring) {
		for (String vertex : g.vertices()) {
			int weight = getWeight(g, verticesColoring, edgeColoring, vertex);
			for (String neighbors : g.neighbors(vertex)) {
				if (getWeight(g, verticesColoring, edgeColoring, neighbors) == weight) {
					return false;
				}
			}
		}
		return true;
	}

	private static void nextState(final Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> edgeColoring,
			final Map<String, Integer> verticesColoring,
			final Map<String, Integer> finalColor,
			List<String[]> listAlterableVertices, String vertex) {

		for (String[] array : listAlterableVertices) {

			if (getWeight(g, verticesColoring, edgeColoring, array[0]) == finalColor
					.get(array[0])) {
				for (Edge<String> e : g.incidentEdges(vertex, array[0])) {
					edgeColoring.replace(e, edgeColoring.get(e) - 1);

				}
			} else {
				for (Edge<String> e : g.incidentEdges(vertex, array[0])) {
					edgeColoring.replace(e, edgeColoring.get(e) + 1);

				}
			}
			if (array[1].equals("0")) {
				array[1] = "1";
				break;
			} else
				array[1] = "0";
		}

	}

	private static void displayColoredList(final Graph<String, Edge<String>> g,
			final Map<String, Integer> verticesColoring,
			final Map<Edge<String>, Integer> edgeColoring, String vertex) {
		System.out.print(vertex + "(c" + verticesColoring.get(vertex) + "-w="
				+ getWeight(g, verticesColoring, edgeColoring, vertex) + ") ");
		for (String neighbor : g.neighbors(vertex)) {
			System.out.print(neighbor + "(w="
					+ getWeight(g, verticesColoring, edgeColoring, neighbor)
					+ ") ");
		}
		System.out.println("");
	}

	/**
	 * Display a colored Graph with adjacent lists, the parentic number is the
	 * weight of the vertices
	 * 
	 * @param g
	 *            graph to display
	 * @param verticesColoring
	 *            will contain the colors of the edges of g
	 * @param edgeColoring
	 *            will contain the colors of the vertices of g
	 */
	public static void displayColoredGraphe(
			final Graph<String, Edge<String>> g,
			final Map<String, Integer> verticesColoring,
			final Map<Edge<String>, Integer> edgeColoring) {
		for (String vertex : g.vertices()) {
			displayColoredList(g, verticesColoring, edgeColoring, vertex);
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

		Map<Edge<String>, Integer> edgeColoring = new HashMap<Edge<String>, Integer>();
		Map<String, Integer> verticesColoring = new HashMap<String, Integer>();
		coloring(g4, verticesColoring, edgeColoring);

		displayColoredGraphe(g4, verticesColoring, edgeColoring);
		if (!checkColoring(g4, verticesColoring, edgeColoring)) {
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

		edgeColoring = new HashMap<Edge<String>, Integer>();
		verticesColoring = new HashMap<String, Integer>();
		coloring(g2, verticesColoring, edgeColoring);

		displayColoredGraphe(g2, verticesColoring, edgeColoring);
		if (!checkColoring(g2, verticesColoring, edgeColoring)) {
			System.out.println("error");
			System.exit(0);
		}

	}

}
