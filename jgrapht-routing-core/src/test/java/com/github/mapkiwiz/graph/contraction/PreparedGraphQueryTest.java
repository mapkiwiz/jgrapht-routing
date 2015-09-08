package com.github.mapkiwiz.graph.contraction;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.ConcaveHullBuilder;
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.FeatureCollection;
import com.github.mapkiwiz.geojson.LineString;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathElement;
import com.github.mapkiwiz.graph.SearchByDistance;
import com.github.mapkiwiz.locator.IndexNodeLocator;
import com.github.mapkiwiz.test.MissingTestDataset;

@MissingTestDataset
@Category(MissingTestDataset.class)
public class PreparedGraphQueryTest {

	public PreparedGraph loadLargeGraph() throws IOException {

		URL node_file = new URL("file:///tmp/rhone-alpes.prepared.nodes.tsv");
		URL edge_file = new URL("file:///tmp/rhone-alpes.prepared.edges.tsv");
		URL shortcut_file = new URL("file:///tmp/rhone-alpes.prepared.shortcuts.tsv");
		CSVPreparedGraphLoader loader = new CSVPreparedGraphLoader(node_file, edge_file, shortcut_file);
		loader.setCoordinatePrecision(6);
		return loader.loadGraph();

	}

	@Test
	public void testQuery() throws IOException {

		PreparedGraph graph = loadLargeGraph();
		graph.contracted = true;
		System.out.println("Nodes : " + graph.vertexSet().size());
		System.out.println("Edges : " + graph.edgeSet().size());
		
		IndexNodeLocator<PreparedNode> nodeLocator = new IndexNodeLocator<PreparedNode>(graph.vertexSet());
		PreparedNode source = nodeLocator.locate(4.834413, 45.767304, 0.05);
		PreparedNode target = nodeLocator.locate(4.890021, 44.930435, 0.05);
		
		System.out.println("Source : " + source);
		System.out.println("Target : " + target);
		
		long startTime = System.currentTimeMillis();
		double distance = graph.shortestPathLength(source, target);
		long duration = System.currentTimeMillis() - startTime;
		
		System.out.println("Distance : " + distance);
		System.out.println("Duration : " + duration + " ms.");
		
		double avgDuration = 0.0;
		for (int i=0; i<500; i++) {
			startTime = System.currentTimeMillis();
			distance = graph.shortestPathLength(source, target);
			duration = System.currentTimeMillis() - startTime;
			assertTrue("Path has not null length", distance > 0);
			avgDuration = (i * avgDuration + duration) / (i+1);
		}
		
		System.out.println("Prepared shortest path Average Duration : " + avgDuration + " ms.");

	}
	
	@Test
	public void testIsochrone() throws IOException {
		
		PreparedGraph graph = loadLargeGraph();
		graph.contracted = true;
		System.out.println("Nodes : " + graph.vertexSet().size());
		System.out.println("Edges : " + graph.edgeSet().size());
		
		IndexNodeLocator<PreparedNode> nodeLocator = new IndexNodeLocator<PreparedNode>(graph.vertexSet());
		PreparedNode source = nodeLocator.locate(4.890021, 44.930435, 0.05);
		
		SearchByDistance searchByDistance = new SearchByDistance();
		
		ConcaveHullBuilder<PreparedNode> concaveHullBuilder = new ConcaveHullBuilder<PreparedNode>();
		
		long startTime = System.currentTimeMillis();
		List<PreparedNode> nodes = searchByDistance.search(graph, source, 50000);
		long duration = System.currentTimeMillis() - startTime;
		List<PreparedNode> hull1 = concaveHullBuilder.buildHull(nodes);
		System.out.println("Hull size : " + hull1.size());
		System.out.println("Execution time : " + duration + " ms.");
		System.out.println(NodeUtils.asPolygon(hull1).toGeoJSON());
		
		startTime = System.currentTimeMillis();
		nodes = searchByDistance.search(graph.filter().shortcuts(false), source, 50000);
		duration = System.currentTimeMillis() - startTime;
		List<PreparedNode> hull2 = concaveHullBuilder.buildHull(nodes);
		System.out.println("Hull (no shortcut) size : " + hull2.size());
		System.out.println("Execution time : " + duration + " ms.");
		System.out.println(NodeUtils.asPolygon(hull2).toGeoJSON());
		
	}
	
	@Test
	public void testUnpack() throws IOException {

		PreparedGraph graph = loadLargeGraph();
		graph.contracted = true;
		System.out.println("Nodes : " + graph.vertexSet().size());
		System.out.println("Edges : " + graph.edgeSet().size());
		
		IndexNodeLocator<PreparedNode> nodeLocator = new IndexNodeLocator<PreparedNode>(graph.vertexSet());
		PreparedNode source = nodeLocator.locate(4.834413, 45.767304, 0.05);
		PreparedNode target = nodeLocator.locate(4.890021, 44.930435, 0.05);
		
		System.out.println("Source : " + source);
		System.out.println("Target : " + target);
		
		List<PreparedEdge> packedEdges = graph.shortestPathEdges(source, target);
		
		List<List<Double>> coordinates = new ArrayList<List<Double>>();
		PreparedNode currentNode = source;
		for (PreparedEdge edge : packedEdges) {
			coordinates.add(currentNode.asCoordinatePair());
			currentNode = Graphs.getOppositeVertex(graph, edge, currentNode);
		}
		coordinates.add(currentNode.asCoordinatePair());
		Feature<LineString> packedPath = new Feature<LineString>();
		packedPath.properties.put("name", "packed");
		packedPath.geometry = new LineString();
		packedPath.geometry.coordinates = coordinates;
		
		Path<PreparedNode> path = graph.shortestPath(source, target);
		
		coordinates = new ArrayList<List<Double>>();
		for (PreparedNode node : path.getNodeList()) {
			coordinates.add(node.asCoordinatePair());
		}
		Feature<LineString> unpackedPath = new Feature<LineString>();
		unpackedPath.properties.put("name", "unpacked");
		unpackedPath.geometry = new LineString();
		unpackedPath.geometry.coordinates = coordinates;
		
		FeatureCollection<LineString> collection = new FeatureCollection<LineString>();
		collection.features.add(packedPath);
		collection.features.add(unpackedPath);
		
		
		System.out.println(collection.toGeoJSON());
		
	}

}
