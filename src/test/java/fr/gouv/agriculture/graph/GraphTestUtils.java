package fr.gouv.agriculture.graph;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public class GraphTestUtils {

	public static EdgeListGraphLoader getTestGraph() throws IOException {

		String node_file = GraphTestUtils.class.getClassLoader().getResource("test.nodes.tsv").getPath();
		String edge_file = GraphTestUtils.class.getClassLoader().getResource("test.edges.tsv").getPath();

		EdgeListGraphLoader graphLoader = new EdgeListGraphLoader();
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph(node_file, edge_file);

		assertNotNull(graph);

		return graphLoader;

	}

}
