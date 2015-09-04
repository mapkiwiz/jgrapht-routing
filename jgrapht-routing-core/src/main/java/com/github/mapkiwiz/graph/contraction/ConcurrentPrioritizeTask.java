package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import com.github.mapkiwiz.graph.contraction.GraphContractor.QueueEntry;
import com.github.mapkiwiz.util.TextProgressTracker;

public class ConcurrentPrioritizeTask implements PrioritizeTask {
	
	private final ShortcutFinder shortcutFinder;
	private final int nThreads;
	
	public ConcurrentPrioritizeTask(ShortcutFinder shortcutFinder, int nThreads) {
		this.shortcutFinder = shortcutFinder;
		this.nThreads = nThreads;
	}

	public PriorityQueue<QueueEntry> prioritize(final PreparedGraph graph, final int minLevel) {

		int totalNodeCount = graph.vertexSet().size();
	
		List<PreparedNode> nodes = new ArrayList<PreparedNode>(totalNodeCount);
		List<QueueEntry> entries = new ArrayList<QueueEntry>(totalNodeCount);
		
		for (PreparedNode node : graph.vertexSet()) {
			if (node.level >= minLevel) {
				nodes.add(node);
			}
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(nThreads);
		List<Future<List<QueueEntry>>> promises = new ArrayList<Future<List<QueueEntry>>>();
		
		totalNodeCount = nodes.size();
		TextProgressTracker progressTracker = new TextProgressTracker(totalNodeCount);
		
		for(int i=0; i<nThreads; i++) {
			int range = totalNodeCount / nThreads;
			int fromIndex = i*range;
			int toIndex = fromIndex + range;
			if (i == (nThreads - 1)) {
				toIndex = totalNodeCount;
			}
			List<PreparedNode> stack = nodes.subList(fromIndex, toIndex);
			PrioritizeTask task = new PrioritizeTask(graph, stack, minLevel, progressTracker);
			Future<List<QueueEntry>> promise = executor.submit(task);
			promises.add(promise);
		}
		
		for (int i=0; i<nThreads; i++) {
			try {
				
				List<QueueEntry> result = promises.get(i).get();
				entries.addAll(result);
				
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return new PriorityQueue<QueueEntry>(entries);
		
	}

	class PrioritizeTask implements Callable<List<QueueEntry>> {

		private final int totalNodeCount;
		private final int minLevel;
		private final List<PreparedNode> stack;
		private final PreparedGraph graph;
		private final TextProgressTracker tracker;

		public PrioritizeTask(
				PreparedGraph graph,
				List<PreparedNode> stack,
				int minLevel,
				TextProgressTracker tracker) {

			this.graph = graph;
			this.stack = stack;
			this.minLevel = minLevel;
			this.totalNodeCount = stack.size();
			this.tracker = tracker;

		}

		public List<QueueEntry> call() throws Exception {

			List<QueueEntry> entries = new ArrayList<QueueEntry>(totalNodeCount / nThreads);

			for (PreparedNode node : stack) {
				NodePriorityCalculator calculator = new NodePriorityCalculator();
				shortcutFinder.findShortcuts(graph, node, minLevel, calculator);
				QueueEntry entry = new QueueEntry(node, calculator.getNodePriority());
				entries.add(entry);
				tracker.increment();
			}
			
			return entries;
		
		}

	}

}
