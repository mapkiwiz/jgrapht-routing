package fr.gouv.agriculture.hull;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.AbstractGraphTest;
import fr.gouv.agriculture.graph.Isochrone;
import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.test.DatabaseTestHelper;
import fr.gouv.agriculture.test.PostgisTest;

@PostgisTest
@Category(PostgisTest.class)
public class TestPostgisConcaveHull extends AbstractGraphTest {
	
	@Test
	public void testPostgisConcaveHull() throws IOException {
		
		DataSource dataSource = DatabaseTestHelper.getDataSource();
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		Node source = getNode(GRENOBLE_NODE_ID);
		double distance = 50000.0;
		List<Node> isochrone = Isochrone.isochroneRaw(graph, source, distance);

		PostgisConcaveHullBuilder hullBuilder = new PostgisConcaveHullBuilder(dataSource);
		Polygon hull = hullBuilder.buildHull(isochrone);
		double duration_loading = hullBuilder.getLoadingDurationSeconds();
		double duration_concavehull = hullBuilder.getHullDurationSeconds();
		
		assertNotNull(hull);
		System.out.println(hull.toGeoJSON());
		System.out.println("Duration (Loading) : " + duration_loading );
		System.out.println("Duration (Concave hull) : " + duration_concavehull);
		
	}

}
