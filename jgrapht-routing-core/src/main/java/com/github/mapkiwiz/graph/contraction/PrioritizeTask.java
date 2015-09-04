package com.github.mapkiwiz.graph.contraction;

import java.util.PriorityQueue;

import com.github.mapkiwiz.graph.contraction.GraphContractor.QueueEntry;

public interface PrioritizeTask {
	
	public PriorityQueue<QueueEntry> prioritize(final PreparedGraph graph, final int minLevel);

}
