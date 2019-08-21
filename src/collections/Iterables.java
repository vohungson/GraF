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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@SuppressWarnings("unused")
public class Iterables {

	public static <E> Iterable<E> iterableWithPredicate(
			final Iterable<? extends E> iterable, final Predicate<? super E> p) {
		/*
		 * athman lambda expresssion
		 */
		return () -> Iterators.iteratorWithPredicate(iterable.iterator(), p);
				/*new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return Iterators.iteratorWithPredicate(iterable.iterator(), p);
			}

		};*/
	}

	public static <E> Iterable<E> append(final Iterable<? extends E> it1,
			final Iterable<? extends E> it2) {
		/*
		 * athman lambda expresssion
		 */
		return () -> Iterators.append(it1.iterator(), it2.iterator());
				/*new Iterable<E>() {

			@Override
			public Iterator<E> iterator() {
				return Iterators.append(it1.iterator(), it2.iterator());
			}

		};*/
	}

	/**
	 * Used mainly for testing. Test if the iterator <tt>it</tt> returns exactly
	 * the values contained in <tt>elements</tt>.
	 * 
	 * @param <E>
	 *            type of the elements.
	 * @param it
	 *            an iterable
	 * @param elements
	 *            an array of elements
	 * @return
	 */
	@SafeVarargs
	public static <E> boolean hasElements(Iterable<E> it, E... elements) {
		return Iterators.hasElements(it.iterator(), elements);
	}

	/**
	 * Used mainly for testing. Returns the number of elements returned by
	 * <tt>it</tt>.
	 * 
	 * @param <E>
	 *            type of the elements.
	 * @param it
	 *            an iterable
	 * 
	 * @return the number of elements returned by the iterable
	 */
	public static <E> int size(Iterable<E> it) {
		return Iterators.size(it.iterator());
	}

	public static <E> Iterable<E> emptyIterable() {
		/*
		 * athman lambda expresssion
		 */
		return () -> Iterators.emptyIterator();
				/*new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return Iterators.emptyIterator();
			}
		};*/
	}

	public static <E> Iterable<E> singleton(final E element) {
		/*
		 * athman lambda expresssion
		 */
		return () -> Iterators.singleton(element);
				/*new Iterable<E>() {
			@Override
			public Iterator<E> iterator() {
				return Iterators.singleton(element);
			}
		};*/
	}

	public static <E> List<E> fillList(List<E> list, Iterable<? extends E> it) {
		for (E element : it)
			list.add(element);
		return list;
	}

	public static <E> List<E> fillList(Iterable<? extends E> it) {
		return fillList(new ArrayList<E>(), it);
	}

	public static void print(Iterable<?> iterable, String separator) {
		Iterators.print(iterable.iterator(), separator);
	}
}
