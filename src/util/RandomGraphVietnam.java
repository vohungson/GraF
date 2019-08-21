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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static graph.Graph.Edge;

public class RandomGraphVietnam {

	public static class Position {
		int x;
		int y;

		public Position(int x, int y) {
			this.x = x;
			this.y = y;
		}
	}

	public static void randomGraph(Graph<String, Edge<String>> g,
			Integer numberOfVertices, Integer numberOfEdges,
			Map<Edge<String>, Integer> w, int wMax) {
		Random rd = new Random();
		List<Position> list = new ArrayList<Position>();

		for (int i = 1; i <= numberOfVertices; i++) {
			for (int j = i + 1; j <= numberOfVertices; j++) {
				list.add(new Position(i, j));
			}
		}

		if (numberOfEdges == null) {
			int max = (numberOfVertices * numberOfVertices) / 2
					- numberOfVertices;
			int min = numberOfVertices - 1;
			numberOfEdges = rd.nextInt(max - min) + min;
			;
		}

		for (int i = 1; i <= numberOfVertices; i++) {
			g.addVertex(String.valueOf(i));
		}
		int number = 0;
		while (number <= numberOfEdges) {
			int rdp = rd.nextInt(list.size());
			Position p = list.get(rdp);
			Edge<String> e = new DirectedEdge<String>(
					String.valueOf(p.x), String.valueOf(p.y));
			g.addEdge(e);
			w.put(e, rd.nextInt(wMax - 1) + 1);
			++number;
			list.remove(rdp);
		}
	}

	public static void randomDiGraph(Graph<String, Edge<String>> g,
			Integer numberOfVertices, Integer numberOfEdges,
			Map<Edge<String>, Integer> w, int wMax) {
		Random rd = new Random();
		List<Position> list = new ArrayList<Position>();

		for (int i = 1; i <= numberOfVertices; i++) {
			for (int j = 1; j <= numberOfVertices; j++) {
				if (i == j)
					continue;
				list.add(new Position(i, j));
			}
		}

		if (numberOfEdges == null) {
			int max = (numberOfVertices * numberOfVertices) / 2
					- numberOfVertices;
			int min = numberOfVertices - 1;
			numberOfEdges = rd.nextInt(max - min) + min;
			;
		}

		for (int i = 1; i <= numberOfVertices; i++) {
			g.addVertex(String.valueOf(i));
		}
		int number = 0;
		while (number <= numberOfEdges) {
			int rdp = rd.nextInt(list.size());
			Position p = list.get(rdp);
			Edge<String> e = new DirectedEdge<String>(
					String.valueOf(p.x), String.valueOf(p.y));
			g.addEdge(e);
			w.put(e, rd.nextInt(wMax - 1) + 1);
			++number;
			list.remove(rdp);
		}
	}

	public static void randomNetwork(Graph<String, Edge<String>> g,
			Integer numberOfVertices, Integer numberOfEdges,
			Map<Edge<String>, Integer> l,
			Map<Edge<String>, Integer> u, int min, int max) {
		Random rd = new Random();
		List<Position> list = new ArrayList<Position>();

		for (int i = 1; i <= numberOfVertices; i++) {
			for (int j = 1; j <= numberOfVertices; j++) {
				if (i == j)
					continue;
				list.add(new Position(i, j));
			}
		}

		if (numberOfEdges == null) {
			int m1 = (numberOfVertices * numberOfVertices) / 2
					- numberOfVertices;
			int m2 = numberOfVertices - 1;
			numberOfEdges = rd.nextInt(m1 - m2) + m1;
			;
		}

		for (int i = 1; i <= numberOfVertices; i++) {
			g.addVertex(String.valueOf(i));
		}
		int number = 0;
		while (number <= numberOfEdges) {
			int rdp = rd.nextInt(list.size());
			Position p = list.get(rdp);
			Edge<String> e = new DirectedEdge<String>(
					String.valueOf(p.x), String.valueOf(p.y));
			g.addEdge(e);
			int mid = (max + min) / 2;
			l.put(e, rd.nextInt(mid - min) + min);
			u.put(e, rd.nextInt(max - mid) + mid);
			++number;
			list.remove(rdp);
		}
	}
}
