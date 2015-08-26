package com.github.mapkiwiz.hull;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.PostgisConcaveHullBuilder;
import com.github.mapkiwiz.graph.AbstractGraphTest;
import com.github.mapkiwiz.graph.Isochrone;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.test.DatabaseTestHelper;
import com.github.mapkiwiz.test.PostgisTest;


@PostgisTest
@Category(PostgisTest.class)
public class TestPostgisConcaveHull extends AbstractGraphTest {
	
	@Test
	public void testPostgisConcaveHull() throws IOException {
		
		DataSource dataSource = DatabaseTestHelper.getDataSource();
		
		Graph<Node, DefaultWeightedEdge> graph = loadLargeGraph();
		Node source = getNode(GRENOBLE_NODE_ID);
		double distance = 50000.0;
		Isochrone processor =
				new Isochrone(new PriorityQueueDijkstraIterator.Factory());
		List<Node> isochrone = processor.isochrone(graph, source, distance);

		PostgisConcaveHullBuilder hullBuilder = new PostgisConcaveHullBuilder(dataSource);
		List<Node> hull = hullBuilder.buildHull(isochrone);
		double duration_loading = hullBuilder.getLoadingDurationSeconds();
		double duration_concavehull = hullBuilder.getHullDurationSeconds();
		
		assertNotNull(hull);
		System.out.println(NodeUtils.asPolygon(hull).toGeoJSON());
		System.out.println("Duration (Loading) : " + duration_loading );
		System.out.println("Duration (Concave hull) : " + duration_concavehull);
		
	}

}
