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

/**
 * All the calls of subgraph's methods become illegal as soon as the super graph
 * has been modified. So a ConcurrentModificationException will be raised in
 * this case.
 */
public interface Subgraph<V, E extends Edge<V>> extends Graph<V, E> {
	/**
	 * Accessor to the super graph
	 * 
	 * @return The super graph
	 */
	Graph<V, E> supergraph();
}
