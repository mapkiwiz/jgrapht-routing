package com.github.mapkiwiz.graph;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.jgrapht.Graph;

public class ShortestPath {
	
	private final DijsktraIteratorFactory iteratorFactory;
	
	public ShortestPath(DijsktraIteratorFactory factory) {
		this.iteratorFactory = factory;
	}

	public <V,E> double shortestPathLength(Graph<V, E> graph, V source, V target) {

		DijkstraIterator<V> iterator = this.iteratorFactory.create(graph, source);

		while (iterator.hasNext()) {
			V next = iterator.next();
			if (next.equals(target)){
				break;
			}
		}

		return iterator.getShortestPathLength(target);

	}

	public <V,E> Path<V> shortestPath(Graph<V,E> graph, V source, V target) {

		DijkstraIterator<V> forwardIterator =
				this.iteratorFactory.create(graph, source);
		DijkstraIterator<V> reverseIterator =
				this.iteratorFactory.create(graph, target);

		Set<V> forwardSet = new HashSet<V>();
		Set<V> reverseSet = new HashSet<V>();
		V middlePoint = null;

		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {

			if (forwardIterator.hasNext()) {
				V next = forwardIterator.next();
				if (reverseSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					forwardSet.add(next);
				}
			}

			if (reverseIterator.hasNext()) {
				V next = reverseIterator.next();
				if (forwardSet.contains(next)) {
					middlePoint = next;
					break;
				} else {
					reverseSet.add(next);
				}
			}
		}

		if (middlePoint != null) {

			Path<V> path = forwardIterator.getPath(middlePoint);
			path.elements.remove(0);
			Collections.reverse(path.elements);
			Path<V> reversePath = reverseIterator.getPath(middlePoint);
			path.elements.addAll(reversePath.elements);

			return path;

		} else {

			return new Path<V>();

		}

	}

	public <V,E> double bidirectionalShortestPathLength(Graph<V, E> graph, V source, V target) {

		DijkstraIterator<V> forwardIterator =
				this.iteratorFactory.create(graph, source);
		DijkstraIterator<V> reverseIterator =
				this.iteratorFactory.create(graph, target);

		V middlePoint = null;

		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {

			if (forwardIterator.hasNext()) {
				V next = forwardIterator.next();
				if (reverseIterator.isSettled(next)) {
					middlePoint = next;
					break;
				}
			}

			if (reverseIterator.hasNext()) {
				V next = reverseIterator.next();
				if (forwardIterator.isSettled(next)) {
					middlePoint = next;
					break;
				}
			}
		}

		if (middlePoint != null) {
			return forwardIterator.getShortestPathLength(middlePoint) + reverseIterator.getShortestPathLength(middlePoint);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

}
