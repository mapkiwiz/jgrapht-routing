package fr.gouv.agriculture.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.hamcrest.core.Is;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import fr.gouv.agriculture.geojson.Feature;
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
		
		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph("/tmp/bdtopo.nodes.tsv", "/tmp/bdtopo.edges.tsv");
		
//		Node source = graphLoader.getNodes().get(24951);
		Node source = graphLoader.getNodes().get(256987);
		double distance = 100000.0; // 50 km
		
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

}
