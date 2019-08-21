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
package collections;

/**
* A Predicate allows to check if an object verify some property. 
* 
* @since 1.0
*/
public interface Predicate<T> {
	/**
	 * Check if an object verifies the predicate.
	 * 
	 * @param o object to check.
	 * @return Returns true if the predicate is verified by the specified object.
	 */
	public boolean predicate(T o);
}
