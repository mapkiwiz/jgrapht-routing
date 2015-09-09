package com.github.mapkiwiz.graph.contraction;

import java.io.FileWriter;
import java.io.IOException;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class PreparedGraphWriter {
	
	private CSVFormat format = CSVFormat.newFormat('\t').withRecordSeparator('\n');
	private double coordinate_precision = 0.0;
	
	public void setCoordinatePrecision(int precision) {
		this.coordinate_precision = Math.pow(10, precision);
	}
	
	public void writeToDisk(PreparedGraph graph, String basename) throws IOException {
		
		writeNodesToFile(graph, basename + ".nodes.tsv");
		writeEdgesToFile(graph, basename + ".edges.tsv", basename + ".shortcuts.tsv");
		
	}
	
	public void writeNodesToFile(PreparedGraph graph, String filename) throws IOException {
	
		CSVPrinter printer = new CSVPrinter(new FileWriter(filename), format);
		
		try {
			
			printer.printRecord("ID", "LON", "LAT", "LEVEL");
			
			for (PreparedNode node : graph.vertexSet()) {
				if (coordinate_precision > 0) {
					int lon = (int) Math.round(node.lon * coordinate_precision);
					int lat = (int) Math.round(node.lat * coordinate_precision);
					printer.printRecord(node.id, lon, lat, node.level);
				} else {
					printer.printRecord(node.id, node.lon, node.lat, node.level);
				}
			}
			
		} finally {
			if (printer != null) printer.close();
		}
		
	}
	
	public void writeEdgesToFile(PreparedGraph graph, String filename, String shortcutFilename) throws IOException {
		
		CSVPrinter printer = new CSVPrinter(new FileWriter(filename), format);
		CSVPrinter shortcutPrinter = new CSVPrinter(new FileWriter(shortcutFilename), format);
		
		try {
			
			printer.printRecord("ID", "SOURCE", "TARGET", "WEIGHT", "FLAGS");
			shortcutPrinter.printRecord("ID", "VIANODE", "INEDGE", "OUTEDGE");

			for (PreparedEdge edge : graph.edgeSet()) {

				int flags = getEdgeFlags(edge.data);
				
				printer.printRecord(
						edge.id,
						edge.source.id,
						edge.target.id,
						edge.weight,
						flags);

				if (edge.data.shortcut) {
					shortcutPrinter.printRecord(
							edge.id,
							edge.data.viaNode.id,
							edge.data.inEdge.id,
							edge.data.outEdge.id);
				}
				
			}
		
		} finally {
			if (printer != null) printer.close();
			if (shortcutPrinter != null) shortcutPrinter.close();
		}
		
	}
	
	public int getEdgeFlags(PreparedEdgeData edgeData) {
		
		int flags = 0;
		
		if (edgeData.shortcut) {
			flags |= (1 << 0);
		}
		
		switch (edgeData.direction) {
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
