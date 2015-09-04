package com.github.mapkiwiz.graph.contraction;

import java.util.PriorityQueue;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GraphContractor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(GraphContractor.class);
	
	private ShortcutFinder shortcutFinder = new ShortcutFinder();
	
	public PriorityQueue<QueueEntry> prioritize(final PreparedGraph graph, final int minLevel) {
		
//		PrioritizeTask task = new DefaultPrioritizeTask(shortcutFinder);
		PrioritizeTask task = new ConcurrentPrioritizeTask(shortcutFinder, 4);
		
		long startTime = System.currentTimeMillis();
		PriorityQueue<QueueEntry> queue = task.prioritize(graph, minLevel);
		long duration = System.currentTimeMillis() - startTime;
		LOGGER.info("Node prioritization : " + (duration / 1000.0) + " .s");
		
		return queue;
		
	}
	
	public void contract(PreparedGraph graph) {

		int totalNodeCount = graph.vertexSet().size();
		int shortcutCount = 0;
		int iteration = 0;
		int level = 0;
		LOGGER.info("Contracting {} edges for {} nodes", graph.edgeSet().size(), totalNodeCount);
		
		PriorityQueue<QueueEntry> queue = prioritize(graph, level);
		LOGGER.info("Contraction initialized");
		
		while (!queue.isEmpty()) {
			
			iteration++;
			
			if (iteration % 1e4 == 0) {
				int nodeCount = level;
				LOGGER.info("Processed {} nodes / {}", nodeCount, totalNodeCount);
			}
			
			if (iteration % 3e5 == 0) {
				queue = prioritize(graph, level);
			}
			
			QueueEntry next = queue.poll();
			ContractorListener listener = new ContractorListener();
			shortcutFinder.findShortcuts(graph, next.node, level, listener);
			
			int priority = listener.getNodePriority();
			
			if (priority > next.priority) {
				queue.add(new QueueEntry(next.node, priority));
				continue;
			}
			
			LOGGER.debug("Removing {}", next.node);
			
			next.node.level = level;
			
			for (PreparedEdge edge : listener.getContractedEdges()) {
				if (edge.level > level) {
					edge.level = level;
				}
			}
			
			for (PreparedEdge shortcut : listener.getShortcuts()) {
				shortcut.viaNode = next.node;
				graph.addEdge(shortcut.source, shortcut.target, shortcut);
				shortcutCount++;
			}
			
			level++;
			
		}
		
		LOGGER.info("Added shortcuts : {}", shortcutCount);
		LOGGER.debug("Max node level : {}", level);
		
		graph.contracted = true;
		
	}
	
	static class QueueEntry implements Comparable<QueueEntry> {
		
		int priority = 0;
		PreparedNode node;
		
		public QueueEntry(PreparedNode node, int priority) {
			this.node = node;
			this.priority = priority;
		}

		public int compareTo(QueueEntry o) {
			if (priority < o.priority) {
				return -1;
			} else if (priority > o.priority) {
				return 1;
			} else {
				if (node.id < o.node.id) {
					return -1;
				} else if (node.id > o.node.id) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		
	}

}
