package com.github.mapkiwiz.graph;

import java.util.Collections;

import org.jgrapht.Graph;

public class ShortestPath {
	
	private final DijsktraIteratorFactory iteratorFactory;
	
	public ShortestPath(DijsktraIteratorFactory factory) {
		this.iteratorFactory = factory;
	}

	public <V,E> double unidirectionalShortestPathLength(Graph<V, E> graph, V source, V target) {

		DijkstraIterator<V> iterator = this.iteratorFactory.create(graph, source);

		while (iterator.hasNext()) {
			V next = iterator.next();
			if (next.equals(target)){
				break;
			}
		}

		return iterator.getShortestPathLength(target);

	}
	
	public static <V> V bidirectionalDijkstra(DijkstraIterator<V> forwardIterator, DijkstraIterator<V> reverseIterator) {
		
		BestPathObserver<V> observer = new BestPathObserver<V>(forwardIterator, reverseIterator);
		
		V middlePoint = null;
		double forwardWeight = 0.0;
		double reverseWeight = 0.0;
		
		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {
			
			if (forwardIterator.hasNext()) {
				V next = forwardIterator.next();
				forwardWeight = forwardIterator.getShortestPathLength(next);
				if (forwardWeight + reverseWeight >= observer.getMinWeight()) {
					if (observer.isMiddlePointSettled()) {
						break;
					}
				}
			}
			
			if (reverseIterator.hasNext()) {
				V next = reverseIterator.next();
				reverseWeight = reverseIterator.getShortestPathLength(next);
				if (forwardWeight + reverseWeight >= observer.getMinWeight()) {
					if (observer.isMiddlePointSettled()) {
						break;
					}
				}
			}
			
		}
		
		middlePoint = observer.getMiddlePoint();
		return middlePoint;
		
	}

	public <V,E> Path<V> shortestPath(Graph<V,E> graph, V source, V target) {

		DijkstraIterator<V> forwardIterator =
				this.iteratorFactory.create(graph, source);
		DijkstraIterator<V> reverseIterator =
				this.iteratorFactory.create(graph, target);

		V middlePoint = bidirectionalDijkstra(forwardIterator, reverseIterator);

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

	public <V,E> double shortestPathLength(Graph<V, E> graph, V source, V target) {

		DijkstraIterator<V> forwardIterator =
				this.iteratorFactory.create(graph, source);
		DijkstraIterator<V> reverseIterator =
				this.iteratorFactory.create(graph, target);

		V middlePoint = bidirectionalDijkstra(forwardIterator, reverseIterator);

		if (middlePoint != null) {
			return forwardIterator.getShortestPathLength(middlePoint) + reverseIterator.getShortestPathLength(middlePoint);
		} else {
			return Double.POSITIVE_INFINITY;
		}
	}

}
