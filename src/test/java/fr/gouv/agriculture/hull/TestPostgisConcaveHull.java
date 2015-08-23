package fr.gouv.agriculture.hull;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.EdgeListGraphLoader;
import fr.gouv.agriculture.graph.GraphTestUtils;
import fr.gouv.agriculture.graph.Isochrone;
import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.hull.PostgisConcaveHullBuilder;

public class TestPostgisConcaveHull {
	
	public DataSource getDataSource() {
		
		String url = "jdbc:postgresql://localhost:5432/refgeo";
		DriverManagerDataSource dataSource =
				new DriverManagerDataSource(url);
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername("refgeo");
		dataSource.setPassword("refgeo");
		
		return dataSource;
		
	}
	
	@Test
	public void testPostgisConcaveHull() throws IOException {
		
		DataSource dataSource = getDataSource();
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader loader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = loader.loadGraph(nodeFile, edgeFile);
		Node source = loader.getNodes().get(GraphTestUtils.GRENOBLE_NODE_ID);
		double distance = 50000.0;
		List<Node> isochrone = Isochrone.isochroneRaw(graph, source, distance);

		PostgisConcaveHullBuilder hullbuilder = new PostgisConcaveHullBuilder(dataSource);
		Polygon hull = hullbuilder.buildHull(isochrone);
		double duration_loading = hullbuilder.getLoadingDurationSeconds();
		double duration_concavehull = hullbuilder.getHullDurationSeconds();
		
		assertNotNull(hull);
		System.out.println(hull.toGeoJSON());
		System.out.println("Duration (Loading) : " + duration_loading );
		System.out.println("Duration (Concave hull) : " + duration_concavehull);
		
	}

}
