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

import static graph.Graph.Edge;

class PartialMultiGraph<V, E extends Edge<V>> extends SubMultiGraph<V, E>
		implements PartialGraph<V, E> {

	public PartialMultiGraph(MultiGraph<V, E> supergraph, Set<E> edges) {
		super(supergraph, supergraph.vertices(), edges);
	}

	@Override
	public boolean addVertex(V vertex) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeAllVertices(Collection<V> vertices) {
		throw new UnsupportedOperationException();
	}

	@Override
	public boolean removeVertex(V vertex) {
		throw new UnsupportedOperationException();
	}
}
