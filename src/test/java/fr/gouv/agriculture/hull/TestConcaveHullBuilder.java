package fr.gouv.agriculture.hull;

import java.io.IOException;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import static org.junit.Assert.*;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.EdgeListGraphLoader;
import fr.gouv.agriculture.graph.GraphTestUtils;
import fr.gouv.agriculture.graph.Isochrone;
import fr.gouv.agriculture.graph.Node;

public class TestConcaveHullBuilder {
	
	@Test
	public void testBuildConcaveHull() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader loader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = loader.loadGraph(nodeFile, edgeFile);
		Node source = loader.getNodes().get(GraphTestUtils.GRENOBLE_NODE_ID);
		double distance = 50000.0;
		List<Node> isochrone = Isochrone.isochroneRaw(graph, source, distance);
		
		ConcaveHullBuilder builder = new ConcaveHullBuilder();
		Polygon polygon = builder.buildHull(isochrone);
		
		assertNotNull(polygon);
		System.out.println(polygon.toGeoJSON());
		System.out.println("Duration (Index) : " + builder.getIndexDurationSeconds());
		System.out.println("Duration (Hull) : " + builder.getHullDurationSeconds());
		
	}
	
	@Test
	public void testBuildConcaveHullPerf() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader loader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = loader.loadGraph(nodeFile, edgeFile);
		Node source = loader.getNodes().get(GraphTestUtils.GRENOBLE_NODE_ID);
		double distance = 30000.0;
		List<Node> isochrone = Isochrone.isochroneRaw(graph, source, distance);
		
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
