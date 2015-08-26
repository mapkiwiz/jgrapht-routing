package com.github.mapkiwiz.graph;

import static org.junit.Assert.*;

import java.io.IOException;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.Test;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.loader.CSVEdgeListGraphLoader;


public class CSVEdgeListGraphLoaderTest extends AbstractGraphTest {

	
	@Test
	public void testLoadGraph() throws IOException {
		
		String node_file = getClass().getClassLoader().getResource("test.nodes.tsv").getPath();
		String edge_file = getClass().getClassLoader().getResource("test.edges.tsv").getPath();

		CSVEdgeListGraphLoader graphLoader = new CSVEdgeListGraphLoader(node_file, edge_file);
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph();
		int numberOfNodes = graph.vertexSet().size();
		int numberOfEdges = graph.edgeSet().size();
		
		System.out.println("Loaded " + numberOfNodes + " nodes.");
		System.out.println("Loaded " + numberOfEdges + " edges.");
		System.out.println("Loading took " + graphLoader.getLoadingTimeSeconds() + " s.");
		
		assertEquals(900, numberOfNodes);
		assertEquals(2581, numberOfEdges);
		
	}
	
	@Test
	public void testLoadGraphGZip() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		CSVEdgeListGraphLoader graphLoader = new CSVEdgeListGraphLoader(nodeFile, edgeFile);
		Graph<Node, DefaultWeightedEdge> graph = graphLoader.loadGraph();
		
		int numberOfNodes = graph.vertexSet().size();
		int numberOfEdges = graph.edgeSet().size();
		
		System.out.println("Loaded " + numberOfNodes + " nodes.");
		System.out.println("Loaded " + numberOfEdges + " edges.");
		System.out.println("Loading took " + graphLoader.getLoadingTimeSeconds() + " s.");
		
		assertEquals(280305, numberOfNodes);
		assertEquals(307083, numberOfEdges);
		
	}

}
