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

import static graph.Graph.Edge;

/**
 * All the calls of spanning subgraph's methods become illegal as soon as the
 * super graph has been modified. So an ConcurrentModificationException will be
 * raised in this case.
 */
public interface PartialGraph<V, E extends Edge<V>> extends Subgraph<V, E> {
	/**
	 * Add an edge if it's already in the super graph. If the edge is not into
	 * the super graph, an IllegalArgumentException will be raised.
	 * 
	 * @param edge
	 *           The edge to add
	 * @exception IllegalArgumentException
	 * 
	 * @return true if the edge was succefuly added
	 */
	boolean addEdge(E edge);

	/**
	 * Unsupported operation, a vertex cannot be added into a spanning subgraph
	 * directly through this method. All the super graph's vertices have to be
	 * into this spanning subgraph. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param vertex
	 *           The vertex to add
	 * @exception UnsupportedOperationException
	 * 
	 */
	boolean addVertex(V vertex);

	/**
	 * Remove an edge from the spanning subgraph.
	 * 
	 * @param edge
	 *           The edge to remove
	 *           
	 * @return true if the edge was succefuly removed 
	 */
	boolean removeEdge(E edge);

	/**
	 * Unsupported operation, a vertex cannot be removed from a spanning
	 * subgraph directly through this method. All the super graph's vertices
	 * have to be into this spanning subgraph. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param vertex
	 *           The vertex to remove
	 * @exception UnsupportedOperationException
	 */
	boolean removeVertex(V vertex);

	/**
	 * Remove the whole edges from the spanning subgraph.
	 * 
	 * @param edges
	 *            The edges collection to remove
	 *            
	 * @return true if the collection was succefuly removed
	 */
	boolean removeAllEdges(Collection<E> edges);

	/**
	 * Unsupported operation, vertex cannot be removed from a spanning subgraph
	 * directly through this method. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param vertices
	 *           The vertices collection to remove
	 * @exception UnsupportedOperationException
	 */
	boolean removeAllVertices(Collection<V> vertices);
}
