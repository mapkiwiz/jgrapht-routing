package com.github.mapkiwiz.graph;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.graph.ShortestPath;
import com.github.mapkiwiz.test.PerformanceTest;


@PerformanceTest
@Category(PerformanceTest.class)
public class ShortestPathPerfTest extends AbstractGraphTest {

	@Test
	public void testBidirectionalShortestPathPerf() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			ShortestPath shortestPath = new ShortestPath(
					new PriorityQueueDijkstraIterator.Factory());
			double distance = shortestPath.shortestPathLength(graph, source, target);
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("BidirectionalShortestPath Average Duration : " + (avgDuration / 1000.0));
		
		
	}
	
	@Test
	public void testShortestPathPerf() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			ShortestPath shortestPath = new ShortestPath(
					new PriorityQueueDijkstraIterator.Factory());
			double distance = shortestPath.unidirectionalShortestPathLength(graph, source, target);
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("ShortestPathLength Average Duration : " + (avgDuration / 1000.0));
		
		
	}
	
	@Test
	public void testShortestPathPathPerf() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long avgDuration = 0;
		for (int i=0; i<100; i++) {
			long start = System.currentTimeMillis();
			ShortestPath shortestPath = new ShortestPath(new PriorityQueueDijkstraIterator.Factory());
			Path<Node> path = shortestPath.shortestPath(graph, source, target);
			double distance = path.getTotalDistance();
			long duration = System.currentTimeMillis() - start;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("ShortestPathPath Average Duration : " + (avgDuration / 1000.0));
		
		
	}

}
