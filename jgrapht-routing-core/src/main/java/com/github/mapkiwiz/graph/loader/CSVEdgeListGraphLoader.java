package com.github.mapkiwiz.graph.loader;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.github.mapkiwiz.geo.Node;


public class CSVEdgeListGraphLoader extends AbstractEdgeListGraphLoader {
	
	private final String edgeFilename;
	private final String nodeFilename;
	private final CSVFormat format;
	
	public CSVEdgeListGraphLoader(String nodeFilename, String edgeFilename) {
		this(nodeFilename, edgeFilename, '\t');
	}
	
	public CSVEdgeListGraphLoader(String nodeFilename, String edgeFilename, char delimiter) {
		assert(nodeFilename != null && edgeFilename != null);
		this.nodeFilename = nodeFilename;
		this.edgeFilename = edgeFilename;
		this.format = CSVFormat.newFormat(delimiter).withSkipHeaderRecord(true);
	}
	
	private Reader getReader(String filename) throws IOException {
		if (filename.endsWith(".gz")) {
			FileInputStream in = new FileInputStream(filename);
			GZIPInputStream gis = new GZIPInputStream(in);
			return new InputStreamReader(gis);
		} else {
			return new FileReader(filename);
		}
	}
	
	public Iterator<Node> getNodeIterator() throws IOException {
		
		Reader reader = getReader(nodeFilename);
		Iterable<CSVRecord> records = format.withHeader("ID", "LON", "LAT").parse(reader);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		
		return new Iterator<Node>() {

			public boolean hasNext() {
				return recordIterator.hasNext(); 
			}

			public Node next() {
				return asNode(recordIterator.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		
	}
	
	public Iterator<EdgeData> getEdgeIterator() throws IOException {
		
		Reader reader = getReader(edgeFilename);
		Iterable<CSVRecord> records = format.withHeader("SOURCE", "TARGET", "WEIGHT", "DATA").parse(reader);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		
		return new Iterator<EdgeData>() {

			public boolean hasNext() {
				return recordIterator.hasNext(); 
			}

			public EdgeData next() {
				return asEdgeData(recordIterator.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		
	}
	
	protected Node asNode(CSVRecord record) {
		int id = Integer.parseInt(record.get("ID"));
		double lon = Double.parseDouble(record.get("LON")) / 1e6;
		double lat = Double.parseDouble(record.get("LAT")) / 1e6;
		return new Node(id, lon, lat);
	}
	
	protected EdgeData asEdgeData(CSVRecord record) {
		
		EdgeData data = new EdgeData();
		data.source = Long.parseLong(record.get("SOURCE"));
		data.target = Long.parseLong(record.get("TARGET"));
		data.weight = Double.parseDouble(record.get("WEIGHT"));
		
		return data;
		
	}

	@Override
	public Map<Long, Node> getNodeMap() {
		return nodeMap;
	}

}
