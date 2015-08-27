package com.github.mapkiwiz.hull;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.ConcaveHullBuilder;
import com.github.mapkiwiz.graph.AbstractGraphTest;
import com.github.mapkiwiz.graph.Isochrone;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.test.PerformanceTest;


public class TestConcaveHullBuilder extends AbstractGraphTest {
	
	@Test
	public void testBuildConcaveHull() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		Node source = getNode(GRENOBLE_NODE_ID);
		double distance = 50000.0;
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<Node> isochrone = processor.isochrone(graph, source, distance);
		
		ConcaveHullBuilder builder = new ConcaveHullBuilder();
		List<Node> hull = builder.buildHull(isochrone);
		
		assertNotNull(hull);
		System.out.println(NodeUtils.asPolygon(hull).toGeoJSON());
		System.out.println("Duration (Index) : " + builder.getIndexDurationSeconds());
		System.out.println("Duration (Hull) : " + builder.getHullDurationSeconds());
		
	}
	
	@Test
	@PerformanceTest
	@Category(PerformanceTest.class)
	public void testBuildConcaveHullPerf() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		Node source = getNode(GRENOBLE_NODE_ID);
		double distance = 30000.0;
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<Node> isochrone = processor.isochrone(graph, source, distance);
		
		ConcaveHullBuilder builder = new ConcaveHullBuilder();
		
		double avg_duration_index = 0.0;
		double avg_duration_hull = 0.0;
		
		for (int i=0; i<100; i++) {
			builder.buildHull(isochrone);
			avg_duration_index = ( i* avg_duration_index + builder.getIndexDurationSeconds() ) / (i+1);
			avg_duration_hull = ( i * avg_duration_hull + builder.getHullDurationSeconds() ) / (i+1);
		}
		
		System.out.println("Avg Duration (Index) : " + avg_duration_index);
		System.out.println("Avg Duration (Hull) : " + avg_duration_hull);
		
	}

}
