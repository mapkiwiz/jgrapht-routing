package com.github.mapkiwiz.graph;

import org.jgrapht.Graph;

public interface DijsktraIteratorFactory {
	
	public <V,E> DijkstraIterator<V> create(Graph<V,E> graph, V source);

}
