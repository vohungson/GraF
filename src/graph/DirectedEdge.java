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

/**
 * 
 * @author amekhzoumi
 *
 * static import to replace Graph.Edge by Edge
 */
import static graph.Graph.Edge;

public class DirectedEdge<V> implements Edge<V> {
	private V source;
	private V target;

	public DirectedEdge(V source, V target) {
		this.source = source;
		this.target = target;
	}

	public V getOpposite(V vertex) {
		if (vertex == null) {
			if (source == null)
				return target;
			else if (target == null)
				return source;
			else
				throw new IllegalArgumentException();
		}
		if (vertex.equals(source))
			return target;
		else if (vertex.equals(target))
			return source;
		else
			throw new IllegalArgumentException();
	}

	public V source() {
		return source;
	}

	public V target() {
		return target;
	}

	public String toString() {
		return "(" + source + ", " + target + ")";
	}
}
