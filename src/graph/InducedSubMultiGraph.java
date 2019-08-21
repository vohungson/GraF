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
import java.util.Collections;
import java.util.Set;

import static graph.Graph.Edge;

class InducedSubMultiGraph<V, E extends Edge<V>> extends
		SubMultiGraph<V, E> implements InducedSubgraph<V, E> {

	@SuppressWarnings("unchecked")
	public InducedSubMultiGraph(MultiGraph<V, E> supergraph, Set<V> vertices) {
		super(supergraph, vertices, (Set<E>) Collections.EMPTY_SET);
		for (V v : vertices) {
			for (E e : supergraph.outgoingEdges(v)) {
				if (vertices.contains(e.target()))
					super.addEdge(e);
			}
		}
	}

	@Override
	public boolean addEdge(E edge) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean addVertex(V vertex) {
		if (super.addVertex(vertex)) {
			for (E e : supergraph.incidentEdges(vertex)) {
				if (vertices().contains(e.getOpposite(vertex))) {
					super.addEdge(e);
				}
			}
			return true;
		}
		return false;
	}

	@Override
	public boolean removeAllEdges(Collection<E> edges) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeEdge(E edge) {
		throw new UnsupportedOperationException();
	}
}
