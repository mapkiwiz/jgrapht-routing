package com.github.mapkiwiz.graph;

import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;

public class PriorityQueueDijkstraIterator<V,E> implements DijkstraIterator<V> {

	private final PriorityQueue<QueueEntry<V, E>> heap;
	private final Graph<V,E> graph;
	private final Map<V, QueueEntry<V, E>> seen;
	private QueueEntry<V, E> next;
	private int maxNodeLimit = -1;
	private int nodeCount = 0;
	private EntryObserver<V> entryObserver;
	
	public PriorityQueueDijkstraIterator(Graph<V,E> graph, V source) {
		
		this.graph = graph;
		this.heap = new PriorityQueue<QueueEntry<V, E>>(graph.vertexSet().size() >>> 1);
		QueueEntry<V, E> entry = new QueueEntry<V, E>(source, null, null, 0.0, 0.0);
		this.heap.add(entry);
		this.seen = new HashMap<V, QueueEntry<V, E>>();
		this.seen.put(source, entry);
		this.next = null;
		
	}
	
	public void setMaxNodeLimit(int maxNodeLimit) {
		this.maxNodeLimit = maxNodeLimit;
	}
	
	public boolean isSettled(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry != null) {
			return entry.frozen;
		} else {
			return false;
		}
	
	}
	
	public boolean isSeenVertex(V vertex) {
		
		return this.seen.containsKey(vertex);
		
	}
	
	public boolean hasNext() {
		
		if (this.next == null) {
			return moveToNext();
		} else {
			return true;
		}
		
	}
	
	private boolean moveToNext() {
		
		if (maxNodeLimit > 0 && nodeCount > maxNodeLimit) {
			return false;
		}
		
		QueueEntry<V, E> next = this.heap.poll();
		
		while (next != null && next.duplicate) {
			next = this.heap.poll();
		}
		
		this.next = next;
		nodeCount++;
		
		return (next != null);
		
	}
	
	public boolean isAccessible(V toNode, V fromNode, E edge) {
		return true;
	}

	public V next() {
		
		assert(this.next != null);
		
		this.next.frozen = true;
		
		for (E edge : graph.edgesOf(this.next.vertex)) {
			
			V vertex = Graphs.getOppositeVertex(graph, edge, this.next.vertex);
			if (!isAccessible(vertex, this.next.vertex, edge)) {
				continue;
			}
			
			double path_element_weight = graph.getEdgeWeight(edge);
			double weight = this.next.weight + path_element_weight;
			
			if (this.seen.containsKey(vertex)) {
			
				QueueEntry<V, E> seenEntry = this.seen.get(vertex);
				if (weight < seenEntry.weight) {
					seenEntry.duplicate = true;
					QueueEntry<V, E> entry = new QueueEntry<V, E>(vertex, next.vertex, edge, weight, path_element_weight);
					this.emit(entry);
					this.heap.add(entry);
					this.seen.put(vertex, entry);
				}
				
			} else {
				
				QueueEntry<V, E> entry = new QueueEntry<V, E>(vertex, next.vertex, edge, weight, path_element_weight);
				this.emit(entry);
				this.heap.add(entry);
				this.seen.put(vertex, entry);
			
			}
			
		}
		
		V vertex = next.vertex;
		this.next = null;
		return vertex;
		
	}
	
	public void emit(QueueEntry<V, E> entry) {
		if (entryObserver != null) {
			entryObserver.observe(this, entry.vertex, entry.weight);
		}
	}
	
	public void setEntryObserver(EntryObserver<V> observer) {
		this.entryObserver = observer;
	}
	
	public V getParent(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry == null) {
			return null;
		} else {
			return entry.parent;
		}
		
	}
	
	public E getParentEdge(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry == null) {
			return null;
		} else {
			return entry.edge;
		}
		
	}
	
	public double getShortestPathLength(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry == null) {
			return Double.POSITIVE_INFINITY;
		} else {
			return entry.weight;
		}
		
	}
	
	public double getPathElementWeight(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry == null) {
			return Double.POSITIVE_INFINITY;
		} else {
			return entry.path_element_weight;
		}
		
	}
	
	public PathElement<V> getPathElement(V vertex) {
		
		QueueEntry<V, E> entry = this.seen.get(vertex);
		if (entry == null) {
			return null;
		} else {
			double distance = 0.0;
			if (entry.parent instanceof Node) {
				distance = NodeUtils.sphericalDistance((Node) entry.parent, (Node) vertex);
			}
			return new PathElement<V>(entry.parent, distance, entry.path_element_weight);
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
	
	static class QueueEntry<V, E> implements Comparable<QueueEntry<V, E>> {
		
		V vertex;
		V parent;
		E edge;
		
		double weight; // total weight from origin
		double path_element_weight; // weight from parent
		boolean frozen = false;
		boolean duplicate = false;
		
		public QueueEntry(V vertex, V parent, E edge, double weight, double path_element_weight) {
			this.vertex = vertex;
			this.parent = parent;
			this.edge = edge;
			this.weight = weight;
			this.path_element_weight = path_element_weight;
		}

		public int compareTo(QueueEntry<V, E> o) {
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
