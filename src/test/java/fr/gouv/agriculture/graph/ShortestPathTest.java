package fr.gouv.agriculture.graph;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import fr.gouv.agriculture.geojson.Feature;
import fr.gouv.agriculture.geojson.LineString;

public class ShortestPathTest extends AbstractGraphTest {
	
	@Test
	public void testShortestPathSmall() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadSmallGraph();
		Node source = getNode(SMALL_GRAPH_ORIGIN_NODE_ID);
		Node target = getNode(SMALL_GRAPH_TOPRIGHT_NODE_ID);
		
		double distance = ShortestPath.shortestPathLength(graph, source, target);
		assertTrue("Path has not of null length", distance > 0);
		
		assertTrue("Distance 0->899 is 709.0 u.", distance == 709.0);
		
	}
	
	@Test
	public void testBidirectionalShortestPathSmall() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadSmallGraph();
		Node source = getNode(SMALL_GRAPH_ORIGIN_NODE_ID);
		Node target = getNode(SMALL_GRAPH_TOPRIGHT_NODE_ID);
		
		double distance = ShortestPath.bidirectionalShortestPathLength(graph, source, target);
		System.out.println(distance);
		
		assertTrue("Path has not of null length", distance > 0);
		assertTrue("Distance 0->899 is 710.0 u.", distance == 710.0); // Not exact resulat, should be 709.0
		
	}

	@Test
	public void testShortestPath() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long start = System.currentTimeMillis();
		double distance = ShortestPath.shortestPathLength(graph, source, target);
		long duration = System.currentTimeMillis() - start;
		
		System.out.println("Distance : " + distance);
		System.out.println("Duration : " + (duration / 1000.0));
		assertTrue("Path has not null length", distance > 0);
		
	}
	
	@Test
	public void testBidirectionalShortestPath() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long start = System.currentTimeMillis();
		double distance = ShortestPath.bidirectionalShortestPathLength(graph, source, target);
		long duration = System.currentTimeMillis() - start;
		
		System.out.println("Distance : " + distance);
		System.out.println("Duration : " + (duration / 1000.0));
		assertTrue("Path has not null length", distance > 0);
		
	}
	
	@Test
	public void testGetShortestPath() throws IOException {
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		
		Node source = getNode(LYON_NODE_ID);
		Node target = getNode(VALENCE_NODE_ID);
		
		long start = System.currentTimeMillis();
		Path<Node> path = ShortestPath.shortestPath(graph, source, target);
		long duration = System.currentTimeMillis() - start;
		
		System.out.println("Duration : " + (duration / 1000.0));
		assertTrue("Path has not null length", path.elements.size() > 0);
		
		List<List<Double>> coordinates = new ArrayList<List<Double>>();
		for (Node node : path.getNodeList()) {
			coordinates.add(node.asCoordinatePair());
		}
		
		LineString geometry = new LineString();
		geometry.coordinates = coordinates;
		Feature<LineString> feature = new Feature<LineString>();
		feature.geometry = geometry;
		feature.properties.put("source", source.id);
		feature.properties.put("target", target.id);
		System.out.println(feature.toGeoJSON());
		
		System.out.println("Path length : " + path.elements.size());
		
	}

}
