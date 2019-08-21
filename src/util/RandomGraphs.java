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

import java.util.Random;

import static graph.Graph.Edge;

public class RandomGraphs {

	private static Random rand = new Random();
	
	public static void setSeed(long seed) {
		rand.setSeed(seed);
	}
	
	public static void setRandom(Random rand) {
		RandomGraphs.rand = rand;
	}
	
	/**
	 * 
	 * @author baudon
	 *
	 * pseudo-random tree generator. 
	 */
	public static Graph<Integer, Edge<Integer>> randomTree(int n) {
		int[] code = new int[n-2];
		for (int i = 0; i < n-2; i++) {
			code[i] = rand.nextInt(n-2);
		}
		Graph<Integer, Edge<Integer>> tree = new MultiGraph<Integer, Edge<Integer>>();
		int[] d = new int[n];
		for (int i = 0; i < n; i++) {
			tree.addVertex(i);
			d[i] = 0;
		}
		int index = 0;
		for (int i = 0; i < n-2; i++) {
			d[code[i]]++;
			if (code[i] == index) {
				while (d[index] > 0) {
					index++;
				}
			}
		}
		int v = index;
		for (int i = 0; i < n-2; i++) {
			int w = code[i];
			tree.addEdge(new DirectedEdge<Integer>(v, w));
			d[w]--;
			if (w < index && d[w] == 0) {
				v = w;
			} else {
				index++;
				while (d[index] > 0) {
					index++;
				}
				v = index;
			}
		}
		tree.addEdge(new DirectedEdge<Integer>(v, n-1));
		return tree;
	}
	
	public static Graph<Integer, Edge<Integer>> randomDirectedGraph(
			int n, double p) {
		if (p < 0 || p > 1)
			throw new IllegalArgumentException("The probability " + p
					+ "is not between 0 and 1");
		Graph<Integer, Edge<Integer>> g = new MultiGraph<Integer, Edge<Integer>>();
		for (int i = 0; i < n; i++) {
			g.addVertex(i);
		}
		for (int i = 0; i < n; i++)
			for (int j = 0; j < n; j++) {
				if (rand.nextDouble() < p) {
					g.addEdge(new DirectedEdge<Integer>(i, j));
				}
			}
		return g;
	}

}
