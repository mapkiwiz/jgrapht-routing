package com.github.mapkiwiz.graph;

import org.junit.Test;

import com.github.mapkiwiz.graph.contraction.PreparedGraph;
import com.github.mapkiwiz.graph.contraction.PreparedNode;
import com.github.mapkiwiz.graph.contraction.TestGraph;

public class DistanceMatrixTest {
	
	@Test
	public void testDistanceMatrix() {
		
		PreparedGraph graph = TestGraph.get();
		DistanceMatrix<PreparedNode> distanceMatrix =
				new DistanceMatrix<PreparedNode>();
		
		PreparedNode[] nodes = graph.vertexSet().toArray(new PreparedNode[graph.vertexSet().size()]);
		double[][] distances =
				distanceMatrix.distances(graph, nodes);
		
		for (int i=0; i<nodes.length; i++) {
			for (int j=0; j<=i; j++) {
				System.out.print(distances[i][j] + " ");
			}
			System.out.println();
		}
		
	}
	
	@Test
	public void testOneToManyDistance() {
		
		PreparedGraph graph = TestGraph.get();
		DistanceMatrix<PreparedNode> distanceMatrix =
				new DistanceMatrix<PreparedNode>();
		
		PreparedNode[] nodes = graph.vertexSet().toArray(new PreparedNode[graph.vertexSet().size()]);
		double[] distances =
				distanceMatrix.distances(graph, nodes[0], nodes);
		
		for (int i=0; i<nodes.length; i++) {
			System.out.println(i + " -> " + distances[i]);
		}
		
	}

}
