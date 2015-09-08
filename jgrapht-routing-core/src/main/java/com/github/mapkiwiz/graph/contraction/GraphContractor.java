package com.github.mapkiwiz.graph.contraction;

import java.net.URL;
import java.util.PriorityQueue;

import org.springframework.util.ClassUtils;

import com.github.mapkiwiz.util.TextProgressTracker;

public class GraphContractor {
	
	private ShortcutFinder shortcutFinder = new ShortcutFinder();
	
	public PriorityQueue<QueueEntry> prioritize(final PreparedGraph graph, final int minLevel) {
		
//		PrioritizeTask task = new DefaultPrioritizeTask(shortcutFinder);
		PrioritizeTask task = new ConcurrentPrioritizeTask(shortcutFinder, 4);
		PriorityQueue<QueueEntry> queue = task.prioritize(graph, minLevel);
		
		return queue;
		
	}
	
	public void contract(PreparedGraph graph) {

		int totalNodeCount = graph.vertexSet().size();
		int shortcutCount = 0;
		int iteration = 0;
		int level = 0;
		TextProgressTracker tracker = new TextProgressTracker("Contracting graph", totalNodeCount); 
		tracker.logMessage("Input nodes : " + totalNodeCount);
		tracker.logMessage("Input edges : " + graph.edgeSet().size());
		
		PriorityQueue<QueueEntry> queue = prioritize(graph, level);
		
		while (!queue.isEmpty()) {
			
			iteration++;
			
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
			
			next.node.level = level;
			
			for (PreparedEdge edge : listener.getContractedEdges()) {
				if (edge.data.level > level) {
					edge.data.level = level;
				}
			}
			
			for (PreparedEdge shortcut : listener.getShortcuts()) {
				graph.addEdge(shortcut.source, shortcut.target, shortcut);
				shortcutCount++;
			}
			
			level++;
			tracker.increment();
			
		}
		
		tracker.done();
		tracker.logMessage("Added shortcuts : " + shortcutCount);
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
	
	public static void main(String[] args) {
		
		try {
			
			URL node_file = ClassUtils.getDefaultClassLoader().getResource("large.nodes.tsv.gz");
			URL edge_file = ClassUtils.getDefaultClassLoader().getResource("large.edges.tsv.gz");
			CSVPreparedGraphLoader loader = new CSVPreparedGraphLoader(node_file, edge_file, false);
			loader.setCoordinatePrecision(6);

			PreparedGraph graph = loader.loadGraph();

			GraphContractor contractor = new GraphContractor();

			long startTime = System.currentTimeMillis();
			contractor.contract(graph);
			long duration = System.currentTimeMillis() - startTime;

			System.out.println("Execution time : " + (duration / 1000.0) + " s.");

			PreparedGraphWriter writer = new PreparedGraphWriter();
			writer.setCoordinatePrecision(6);
			writer.writeToDisk(graph, "/tmp/rhone-alpes.prepared");
			
			System.exit(0);
		
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(e);
		}
		
	}

}
