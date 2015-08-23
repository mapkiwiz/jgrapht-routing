package fr.gouv.agriculture.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import fr.gouv.agriculture.geojson.Feature;
import fr.gouv.agriculture.geojson.FeatureCollection;
import fr.gouv.agriculture.geojson.Polygon;

public class IsochroneTest {

	@Test
	public void testIsochroneOnTestGraph() throws IOException {
		
		EdgeListGraphLoader graphLoader = GraphTestUtils.getTestGraph();
		Node source = graphLoader.getNodes().get(0);
		double distance = 300.0;
		
		List<Node> isochrone = Isochrone.isochrone(graphLoader.getGraph(), source, distance);
		
		assertTrue("Isochrone has not null length", isochrone.size() > 0);
		
		for (Node n : isochrone) {
			System.out.println(n);
		}
		
		System.out.println("Isochrone nodes : " + isochrone.size());
		
	}
	
	@Test
	public void testIsochrone() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(nodeFile, edgeFile);
		
//		Node source = graphLoader.getNodes().get(24951);
		Node source = graphLoader.getNodes().get(256987);
		double distance = 30000.0; // 30 km
		
		long start = System.currentTimeMillis();
		List<Node> isochrone = Isochrone.isochrone(graph, source, distance);
		assertTrue("Isochrone has not null length", isochrone.size() > 0);
		assertEquals(isochrone.get(0), isochrone.get(isochrone.size() - 1));
		long duration = System.currentTimeMillis() - start;
		
		Polygon geometry = Isochrone.asPolygon(isochrone);
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
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(nodeFile, edgeFile);
		
//		Node source = graphLoader.getNodes().get(24951);
		Node source = graphLoader.getNodes().get(256987);
		double[] distances = { 10000.0, 20000.0, 30000.0 };
		
		long start = System.currentTimeMillis();
		List<List<Node>> isochrones = Isochrone.isochrones(graph, source, distances);
		long duration = System.currentTimeMillis() - start;
		
		FeatureCollection<Polygon> collection = new FeatureCollection<Polygon>();
		int k = 0;
		
		for (List<Node> isochrone : isochrones) {
		
			double distance = distances[k++];
			assertTrue("Isochrone has not null length", isochrone.size() > 0);
			assertEquals(isochrone.get(0), isochrone.get(isochrone.size() - 1));
			System.out.println("Isochrone (d=" + distance + ") nodes : " + isochrone.size());
			
			Polygon geometry = Isochrone.asPolygon(isochrone);
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
