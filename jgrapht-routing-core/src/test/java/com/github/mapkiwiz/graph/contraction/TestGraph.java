package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.List;

public class TestGraph {
	
	private List<PreparedNode> nodes = new ArrayList<PreparedNode>();
	private PreparedGraph graph;
	
	public static PreparedGraph get() {
		TestGraph testGraph = new TestGraph();
		return testGraph.getTestGraph();
	}
	
	public PreparedGraph getTestGraph() {
		if (graph == null) {
			graph = createTestGraph();
		}
		return graph;
	}
	
	private PreparedGraph createTestGraph() {
		
		graph = new PreparedGraph();
		
		for (int i=0; i<13; i++) {
			PreparedNode node = new PreparedNode(i+1, 0, 0);
			graph.addVertex(node);
			nodes.add(node);
		}
		
		addEdge(1, 2, 3.0);
		addEdge(1, 3, 4.0);
		addEdge(1, 5, 7.0);
		
		addEdge(2, 3, 5.0);
		addEdge(2, 4, 2.0);
		
		addEdge(3, 4, 2.0);
		addEdge(3, 6, 1.0);
		
		addEdge(4, 7, 5.0);
		
		addEdge(5, 6, 4.0);
		addEdge(5, 8, 6.0);
		
		addEdge(6, 7, 3.0);
		addEdge(6, 9, 1.0);
		
		addEdge(7, 10, 7.0);
		
		addEdge(8, 9, 3.0);
		addEdge(8, 12, 5.0);
		
		addEdge(9, 10, 3.0);
		addEdge(9, 11, 1.0);
		
		addEdge(10, 13, 4.0);
		
		addEdge(11, 12, 2.0);
		addEdge(11, 13, 3.0);
		
		addEdge(12, 13, 4.0);
		
		return graph;
		
	}
	
	private void addEdge(int source, int target, double weight) {
		
		PreparedNode sourceNode = nodes.get(source-1);
		PreparedNode targetNode = nodes.get(target-1);
		PreparedEdge edge = new PreparedEdge(sourceNode, targetNode, weight);
		
		graph.addEdge(edge.source, edge.target, edge);
		
	}

}
