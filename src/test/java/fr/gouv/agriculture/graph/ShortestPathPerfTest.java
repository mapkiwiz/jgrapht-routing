package fr.gouv.agriculture.graph;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

public class ShortestPathPerfTest {

	@Test
	public void testBidirectionalShortestPathPerf() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(nodeFile, edgeFile);
		
		Node source = graphLoader.getNodes().get(24951);
		Node target = graphLoader.getNodes().get(256987);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			double distance = ShortestPath.bidirectionalShortestPathLength(graph, source, target);
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("BidirectionalShortestPath Average Duration : " + (avgDuration / 1000.0));
		
		
	}
	
	@Test
	public void testShortestPathPerf() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(nodeFile, edgeFile);
		
		Node source = graphLoader.getNodes().get(24951);
		Node target = graphLoader.getNodes().get(256987);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			double distance = ShortestPath.shortestPathLength(graph, source, target);
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("ShortestPathLength Average Duration : " + (avgDuration / 1000.0));
		
		
	}
	
	@Test
	public void testShortestPathPathPerf() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(nodeFile, edgeFile);
		
		Node source = graphLoader.getNodes().get(24951);
		Node target = graphLoader.getNodes().get(256987);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			Path<Node> path = ShortestPath.shortestPath(graph, source, target);
			double distance = path.getTotalDistance();
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("ShortestPathPath Average Duration : " + (avgDuration / 1000.0));
		
		
	}

}
