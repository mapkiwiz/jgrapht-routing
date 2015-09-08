package com.github.mapkiwiz.graph;


public class BestPathObserver<V> implements EntryObserver<V> {

	private final DijkstraIterator<V> forwardIterator;
	private final DijkstraIterator<V> reverseIterator;
	private double minWeight = Double.POSITIVE_INFINITY;
	private V middlePoint = null;
	
	public BestPathObserver(DijkstraIterator<V> forwardIterator, DijkstraIterator<V> reverseIterator) {
		this.forwardIterator = forwardIterator;
		this.reverseIterator = reverseIterator;
		this.forwardIterator.setEntryObserver(this);
		this.reverseIterator.setEntryObserver(this);
	}
	
	public void observe(Object emitter, V node, double weight) {
		
		boolean forward = (forwardIterator == emitter);
		
		if (forward) {
			updateMinWeight(node, weight, reverseIterator);
		} else {
			updateMinWeight(node, weight, forwardIterator);
		}
		
	}
	
	protected void updateMinWeight(V node, double weight, DijkstraIterator<V> iterator) {
		
		if (iterator.isSeenVertex(node)) {
			double w = weight + iterator.getShortestPathLength(node);
			if (w < minWeight) {
				minWeight = w;
				middlePoint = node;
			}
		}
		
	}
	
	public double getMinWeight() {
		return minWeight;
	}
	
	public V getMiddlePoint() {
		return middlePoint;
	}
	
	public boolean isMiddlePointSettled() {
		return forwardIterator.isSettled(middlePoint) && reverseIterator.isSettled(middlePoint);
	}

}
