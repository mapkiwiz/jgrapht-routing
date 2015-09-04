package com.github.mapkiwiz.graph.contraction;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URL;

import org.junit.Test;


public class GraphContractionTest {
	
	public PreparedGraph loadLargeGraph() throws IOException {
		
		URL node_file = getClass().getClassLoader().getResource("large.nodes.tsv.gz");
		URL edge_file = getClass().getClassLoader().getResource("large.edges.tsv.gz");
		CSVPreparedGraphLoader loader = new CSVPreparedGraphLoader(node_file, edge_file);
		return loader.loadGraph();
		
	}
	
	//@Test
	public void testLoadPreparedGraph() throws IOException {
		
		PreparedGraph graph = loadLargeGraph();
		assertNotNull(graph);
		
	}
	
	//@Test
	public void testContractLargeGraph() throws IOException {
		
		PreparedGraph graph = loadLargeGraph();
		assertNotNull(graph);
		
		GraphContractor contractor = new GraphContractor();
		
		long startTime = System.currentTimeMillis();
		contractor.contract(graph);
		long duration = System.currentTimeMillis() - startTime;
		
		System.out.println("Execution time : " + (duration / 1000.0) + " s.");
		
		PreparedGraphWriter writer = new PreparedGraphWriter();
		writer.writeToDisk(graph, "/tmp/rhone-alpes.prepared");
		
	}

}
