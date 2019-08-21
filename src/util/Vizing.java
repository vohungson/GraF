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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class Vizing {

	public static final boolean DEBUG = true;

	
	/**
	 * Make a coloring of a graph with the Vizing's algorithm
	 * @param g graph to color
	 * @return coloring of the graph which will contain delta+1 colors
	 */
	public static Map<Edge<String>, Integer> edgeColoring(
			Graph<String, Edge<String>> g) {
		Map<Edge<String>, Integer> coloring = new HashMap<Edge<String>, Integer>(); 

		if (DEBUG) {
			if (!coloredGraphIsValid(g, coloring)) {
				System.out.println("Error first Coloration ");
			}
			System.out.println("Naive Coloring :");
			displayColoredGraph(g, coloring);
		}

		for (Edge<String> e : g.edges()) {
			if (!coloring.containsKey(e)) {
				if (DEBUG) {
					System.out.print("\nEdge between " + e.source() + " and "
							+ e.target() + " is uncolored ");
					displayListColor(g, coloring, e.source());
				}

				LinkedList<String> fan = createMaximalFan(g, coloring,
						e.source(), e);

				if (DEBUG) {
					if (!fanIsCorrect(g, coloring, fan, e.source())) {
						break;
					}

					System.out.print("\nMaximal Fan : ");
					for (String v : fan) {
						System.out.print(v + " ");
					}
				}

				int colorD = getColorFreeAtVertex(g, coloring, fan.getLast());
				int colorC = getColorFreeAtVertex(g, coloring, e.source(),
						colorD);

				if (DEBUG) {
					System.out.print("\nCd Path : ");
				}

				invertCdPath(g, coloring, e.source(), colorD, colorC);

				if (DEBUG) {
					if (!coloredGraphIsValid(g, coloring)) {
						System.out.println("Error CdPath ");
						break;
					}
				}

				LinkedList<String> fanW = new LinkedList<String>();
				for (String vertex : fan) {
					fanW.add(vertex);
					if (colorFreeAtVertex(g, coloring, vertex, colorD)) {

						if (DEBUG) {
							System.out.print("\nw : " + vertex + " fanW : ");
							for (String v : fanW) {
								System.out.print(v + " ");
							}
						}

						break;
					}
				}
				rotateFan(g, coloring, fanW, e.source());

				if (DEBUG) {
					if (!coloredGraphIsValid(g, coloring)) {
						System.out.println("Error rotate ");
						break;
					}
				}

				for (Edge<String> Xw : g.incidentEdges(e.source(),
						fanW.getLast())) {
					if (DEBUG) {
						System.out.println("Color the edge " + Xw.source()
								+ "-" + Xw.target() + " into the color "
								+ colorD);
					}
					coloring.put(Xw, colorD);
				}

				if (DEBUG) {
					if (!coloredGraphIsValid(g, coloring)) {
						System.out.println("Error ");
						break;
					}
				}
			}
		}

		return coloring;
	}

	public static void testColor() {
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
		Map<Edge<String>, Integer> color = edgeColoring(g4);
		displayColoredGraph(g4, color);

		System.out.println("\ntest 2");

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
				{ { "18", "20" } }, { { "19", "20" } } };
		Graph<String, Edge<String>> g2 = new MultiGraph<String, Edge<String>>();
		for (String v : vertices2)
			g2.addVertex(v);
		for (String[][] e : edges2) {
			Edge<String> edge = new DirectedEdge<String>(e[0][0], e[0][1]);
			g2.addEdge(edge);
		}
		Map<Edge<String>, Integer> color2 = edgeColoring(g2);// color(g2);
		displayColoredGraph(g2, color2);

		System.out.println("\ntest 3");

		String[] vertices3 = { "1", "2", "3", "4", "5", "6", "7", "8", "9",
				"10", "11", "12", "13", "14", "15", "16", "17", "18", "19",
				"20", "21", "22", "23", "24", "25", "26", "27", "28", "29",
				"30", "31", "32", "33", "34", "35", "36", "37", "38", "39",
				"40", "41", "42", "43", "44", "45", "46", "47", "48", "49",
				"50", "51", "52", "53", "54", "55", "56", "57", "58", "59",
				"60", "61", "62", "63", "64", "65", "66", "67", "68", "69",
				"70", "71", "72", "73", "74", "75", "76", "77", "78", "79",
				"80", "81", "82", "83", "84", "85", "86", "87", "88", "89",
				"90", "91", "92", "93", "94", "95", "96", "97", "98", "99",
				"100", "101", "102", "103", "104", "105", "106", "107", "108",
				"109", "110", "111", "112", "113", "114", "115", "116", "117",
				"118", "119", "120", "121", "122", "123", "124", "125", "126",
				"127", "128", "129", "130", "131", "132", "133", "134", "135",
				"136", "137", "138", "139", "140", "141", "142", "143", "144",
				"145", "146", "147", "148", "149", "150", "151", "152", "153",
				"154", "155", "156", "157", "158", "159", "160", "161", "162",
				"163", "164", "165", "166", "167", "168", "169", "170", "171",
				"172", "173", "174", "175", "176", "177", "178", "179", "180",
				"181", "182", "183", "184", "185", "186", "187", "188", "189",
				"190", "191", "192", "193", "194", "195", "196", "197", "198",
				"199", "200", "201", "202", "203", "204", "205", "206", "207",
				"208", "209", "210", "211", "212", "213", "214", "215", "216",
				"217", "218", "219", "220", "221", "222", "223", "224" };
		String[][][] edges3 = { { { "1", "4" } }, { { "1", "5" } },
				{ { "2", "5" } }, { { "2", "6" } }, { { "3", "6" } },
				{ { "3", "7" } }, { { "4", "7" } }, { { "8", "11" } },
				{ { "8", "13" } }, { { "9", "12" } }, { { "9", "13" } },
				{ { "9", "14" } }, { { "10", "12" } }, { { "10", "13" } },
				{ { "10", "14" } }, { { "12", "14" } }, { { "15", "18" } },
				{ { "15", "19" } }, { { "15", "21" } }, { { "16", "19" } },
				{ { "16", "20" } }, { { "16", "21" } }, { { "17", "19" } },
				{ { "17", "20" } }, { { "17", "21" } }, { { "18", "20" } },
				{ { "22", "25" } }, { { "22", "26" } }, { { "22", "27" } },
				{ { "22", "28" } }, { { "23", "26" } }, { { "24", "27" } },
				{ { "25", "26" } }, { { "25", "27" } }, { { "25", "28" } },
				{ { "26", "28" } }, { { "27", "28" } }, { { "29", "32" } },
				{ { "29", "33" } }, { { "29", "35" } }, { { "30", "32" } },
				{ { "30", "34" } }, { { "30", "35" } }, { { "31", "33" } },
				{ { "31", "34" } }, { { "31", "35" } }, { { "32", "34" } },
				{ { "36", "39" } }, { { "36", "40" } }, { { "36", "42" } },
				{ { "37", "39" } }, { { "37", "41" } }, { { "37", "42" } },
				{ { "38", "40" } }, { { "38", "41" } }, { { "39", "41" } },
				{ { "40", "42" } }, { { "43", "46" } }, { { "43", "47" } },
				{ { "43", "48" } }, { { "43", "49" } }, { { "44", "46" } },
				{ { "44", "47" } }, { { "44", "48" } }, { { "44", "49" } },
				{ { "45", "47" } }, { { "46", "48" } }, { { "46", "49" } },
				{ { "48", "49" } }, { { "50", "53" } }, { { "50", "54" } },
				{ { "50", "55" } }, { { "50", "56" } }, { { "51", "53" } },
				{ { "51", "54" } }, { { "51", "55" } }, { { "51", "56" } },
				{ { "52", "54" } }, { { "52", "55" } }, { { "52", "56" } },
				{ { "53", "55" } }, { { "54", "56" } }, { { "57", "60" } },
				{ { "57", "61" } }, { { "57", "62" } }, { { "57", "63" } },
				{ { "58", "60" } }, { { "58", "61" } }, { { "58", "62" } },
				{ { "58", "63" } }, { { "59", "60" } }, { { "59", "61" } },
				{ { "59", "62" } }, { { "59", "63" } }, { { "60", "63" } },
				{ { "64", "67" } }, { { "64", "68" } }, { { "64", "69" } },
				{ { "64", "70" } }, { { "65", "67" } }, { { "65", "68" } },
				{ { "65", "69" } }, { { "65", "70" } }, { { "66", "67" } },
				{ { "66", "68" } }, { { "66", "69" } }, { { "67", "70" } },
				{ { "68", "70" } }, { { "71", "74" } }, { { "71", "75" } },
				{ { "71", "76" } }, { { "71", "77" } }, { { "72", "74" } },
				{ { "72", "75" } }, { { "72", "76" } }, { { "72", "77" } },
				{ { "73", "74" } }, { { "73", "75" } }, { { "73", "76" } },
				{ { "73", "77" } }, { { "74", "76" } }, { { "74", "77" } },
				{ { "75", "76" } }, { { "75", "77" } }, { { "78", "80" } },
				{ { "78", "82" } }, { { "78", "83" } }, { { "79", "81" } },
				{ { "79", "83" } }, { { "79", "84" } }, { { "80", "82" } },
				{ { "80", "84" } }, { { "81", "83" } }, { { "81", "84" } },
				{ { "85", "87" } }, { { "85", "89" } }, { { "85", "90" } },
				{ { "85", "91" } }, { { "86", "88" } }, { { "86", "91" } },
				{ { "87", "89" } }, { { "87", "90" } }, { { "87", "91" } },
				{ { "89", "90" } }, { { "89", "91" } }, { { "92", "94" } },
				{ { "92", "96" } }, { { "92", "97" } }, { { "92", "98" } },
				{ { "93", "95" } }, { { "93", "96" } }, { { "94", "96" } },
				{ { "94", "97" } }, { { "94", "98" } }, { { "95", "97" } },
				{ { "96", "98" } }, { { "97", "98" } }, { { "99", "101" } },
				{ { "99", "103" } }, { { "99", "104" } }, { { "99", "105" } },
				{ { "100", "102" } }, { { "100", "103" } },
				{ { "100", "104" } }, { { "100", "105" } },
				{ { "101", "103" } }, { { "101", "104" } },
				{ { "101", "105" } }, { { "102", "104" } },
				{ { "103", "105" } }, { { "106", "108" } },
				{ { "106", "110" } }, { { "106", "111" } },
				{ { "106", "112" } }, { { "107", "109" } },
				{ { "107", "110" } }, { { "107", "111" } },
				{ { "107", "112" } }, { { "108", "110" } },
				{ { "108", "111" } }, { { "109", "111" } },
				{ { "109", "112" } }, { { "110", "112" } },
				{ { "113", "115" } }, { { "113", "117" } },
				{ { "113", "118" } }, { { "113", "119" } },
				{ { "114", "116" } }, { { "114", "117" } },
				{ { "114", "118" } }, { { "114", "119" } },
				{ { "115", "117" } }, { { "115", "118" } },
				{ { "115", "119" } }, { { "116", "117" } },
				{ { "116", "118" } }, { { "116", "119" } },
				{ { "120", "122" } }, { { "120", "123" } },
				{ { "120", "125" } }, { { "120", "126" } },
				{ { "121", "123" } }, { { "121", "124" } },
				{ { "121", "125" } }, { { "121", "126" } },
				{ { "122", "124" } }, { { "122", "125" } },
				{ { "122", "126" } }, { { "123", "125" } },
				{ { "123", "126" } }, { { "127", "129" } },
				{ { "127", "130" } }, { { "127", "131" } },
				{ { "127", "133" } }, { { "128", "130" } },
				{ { "128", "131" } }, { { "128", "132" } },
				{ { "128", "133" } }, { { "129", "131" } },
				{ { "129", "132" } }, { { "129", "133" } },
				{ { "130", "132" } }, { { "130", "133" } },
				{ { "134", "136" } }, { { "134", "137" } },
				{ { "134", "138" } }, { { "134", "140" } },
				{ { "135", "137" } }, { { "135", "138" } },
				{ { "135", "139" } }, { { "135", "140" } },
				{ { "136", "138" } }, { { "136", "139" } },
				{ { "136", "140" } }, { { "137", "139" } },
				{ { "138", "140" } }, { { "141", "143" } },
				{ { "141", "144" } }, { { "141", "145" } },
				{ { "141", "147" } }, { { "142", "144" } },
				{ { "142", "145" } }, { { "142", "146" } },
				{ { "142", "147" } }, { { "143", "145" } },
				{ { "143", "146" } }, { { "144", "146" } },
				{ { "144", "147" } }, { { "145", "147" } },
				{ { "148", "150" } }, { { "148", "151" } },
				{ { "148", "152" } }, { { "148", "153" } },
				{ { "149", "151" } }, { { "149", "152" } },
				{ { "149", "153" } }, { { "149", "154" } },
				{ { "150", "152" } }, { { "150", "153" } },
				{ { "150", "154" } }, { { "151", "153" } },
				{ { "151", "154" } }, { { "152", "154" } },
				{ { "155", "157" } }, { { "155", "158" } },
				{ { "155", "159" } }, { { "155", "160" } },
				{ { "155", "161" } }, { { "156", "158" } },
				{ { "156", "159" } }, { { "156", "160" } },
				{ { "156", "161" } }, { { "157", "159" } },
				{ { "157", "160" } }, { { "157", "161" } },
				{ { "158", "159" } }, { { "158", "160" } },
				{ { "158", "161" } }, { { "159", "161" } },
				{ { "162", "164" } }, { { "162", "165" } },
				{ { "162", "166" } }, { { "162", "167" } },
				{ { "162", "168" } }, { { "163", "165" } },
				{ { "163", "166" } }, { { "163", "167" } },
				{ { "163", "168" } }, { { "164", "166" } },
				{ { "164", "167" } }, { { "164", "168" } },
				{ { "165", "166" } }, { { "165", "167" } },
				{ { "166", "168" } }, { { "167", "168" } },
				{ { "169", "171" } }, { { "169", "172" } },
				{ { "169", "173" } }, { { "169", "174" } },
				{ { "169", "175" } }, { { "170", "172" } },
				{ { "170", "173" } }, { { "170", "174" } },
				{ { "170", "175" } }, { { "171", "172" } },
				{ { "171", "173" } }, { { "171", "174" } },
				{ { "171", "175" } }, { { "172", "173" } },
				{ { "172", "174" } }, { { "172", "175" } },
				{ { "173", "174" } }, { { "173", "175" } },
				{ { "174", "175" } }, { { "176", "178" } },
				{ { "176", "179" } }, { { "176", "180" } },
				{ { "176", "181" } }, { { "176", "182" } },
				{ { "177", "178" } }, { { "177", "179" } },
				{ { "177", "180" } }, { { "177", "181" } },
				{ { "177", "182" } }, { { "178", "180" } },
				{ { "178", "181" } }, { { "178", "182" } },
				{ { "179", "182" } }, { { "180", "181" } },
				{ { "180", "182" } }, { { "183", "185" } },
				{ { "183", "186" } }, { { "183", "187" } },
				{ { "183", "188" } }, { { "183", "189" } },
				{ { "184", "185" } }, { { "184", "186" } },
				{ { "184", "187" } }, { { "184", "188" } },
				{ { "184", "189" } }, { { "185", "187" } },
				{ { "185", "188" } }, { { "185", "189" } },
				{ { "187", "188" } }, { { "187", "189" } },
				{ { "188", "189" } }, { { "190", "192" } },
				{ { "190", "193" } }, { { "190", "194" } },
				{ { "190", "195" } }, { { "190", "196" } },
				{ { "191", "192" } }, { { "191", "193" } },
				{ { "191", "194" } }, { { "191", "195" } },
				{ { "192", "194" } }, { { "192", "195" } },
				{ { "192", "196" } }, { { "193", "196" } },
				{ { "194", "195" } }, { { "194", "196" } },
				{ { "195", "196" } }, { { "197", "199" } },
				{ { "197", "200" } }, { { "197", "201" } },
				{ { "197", "202" } }, { { "197", "203" } },
				{ { "198", "199" } }, { { "198", "200" } },
				{ { "198", "201" } }, { { "198", "202" } },
				{ { "198", "203" } }, { { "199", "201" } },
				{ { "199", "202" } }, { { "199", "203" } },
				{ { "200", "201" } }, { { "200", "202" } },
				{ { "200", "203" } }, { { "201", "203" } },
				{ { "204", "206" } }, { { "204", "207" } },
				{ { "204", "208" } }, { { "204", "209" } },
				{ { "204", "210" } }, { { "205", "206" } },
				{ { "205", "207" } }, { { "205", "208" } },
				{ { "205", "209" } }, { { "205", "210" } },
				{ { "206", "208" } }, { { "206", "209" } },
				{ { "206", "210" } }, { { "207", "208" } },
				{ { "207", "209" } }, { { "207", "210" } },
				{ { "208", "209" } }, { { "208", "210" } },
				{ { "209", "210" } }, { { "211", "213" } },
				{ { "211", "214" } }, { { "211", "215" } },
				{ { "211", "216" } }, { { "211", "217" } },
				{ { "212", "213" } }, { { "212", "214" } },
				{ { "212", "215" } }, { { "212", "216" } },
				{ { "212", "217" } }, { { "213", "214" } },
				{ { "213", "215" } }, { { "213", "216" } },
				{ { "213", "217" } }, { { "214", "215" } },
				{ { "214", "216" } }, { { "214", "217" } },
				{ { "215", "216" } }, { { "215", "217" } },
				{ { "216", "217" } }, { { "218", "219" } },
				{ { "218", "220" } }, { { "218", "221" } },
				{ { "218", "222" } }, { { "218", "223" } },
				{ { "218", "224" } }, { { "219", "220" } },
				{ { "219", "221" } }, { { "219", "222" } },
				{ { "219", "223" } }, { { "219", "224" } },
				{ { "220", "221" } }, { { "220", "222" } },
				{ { "220", "223" } }, { { "220", "224" } },
				{ { "221", "222" } }, { { "221", "223" } },
				{ { "221", "224" } }, { { "222", "223" } },
				{ { "222", "224" } }, { { "223", "224" } } };
		Graph<String, Edge<String>> g3 = new MultiGraph<String, Edge<String>>();
		for (String v : vertices3)
			g3.addVertex(v);
		for (String[][] e : edges3) {
			Edge<String> edge = new DirectedEdge<String>(e[0][0], e[0][1]);
			g3.addEdge(edge);
		}
		Map<Edge<String>, Integer> color3 = edgeColoring(g3);
		displayColoredGraph(g3, color3);

		System.out.println("\n------  end testColor------");
	}

	/**
	 * Do a rotation of the color of the edges around a vertex X; For each
	 * vertex v of the fan : the edge Xv will take the color of the edge Xv+
	 * where v+ is the successor of v in the fan. The edge linking X and the
	 * last vertex of the fan becomes uncolored
	 * 
	 * @param g
	 *            graph where the rotating algorithm will occur
	 * @param coloring
	 *            contains colors of the edges
	 * @param fan
	 *            contains the vertices which the color of the edge with X will
	 *            change
	 * @param X
	 *            central vertex of the rotation
	 */
	public static void rotateFan(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, LinkedList<String> fan,
			String X) {
		if (DEBUG) {
			System.out.println("\nRotation :");
		}
		for (int i = 0; i < fan.size() - 1; i++) {
			for (Edge<String> e1 : g.incidentEdges(X, fan.get(i))) {
				for (Edge<String> e2 : g.incidentEdges(X, fan.get(i + 1))) {
					if (DEBUG) {
						System.out.print(e1.source() + "-" + e1.target()
								+ " : ");
					}
					if (i != 0) {
						if (DEBUG) {
							System.out.print(coloring.get(e1));
						}
						coloring.replace(e1, coloring.get(e2));
					} else {
						if (DEBUG) {
							System.out.print("uncolored");
						}
						coloring.put(e1, coloring.get(e2));
					}
					if (DEBUG) {
						System.out.println("->" + coloring.get(e2));
					}
				}
			}
		}
		for (Edge<String> Xw : g.incidentEdges(X, fan.getLast())) {
			if (fan.size() != 1) {
				System.out.println(Xw.source() + "-" + Xw.target() + " : "
						+ coloring.get(Xw) + "->uncolored");
			} else
				System.out.println("None");
			coloring.remove(Xw);
		}
	}

	/**
	 * Make a naive coloration of the edges of the graph g
	 * 
	 * @param g
	 *            graph where the coloration will occur
	 * @return Map which contains colors associated with edges. Uncolored edges
	 *         do not appear in the map.
	 */
	public static Map<Edge<String>, Integer> naiveColoration(
			Graph<String, Edge<String>> g) {
		Map<Edge<String>, Integer> coloring = new HashMap<Edge<String>, Integer>();
		for (Edge<String> currentEdge : g.edges()) {
			for (int currentColor = 0; currentColor < degreeMax(g) + 1; currentColor++) {
				if (!coloring.containsKey(currentEdge)
						&& colorFreeAtVertex(g, coloring, currentEdge.source(),
								currentColor)
						&& colorFreeAtVertex(g, coloring, currentEdge.target(),
								currentColor)) {
					coloring.put(currentEdge, currentColor);
				}
			}

		}
		return coloring;
	}

	/**
	 * Check if the fan is correct; A fan is correct if for each vertex v in
	 * <fan[0]...fan[size-1]> , the color of the edge Xv+ is free at v, where v+
	 * is the successor of v in the fan
	 * 
	 * @param g
	 *            graph of the fan
	 * @param coloring
	 *            contains colors of the edges
	 * @param fan
	 *            fan to checked
	 * @param vertex
	 *            which possess the fan
	 * @return true if the fan is correct, false otherwise
	 */
	private static boolean fanIsCorrect(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring,
			final LinkedList<String> fan, final String vertex) {
		for (int i = 0; i < fan.size() - 1; i++) {
			for (Edge<String> e : g.incidentEdges(vertex, fan.get(i + 1))) {
				if (!colorFreeAtVertex(g, coloring, fan.get(i), coloring.get(e))) {
					System.out.println("\nThe fan is incorrect : the color "
							+ coloring.get(e) + "is not free at " + fan.get(i));
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Check if a colored graph is valid; A colored graph is valid if no two
	 * edges incident on a vertex have the same color. An edge of a valid graph
	 * can be uncolored
	 * 
	 * @param g
	 *            graph to check
	 * @param coloring
	 *            contains colors of the edges
	 * @return true if the colored graph is valid, false otherwise
	 */
	private static boolean coloredGraphIsValid(
			Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring) {
		for (String vertex : g.vertices()) {
			LinkedList<Integer> listColor = new LinkedList<Integer>();
			for (Edge<String> e : g.incidentEdges(vertex)) {
				if (coloring.containsKey(e)) {
					if (listColor.contains(coloring.get(e))) {
						System.out.println("Error vertex " + vertex
								+ "has twice the color " + coloring.get(e));
						return false;
					} else
						listColor.add(coloring.get(e));
				}
			}
		}
		return true;
	}

	/**
	 * Convert a fan which contains edges to a fan with vertices
	 * 
	 * @param fanEdge
	 *            fan to convert
	 * @param vertex
	 *            central vertex of the fan
	 * @return fan with vertices
	 */
	private static LinkedList<String> convertFanEdgesToFanVertices(
			LinkedList<Edge<String>> fanEdge, String vertex) {
		LinkedList<String> fan = new LinkedList<String>();
		for (Edge<String> e : fanEdge) {
			fan.add(e.getOpposite(vertex));
		}
		return fan;
	}

	/**
	 * Display a colored Graph with adjacent lists, the parenthic number is the
	 * color of the edge
	 * 
	 * @param g
	 *            graph to display
	 * @param coloring
	 *            contains colors of the edges
	 */
	public static void displayColoredGraph(
			Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring) {
		for (String vertex : g.vertices()) {
			displayListColor(g, coloring, vertex);
		}
	}

	/**
	 * Display a adjacent list of an vertex of a colored graph. The parenthic
	 * number is the color of the edge
	 * 
	 * @param g
	 *            graph which contains the vertex to display
	 * @param coloring
	 *            contains colors of the edges of g
	 * @param vertex
	 *            vertex to display
	 */
	private static void displayListColor(Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, String vertex) {
		System.out.print(" \n" + vertex + " : ");
		for (Edge<String> edge : g.incidentEdges(vertex)) {
			System.out.print(edge.getOpposite(vertex));
			if (coloring.containsKey(edge)) {
				System.out.print("(" + coloring.get(edge) + ")");
			} else
				System.out.print("(uncolored)");
			System.out.print(" ");
		}
	}

	/**
	 * Create a maximal fan associated with vertex X. The vertex linked with X
	 * with uncoloredEdge is the first element of the fan. A fan of a vertex X
	 * is a nonempty sequence of distinct neighbors of X and have the following
	 * proprieties The edge linking the first vertex of the fan and X is
	 * uncolored. The other vertices of the fan are linked with X with a colored
	 * edge. For each vertex v of the fan (except the last), the color of edge
	 * Xv+ is free at v, where v+ is the successor of v in the fan
	 * 
	 * @param g
	 *            graph which contain the vertex X
	 * @param coloring
	 *            contains colors of the edges
	 * @param X
	 *            vertex which from the fan will be created
	 * @param uncoloredEdge
	 *            edge linking X and the first element of the fan, do not appear
	 *            in map
	 * @return a correct fan created from X
	 */
	public static LinkedList<String> createMaximalFan(
			Graph<String, Edge<String>> g,
			Map<Edge<String>, Integer> coloring, String X,
			Edge<String> uncoloredEdge) {
		LinkedList<Edge<String>> fan = new LinkedList<Edge<String>>();
		List<Edge<String>> remainingEdges = new LinkedList<Edge<String>>();
		for (Edge<String> e : g.incidentEdges(X)) {
			if (coloring.containsKey(e)) {
				remainingEdges.add(e);
			}
		}
		fan.add(uncoloredEdge);
		int i = 0;
		while (i != remainingEdges.size()) {
			Edge<String> e = remainingEdges.get(i);
			i++;
			if (coloring.containsKey(e)) {
				if (colorFreeAtVertex(g, coloring,
						fan.getLast().getOpposite(X), coloring.get(e))) {
					fan.add(e);
					remainingEdges.remove(e);
					i = 0;
				}
			}
		}
		return convertFanEdgesToFanVertices(fan, X);

	}

	public static void createCdPath(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring,
			final String vertex, int colorD, int colorC,
			LinkedList<String> cdPath) {
		System.out.print(vertex);

		cdPath.add(vertex);
		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (coloring.containsKey(e)) {
				if (coloring.get(e) == colorD) {
					System.out.print("-(" + colorD + ")->");
					createCdPath(g, coloring, e.getOpposite(vertex), colorC,
							colorD, cdPath);
				}
			}
		}
	}

	/**
	 * Create the cdPath from a vertex. A cdPath is a path of vertex linked by
	 * edges colored only c or d. The first vertex of the path will be vertex
	 * and the second will be the vertex linked to the first vertex by a
	 * d-colored edge
	 * 
	 * @param g
	 *            graph from which the path will be extracted
	 * @param coloring
	 *            contains colors of the edges
	 * @param vertex
	 *            first vertex of the path
	 * @param colorD
	 *            color d (will be the color of the first edge of the path)
	 * @param colorC
	 *            color c
	 * @return linkedList which contains the cdPath
	 */
	public static LinkedList<String> createCdPath(
			final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring,
			final String vertex, int colorD, int colorC) {

		LinkedList<String> cdPath = new LinkedList<String>();

		createCdPath(g, coloring, vertex, colorD, colorC, cdPath);

		return cdPath;
	}

	/**
	 * Invert the colors of the edges of a cdPath. A cdPath is a path of vertex
	 * linked by edges colored only c or d.
	 * 
	 * @param cdPath
	 *            cdPath to invert
	 * @param g
	 *            graph which contains the cdPath
	 * @param coloring
	 *            contains colors of the edges
	 * @param colorD
	 *            color d
	 * @param colorC
	 *            color c
	 */
	public static void invertCdPath(final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring, final String X,
			int colorD, int colorC) {

		LinkedList<String> cdPath = createCdPath(g, coloring, X, colorD, colorC);

		System.out.print("\nInversion of the cdPath :");
		if (cdPath.size() > 0) {
			System.out.print(cdPath.getFirst());
		}
		for (int i = 1; i < cdPath.size(); i++) {
			for (Edge<String> e : g.incidentEdges(cdPath.get(i - 1),
					cdPath.get(i))) {
				if (coloring.get(e) == colorD) {
					coloring.replace(e, colorD, colorC);
					System.out.print("-(" + colorC + ")->");
				} else {
					coloring.replace(e, colorC, colorD);
					System.out.print("-(" + colorD + ")->");
				}
			}
			System.out.print(cdPath.get(i));
		}
	}

	/**
	 * Return the list of the free colors at vertex. A color c is free at vertex
	 * v if no incident edge on v has the color c.
	 * 
	 * @param g
	 *            graph which contains the vertex
	 * @param coloring
	 *            contains colors of the edges
	 * @param vertex
	 *            vertex to extract the list
	 * @return list of the colors which are free at vertex
	 */
	/**
	 * Return a color free at vertex.
	 * 
	 * @param g
	 *            graph which contains the vertex
	 * @param coloring
	 *            contains colors of the edges
	 * @param vertex
	 *            vertex to extract the list
	 * @param otherThanTheseColors
	 *            the color returned by this method will be different from the
	 *            colors given in this parameter
	 * @return a color free at vertex < delta(g) + 1. If no free color exit -1
	 *         is returned
	 */
	public static int getColorFreeAtVertex(
			final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring,
			final String vertex, int... otherThanTheseColors) {
		for (int i = 0; i <= degreeMax(g); i++) {
			if (colorFreeAtVertex(g, coloring, vertex, i)) {
				int j;
				for (j = 0; j < otherThanTheseColors.length; j++) {
					if (otherThanTheseColors[j] == i) {
						j = otherThanTheseColors.length + 1;
					}
				}
				if (j == otherThanTheseColors.length) {
					return i;
				}
			}
		}
		return -1;
	}

	/**
	 * Check if a color is free at a vertex. A color c is free at vertex v if no
	 * incident edge on v has the color c.
	 * 
	 * @param g
	 *            graph which contains the vertex
	 * @param coloring
	 *            contains colors of the edges
	 * @param vertex
	 *            vertex to check
	 * @param color
	 *            color to test
	 * @return true if color is free at vertex, false otherwise
	 */
	private static boolean colorFreeAtVertex(
			final Graph<String, Edge<String>> g,
			final Map<Edge<String>, Integer> coloring,
			final String vertex, final int color) {

		for (Edge<String> e : g.incidentEdges(vertex)) {
			if (coloring.containsKey(e) && coloring.get(e) == color) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Return the delta of a graph
	 * 
	 * @param g
	 *            graph which we want the delta
	 * @return delta of the graph
	 */
	public static int degreeMax(Graph<String, Edge<String>> g) {

		int max = 0;
		for (String vertex : g.vertices()) {
			if (max < g.degree(vertex)) {
				max = g.degree(vertex);
			}
		}
		return max;
	}

}
