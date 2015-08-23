package fr.gouv.agriculture.graph;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public class EdgeListGraphLoader {
	
	private Map<Integer, Node> nodeMap =
			new HashMap<Integer, Node>();
	private SimpleWeightedGraph<Node, DefaultWeightedEdge> graph =
			new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	private int edges = 0;
	private long loadingTime = 0;
	
	private Reader getReader(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			FileInputStream in = new FileInputStream(filename);
			GZIPInputStream gis = new GZIPInputStream(in);
			return new InputStreamReader(gis);
		} else {
			return new FileReader(filename);
		}
	}
	
	public SimpleWeightedGraph<Node, DefaultWeightedEdge> loadGraph(String nodeFile, String edgeFile)
			throws IOException {
				
		CSVFormat format = CSVFormat.newFormat('\t').withSkipHeaderRecord(true);
		long start = System.currentTimeMillis();
		
		Reader reader = getReader(nodeFile);
		Iterable<CSVRecord> records = format.withHeader("ID", "LON", "LAT").parse(reader);
		for (CSVRecord record : records) {
			Node node = asNode(record);
			nodeMap.put(node.id, node);
			graph.addVertex(node);
		}
		reader.close();
		
		reader = getReader(edgeFile);
		records = format.withHeader("SOURCE", "TARGET", "WEIGHT", "DATA").parse(reader);
		for (CSVRecord record : records) {
			int source = Integer.parseInt(record.get("SOURCE"));
			int target = Integer.parseInt(record.get("TARGET"));
			double weight = Double.parseDouble(record.get("WEIGHT"));
			Node sourceNode = nodeMap.get(source);
			Node targetNode = nodeMap.get(target);
			DefaultWeightedEdge edge = graph.addEdge(sourceNode, targetNode);
			graph.setEdgeWeight(edge, weight);
			edges++;
		}
		reader.close();
		
		this.loadingTime = System.currentTimeMillis() - start;
		return this.graph;
		
	}
	
	protected Node asNode(CSVRecord record) {
		int id = Integer.parseInt(record.get("ID"));
		double lon = Double.parseDouble(record.get("LON")) / 1e6;
		double lat = Double.parseDouble(record.get("LAT")) / 1e6;
		return new Node(id, lon, lat);
	}
	
	public Map<Integer, Node> getNodes() {
		return this.nodeMap;
	}
	
	public int getNumberOfNodes() {
		return this.nodeMap.size();
	}
	
	public int getNumberOfEdges() {
		return edges;
	}
	
	public SimpleWeightedGraph<Node, DefaultWeightedEdge> getGraph() {
		return this.graph;
	}
	
	public double getLoadingTimeSeconds() {
		return this.loadingTime / 1000.0;
	}

}
