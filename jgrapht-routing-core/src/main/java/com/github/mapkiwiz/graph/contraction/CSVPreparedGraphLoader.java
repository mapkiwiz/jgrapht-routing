package com.github.mapkiwiz.graph.contraction;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.csv.CSVRecord;
import org.jgrapht.Graph;

import com.github.mapkiwiz.graph.loader.CSVEdgeListGraphLoaderBase;

public class CSVPreparedGraphLoader extends CSVEdgeListGraphLoaderBase<PreparedNode, PreparedEdge, Long> {

	public CSVPreparedGraphLoader(String nodeFilename, String edgeFilename)
			throws MalformedURLException {
		super(nodeFilename, edgeFilename);
	}

	public CSVPreparedGraphLoader(URL nodeFileURL, URL edgeFileURL,
			char delimiter) {
		super(nodeFileURL, edgeFileURL, delimiter);
	}

	public CSVPreparedGraphLoader(URL nodeFileURL, URL edgeFileURL) {
		super(nodeFileURL, edgeFileURL);
	}
	
	@Override
	public PreparedGraph loadGraph() throws IOException {
		return (PreparedGraph) super.loadGraph();
	}

	@Override
	protected PreparedNode asNode(CSVRecord record) {
		int id = Integer.parseInt(record.get("ID"));
		double lon = Double.parseDouble(record.get("LON")); // / 1e6;
		double lat = Double.parseDouble(record.get("LAT")); // / 1e6;
		int level = Integer.parseInt(record.get("LEVEL"));
		PreparedNode node = new PreparedNode(id, lon, lat);
		node.level = level;
		return node;
	}

	@Override
	public Graph<PreparedNode, PreparedEdge> createNewGraph() {
		return new PreparedGraph();
	}

	@Override
	public Long getNodeId(PreparedNode node) {
		return node.id;
	}

	@Override
	public void addEdge(Graph<PreparedNode, PreparedEdge> graph,
			PreparedNode source, PreparedNode target, double weight) {
		
		PreparedEdge edge = new PreparedEdge(source, target, weight);
		graph.addEdge(source, target, edge);
		
	}

}
