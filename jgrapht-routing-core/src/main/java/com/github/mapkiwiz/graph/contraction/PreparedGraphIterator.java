package com.github.mapkiwiz.graph.contraction;

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

}
