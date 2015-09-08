package com.github.mapkiwiz.graph;

public interface EntryObserver<V> {
	
	public void observe(Object emitter, V node, double weight);

}
