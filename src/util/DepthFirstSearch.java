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

import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

import graph.Graph;

import static graph.Graph.Edge;

public class DepthFirstSearch {
	public static interface DepthFirstSearchData<V> {
		public V father();

//		public int d();

		public int f();
	}

	private static class DepthFirstSearchDataImpl<V> implements
			DepthFirstSearchData<V> {

		V father;
//		int first;
		int last;
		Color color;

		@Override
		public V father() {
			return father;
		}

//		@Override
//		public int d() {
//			return first;
//		}

		@Override
		public int f() {
			return last;
		}
	}

	public static <V, E extends Edge<V>> Map<V, DepthFirstSearchData<V>> depthFirstSearch(
			Graph<V, E> g) {
		return depthFirstSearch(g, true);
	}

	public static <V, E extends Edge<V>> Map<V, DepthFirstSearchData<V>> depthFirstSearch(
			Graph<V, E> g, boolean directed) {
		Map<V, DepthFirstSearchData<V>> map = new HashMap<V, DepthFirstSearchData<V>>(
				g.order());
		for (V v : g.vertices()) {
			DepthFirstSearchDataImpl<V> data = new DepthFirstSearchDataImpl<V>();
			data.color = Color.WHITE;
			data.father = null;
			map.put(v, data);
		}
		int time = 0;
		for (V v : g.vertices()) {
			if (((DepthFirstSearchDataImpl<V>) map.get(v)).color == Color.WHITE)
				visitDFS(g, v, time, map, directed);
		}
		return map;
	}

	private static <V, E extends Edge<V>> void visitDFS(Graph<V, E> g,
			V v, int time, Map<V, DepthFirstSearchData<V>> map, boolean directed) {
		DepthFirstSearchDataImpl<V> vData = (DepthFirstSearchDataImpl<V>) map
				.get(v);
		vData.color = Color.GRAY;
		Iterable<V> neighbors = directed ? g.successors(v) : g.neighbors(v);
		for (V w : neighbors) {
			DepthFirstSearchDataImpl<V> wData = (DepthFirstSearchDataImpl<V>) map
					.get(w);
			if (wData.color == Color.WHITE) {
				wData.father = v;
				visitDFS(g, w, time, map, directed);
			}
		}
		vData.color = Color.BLACK;
		vData.last = time++;
	}
}
