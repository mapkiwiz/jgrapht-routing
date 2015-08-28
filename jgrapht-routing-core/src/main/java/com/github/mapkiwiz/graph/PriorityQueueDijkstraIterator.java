package com.github.mapkiwiz.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

public class PriorityQueueDijkstraIterator<V,E> implements DijkstraIterator<V> {

	private final PriorityQueue<QueueEntry<V>> heap;
	private final Graph<V,E> graph;
	private final Map<V, QueueEntry<V>> seen;
	private QueueEntry<V> next;
	
	public PriorityQueueDijkstraIterator(Graph<V,E> graph, V source) {
		
		this.graph = graph;
		this.heap = new PriorityQueue<QueueEntry<V>>(graph.vertexSet().size() >>> 1);
		QueueEntry<V> entry = new QueueEntry<V>(source, null, 0.0, 0.0);
		this.heap.add(entry);
		this.seen = new HashMap<V, QueueEntry<V>>();
		this.seen.put(source, entry);
		this.next = null;
		
	}
	
	public boolean isSettled(V vertex) {
		
		QueueEntry<V> entry = this.seen.get(vertex);
		if (entry != null) {
			return entry.frozen;
		} else {
			return false;
		}
	
	}
	
	public boolean hasNext() {
		
		if (this.next == null) {
			return moveToNext();
		} else {
			return true;
		}
		
	}
	
	private boolean moveToNext() {
		
		QueueEntry<V> next = this.heap.poll();
		
		while (next != null && next.duplicate) {
			next = this.heap.poll();
		}
		
		this.next = next;
		
		return (next != null);
		
	}

	public V next() {
		
		assert(this.next != null);
		
		this.next.frozen = true;
		
		for (E edge : graph.edgesOf(this.next.vertex)) {
			
			V vertex = Graphs.getOppositeVertex(graph, edge, this.next.vertex);
			double path_element_weight = graph.getEdgeWeight(edge);
			double weight = this.next.weight + path_element_weight;
			
			if (this.seen.containsKey(vertex)) {
			
				QueueEntry<V> seenEntry = this.seen.get(vertex);
				if (weight < seenEntry.weight) {
					seenEntry.duplicate = true;
					QueueEntry<V> entry = new QueueEntry<V>(seenEntry.vertex, next.vertex, weight, path_element_weight);
					this.heap.add(entry);
					this.seen.put(vertex, entry);
				}
				
			} else {
				
				QueueEntry<V> entry = new QueueEntry<V>(vertex, next.vertex, weight, path_element_weight);
				this.heap.add(entry);
				this.seen.put(vertex, entry);
			
			}
			
		}
		
		V vertex = next.vertex;
		this.next = null;
		return vertex;
		
	}
	
	public V getParent(V vertex) {
		
		QueueEntry<V> entry = this.seen.get(vertex);
		if (entry == null) {
			return null;
		} else {
			return entry.parent;
		}
		
	}
	
	public double getShortestPathLength(V vertex) {
		
		QueueEntry<V> entry = this.seen.get(vertex);
		if (entry == null) {
			return Double.POSITIVE_INFINITY;
		} else {
			return entry.weight;
		}
		
	}
	
	public double getPathElementWeight(V vertex) {
		
		QueueEntry<V> entry = this.seen.get(vertex);
		if (entry == null) {
			return Double.POSITIVE_INFINITY;
		} else {
			return entry.path_element_weight;
		}
		
	}
	
	public PathElement<V> getPathElement(V vertex) {
		
		QueueEntry<V> entry = this.seen.get(vertex);
		if (entry == null) {
			return null;
		} else {
			return new PathElement<V>(entry.parent, entry.path_element_weight, entry.path_element_weight);
		}
		
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

	public void remove() {
		throw new UnsupportedOperationException();
	}
	
	static class QueueEntry<V> implements Comparable<QueueEntry<V>> {
		
		V vertex;
		V parent;
		double weight; // total weight from origin
		double path_element_weight; // weight from parent
		boolean frozen = false;
		boolean duplicate = false;
		
		public QueueEntry(V vertex, V parent, double weight, double path_element_weight) {
			this.vertex = vertex;
			this.parent = parent;
			this.weight = weight;
			this.path_element_weight = path_element_weight;
		}

		public int compareTo(QueueEntry<V> o) {
			if (this.weight > o.weight) {
				return 1;
			} else if (this.weight < o.weight) {
				return -1;
			} else {
				return 0;
			}
		}
		
	}
	
	public static class Factory implements DijsktraIteratorFactory {

		public <V, E> DijkstraIterator<V> create(Graph<V, E> graph, V source) {
			return new PriorityQueueDijkstraIterator<V, E>(graph, source);
		}
		
	}

}
