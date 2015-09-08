package com.github.mapkiwiz.graph.contraction;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.csv.CSVRecord;
import org.jgrapht.Graph;

import com.github.mapkiwiz.graph.loader.CSVEdgeListGraphLoaderBase;

public class CSVPreparedGraphLoader extends CSVEdgeListGraphLoaderBase<PreparedNode, PreparedEdge, Long> {

	private boolean contracted;
	private Map<Integer, PreparedEdge> edges = new HashMap<Integer, PreparedEdge>();
	private URL shortcutFileURL;
	
	public CSVPreparedGraphLoader(String nodeFilename, String edgeFilename, boolean contracted)
			throws MalformedURLException {
		super(nodeFilename, edgeFilename);
		this.contracted = contracted;
	}

	public CSVPreparedGraphLoader(URL nodeFileURL, URL edgeFileURL,
			char delimiter, boolean contracted) {
		super(nodeFileURL, edgeFileURL, delimiter);
		this.contracted = contracted;
	}

	public CSVPreparedGraphLoader(URL nodeFileURL, URL edgeFileURL, boolean contracted) {
		super(nodeFileURL, edgeFileURL);
		this.contracted = contracted;
	}
	
	public CSVPreparedGraphLoader(URL nodeFileURL, URL edgeFileURL, URL shortcutFileURL) {
		super(nodeFileURL, edgeFileURL);
		this.shortcutFileURL = shortcutFileURL;
		this.contracted = true;
	}
	
	@Override
	public PreparedGraph loadGraph() throws IOException {
		PreparedGraph graph = (PreparedGraph) super.loadGraph();
		if (contracted && shortcutFileExists()) {
			loadShortcutData(graph, shortcutFileURL);
		}
		graph.contracted = contracted;
		return graph;
	}
	
	private boolean shortcutFileExists() {
		
		if (shortcutFileURL == null) {
			return false;
		}
		
		File shortcutFile = new File(shortcutFileURL.getFile());
		return shortcutFile.exists();
		
	}
	
	protected void loadShortcutData(PreparedGraph graph, URL shortcutFileURL) throws IOException {
		
		Reader reader = getReader(shortcutFileURL);
		Iterable<CSVRecord> records = format.withHeader(getShortcutHeader()).parse(reader);
		
		for (CSVRecord record : records) {
			
			int id = Integer.parseInt(record.get("ID"));
			long viaNode = Long.parseLong(record.get("VIANODE"));
			int inEdge = Integer.parseInt(record.get("INEDGE"));
			int outEdge = Integer.parseInt(record.get("OUTEDGE"));
			
			PreparedEdge edge = edges.get(id);
			edge.data.shortcut = true;
			edge.data.viaNode = nodeMap.get(viaNode);
			edge.data.inEdge = edges.get(inEdge);
			edge.data.outEdge = edges.get(outEdge);
			
		}
		
	}

	@Override
	protected PreparedNode asNode(CSVRecord record) {
		
		int id = Integer.parseInt(record.get("ID"));
		double lon = Double.parseDouble(record.get("LON")) / coordinate_precision;
		double lat = Double.parseDouble(record.get("LAT")) / coordinate_precision;
		PreparedNode node = new PreparedNode(id, lon, lat);
		
		if (contracted) {
			int level = Integer.parseInt(record.get("LEVEL"));
			node.level = level;
		}
		
		return node;
		
	}

	@Override
	protected Graph<PreparedNode, PreparedEdge> createNewGraph() {
		return new PreparedGraph();
	}

	@Override
	protected Long getNodeId(PreparedNode node) {
		return node.id;
	}

	@Override
	public void addEdge(Graph<PreparedNode, PreparedEdge> graph,
			PreparedNode source, PreparedNode target, EdgeData data) {
		
		PreparedEdge edge;
		if (contracted) {
			edge = new PreparedEdge(data.id, source, target, data.weight);
		} else {
			edge = new PreparedEdge(source, target, data.weight);
		}
		
		graph.addEdge(source, target, edge);
		edges.put(edge.id, edge);
		
	}
	
	@Override
	protected String[] getNodeHeader() {
		
		if (contracted) {
			return new String[] { "ID", "LON", "LAT", "LEVEL" };
		} else {
			return super.getNodeHeader();
		}
		
	}

	@Override
	protected String[] getEdgeHeader() {
		
		if (contracted) {
			return new String[] { "ID", "SOURCE", "TARGET", "WEIGHT", "FLAGS" };
		} else {
			return super.getEdgeHeader();
		}
		
	}
	
	protected String[] getShortcutHeader() {
		
		return new String[] { "ID", "VIANODE", "INEDGE", "OUTEDGE" };
		
	}

	@Override
	protected EdgeData asEdgeData(CSVRecord record) {
		
		EdgeData data = super.asEdgeData(record);
		
		if (contracted) {
			data.id = Integer.parseInt(record.get("ID"));
		}
		
		return data;
		
	}

}
