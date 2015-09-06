package com.github.mapkiwiz.graph;

import org.jgrapht.Graph;

public class DistanceMatrix<V> {
	
	boolean bidirectional = true;
	private final DijsktraIteratorFactory factory;
	
	public DistanceMatrix() {
		this.factory = new PriorityQueueDijkstraIterator.Factory();
	}
	
	public <E> double[] distances(Graph<V, E> graph, V source, V... targets) {
		
		double[] vector = new double[targets.length];
		DijkstraIterator<V> iterator = factory.create(graph, source);
		
		for (int i=0; i<targets.length; i++) {
			V target = targets[i];
			if (source.equals(target)) {
				vector[i] = 0.0;
			} else {
				vector[i] = distance(target, iterator);
			}
		}
		
		return vector;
		
	}
	
	public <E> double[][] distances(Graph<V, E> graph, V... nodes) {
		
		double[][] matrix = new double[nodes.length][nodes.length];
		
		for (int i=0; i<nodes.length; i++) {
			
			V source = nodes[i];
			DijkstraIterator<V> iterator = factory.create(graph, source);
			matrix[i][i] = 0.0;
			
			for (int j=i+1; j<nodes.length; j++) {
				V target = nodes[j];
				double d = distance(target, iterator);
				matrix[i][j] = d;
				if (bidirectional) {
					matrix[j][i] = d;
				}
			}
			
			if (!bidirectional) {
				for (int j=0; j<i; j++) {
					V target = nodes[j];
					double d = distance(target, iterator);
					matrix[i][j] = d;
				}
			}
			
		}
		
		return matrix;
		
	}
	
	public double distance(V target, DijkstraIterator<V> iterator) {
		
		if (iterator.isSettled(target)) {
			return iterator.getShortestPathLength(target);
		}
		
		while (iterator.hasNext()) {
			V next = iterator.next();
			if (target.equals(next)) {
				break;
			}
		}
		
		return iterator.getShortestPathLength(target);
		
	}

}
