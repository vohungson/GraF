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
import java.util.ConcurrentModificationException;
import java.util.Set;

import static graph.Graph.Edge;

class SubMultiGraph<V, E extends Edge<V>> extends MultiGraph<V, E>
		implements Subgraph<V, E> {

	MultiGraph<V, E> supergraph;
	private int supergraphModCount;

	public SubMultiGraph(MultiGraph<V, E> supergraph, Set<V> vertices,
			Set<E> edges) {
		this.supergraph = supergraph;
		this.supergraphModCount = supergraph.modCount;
		for (V v : vertices)
			super.addVertex(v);
		for (E e : edges)
			super.addEdge(e);
	}

	private void testModCount() {
		if (supergraphModCount != supergraph.modCount)
			throw new ConcurrentModificationException();
	}

	@Override
	public boolean addEdge(E edge) {
		testModCount();
		if (supergraph.containsEdge(edge))
			return super.addEdge(edge);
		else
			throw new IllegalArgumentException();
	}

	@Override
	public boolean addVertex(V vertex) {
		testModCount();
		if (supergraph.containsVertex(vertex))
			return super.addVertex(vertex);
		else
			throw new IllegalArgumentException();
	}

	@Override
	public boolean removeAllEdges(Collection<E> edges) {
		testModCount();
		return super.removeAllEdges(edges);
	}

	@Override
	public boolean removeAllVertices(Collection<V> vertices) {
		testModCount();
		return super.removeAllVertices(vertices);
	}

	@Override
	public boolean removeEdge(E edge) {
		testModCount();
		return super.removeEdge(edge);
	}

	@Override
	public boolean removeVertex(V vertex) {
		testModCount();
		return super.removeVertex(vertex);
	}

	@Override
	public boolean areNeighbors(V vertex1, V vertex2) {
		testModCount();
		return super.areNeighbors(vertex1, vertex2);
	}

	@Override
	public void clear() {
		testModCount();
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean containsEdge(E edge) {
		testModCount();
		return super.containsEdge(edge);
	}

	@Override
	public boolean containsVertex(V vertex) {
		testModCount();
		return super.containsVertex(vertex);
	}

	@Override
	public int degree(V vertex) {
		testModCount();
		return super.degree(vertex);
	}

	@Override
	public Set<E> edges() {
		testModCount();
		return super.edges();
	}

	@Override
	public Iterable<E> incidentEdges(V vertex) {
		testModCount();
		return super.incidentEdges(vertex);
	}

	@Override
	public Iterable<E> incidentEdges(V vertex1, V vertex2) {
		testModCount();
		return super.incidentEdges(vertex1, vertex2);
	}

	@Override
	public Iterable<E> incomingEdges(V vertex) {
		testModCount();
		return super.incomingEdges(vertex);
	}

	@Override
	public int indegree(V vertex) {
		testModCount();
		return super.indegree(vertex);
	}

	@Override
	public boolean isEmpty() {
		testModCount();
		return super.isEmpty();
	}

	@Override
	public Iterable<V> neighbors(V vertex) {
		testModCount();
		return super.neighbors(vertex);
	}

	@Override
	public int order() {
		testModCount();
		return super.order();
	}

	@Override
	public int outdegree(V vertex) {
		testModCount();
		return super.outdegree(vertex);
	}

	@Override
	public Iterable<E> outgoingEdges(V vertex) {
		testModCount();
		return super.outgoingEdges(vertex);
	}

	@Override
	public Iterable<E> outgoingEdges(V source, V target) {
		testModCount();
		return super.outgoingEdges(source, target);
	}

	@Override
	public Iterable<V> predecessors(V vertex) {
		testModCount();
		return super.predecessors(vertex);
	}

	@Override
	public int size() {
		testModCount();
		return super.size();
	}

	@Override
	public Iterable<V> successors(V vertex) {
		testModCount();
		return super.successors(vertex);
	}

	@Override
	public Set<V> vertices() {
		testModCount();
		return super.vertices();
	}

	@Override
	public Graph<V, E> supergraph() {
		testModCount();
		return supergraph;
	}

	@Override
	public InducedSubgraph<V, E> inducedSubgraph(Set<V> vertices) {
		testModCount();
		return super.inducedSubgraph(vertices);
	}

	@Override
	public PartialGraph<V, E> partialGraph(Set<E> edges) {
		testModCount();
		return super.partialGraph(edges);
	}

	@Override
	public Subgraph<V, E> subgraph(Set<V> vertices, Set<E> edges) {
		testModCount();
		return super.subgraph(vertices, edges);
	}
}
