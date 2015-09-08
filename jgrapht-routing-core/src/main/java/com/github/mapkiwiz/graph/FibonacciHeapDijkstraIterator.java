package com.github.mapkiwiz.graph;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;
import org.jgrapht.traverse.ClosestFirstIterator;

public class FibonacciHeapDijkstraIterator<V,E> implements DijkstraIterator<V> {

	private final ClosestFirstIterator<V, E> iterator;
	private final Graph<V,E> graph;
	
	public FibonacciHeapDijkstraIterator(Graph<V,E> graph, V source) {
		this.graph = graph;
		this.iterator = new ClosestFirstIterator<V, E>(graph, source);
	}
	
	public boolean hasNext() {
		return this.iterator.hasNext();
	}

	public V next() {
		return this.iterator.next();
	}

	public void remove() {
		this.iterator.remove();
	}

	public V getParent(V vertex) {
		E edge =  this.iterator.getSpanningTreeEdge(vertex);
		return Graphs.getOppositeVertex(graph, edge, vertex);
	}

	public double getShortestPathLength(V vertex) {
		return this.iterator.getShortestPathLength(vertex);
	}

	public double getPathElementWeight(V vertex) {
		E edge = this.iterator.getSpanningTreeEdge(vertex);
		return graph.getEdgeWeight(edge);
	}

	public PathElement<V> getPathElement(V vertex) {
		
		E edge = this.iterator.getSpanningTreeEdge(vertex);
		V parent = Graphs.getOppositeVertex(graph, edge, vertex);
		double weight = this.graph.getEdgeWeight(edge);
		double distance = weight;
		
		return new PathElement<V>(parent, distance, weight);
		
	}

	public Path<V> getPath(V vertex) {
		
		Path<V> path = new Path<V>();

		PathElement<V> pathElement = new PathElement<V>(vertex, 0.0, 0.0);
		
		while (pathElement.node != null) {
			path.elements.add(pathElement);
			pathElement = getPathElement(pathElement.node);
		}

		return path;
		
	}
	
	public static class Factory implements DijsktraIteratorFactory {

		public <V, E> DijkstraIterator<V> create(Graph<V, E> graph, V source) {
			return new FibonacciHeapDijkstraIterator<V, E>(graph, source);
		}
		
	}

	public boolean isSettled(V vertex) {
		return (this.iterator.getShortestPathLength(vertex) < Double.POSITIVE_INFINITY);
	}

	public boolean isSeenVertex(V vertex) {
//		return iterator.isSeenVertex(vertex);
		throw new UnsupportedOperationException();
	}

	public void setEntryObserver(EntryObserver<V> observer) {
		throw new UnsupportedOperationException();
	}

}
