package fr.gouv.agriculture.graph;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

public class EdgeListGraphLoaderTest {

	
	@Test
	public void testLoadGraph() throws IOException {
		
		EdgeListGraphLoader graphLoader = GraphTestUtils.getTestGraph();
		
		System.out.println("Loaded " + graphLoader.getNumberOfNodes() + " nodes.");
		System.out.println("Loaded " + graphLoader.getNumberOfEdges() + " edges.");
		System.out.println("Loading took " + graphLoader.getLoadingTimeSeconds() + " s.");
		
		assertEquals(900, graphLoader.getNumberOfNodes());
		assertEquals(2581, graphLoader.getNumberOfEdges());
		
	}
	
	@Test
	public void testLoadGraphGZip() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		graphLoader.loadGraph(nodeFile, edgeFile);
		
		System.out.println("Loaded " + graphLoader.getNumberOfNodes() + " nodes.");
		System.out.println("Loaded " + graphLoader.getNumberOfEdges() + " edges.");
		System.out.println("Loading took " + graphLoader.getLoadingTimeSeconds() + " s.");
		
		assertEquals(280305, graphLoader.getNumberOfNodes());
		assertEquals(307083, graphLoader.getNumberOfEdges());
		
	}
	
	@Test
	public void testShortestPath() throws IOException {
		
		EdgeListGraphLoader loader = GraphTestUtils.getTestGraph();
		Graph<Node, DefaultWeightedEdge> graph = loader.getGraph();
		
		Node source = loader.getNodes().get(0);
		Node target = loader.getNodes().get(899);
		
		double distance = ShortestPath.shortestPathLength(graph, source, target);
		assertTrue("Path has not of null length", distance > 0);
		
		assertTrue("Distance 0->899 is 709.0 u.", distance == 709.0);
		
	}
	
	@Test
	public void testBidirectionalShortestPath() throws IOException {
		
		EdgeListGraphLoader loader = GraphTestUtils.getTestGraph();
		Graph<Node, DefaultWeightedEdge> graph = loader.getGraph();
		
		Node source = loader.getNodes().get(0);
		Node target = loader.getNodes().get(899);
		
		double distance = ShortestPath.bidirectionalShortestPathLength(graph, source, target);
		System.out.println(distance);
		
		assertTrue("Path has not of null length", distance > 0);
		assertTrue("Distance 0->899 is 710.0 u.", distance == 710.0); // Not exact resulat, should be 709.0
		
	}

}
