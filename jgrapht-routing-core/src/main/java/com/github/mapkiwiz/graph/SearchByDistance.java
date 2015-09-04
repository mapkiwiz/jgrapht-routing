package com.github.mapkiwiz.graph;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;

public class SearchByDistance {
	
	private final DijsktraIteratorFactory iteratorFactory;
	
	public SearchByDistance(DijsktraIteratorFactory factory) {
		this.iteratorFactory = factory;
	}
	
	public SearchByDistance() {
		this(new PriorityQueueDijkstraIterator.Factory());
	}
	
	public <V, E> List<V> search(Graph<V, E> graph, V source, double distance) {

		DijkstraIterator<V> iterator =
				this.iteratorFactory.create(graph, source);
		List<V> results = new ArrayList<V>();

		while (iterator.hasNext()) {

			V currentNode = iterator.next();
			double currentDistance = iterator.getShortestPathLength(currentNode);

			if (currentDistance > distance) {
				break;
			}

			results.add(currentNode);

		}

		return results;

	}

}
