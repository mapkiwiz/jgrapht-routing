package com.github.mapkiwiz.graph.contraction;

import org.jgrapht.Graph;

import com.github.mapkiwiz.graph.DijkstraIterator;
import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;

public class PreparedGraphIterator extends PriorityQueueDijkstraIterator<PreparedNode, PreparedEdge> {

	public PreparedGraphIterator(PreparedGraph graph, PreparedNode source) {
		super(graph, source);
	}

	@Override
	public boolean isAccessible(PreparedNode toNode, PreparedNode fromNode,
			PreparedEdge edge) {
		
		return (toNode.level > fromNode.level);
		
	}
	
	public static class Factory implements DijsktraIteratorFactory {

		@SuppressWarnings("unchecked")
		public <V, E> DijkstraIterator<V> create(Graph<V, E> graph, V source) {
			
			if (graph instanceof PreparedGraph) {
				return (DijkstraIterator<V>) new PreparedGraphIterator((PreparedGraph) graph, (PreparedNode) source);
			}
			
			throw new IllegalArgumentException();
			
		}
		
	}

}
