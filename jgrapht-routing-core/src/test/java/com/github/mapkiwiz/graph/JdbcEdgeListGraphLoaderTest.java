package com.github.mapkiwiz.graph;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.graph.ShortestPath;
import com.github.mapkiwiz.graph.loader.JdbcEdgeListGraphLoader;
import com.github.mapkiwiz.test.AbstractHsqlDbTest;


public class JdbcEdgeListGraphLoaderTest extends AbstractHsqlDbTest {
	
	@Test
	public void testLoadGraph() throws IOException {
		
		String nodeTemplateQuery =
				"SELECT id, (lon / 1000000.0) AS lon, (lat / 1000000.0) as lat" +
				" FROM nodes ORDER BY id";
		
		String edgeTemplateQuery =
				"SELECT source, target, weight FROM edges ORDER BY source, target";
		
		JdbcEdgeListGraphLoader loader =
				new JdbcEdgeListGraphLoader(
						dataSource,
						nodeTemplateQuery,
						edgeTemplateQuery);
		
		Graph<Node, DefaultWeightedEdge> graph = loader.loadGraph();
		
		System.out.println("Loaded " + graph.vertexSet().size() + " nodes.");
		System.out.println("Loaded " + graph.edgeSet().size() + " edges.");
		System.out.println("Loading Time : " + loader.getLoadingTimeSeconds() + " s.");
		
		Node source = loader.getNode(AbstractGraphTest.LYON_NODE_ID);
		Node target = loader.getNode(AbstractGraphTest.VALENCE_NODE_ID);
		
		ShortestPath shortestPath = new ShortestPath(new PriorityQueueDijkstraIterator.Factory());
		double distance = shortestPath.bidirectionalShortestPathLength(graph, source, target);
		System.out.println("Distance : " + distance);
		
	}

}
