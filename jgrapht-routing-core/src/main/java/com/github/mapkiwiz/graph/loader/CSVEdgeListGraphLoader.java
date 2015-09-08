package com.github.mapkiwiz.graph.loader;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.csv.CSVRecord;
import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

import com.github.mapkiwiz.geo.Node;


public class CSVEdgeListGraphLoader extends CSVEdgeListGraphLoaderBase<Node, DefaultWeightedEdge, Long> {

	public CSVEdgeListGraphLoader(String nodeFilename, String edgeFilename)
			throws MalformedURLException {
		
		super(nodeFilename, edgeFilename);
	
	}

	public CSVEdgeListGraphLoader(URL nodeFileURL, URL edgeFileURL,
			char delimiter) {
	
		super(nodeFileURL, edgeFileURL, delimiter);
	
	}

	public CSVEdgeListGraphLoader(URL nodeFileURL, URL edgeFileURL) {
	
		super(nodeFileURL, edgeFileURL);
		
	}

	@Override
	protected Graph<Node, DefaultWeightedEdge> createNewGraph() {
		return new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	@Override
	protected Long getNodeId(Node node) {
		return node.id;
	}

	@Override
	public void addEdge(Graph<Node, DefaultWeightedEdge> graph, Node source, Node target, EdgeData data) {
		
		DefaultWeightedEdge edge = graph.addEdge(source, target);
		((SimpleWeightedGraph<Node, DefaultWeightedEdge>) graph).setEdgeWeight(edge, data.weight);
		
	}

	protected Node asNode(CSVRecord record) {
		int id = Integer.parseInt(record.get("ID"));
		double lon = Double.parseDouble(record.get("LON")) / coordinate_precision;
		double lat = Double.parseDouble(record.get("LAT")) / coordinate_precision;
		return new Node(id, lon, lat);
	}

}
