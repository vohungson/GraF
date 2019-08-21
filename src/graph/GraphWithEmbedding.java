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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** A graph with embedding is a graphe where incident edges of any vertex are cyclically 
 * ordered.
 */
public class GraphWithEmbedding<V, E extends Edge<V>> {

	private Map<V, List<E>> incidentEdges = new HashMap<V, List<E>>();

	private class InternalGraph extends MultiGraph<V, E> {

		public boolean addVertex(V v) {
			boolean modified = super.addVertex(v);
			if (modified)
				incidentEdges.put(v, new ArrayList<E>());
			return modified;
		}

		public boolean removeVertex(V v) {
			boolean modified = super.removeVertex(v);
			if (modified)
				incidentEdges.remove(v);
			return modified;
		}

		public boolean addEdge(E e) {
			boolean modified = super.addEdge(e);
			if (modified) {
				incidentEdges.get(e.source()).add(e);
				incidentEdges.get(e.target()).add(e);
			}
			return modified;
		}

		public boolean removeEdge(E e) {
			boolean modified = super.removeEdge(e);
			if (modified) {
				incidentEdges.get(e.source()).remove(e);
				incidentEdges.get(e.target()).remove(e);
			}
			return modified;
		}
	}

	private InternalGraph g = new InternalGraph();

	public GraphWithEmbedding() {
	}

	/**
	 * constructor
	 * 
	 * @return the embedded graph
	 */
	public Graph<V, E> graph() {
		return g;
	}

	/**
	 *Return the vertex incident edges collection  
	 * @param vertex
	 * 			the vertex to get incident edges
	 * @return incident edges collection  
	 * 
	 * @exception IllegalArgumentException
	 */
	public List<E> incidentEdges(V vertex) {
		if (!incidentEdges.containsKey(vertex))
			throw new IllegalArgumentException();
		return incidentEdges.get(vertex);
	}

	/**
	 * Swap two vertex incident edges 
	 * @param v
	 * 			the vertex to swap incident edges
	 * @param i
	 * 			first incident edge rank
	 * @param j
	 * 			second incident edge rank
	 */
	public void swapIncidentEdges(V v, int i, int j) {
		List<E> vEdges = incidentEdges.get(v);
		E ei = vEdges.get(i);
		vEdges.set(i, vEdges.get(j));
		vEdges.set(j, ei);
	}
}
