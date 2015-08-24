package fr.gouv.agriculture.graph;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;

public abstract class AbstractGraphTest {
	
	public static final Long LYON_NODE_ID = 24951L;
	public static final Long GRENOBLE_NODE_ID = 241847L;
	public static final Long VALENCE_NODE_ID = 256987L;
	public static final Long ANNEMASSE_NODE_ID = 63769L;
	public static final Long SMALL_GRAPH_ORIGIN_NODE_ID = 0L;
	public static final Long SMALL_GRAPH_TOPRIGHT_NODE_ID = 899L;
	
	Map<Long, Node> nodeMap = Collections.emptyMap();
	
	public Graph<Node, DefaultWeightedEdge> loadSmallGraph() {
		
		String node_file = getClass().getClassLoader().getResource("test.nodes.tsv").getPath();
		String edge_file = getClass().getClassLoader().getResource("test.edges.tsv").getPath();

		CSVEdgeListGraphLoader graphLoader = new CSVEdgeListGraphLoader(node_file, edge_file);
		Graph<Node, DefaultWeightedEdge> graph;
		
		try {
			graph = graphLoader.loadGraph();
			nodeMap = graphLoader.nodeMap;
		} catch (IOException e) {
			fail(e.getMessage());
			return null;
		}

		assertNotNull(graph);

		return graph;
		
	}
	
	public Graph<Node, DefaultWeightedEdge> loadLargeGraph() {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		CSVEdgeListGraphLoader graphLoader = new CSVEdgeListGraphLoader(nodeFile, edgeFile);
		Graph<Node, DefaultWeightedEdge> graph;
		
		try {
			graph = graphLoader.loadGraph();
			nodeMap = graphLoader.nodeMap;
		} catch (IOException e) {
			fail(e.getMessage());
			return null;
		}
		
		assertNotNull(graph);
		
		return graph;
		
	}
	
	public Node getNode(Long id) {
		
		return nodeMap.get(id);
	
	}

}
