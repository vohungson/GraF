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
import static graph.Graph.Edge;
import java.util.Collection;

/**
 * All the calls of induced subgraph's methods become illegal as soon as the
 * super graph has been modified. A ConcurrentModificationException will be
 * raised in this case.
 */
public interface InducedSubgraph<V, E extends Edge<V>> extends
		Subgraph<V, E> {
	/**
	 * Unsupported operation, an edge cannot be added into an induced subgraph
	 * directly through this method. Either edges is automatically added when a
	 * vertex is added, Or the edge is not into the super graph, and it cannot
	 * be added into the induced subgraph. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param edge
	 *           The edge to add
	 *           
	 * @exception UnsupportedOperationException
	 * 
	 */
	boolean addEdge(E edge);

	/**
	 * Add a vertex if it's already in the super graph. If the vertex is not
	 * into the super graph, an IllegalArgumentException will be raised.
	 * 
	 * @param vertex
	 *          The vertex to add
	 * @exception IllegalArgumentException
	 * 
	 * @return true if the vertex was successfully added
	 */
	boolean addVertex(V vertex);

	/**
	 * Unsupported operation, an edge cannot be removed from an induced subgraph
	 * directly through this method. Incident edges to a vertex will be removed
	 * at this vertex removing. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param edge
	 *           The edge to remove
	 * @exception UnsupportedOperationException
	 */
	boolean removeEdge(E edge);

	/**
	 * Remove a vertex from the induced subgraph. All the incident edges will be
	 * removed.
	 * 
	 * @param vertex
	 *           The vertex to remove
	 *           
	 * @return true if the vertex was successfully removed
	 */
	boolean removeVertex(V vertex);

	/**
	 * Unsupported operation, edges cannot be removed from an induced subgraph
	 * directly through this method. Incident edges to a vertex will be removed
	 * at this vertex removing. This method call will raise an
	 * UnsupportedOperationException.
	 * 
	 * @param edges
	 *          The edges collection to remove
	 * @exception UnsupportedOperationException
	 */
	boolean removeAllEdges(Collection<E> edges);

	/**
	 * Remove the whole vertices from the induced subgraph. All the incident
	 * edges of each vertex will be removed.
	 * 
	 * @param vertices
	 *           The vertices collection to remove
	 *           
	 * @return true if the collection was successfully removed
	 */
	boolean removeAllVertices(Collection<V> vertices);

	/**
	 * Access to the super graph
	 * 
	 * @return The super graph
	 */
	Graph<V, E> supergraph();
}
