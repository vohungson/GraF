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
import graph.Graph.Edge;
import graph.MultiGraph;
import graph.PartialGraph;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

public class Graphs {

	/**
	 * Compute a Breadth First Search Tree rooted on vertex root.
	 * 
	 */
	public static <V, E extends Graph.Edge<V>> PartialGraph<V, E> breadthFirstSearch(
			Graph<V, E> g, V root) {
		@SuppressWarnings("unchecked")
		PartialGraph<V, E> bfsTree = g
				.partialGraph((Set<E>) Collections.EMPTY_SET);
		Set<V> markedVertices = new HashSet<V>();
		Queue<V> queue = new LinkedList<V>();
		queue.add(root);
		markedVertices.add(root);

		while (!queue.isEmpty()) {
			V currentVertex = queue.poll();
			for (E e : g.incidentEdges(currentVertex)) {
				V neighbor = e.getOpposite(currentVertex);
				if (!markedVertices.contains(neighbor)) {
					queue.add(neighbor);
					markedVertices.add(neighbor);
					bfsTree.addEdge(e);
				}
			}
		}
		return bfsTree;
	}

	/**
	 * Check if the graph g is connected.
	 * 
	 * Note that the empty graph is connected.
	 * 
	 */
	public static <V, E extends Graph.Edge<V>> boolean isConnected(Graph<V, E> g) {
		if (g.size() < g.order() - 1)
			return false;
		if (g.isEmpty())
			return true;
		V v = g.vertices().iterator().next();
		Graph<V, E> bfsTree = breadthFirstSearch(g, v);
		return bfsTree.order() == g.order();
	}

	/**
	 * Check if the graph g is a tree.
	 * 
	 */
	public static <V, E extends Graph.Edge<V>> boolean isTree(Graph<V, E> g) {
		return g.size() == g.order() - 1 && isConnected(g);
	}

	public static class ShortestPaths<V> {
		public Map<V, V> predecessors = new HashMap<V, V>();
		public Map<V, Double> distances = new HashMap<V, Double>();;

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String t = "";
			t += "------------begin-------------\n";
			t += "predecessors: " + predecessors + "\n";
			t += "distances: " + distances + "\n";
			t += "target<=source:distance\n";
			for (V v : distances.keySet()) {
				t += "[" + v + "]";
				double dist = distances.get(v);
				while (true) {
					v = predecessors.get(v);
					t += "<-" + v;
					if (distances.get(v) == null || distances.get(v) == 0)
						break;
				}
				t += ":" + dist + "\n";
			}
			t += "------------end-------------\n";
			return t;
		}
	}

	private static <V, E extends Graph.Edge<V>> ShortestPaths<V> initializeShortestPaths(
			Graph<V, E> g, V source) {
		ShortestPaths<V> result = new ShortestPaths<V>();
		for (V v : g.vertices()) {
			result.predecessors.put(v, null);
			result.distances.put(v, v.equals(source) ? 0
					: Double.POSITIVE_INFINITY);
		}
		return result;
	}

	private static <V, E extends Graph.Edge<V>> boolean relax(E e,
			ShortestPaths<V> sp, double we) {
		V u = e.source();
		V v = e.target();
		double d = sp.distances.get(u) + we;
		if (d < sp.distances.get(v)) {
			sp.distances.put(v, d);
			sp.predecessors.put(v, u);
			return true;
		} else
			return false;
	}

	/**
	 * Single source shortest path using Dijskstra's algorithm to find the
	 * shortest path. The weights of the edges must be non negative.
	 * 
	 * @param g
	 *            graph
	 * @param source
	 *            source vertex
	 * @param weights
	 *            for each edge of g, distance between the origin and the target
	 *            of the edge. The weights must be not negative.
	 * @return instance of ShortestPaths containing for each vertex its
	 *         predecessor on a shortest path from the source and the length of
	 *         this shortest path.
	 * @throws IllegalArgumentException
	 *             if the keys of the map containing the weights is not equal to
	 *             the set of edges of the graph g or if a value in the map is
	 *             negative.
	 */
	public static <V, E extends Graph.Edge<V>> ShortestPaths<V> dijskstra(
			Graph<V, E> g, V source, Map<E, Double> weights)
			throws NegativeEdgeException {
		final ShortestPaths<V> result = initializeShortestPaths(g, source);
		Queue<V> q = new PriorityQueue<V>(g.order(), new Comparator<V>() {
			@Override
			public int compare(V o1, V o2) {
				return result.distances.get(o1).compareTo(
						result.distances.get(o2));
			}
		});
		q.addAll(g.vertices());
		while (!q.isEmpty()) {
			V u = q.remove();
			for (E e : g.outgoingEdges(u)) {
				double we = weights.get(e);
				if (we < 0)
					throw new NegativeEdgeException(e, we);
				if (relax(e, result, we)) {
					V v = e.target();
					q.remove(v);
					q.add(v);
				}
			}
		}
		return result;
	}

	/**
	 * Single source shortest path using Bellman-Ford's algorithm to find the
	 * shortest path
	 * 
	 * @param g
	 *            graph
	 * @param source
	 *            source vertex
	 * @param weights
	 *            for each edge of g, distance between the origin and the target
	 *            of the edge.
	 * @return instance of ShortestPaths containing for each vertex its
	 *         predecessor on a shortest path from the source and the length of
	 *         this shortest path.
	 * @throws NegativeCircuitException
	 *             if the graph contains a negative circuit.
	 */
	public static <V, E extends Graph.Edge<V>> ShortestPaths<V> bellmanFord(
			Graph<V, E> g, V source, Map<E, Double> weights)
			throws NegativeCircuitException {
		final ShortestPaths<V> result = initializeShortestPaths(g, source);
		boolean modified = true;
		for (int i = 0; i < g.order() - 1 && modified; i++) {
			modified = false;
			for (E e : g.edges()) {
				modified |= relax(e, result, weights.get(e));
			}
		}
		if (modified) {
			for (E e : g.edges()) {
				if (relax(e, result, weights.get(e))) {
					List<V> circuit = new ArrayList<V>();
					V u = e.source();
					V v = e.target();
					circuit.add(v);
					circuit.add(u);
					while (!u.equals(v)) {
						u = result.predecessors.get(u);
					}
					Collections.reverse(circuit);
					throw new NegativeCircuitException(circuit);
				}
			}
		}
		return result;
	}

	/**
	 * 
	 * @author baudon
	 * 
	 * @param <V>
	 *            For each vertex v, the associated value in the map distances
	 *            is a map which, for any vertex w, give the distance from v to
	 *            w using a shortest path.
	 * 
	 *            For each vertex v, the associated value in the map
	 *            predecessors is a map which, for any vertex w, give the
	 *            predecessor of w in a shortest path from v to w.
	 */
	public static class ShortestPathsMatrices<V> {
		public Map<V, Map<V, Double>> distances = new HashMap<V, Map<V, Double>>();;
		public Map<V, Map<V, V>> predecessors = new HashMap<V, Map<V, V>>();

		private String getPath(V s, V t) {
			V v = predecessors.get(s).get(t);
			if (v == null || v.equals(s)) {
				return "";
			} else {
				String s1 = getPath(s, v);
				String s2 = getPath(v, t);
				return (s1.equals("") ? "" : s1 + "->") + v
						+ (s2.equals("") ? "" : "->" + s2);
			}
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			String rs = "";
			rs += "------------begin-------------\n";
			for (V s : distances.keySet()) {
				rs += s + predecessors.get(s).toString() + "\n";
				for (V t : distances.get(s).keySet()) {
					rs += " ->" + t;
					double dist = distances.get(s).get(t);
					rs += ":" + s + "->" + getPath(s, t) + "->" + t + ":"
							+ dist + "\n";
				}
			}
			rs += "------------end-------------\n";
			return rs;
		}
	}

	/**
	 * All-pair shortest path using Floyd-Warshall's algorithm to find all pair
	 * shortest paths
	 * 
	 * @param g
	 *            graph
	 * @param weights
	 *            for each edge of g, distance between the origin and the target
	 *            of the edge.
	 * @return instance of ShortestPathsMatrices
	 * @throws NegativeCircuitException 
	 */
	public static <V, E extends Graph.Edge<V>> ShortestPathsMatrices<V> floydWarshall(
			Graph<V, E> g, Map<E, Double> weights) throws NegativeCircuitException {
		ShortestPathsMatrices<V> result = new ShortestPathsMatrices<V>();
		for (V v : g.vertices()) {
			Map<V, Double> dist = new HashMap<V, Double>();
			Map<V, V> pred = new HashMap<V, V>();
			result.distances.put(v, dist);
			result.predecessors.put(v, pred);
			for (V w : g.vertices()) {
				dist.put(w, Double.POSITIVE_INFINITY);
				pred.put(w, null);
			}
			dist.put(v, 0.0);
		}
		for (E e : g.edges()) {
			V u = e.source();
			V v = e.target();
			// the following test is necessary only in case of multiple edges
			if (result.distances.get(u).get(v) > weights.get(e)) {
				result.distances.get(u).put(v, weights.get(e));
				result.predecessors.get(u).put(v, u);
			}
		}
		for (V u : g.vertices()) {
			for (V v : g.vertices()) {
				for (V w : g.vertices()) {
					double d = result.distances
							.get(v).get(u) + result.distances.get(u).get(w);
					if (result.distances.get(v).get(w) > result.distances
							.get(v).get(u) + result.distances.get(u).get(w)) {
						result.distances.get(v).put(w, d);
						result.predecessors.get(v).put(w, result.predecessors.get(u).get(w));
					}
				}
			}
		}
		for (V u : g.vertices()) {
			if (result.distances.get(u).get(u) < 0) {
				List<V> circuit = new ArrayList<V>();
				V v = result.predecessors.get(u).get(u);
				circuit.add(u);
				while (!v.equals(u)) {
					circuit.add(v);
					v = result.predecessors.get(u).get(v);
				}
				Collections.reverse(circuit);
				throw new NegativeCircuitException(circuit);
			}
		}
		return result;
	}

//	/**
//	 * 
//	 * @author baudon
//	 * 
//	 * @param <E>
//	 *            type of the edges
//	 * 
//	 *            value is the value of the flow.
//	 * 
//	 *            maximumFlow gives for each edge the value of the flow on it.
//	 * 
//	 *            minimumCut gives a set of edges which is a cut with minimum
//	 *            capacity.
//	 */
	public static class FlowResults<E> {
		public int value=0;
		public Map<E, Integer> maximumFlow = new HashMap<E, Integer>();
		public Set<E> minimumCut = new HashSet<E>();

		@Override
		public String toString() {
			String rs = "";
			rs += "--------------------begin-----------------\n";
			rs += "value: " + value + "\n";
			rs += "maximum flow:\n";
			for (E e : maximumFlow.keySet()) {
				rs += e.toString() + ":" + maximumFlow.get(e) + "\n";
			}
			rs += "minimum cut:\n";
			for (E e : minimumCut) {
				rs += e.toString() + "\n";
			}
			rs += "--------------------end-----------------\n";
			return rs;
		}
	}

	/**
	 * Ford-Fulkerson algorithm to find a flow that has a maximum value.
	 * 
	 * @param g
	 *            the graph
	 * @param source
	 *            the source vertex
	 * @param sink
	 *            the sink vertex
	 * @param capacities
	 *            the capacities of the edges
	 * @return instance of FlowResults
	 */
	

	/**
	 * A connected graph is a graph such that there exists a path between all pairs of vertices.
	 * 
	 * solution: use dfs, the graph is connected if all the vertices is vistited
	 * 
	 * @param g
	 * 			the graph (considered as undirected)
	 * @return
	 */
	private static <V, E extends Graph.Edge<V>> boolean checkIsConnected(Graph<V, E> g){
		Set<V> marked = new HashSet<V>();
		
		Iterator<V> it = g.vertices().iterator();
		if (it.hasNext()) {
			V v = it.next();
			marked.add(v);
			for (V vn : g.neighbors(v)){
				if (!marked.contains(vn)){
					dfsUndirected(g, vn, marked);
				}
			}
		}else{
			return false;
		}
		return g.vertices().size()==marked.size();
	}
	
	private static <V, E extends Graph.Edge<V>> void dfsUndirected(Graph<V, E> g, V v, Set<V> marked){
		marked.add(v);
		for (V vn : g.neighbors(v)){
			if (!marked.contains(vn)){
				dfsUndirected(g, vn, marked);
			}
		}
	}
	
	/**
	 * If the graph is a directed graph, and there exists a path from each vertex 
	 * to every other vertex, then it is a strongly connected graph.
	 * 
	 * solution: use dfs
	 * 
	 * @param g
	 * 			the graph (considered as directed)
	 * @return
	 */
	private static <V, E extends Graph.Edge<V>> boolean checkIsStronglyConnected(Graph<V, E> g){
		Set<V> marked = null;
		
		Iterator<V> it = g.vertices().iterator();
		while (it.hasNext()) {
			V v = it.next();
			marked = new HashSet<V>();
			marked.add(v);
			for (V vn : g.successors(v)){
				if (!marked.contains(vn)){
					dfsDirected(g, vn, marked);
				}
			}
			if (g.vertices().size()!=marked.size()) return false;
		}
		return true;
	}
	
	private static <V, E extends Graph.Edge<V>> void dfsDirected(Graph<V, E> g, V v, Set<V> marked){
		marked.add(v);
		for (V vn : g.successors(v)){
			if (!marked.contains(vn)){
				dfsDirected(g, vn, marked);
			}
		}
	}
	
	/**
	 * 
	 * @param g
	 *            the graph (considered as undirected)
	 * 
	 * @return a set of vertices which disconnect the graph, with a minimum size
	 *         (empty if the graph is not connected).
	 *         
	 *         
	 */
	public static <V, E extends Graph.Edge<V>> Set<V> minimumCutset(Graph<V, E> g) {
		Set<V> result = new HashSet<V>();
		if (!checkIsConnected(g)) return result;
		Map<V, Graph.Edge<String>> internalEdge = new HashMap<V, Graph.Edge<String>>();
		Map<Graph.Edge<String>, Integer> capacities = new HashMap<Graph.Edge<String>, Integer>();
		Graph<String, Graph.Edge<String>> g2 = vertexConnectivityNetwork(g,internalEdge, capacities, false);
		int min = Integer.MAX_VALUE;
		Set<V> marked = new HashSet<V>();
		for (V u : g.vertices()){
			marked.add(u);
			for (V v : g.vertices()){		
				//System.out.println(u+"->" + v);
				if (!u.equals(v)&& !g.areNeighbors(u, v) && !marked.contains(v)) {
					String s = internalEdge.get(u).target();
					String t = internalEdge.get(v).source();
					FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(g2, s, t, null, capacities);
					if (fr.value<min) {
						System.out.println(u+"->" + v + " have f=" + fr.value+ " mc:" +fr.minimumCut);
						min = fr.value;	
						result = new HashSet<V>();
						for (Graph.Edge<String> e : fr.minimumCut){
							V ie = findVertexConnectivity(internalEdge, e);
							if (ie!=null&&!ie.equals(u)&&!ie.equals(v)){
								result.add(ie);
								if (result.size()==min) break;
							}
						}						
					}
				}
			}
		}
		return result;
	}
	
	private static <V> V findVertexConnectivity(Map<V, Graph.Edge<String>> internalEdge, Graph.Edge<String> e){
		V result = null;
		for (V v : internalEdge.keySet()){
			if (internalEdge.get(v).equals(e)) return v;
		}
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> Graph<String, Graph.Edge<String>> vertexConnectivityNetwork(Graph<V, E> g, Map<V, Graph.Edge<String>> internalEdge, Map<Graph.Edge<String>, Integer> capacities, boolean isDirected) {
		Graph<String, Graph.Edge<String>> result = new MultiGraph<String, Graph.Edge<String>>();
		for (V v : g.vertices()){
			String v1 = new String(v.toString() + "'");
			String v2 = new String(v.toString() + "\"");	
			result.addVertex(v1);
			result.addVertex(v2);
			Edge<String> e = new DirectedEdge<String>(v1,v2);
			result.addEdge(e);
			internalEdge.put(v, e);
			capacities.put(e, 1);
		}
		
		for (E e : g.edges()){
			V u = e.source();
			V v = e.target();
			Edge<String> e1 = new DirectedEdge<String>(internalEdge.get(u).target(),internalEdge.get(v).source());
			result.addEdge(e1);
			capacities.put(e1, Integer.MAX_VALUE);
			if (!isDirected){
				Edge<String> e2 = new DirectedEdge<String>(internalEdge.get(v).target(),internalEdge.get(u).source());
				result.addEdge(e2);
				capacities.put(e2, Integer.MAX_VALUE);
			}
		}
		return result;
	}	
	
	/**
	 * 
	 * @param g
	 *            the graph (considered as directed)
	 * @return a set of vertices which disconnect the graph, with a minimum size
	 *         (empty if the graph is not strongly connected).
	 */
	public static <V, E extends Graph.Edge<V>> Set<V> minimumDigraphCutset(
			Graph<V, E> g) {
		Set<V> result = new HashSet<V>();
		if (!checkIsStronglyConnected(g)) return result;
		Map<V, Graph.Edge<String>> internalEdge = new HashMap<V, Graph.Edge<String>>();
		Map<Graph.Edge<String>, Integer> capacities = new HashMap<Graph.Edge<String>, Integer>();
		Graph<String, Graph.Edge<String>> g2 = vertexConnectivityNetwork(g,internalEdge, capacities, true);
		System.out.println(g2);
		int min = Integer.MAX_VALUE;
		for (V u : g.vertices()){
			for (V v : g.vertices()){				
				if (!u.equals(v)&& !g.areNeighbors(u, v)) {
					System.out.println(u+"->" + v);
					String s = internalEdge.get(u).target();
					String t = internalEdge.get(v).source();
					FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(g2, s, t, null, capacities);
					if (fr.value<min) {
						System.out.println(u+"->" + v + " have f=" + fr.value+ " mc:" +fr.minimumCut);
						min = fr.value;	
						result = new HashSet<V>();
						for (Graph.Edge<String> e : fr.minimumCut){
							V ie = findVertexConnectivity(internalEdge, e);
							if (ie!=null){
								result.add(ie);
								if (result.size()==min) break;
							}
						}						
					}
				}
			}
		}
		return result;
	}
	
	/**
	 * 
	 * @param g
	 *            the graph (considered as undirected)
	 * 
	 * @return a set of edges which disconnect the graph, with a minimum size
	 *         (empty if the graph is not connected).
	 * 
	 * 
	 */
	public static <V, E extends Graph.Edge<V>> Set<E> minimumEdgeCutset(
			Graph<V, E> g) {
		Set<E> result = new HashSet<E>();
		if (!checkIsConnected(g)) return result;
		Map<E, Graph.Edge<String>> internalEdge = new HashMap<E, Graph.Edge<String>>();
		Map<Graph.Edge<String>, Integer> capacities = new HashMap<Graph.Edge<String>, Integer>();
		Graph<String, Graph.Edge<String>> g2 = edgeConnectivityNetwork(g,internalEdge ,capacities, false);
		int min = Integer.MAX_VALUE;
		Set<V> marked = new HashSet<V>();
		for (V u : g.vertices()){
			marked.add(u);
			for (V v : g.vertices()){		
				if (!u.equals(v)&& !g.areNeighbors(u, v) && !marked.contains(v)) {
					String s = u.toString();
					String t = v.toString();
					FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(g2, s, t, null, capacities);
					if (fr.value<min) {
						System.out.println(u+"->" + v + " have f=" + fr.value+ " mc:" +fr.minimumCut);
						min = fr.value;	
						result = new HashSet<E>();
						for (Graph.Edge<String> e : fr.minimumCut){
							E ie = findEdgeConnectivity(internalEdge, e);
							if (ie!=null&&!result.contains(ie)){
								result.add(ie);
								if (result.size()==min) break;
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	private static <E> E findEdgeConnectivity(Map<E, Graph.Edge<String>> internalEdge, Graph.Edge<String> e){
		E result = null;
		for (E e1 : internalEdge.keySet()){
			if (internalEdge.get(e1).equals(e)) return e1;
		}
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> Graph<String, Graph.Edge<String>> edgeConnectivityNetwork(Graph<V, E> g, Map<E, Graph.Edge<String>> internalEdge, Map<Graph.Edge<String>, Integer> capacities, boolean isDirected) {
		Graph<String, Graph.Edge<String>> result = new MultiGraph<String, Graph.Edge<String>>();
		for (E e : g.edges()){
			V u = e.source();
			V v = e.target();
			result.addVertex(u.toString());
			result.addVertex(v.toString());
			Edge<String> e1 = new DirectedEdge<String>(u.toString(),v.toString());
			result.addEdge(e1);
			internalEdge.put(e, e1);
			capacities.put(e1, 1);
			if (!isDirected){
				Edge<String> e2 = new DirectedEdge<String>(v.toString(),u.toString());	
				result.addEdge(e2);
				internalEdge.put(e, e2);
				capacities.put(e2, 1);
			}
			
		}
		return result;
	}	
	
	/**
	 * 
	 * @param g
	 *            the graph (considered as directed)
	 * @return a set of edges which disconnect the graph, with a minimum size
	 *         (empty if the graph is not strongly connected).
	 */
	public static <V, E extends Graph.Edge<V>> Set<E> minimumDigraphEdgeCutset(Graph<V, E> g) {
		Set<E> result = new HashSet<E>();
		if (!checkIsStronglyConnected(g)) return result;
		Map<E, Graph.Edge<String>> internalEdge = new HashMap<E, Graph.Edge<String>>();
		Map<Graph.Edge<String>, Integer> capacities = new HashMap<Graph.Edge<String>, Integer>();
		Graph<String, Graph.Edge<String>> g2 = edgeConnectivityNetwork(g,internalEdge ,capacities, true);
		int min = Integer.MAX_VALUE;
		for (V u : g.vertices()){
			for (V v : g.vertices()){		
				if (!u.equals(v)&& !g.areNeighbors(u, v)) {
					String s = u.toString();
					String t = v.toString();
					FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(g2, s, t, null, capacities);
					if (fr.value<min) {
						System.out.println(u+"->" + v + " have f=" + fr.value+ " mc:" +fr.minimumCut + "\n" + fr);
						min = fr.value;	
						result = new HashSet<E>();
						for (Graph.Edge<String> e : fr.minimumCut){
							E ie = findEdgeConnectivity(internalEdge, e);
							if (ie!=null&&!result.contains(ie)&&fr.maximumFlow.get(e)>0){
								result.add(ie);
								if (result.size()==min) break;
							}
						}
					}
				}
			}
		}
		return result;
	}
	
	public static <V, E extends Graph.Edge<V>> Set<E> maximumBipartiteMatching(Graph<V, E> g, V[] multiSource, V[] multiSink){
		Set<E> result = new HashSet<E>();
		
		if (!isBipartite(g)) {
			return result;
		}
		
		Map<Graph.Edge<String>, E> matching = new HashMap<Graph.Edge<String>, E>();
		Graph<String, Graph.Edge<String>> network = new MultiGraph<String, Graph.Edge<String>>();
		Map<Graph.Edge<String>, Integer> capacities = new HashMap<Graph.Edge<String>, Integer>();
		network.addVertex("source");
		network.addVertex("sink");
		for (V t : multiSink){
			network.addVertex(t.toString());
			DirectedEdge<String> e = new DirectedEdge<String>(t.toString(),"sink");
			network.addEdge(e);
			capacities.put(e, 1);
		}
		for (V s : multiSource){
			network.addVertex(s.toString());
			DirectedEdge<String> e = new DirectedEdge<String>("source",s.toString());
			network.addEdge(e);
			capacities.put(e, 1);
			for(E edge : g.incidentEdges(s)){
				DirectedEdge<String> e2 = new DirectedEdge<String>(s.toString(),edge.target().toString());
				network.addEdge(e2);
				capacities.put(e2, 1);
				matching.put(e2, edge);
			}
		}
		
		FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(network, "source", "sink", null, capacities);
		for (Graph.Edge<String> e : fr.maximumFlow.keySet()){
			if (fr.maximumFlow.get(e)==1&&!e.source().equals("source")&&!e.target().equals("sink")){
				result.add(matching.get(e));
			}
		}		
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> boolean isBipartite(Graph<V, E> g){
		Set<V> marked = new HashSet<V>();
		Map<V, Boolean> color = new HashMap<V, Boolean>();
		for (V v : g.vertices()){
			if (!marked.contains(v)){
				color.put(v, true);
				return dfsBipartite(g, v, marked, color);
			}
		}
		return true;
	}
	
	private static <V, E extends Graph.Edge<V>> boolean dfsBipartite(Graph<V, E> g, V v, Set<V> marked, Map<V, Boolean> color){
		marked.add(v);
		for (V w : g.neighbors(v)){
			if (!marked.contains(w)){
				color.put(w, !color.get(v));
				dfsBipartite(g, w, marked, color);
			}else if (color.get(w).equals(color.get(v))){
				return false;
			}
		}
		return true;
	}
	
	public static <V, E extends Graph.Edge<V>> PartialGraph<V, E> kruskal(Graph<V, E> g, final Map<E, Integer> weight){
		@SuppressWarnings("unchecked")
		PartialGraph<V, E> tree = g.partialGraph((Set<E>) Collections.EMPTY_SET);
		Queue<E> q = new PriorityQueue<E>(g.order(), new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return weight.get(o1).compareTo(weight.get(o2));
			}
		});
		//make-set
		Set<Set<V>> ms = new HashSet<Set<V>>();
		for (V v : g.vertices()){
			Set<V> s = new HashSet<V>();
			s.add(v);
			ms.add(s);
		}
		q.addAll(g.edges());
		while (!q.isEmpty()){			
			E e = q.poll();
			//find-set
			Set<V> s1 = null;
			Set<V> s2 = null;
			for (Set<V> s : ms) {
				if (s.contains(e.source())) s1 = s;
				if (s.contains(e.target())) s2 = s;
			}
			if (!s1.equals(s2)){				
				tree.addEdge(e);
				//union
				s1.addAll(s2);
				ms.remove(s2);
			}			
		}
		return tree;
	}
	
	
	public static <V, E extends Graph.Edge<V>> PartialGraph<V, E> prim(Graph<V, E> g, final Map<E, Integer> weight, V root){
		@SuppressWarnings("unchecked")
		PartialGraph<V, E> tree = g.partialGraph((Set<E>) Collections.EMPTY_SET);
		Queue<E> q = new PriorityQueue<E>(g.order(), new Comparator<E>() {
			@Override
			public int compare(E o1, E o2) {
				return weight.get(o1).compareTo(weight.get(o2));
			}
		});		
		Set<V> marked = new HashSet<V>();
		marked.add(root);
		for (E e :g.incidentEdges(root)){
			q.add(e);
		}
		while (!q.isEmpty()){
			E e = q.remove();
			tree.addEdge(e);
			V next = marked.contains(e.source())?e.target():e.source();
			marked.add(next);
			for (E e1 :g.incidentEdges(next)){
				V opp = e1.getOpposite(next);
				if (marked.contains(opp)) {
					continue;
				}else{
					q.add(e1);
				}
			}
			Set<E> remove = new HashSet<E>();
			for (Iterator<E> it = q.iterator();it.hasNext();){
				E e1 = it.next();
				if (marked.contains(e1.source())&&marked.contains(e1.target()))  remove.add(e1);	
				else continue;
			}
			q.removeAll(remove);
		}
		return tree;
	}
	
	public static class BipartitionResult<V, E extends Graph.Edge<V>>{
		public Graph<V,E> v1= new MultiGraph<V, E>();
		public Graph<V,E> v2= new MultiGraph<V, E>();
	}
	
	private static <V, E extends Graph.Edge<V>> Graph<V,E> graph2dfsTree(Graph<V,E> g, V s1, V s2){
		Graph<V,E> result = new MultiGraph<V, E>();
		result.addVertex(s1);
		result.addVertex(s2);
		@SuppressWarnings("unchecked")
		E eNew = (E)new DirectedEdge<V>(s1, s2);
		result.addEdge(eNew);
		for (E e : g.incidentEdges(s2)){
			V next = e.getOpposite(s2); 
			if (!result.containsVertex(next)) {
				dfsTree(g, s2, next, result);
			}
		}
		result.addVertex(s1);
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> void dfsTree(Graph<V, E> g, V s, V t, Graph<V,E> result){
		result.addVertex(t);
		@SuppressWarnings("unchecked")
		E eNew = (E)new DirectedEdge<V>(s, t);
		result.addEdge(eNew);
		for (E e : g.incidentEdges(t)){
			V next = e.getOpposite(t); 
			if (!result.containsVertex(next)) {
				dfsTree(g, t, next, result);
			}
		}
	}
	
	private static <V, E extends Graph.Edge<V>> Set<V> DES(Graph<V,E> tree, V a){
		Set<V> result = new HashSet<V>();
		result.add(a);
		for (V v : tree.successors(a)){
			dfsDES(tree, v, result);
		}
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> void dfsDES(Graph<V,E> tree, V a, Set<V> result){
		result.add(a);
		for (V v : tree.successors(a)){
			if (!result.contains(v)) {
				dfsDES(tree, v, result);
			}
		}
	}
	
	private static <V, E extends Graph.Edge<V>> Graph<V,E> inducedGraph(Graph<V,E> g, Set<V> vertices){
		Graph<V,E> result = new MultiGraph<V, E>();
		for (V v : vertices){
			result.addVertex(v);
		}
		for (E e : g.edges()){
			if (vertices.contains(e.source())&&vertices.contains(e.target())){
				result.addEdge(e);
			}
		}
		return result;
	}
	
	public static <V, E extends Graph.Edge<V>> BipartitionResult<V,E> part2(Graph<V,E> g, Graph<V,E> t, V s1, V s2, int n1, int n2){
		if (t==null) {
			t=graph2dfsTree(g,s1,s2);
		}
		BipartitionResult<V,E> result = new BipartitionResult<V, E>();
		if (n1==1){
			result.v1.addVertex(s1);
			Set<V> sv2 = new HashSet<V>();
			for (V v : g.vertices()){
				if (!v.equals(s1)) sv2.add(v);
			}
			result.v2 = inducedGraph(g,sv2);
			return result;
		}else if (n2==1){
			result.v2.addVertex(s2);
			Set<V> sv1 = new HashSet<V>();
			for (V v : g.vertices()){
				if (!v.equals(s2)) sv1.add(v);
			}
			result.v1 = inducedGraph(g,sv1);
			return result;
		}
		V a = t.successors(s2).iterator().next();
		Set<V> desA = DES(t,a);
		if (t.outdegree(s2)>=2){			
			if (desA.size()+1<=n2){
				Set<V> sv = new HashSet<V>();
				for (V v:g.vertices()){
					if (!desA.contains(v)) sv.add(v);
				}
				Graph<V,E> g21 = inducedGraph(g,sv);
				Graph<V,E> t21 = inducedGraph(t,sv);
				BipartitionResult<V,E> result21 = part2(g21, t21, s1, s2, n1, g21.order()-n1);
				sv = new HashSet<V>();
				for (V v : g.vertices()){
					if (!result21.v1.containsVertex(v)) sv.add(v);
				}
				result21.v2 = inducedGraph(g,sv);
				return result21;
			}else{
				Set<V> desS2 = DES(t,s2);
				desS2.removeAll(desA);
				desS2.remove(s2);
				Set<V> sv = new HashSet<V>();
				for (V v:g.vertices()){
					if (!desS2.contains(v)) sv.add(v);
				}
				Graph<V,E> g22 = inducedGraph(g,sv);
				Graph<V,E> t22 = inducedGraph(t,sv);
				BipartitionResult<V,E> result22 = part2(g22, t22, s1, s2, g22.order()-n2, n2);
				sv = new HashSet<V>();
				for (V v : g.vertices()){
					if (!result22.v2.containsVertex(v)) sv.add(v);
				}
				result22.v1 = inducedGraph(g,sv);
				return result22;
			}
		}else{
			V b = t.successors(a).iterator().next();
			Set<V> desB = DES(t, b);
			boolean isAdjacent = false;
			for (V v : desB){
				if (g.areNeighbors(v, s1)){
					isAdjacent=true;
					break;
				}
			}
			if (isAdjacent){
				if (desB.size()+1<=n1){
					Set<V> sv = new HashSet<V>();
					for (V v:g.vertices()){
						if (!desB.contains(v)) sv.add(v);
					}
					Graph<V,E> g311 = inducedGraph(g,sv);
					Graph<V,E> t311 = inducedGraph(t,sv);
					BipartitionResult<V,E> result311 = part2(g311, t311, s1, s2, g311.order()-n2, n2);
					sv = new HashSet<V>();
					for (V v : g.vertices()){
						if (!result311.v2.containsVertex(v)) sv.add(v);
					}
					result311.v1 = inducedGraph(g,sv);
					return result311;
				}
				else{
					desA.removeAll(desB);
					Set<V> sv = new HashSet<V>();
					for (V v:g.vertices()){
						if (!desA.contains(v)) sv.add(v);
					}
					Graph<V,E> g312 = inducedGraph(g,sv);
					@SuppressWarnings("unchecked")
					E eNew = (E)new DirectedEdge<V>(s2, b);
					t.addEdge(eNew);
					Graph<V,E> t312 = inducedGraph(t,sv);
					BipartitionResult<V,E> result312 = part2(g312, t312, s1, s2, n1, g312.order()-n1);
					
					sv = new HashSet<V>();
					for (V v : g.vertices()){
						if (!result312.v1.containsVertex(v)) sv.add(v);
					}
					result312.v2 = inducedGraph(g,sv);
					return result312;
				}
			}else{
				if (desB.size()+1<=n2){
					Set<V> sv = new HashSet<V>();
					for (V v:g.vertices()){
						if (!desB.contains(v)) sv.add(v);
					}
					Graph<V,E> g321 = inducedGraph(g,sv);
					Graph<V,E> t321 = inducedGraph(t,sv);
					BipartitionResult<V,E> result321 = part2(g321, t321, s1, s2, n1, g321.order()-n1);
					sv = new HashSet<V>();
					for (V v : g.vertices()){
						if (!result321.v1.containsVertex(v)) sv.add(v);
					}
					result321.v2 = inducedGraph(g,sv);
					return result321;
				}else{
					desA.removeAll(desB);
					Set<V> sv = new HashSet<V>();
					for (V v:g.vertices()){
						if (!desA.contains(v)) sv.add(v);
					}
					Graph<V,E> g322 = inducedGraph(g,sv);
					Graph<V,E> t322 = inducedGraph(t,sv);
					for (E e : t322.edges()){
						if (e.source().equals(s1)&&e.target().equals(s2)){
							t322.removeEdge(e);
							@SuppressWarnings("unchecked")
							E eNew = (E)new DirectedEdge<V>(s2, s1);
							t322.addEdge(eNew);
							break;
						}
					}
					@SuppressWarnings("unchecked")
					E eOther=(E)new DirectedEdge<V>(s1, b);
					t322.addEdge(eOther);
					BipartitionResult<V,E> result322 = part2(g322, t322, s2, s1, n2, g322.order()-n2);
					result322.v2 = result322.v1;
					sv = new HashSet<V>();
					for (V v : g.vertices()){
						if (!result322.v2.containsVertex(v)) sv.add(v);
					}
					result322.v1 = inducedGraph(g,sv);
					return result322;
				}
			}
		}
	}
	
	public static class Lamda<E> {
		E edge;
		int value;
		public Lamda(E edge,
		int value){
			this.edge = edge;
			this.value = value;
		}
		public Lamda() {
			// TODO Auto-generated constructor stub
		}
	}
	
	private static <V, E extends Graph.Edge<V>> E hasUsefulEdge(Graph<V, E> g,
			Map<E, Integer> b,
			Map<E, Integer> c, 
			FlowResults<E> f, Map<V, Lamda<E>> lamda){
		E edge = null;
		for (E e : g.edges()){
			int be = b!=null?b.get(e):0;
			if ((lamda.get(e.source())!=null
					&&lamda.get(e.target())==null
					&&f.maximumFlow.get(e)<c.get(e))
				||
				(lamda.get(e.source())==null
					&&lamda.get(e.target())!=null
					&&f.maximumFlow.get(e)>be)){
				return e;
			}
		}
		return edge;
	}
	
	private static <V, E extends Graph.Edge<V>> void label(Graph<V, E> g, V target,
			Map<E, Integer> b,
			Map<E, Integer> c, 
			FlowResults<E> f, 
			Map<V, Lamda<E>> lamda, 
			Map<E, Integer> delta){
		E edge = hasUsefulEdge(g, b, c, f, lamda);
		while (edge!=null && lamda.get(target)==null){
			int be = b!=null?b.get(edge):0;
			if (lamda.get(edge.source())!=null){
				lamda.put(edge.target(), new Lamda<E>(edge, 1));
				delta.put(edge, c.get(edge) - f.maximumFlow.get(edge));
			}
			else{
				lamda.put(edge.source(), new Lamda<E>(edge, -1));
				delta.put(edge, f.maximumFlow.get(edge)-be);
			}
			edge = hasUsefulEdge(g, b, c, f, lamda);
		}		
		if (edge==null){
			for (E e : g.edges()){
				if ((lamda.get(e.source())!=null
						&&lamda.get(e.target())==null)
					||
					(lamda.get(e.source())==null
						&&lamda.get(e.target())!=null)){
					f.minimumCut.add(e);					
				}
			}			
		}
	}
	
	private static <V, E extends Graph.Edge<V>> void augment(Graph<V, E> g,V source, V target,  
			FlowResults<E> f, 
			Map<V, Lamda<E>> lamda, 
			Map<E, Integer> delta){
		Queue<Lamda<E>> q = new LinkedList<Lamda<E>>();
		int vDelta = Integer.MAX_VALUE;
		V v = target;
		while (!v.equals(source)){
			q.add(lamda.get(v));
			E e = lamda.get(v).edge;
			int vE = delta.get(e);
			vDelta = vDelta < vE ? vDelta : vE;
			v = e.getOpposite(v);
		}
		while (!q.isEmpty()){
			Lamda<E> ld = q.poll();
			if (ld.value==1) {
				f.maximumFlow.put(ld.edge, f.maximumFlow.get(ld.edge) + vDelta);
			}else{
				f.maximumFlow.put(ld.edge, f.maximumFlow.get(ld.edge) - vDelta);
			}
		}
		
	}
	
	public static <V, E extends Graph.Edge<V>> FlowResults<E> fordFulkersonV01(Graph<V, E> g,V source, V target, Map<E, Integer> b, Map<E, Integer> c){
		FlowResults<E> result = new FlowResults<E>();
		for (E e : g.edges()){
			result.maximumFlow.put(e, 0);
		}		
		Map<V, Lamda<E>> lamda = new HashMap<V, Lamda<E>>();
		for (V v : g.vertices()){
			lamda.put(v, null);
		}
		lamda.put(source, new Lamda<E>());
		Map<E, Integer> delta = new HashMap<E, Integer>();	
		
		label(g, target, b, c, result , lamda, delta);
		while (lamda.get(target)!=null){
			augment(g, source, target, result, lamda, delta);
			for (V v: g.vertices()){
				if (v.equals(source)) continue;
				lamda.put(v, null);				
			}
			label(g, target, b, c, result, lamda, delta);
		}
		for (E e : g.edges()) {
			if (e.source().equals(source)) {
				result.value += result.maximumFlow.get(e);
			}
		}
		return result;	
	}
	
	public static <V, E extends Graph.Edge<V>> FlowResults<E> networkLowerUpperBound(Graph<V, E> g,V source, V target,  
			Map<E, Integer> b, Map<E, Integer> c) throws NotLegalFlowException {
		if (!hasLegalFlow(g, source, target, b, c)) {
			throw new NotLegalFlowException();
		}
		FlowResults<E> result = new FlowResults<E>();
		result = fordFulkersonV01(g, source, target, b, c);
		return result;	
	}
	
	private static <V, E extends Graph.Edge<V>> boolean hasLegalFlow(Graph<V, E> g,V source, V target, Map<E, Integer> b, Map<E, Integer> c){
		Graph<String, Graph.Edge<String>> g2 = new MultiGraph<String, Graph.Edge<String>>();		
		Map<Graph.Edge<String>, Integer> c2 = new HashMap<Graph.Edge<String>, Integer>();
		for (E e : g.edges()){
			V u = e.source();
			V v = e.target();
			g2.addVertex(u.toString());
			g2.addVertex(v.toString());
			Edge<String> edge = new DirectedEdge<String>(u.toString(),v.toString());
			g2.addEdge(edge);
			c2.put(edge, c.get(e)-b.get(e));
		}
		g2.addVertex(source+"'");
		g2.addVertex(target+"'");
		for (V v : g.vertices()){
			Edge<String> edge=null;
			int ce=0;
			if (!v.equals(target)){
				edge = new DirectedEdge<String>(v.toString(),target+"'");
				g2.addEdge(edge);
				ce = 0;
				for (E e : g.outgoingEdges(v)) {
					ce += b.get(e);
				}
				c2.put(edge, ce);
			}
			if (!v.equals(source)){
				edge = new DirectedEdge<String>(source+"'", v.toString());
				g2.addEdge(edge);
				ce = 0;
				for (E e : g.incomingEdges(v)) {
					ce += b.get(e);
				}
				c2.put(edge, ce);
			}
		}
		
		Edge<String> edge = new DirectedEdge<String>(source.toString(),target.toString());
		g2.addEdge(edge);
		c2.put(edge, Integer.MAX_VALUE);
		edge = new DirectedEdge<String>(target.toString(),source.toString());
		g2.addEdge(edge);
		c2.put(edge, Integer.MAX_VALUE);
		
		FlowResults<Graph.Edge<String>> fr = fordFulkersonV01(g2, source+"'", target+"'", null, c2);
		
		for (Graph.Edge<String> e: g2.outgoingEdges(source+"'")){
			if (fr.maximumFlow.get(e)!= c2.get(e)) return false;
		}
		
		return true;
	}
	
	public static <V, E extends Graph.Edge<V>> FlowResults<E> fordFulkersonV02(Graph<V, E> g, V source, V target, Map<E, Integer> c) {
		FlowResults<E> result = new FlowResults<E>();	
		for (E e : g.edges()){
			result.maximumFlow.put(e, 0);		
		}
		Graph<V, E> residual = new MultiGraph<V, E>();
		Map<E, Integer> cResidual = new HashMap<E, Integer>();
		updateResidualGraph(g, c, result.maximumFlow, residual, cResidual);
		Set<E> path = findArgumentPathByBFS(residual, cResidual, source, target); 
		while (path!=null){
			System.out.println(path);
			int min = getMin(path, cResidual);
			for (E e : path){
				if (g.containsEdge(e)){
					result.maximumFlow.put(e, result.maximumFlow.get(e) + min);
				}else{
					E e1 = g.outgoingEdges(e.target(), e.source()).iterator().next();
					result.maximumFlow.put(e1, result.maximumFlow.get(e1) - min);
				}
			}
			residual = new MultiGraph<V, E>();
			cResidual = new HashMap<E, Integer>();
			updateResidualGraph(g, c, result.maximumFlow, residual, cResidual);
			path = findArgumentPathByBFS(residual, cResidual, source, target);
		}
		updateFlowResult(g, residual, source, result);
		return result;
	}
	
	private static <V, E extends Graph.Edge<V>> void updateFlowResult(Graph<V, E> g,Graph<V, E> residual, V source, FlowResults<E> result){
		//update minimum cutset of result
		Set<V> marked = new HashSet<V>();
		Queue<V> q = new LinkedList<V>();
		q.add(source);
		while (!q.isEmpty()){
			V v = q.poll();
			marked.add(v);
			for (V v1 : residual.successors(v)){
				if (!marked.contains(v1)) {
					q.add(v1);
				}
			}
		}
		for (E e : g.edges()) {
			if (marked.contains(e.source()) && !marked.contains(e.target())
					|| !marked.contains(e.source())	&& marked.contains(e.target()))
				result.minimumCut.add(e);
		}
		//update value of result
		for (E e : g.outgoingEdges(source)){
			result.value += result.maximumFlow.get(e);
		}
	}
	
	private static <V, E extends Graph.Edge<V>> void updateResidualGraph(Graph<V, E> g, Map<E, Integer> c, 
			Map<E, Integer> f, Graph<V, E> r, Map<E, Integer> cR) {
		for (V v : g.vertices()) { r.addVertex(v); }
		for (E e : g.edges()){				
			Iterator<E> iter= g.outgoingEdges(e.target(), e.source()).iterator();
			//has reversal edge?
			if (iter.hasNext()){
				E reversal = iter.next();
				int i = c.get(e) + f.get(reversal) - f.get(e); 
				if (i > 0){
					r.addEdge(e);
					cR.put(e, i);
				}				
			}else{	
				int i = c.get(e)-f.get(e); 
				if (i > 0){
					r.addEdge(e);
					cR.put(e, i);
				}
				if (f.get(e)>0){
					@SuppressWarnings("unchecked")
					E reversal = (E) new DirectedEdge<V>(e.target(), e.source());
					r.addEdge(reversal);
					cR.put(reversal, f.get(e));	
				}				
			}
		}
	}
	
	private static <V, E extends Graph.Edge<V>> Set<E> findArgumentPathByBFS(Graph<V, E> r, Map<E, Integer> cR, V s, V t) {		
		Set<V> marked = new HashSet<V>();
		Map<V,E> parent = new HashMap<V, E>();
		Queue<V> q = new LinkedList<V>();
		q.add(s);
		while (!q.isEmpty()){
			V v = q.poll();
			marked.add(v);
			for (E e : r.outgoingEdges(v)){
				if (!marked.contains(e.target())){
					parent.put(e.target(),e);
					if (e.target().equals(t)){
						Set<E> path = new HashSet<E>();
						E e1 = parent.get(t);
						path.add(e1);
						while(!e1.source().equals(s)){
							e1 = parent.get(e1.source());
							path.add(e1);
						}
						return path;
					}else{
						 q.add(e.target());
					}
				}				 
			}
		}
		return null;
	}
	
	private static <V, E extends Graph.Edge<V>> int getMin(Set<E> path, Map<E, Integer> cR) {
		// TODO Auto-generated method stub
		int min = Integer.MAX_VALUE;
		for (E e : path){
			int cf = cR.get(e);
			min = cf < min ? cf :min;
		}
		return min;
	}
}
