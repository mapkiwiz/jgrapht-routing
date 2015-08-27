package com.github.mapkiwiz.graph.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Map;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import com.github.mapkiwiz.geo.Node;


public class CSVEdgeListGraphLoader extends AbstractEdgeListGraphLoader {
	
	private final URL edgeFileURL;
	private final URL nodeFileURL;
	private final CSVFormat format;
	
	public CSVEdgeListGraphLoader(String nodeFilename, String edgeFilename) throws MalformedURLException {
		this(new URL("file://" + nodeFilename), new URL("file://" + edgeFilename), '\t');
	}
	
	public CSVEdgeListGraphLoader(URL nodeFileURL, URL edgeFileURL) {
		this(nodeFileURL, edgeFileURL, '\t');
	}
	
	public CSVEdgeListGraphLoader(URL nodeFileURL, URL edgeFileURL, char delimiter) {
		assert(nodeFileURL != null && edgeFileURL != null);
		this.nodeFileURL = nodeFileURL;
		this.edgeFileURL = edgeFileURL;
		this.format = CSVFormat.newFormat(delimiter).withSkipHeaderRecord(true);
	}
	
	private Reader getReader(URL url) throws IOException {
		if (url.getFile().endsWith(".gz")) {
			InputStream in = url.openStream();
			GZIPInputStream gis = new GZIPInputStream(in);
			return new InputStreamReader(gis);
		} else {
			return new InputStreamReader(url.openStream());
		}
	}
	
	public Iterator<Node> getNodeIterator() throws IOException {
		
		Reader reader = getReader(nodeFileURL);
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
		
		Reader reader = getReader(edgeFileURL);
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
