package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

import com.github.mapkiwiz.graph.contraction.GraphContractor.QueueEntry;
import com.github.mapkiwiz.util.TextProgressTracker;

public class DefaultPrioritizeTask implements PrioritizeTask {
	
	private final ShortcutFinder shortcutFinder;

	public DefaultPrioritizeTask(ShortcutFinder shortcutFinder) {
		this.shortcutFinder = shortcutFinder;
	}

	public PriorityQueue<QueueEntry> prioritize(PreparedGraph graph,
			int minLevel) {

		final int totalNodeCount = graph.vertexSet().size();
		TextProgressTracker progressTracker = new TextProgressTracker("Computing node priority", totalNodeCount);
		
		List<QueueEntry> entries = new ArrayList<QueueEntry>(totalNodeCount);
		
		for (PreparedNode node : graph.vertexSet()) {
			if (node.level >= minLevel) {
				NodePriorityCalculator calculator = new NodePriorityCalculator();
				shortcutFinder.findShortcuts(graph, node, minLevel, calculator);
				QueueEntry entry = new QueueEntry(node, calculator.getNodePriority());
				entries.add(entry);
				progressTracker.increment();
			}
		}
		
		progressTracker.done();
		progressTracker.logMessage("Sorting nodes by priority");
		PriorityQueue<GraphContractor.QueueEntry> queue = new PriorityQueue<GraphContractor.QueueEntry>(entries);
		
		return queue;
	}
	
}
