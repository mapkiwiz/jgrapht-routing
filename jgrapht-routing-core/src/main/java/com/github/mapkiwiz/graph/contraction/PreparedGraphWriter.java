package com.github.mapkiwiz.graph.contraction;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class PreparedGraphWriter {
	
	private CSVFormat format = CSVFormat.newFormat('\t').withRecordSeparator('\n');
	
	public void writeToDisk(PreparedGraph graph, String basename) throws IOException {
		
		writeNodesToFile(graph, basename + ".nodes.tsv");
		writeEdgesToFile(graph, basename + ".edges.tsv");
		
	}
	
	public void writeNodesToFile(PreparedGraph graph, String filename) throws IOException {
	
		CSVPrinter printer = new CSVPrinter(new FileWriter(filename), format);
		printer.printRecord("ID", "LON", "LAT", "LEVEL");
		
		for (PreparedNode node : graph.vertexSet()) {
			printer.printRecord(node.id, node.lon, node.lat, node.level);
		}
		
		printer.close();
		
	}
	
	public void writeEdgesToFile(PreparedGraph graph, String filename) throws IOException {
		
		CSVPrinter printer = new CSVPrinter(new FileWriter(filename), format);
		printer.printRecord("SOURCE", "TARGET", "WEIGHT", "FLAGS");
		
		for (PreparedEdge edge : graph.edgeSet()) {
			int flags = getEdgeFlags(edge);
			printer.printRecord(edge.source.id, edge.target.id, edge.weight, flags);
		}
		
		printer.close();
		
	}
	
	public int getEdgeFlags(PreparedEdge edge) {
		
		int flags = 0;
		
		if (edge.shortcut) {
			flags |= (1 << 0);
		}
		
		switch (edge.direction) {
		case FORWARD:
			flags |= (1 << 1);
			break;
			
		case REVERSE:
			flags |= (1 << 2);
			break;
			
		case BIDIRECTIONAL:
			flags |= (1 << 3);
			break;

		default:
			break;
		}
		
		return flags;
	
	}

}
