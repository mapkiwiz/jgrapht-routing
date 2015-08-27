package com.github.mapkiwiz.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.ConvexHullBuilder;
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.FeatureCollection;
import com.github.mapkiwiz.geojson.Polygon;
import com.github.mapkiwiz.graph.Isochrone;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;


public class IsochroneTest extends AbstractGraphTest {

	@Test
	public void testIsochroneOnTestGraph() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadSmallGraph();
		Node source =getNode(0L);
		double distance = 300.0;
		
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<Node> isochrone = processor.isochrone(graph, source, distance);
		
		assertTrue("Isochrone has not null length", isochrone.size() > 0);
		
		for (Node n : isochrone) {
			System.out.println(n);
		}
		
		System.out.println("Isochrone nodes : " + isochrone.size());
		
	}
	
	@Test
	public void testIsochrone() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
//		Node source = getNode(LYON_NODE_ID);
		Node source = getNode(VALENCE_NODE_ID);
		double distance = 30000.0; // 30 km
		
		long start = System.currentTimeMillis();
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<Node> isochrone = processor.isochrone(graph, source, distance);
		List<Node> hull = ConvexHullBuilder.convexHull(isochrone);
		assertTrue("Isochrone has not null length", hull.size() > 0);
		assertEquals(hull.get(0), hull.get(hull.size() - 1));
		long duration = System.currentTimeMillis() - start;
		
		Polygon geometry = NodeUtils.asPolygon(hull);
		Feature<Polygon> feature = new Feature<Polygon>();
		feature.geometry = geometry;
		feature.properties.put("source", source.id);
		feature.properties.put("distance", distance);
		
		System.out.println(feature.toGeoJSON());
		System.out.println("Isochrone nodes : " + isochrone.size());
		System.out.println("Duration : " + (duration / 1000.0));
		
	}
	
	@Test
	public void testIsochrones() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
//		Node source = getNode(LYON_NODE_ID);
		Node source = getNode(VALENCE_NODE_ID);
		double[] distances = { 10000.0, 20000.0, 30000.0 };
		
		long start = System.currentTimeMillis();
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<List<Node>> isochrones = processor.isochrones(graph, source, distances);
		long duration = System.currentTimeMillis() - start;
		
		FeatureCollection<Polygon> collection = new FeatureCollection<Polygon>();
		int k = 0;
		
		for (List<Node> isochrone : isochrones) {
		
			double distance = distances[k++];
			List<Node> hull = ConvexHullBuilder.convexHull(isochrone);
			assertTrue("Isochrone has not null length", hull.size() > 0);
			assertEquals(hull.get(0), hull.get(hull.size() - 1));
			System.out.println("Isochrone (d=" + distance + ") nodes : " + isochrone.size());
			
			Polygon geometry = NodeUtils.asPolygon(hull);
			Feature<Polygon> feature = new Feature<Polygon>();
			feature.geometry = geometry;
			feature.properties.put("source", source.id);
			feature.properties.put("distance", distance);
			
			collection.features.add(feature);
		
		}
		
		System.out.println(collection.toGeoJSON());
		System.out.println("Duration : " + (duration / 1000.0));
		
	}

}
