package com.github.mapkiwiz.graph.contraction;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.PriorityQueue;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;

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
			
			DefaultParser commandLineParser = new DefaultParser();
			CommandLine options = commandLineParser.parse(new GraphContractorOptions(), args);
			
			File node_file = new File(options.getOptionValue("n"));
			File edge_file = new File(options.getOptionValue("e"));
			
			if (!node_file.exists()) {
				throw new FileNotFoundException(node_file.getName());
			}
			
			if (!edge_file.exists()) {
				throw new FileNotFoundException(edge_file.getName());
			}
			
			File outputDir = new File(options.getOptionValue("d"));
			if (!outputDir.exists()) {
				outputDir.mkdirs();
			} else if (!outputDir.isDirectory()) {
				throw new ParseException("output-dir must be a directory");
			}
			
			CSVPreparedGraphLoader loader = new CSVPreparedGraphLoader(node_file.getAbsolutePath(), edge_file.getAbsolutePath(), false);
			loader.setCoordinatePrecision(Integer.parseInt(options.getOptionValue("p", "0")));

			PreparedGraph graph = loader.loadGraph();

			GraphContractor contractor = new GraphContractor();

			long startTime = System.currentTimeMillis();
			contractor.contract(graph);
			long duration = System.currentTimeMillis() - startTime;

			System.out.println("Execution time : " + (duration / 1000.0) + " s.");

			PreparedGraphWriter writer = new PreparedGraphWriter();
			writer.setCoordinatePrecision(6);
			writer.writeToDisk(graph, options.getOptionValue("d") + "/prepared");
			
			System.exit(0);
		
		} catch(ParseException e) {
			
			System.out.println(e.getMessage());
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(GraphContractor.class.getSimpleName(), new GraphContractorOptions());
			
		} catch(FileNotFoundException e) {
			
			System.out.println(e.getMessage());
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(GraphContractor.class.getSimpleName(), new GraphContractorOptions());
			
		} catch (Exception e) {
			System.err.println(e.getMessage());
			throw new RuntimeException(e);
		}
		
	}

}
