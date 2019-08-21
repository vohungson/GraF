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
package graph;

import java.util.Collection;
import java.util.Set;

public interface Graph<V, E extends Graph.Edge<V>> {

	/**
	 * An edge. An edge links two vertices, called respectively the source and the
	 * target of the edge.
	 * 
	 * @param <V>
	 *            the type of vertices
	 */
	public static interface Edge<V> {
		/**
		 * Returns the vertex source of this edge.
		 * 
		 * @return the vertex source of this edge
		 */
		V source();

		/**
		 * Returns the target of the edge.
		 * 
		 * @return the target vertex
		 */
		V target();

		/**
		 * Returns the source if vertex is the target and vice-versa.
		 * 
		 * @param vertex
		 *            a vertex composing the edge
		 * @throws IllegalArgumentException
		 *             if <tt>vertex</tt> does not belong to the edge
		 * @return the other vertex composing the edge
		 */
		default V getOpposite(V vertex) {
			V source = source();
			V target = target();
			if (vertex == null) {
				if (source == null)
					return target;
				else if (target == null)
					return source;
			} else {
				if (vertex.equals(source))
					return target;
				else if (vertex.equals(target))
					return source;
			}
			throw new IllegalArgumentException("Vertex " + vertex + " does not belong to the graph");
		};
	}

	/**
	 * Add edge to the graph.
	 * 
	 * @param edge
	 *            the edge to be added to this graph
	 * @return <tt>true</tt> if the edge was added without error
	 * @throws UnsupportedOperationException
	 *             if the <tt>addEdge</tt> operation is not supported by this graph
	 * @throws ClassCastException
	 *             if the class of the specified edge prevents it from being added
	 *             to this graph
	 * @throws NullPointerException
	 *             if the specified edge is null
	 * @throws IllegalArgumentException
	 *             if some property of the specified edge prevents it from being
	 *             added to this graph. Especially, the vertices composing the edge
	 *             must belong to the graph.
	 */
	boolean addEdge(E edge);

	/**
	 * Add a vertex into the graph
	 * 
	 * @param vertex
	 *            the vertex to add in the graph
	 * @return true if the vertex was added without error
	 */
	boolean addVertex(V vertex);

	/**
	 * Test if vertex1 and vertex2 are neighbors (e.g: if there is an edge between
	 * vertex1 to vertex2)
	 * 
	 * @param vertex1
	 *            a vertex
	 * @param vertex2
	 *            a vertex
	 * @return true if vertex1 and vertex2 are neighbors else it returns false
	 */
	boolean areNeighbors(V vertex1, V vertex2);

	/**
	 * Remove all edges and vertices in the graph
	 * 
	 * @return void
	 */
	void clear();

	/**
	 * Check if the graph is empty (no vertices)
	 * 
	 * @return true if there is no vertices
	 */
	boolean isEmpty();

	/**
	 * Check if a given vertex is in the graph.
	 * 
	 * @param vertex
	 *            vertex to check if it is in the graph
	 * @return true if vertex is in the graph
	 */
	default boolean containsVertex(V vertex) {
		return vertices().contains(vertex);
	}

	/**
	 * Check if a given edge is in the graph.
	 * 
	 * @param edge
	 *            edge to check if it is in the graph
	 * @return true if edge is in the graph
	 */
	default boolean containsEdge(E edge) {
		return edges().contains(edge);
	}

	/**
	 * Remove edge from the graph.
	 * 
	 * @param edge
	 *            edge to remove
	 * @return true if edge was successfully removed from the graph
	 */
	boolean removeEdge(E edge);

	/**
	 * Remove vertex from the graph.
	 * 
	 * @param vertex
	 *            vertex to remove
	 * @return true if vertex was successfully removed from the graph
	 */
	boolean removeVertex(V vertex);

	/**
	 * Return the vertices of the graph.
	 * 
	 * @return the set of vertices in the graph as a view
	 */
	Set<V> vertices();

	/**
	 * return the edges of the graph.
	 * 
	 * @return the set of edges in the graph as a view
	 */
	Set<E> edges();

	/**
	 * return number of edges of the graph.
	 * 
	 * @return number of edges in the graph
	 */
	default int size() {
		return edges().size();
	}

	/**
	 * return number of vertices of the graph.
	 * 
	 * @return number of vertices in the graph
	 */
	default int order() {
		return vertices().size();
	}

	/**
	 * return the degree of a given vertex (outdegree + indegree)
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return outdegree + indegree
	 */
	int degree(V vertex);

	/**
	 * return the indegree of a given vertex, i.e. its number of incoming edges.
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return number of incoming edges
	 */
	int indegree(V vertex);

	/**
	 * return the outdegree of a given vertex, i.e. its number of outgoing edges.
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return number of outgoing edges
	 */
	int outdegree(V vertex);

	/**
	 * Gives incident (incoming + outgoing) edges of a given vertex.
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return iterable containing all incident edges
	 */
	Iterable<E> incidentEdges(V vertex);

	/**
	 * Gives edges linking vertex1 and vertex2
	 * 
	 * @param vertex1
	 *            a vertex in the graph
	 * @param vertex2
	 *            a vertex in the graph
	 * @return iterable containing all incident edges between vertex1 and vertex2
	 */
	Iterable<E> incidentEdges(V vertex1, V vertex2);

	/**
	 * Gives incoming edges of a given vertex.
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return iterable containing all incoming edges
	 */
	Iterable<E> incomingEdges(V vertex);

	/**
	 * Gives outgoing edges of a given vertex.
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return iterable containing all outgoing edges from vertex
	 */
	Iterable<E> outgoingEdges(V vertex);

	/**
	 * Gives edges linking source to target.
	 * 
	 * @param source
	 *            a vertex in the graph
	 * @param target
	 *            a vertex in the graph
	 * @return iterable containing all outgoing edges linking source to target
	 */
	Iterable<E> outgoingEdges(V source, V target);

	/**
	 * Gives the neighbors of a vertex.
	 * 
	 * @param vertex
	 *            a vertex of the graph
	 * @return iterable containing all the neighbors of vertex
	 */
	Iterable<V> neighbors(V vertex);

	/**
	 * Gives predecessors of a given vertex
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return iterable containing all the predecessors of vertex
	 */
	Iterable<V> predecessors(V vertex);

	/**
	 * Gives successors of a given vertex
	 * 
	 * @param vertex
	 *            a vertex in the graph
	 * @return iterable containing all the successors of vertex
	 */
	Iterable<V> successors(V vertex);

	/**
	 * Remove a collection of edges from the graph.
	 * 
	 * @param edges
	 *            a collection of edges in the graph
	 * @return true if all edges from the collection are removed successfully
	 */
	boolean removeAllEdges(Collection<E> edges);

	/**
	 * Remove a collection of vertices from the graph.
	 * 
	 * @param vertices
	 *            a collection of vertices in the graph
	 * @return true if all vertices of the collection are removed successfully
	 */
	boolean removeAllVertices(Collection<V> vertices);

	/**
	 * Returns an InducedSubgraph containing all the vertices given in a set and the
	 * edges linking them in the graph.
	 * 
	 * @param vertices
	 *            a Set of vertices which will compose the InducedSubgraph
	 * @return an InducedSubgraph<V, E> containing all vertices given in the set and
	 *         edges linking them
	 */
	InducedSubgraph<V, E> inducedSubgraph(Set<V> vertices);

	/**
	 * Returns a PartialGraph containing all edges given in a set and all the
	 * vertices of the graph
	 * 
	 * @param edges
	 *            a Set of edges which will compose the PartialGraph returned
	 * @return a PartialGraph<V, E> containing all edges given and vertices linked
	 *         by them
	 */
	PartialGraph<V, E> partialGraph(Set<E> edges);

	/**
	 * Returns a Subgraph containing vertices and edges given by sets
	 * 
	 * @param vertices
	 *            a Set of vertices that exists in the graph
	 * @param edges
	 *            a Set of edges that exists in the graph
	 * @return a Subgraph<V, E> containing vertices and edges
	 */
	Subgraph<V, E> subgraph(Set<V> vertices, Set<E> edges);

}
