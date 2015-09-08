package com.github.mapkiwiz.graph.contraction;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.graph.ShortestPath;

public class PreparedGraphTest {
	
	@Test
	public void testUnpreparedShortestPath() {
		
		PreparedGraph graph = TestGraph.get();
		ShortestPath shortestPath = new ShortestPath(new PriorityQueueDijkstraIterator.Factory());
		Map<Long, PreparedNode> nodeMap = new HashMap<Long, PreparedNode>();
		for (PreparedNode node : graph.vertexSet()) {
			nodeMap.put(node.id, node);
		}
		
		double distance = shortestPath.unidirectionalShortestPathLength(graph,
				nodeMap.get(1L),
				nodeMap.get(13L));
		
		System.out.println("Shortest path : " + distance);
		
	}
	
	@Test
	public void testFindShortcuts() {
		
		PreparedGraph graph = TestGraph.get();
		Map<Long, PreparedNode> nodeMap = new HashMap<Long, PreparedNode>();
		for (PreparedNode node : graph.vertexSet()) {
			nodeMap.put(node.id, node);
		}
		
		ShortcutFinder shortcutFinder = new ShortcutFinder();
		
		PreparedNode node = nodeMap.get(1L);
		ContractorListener listener = new ContractorListener();	
		shortcutFinder.findShortcuts(graph, node, 0, listener);
		Collection<PreparedEdge> shortcuts = listener.getShortcuts();
		assertEquals(0, shortcuts.size());
		
		node = nodeMap.get(3L);
		listener = new ContractorListener();	
		shortcutFinder.findShortcuts(graph, node, 0, listener);
		shortcuts = listener.getShortcuts();
		assertEquals(3, shortcuts.size());
		
		node = nodeMap.get(6L);
		listener = new ContractorListener();	
		shortcutFinder.findShortcuts(graph, node, 0, listener);
		shortcuts = listener.getShortcuts();
		assertEquals(6, shortcuts.size());
		
		node = nodeMap.get(13L);
		listener = new ContractorListener();	
		shortcutFinder.findShortcuts(graph, node, 0, listener);
		shortcuts = listener.getShortcuts();
		assertEquals(0, shortcuts.size());
		
	}
	
	@Test
	public void testContract() {
		
		PreparedGraph graph = TestGraph.get();
		Map<Long, PreparedNode> nodeMap = new HashMap<Long, PreparedNode>();
		for (PreparedNode node : graph.vertexSet()) {
			nodeMap.put(node.id, node);
		}
		
		GraphContractor contractor = new GraphContractor();
		contractor.contract(graph);
		
//		for (PreparedEdge edge : graph.edgeSet()) {
//			System.out.println(edge);
//		}
//		
//		for (PreparedNode node : graph.vertexSet()) {
//			System.out.println(node);
//		}
		
		PreparedNode source = nodeMap.get(1L);
		PreparedNode target = nodeMap.get(13L);
		
		double distance = graph.shortestPathLength(source, target);
		System.out.println("Distance : " + distance);
		
	}

}
