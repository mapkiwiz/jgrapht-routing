package com.github.mapkiwiz.graph.loader;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.zip.GZIPInputStream;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;


public abstract class CSVEdgeListGraphLoaderBase<V, E, VID> extends AbstractEdgeListGraphLoader<V, E, VID> {
	
	private final URL edgeFileURL;
	private final URL nodeFileURL;
	protected final CSVFormat format;
	protected double coordinate_precision = 1.0;
	
	public CSVEdgeListGraphLoaderBase(String nodeFilename, String edgeFilename) throws MalformedURLException {
		this(new URL("file://" + nodeFilename), new URL("file://" + edgeFilename), '\t');
	}
	
	public CSVEdgeListGraphLoaderBase(URL nodeFileURL, URL edgeFileURL) {
		this(nodeFileURL, edgeFileURL, '\t');
	}
	
	public CSVEdgeListGraphLoaderBase(URL nodeFileURL, URL edgeFileURL, char delimiter) {
		assert(nodeFileURL != null && edgeFileURL != null);
		this.nodeFileURL = nodeFileURL;
		this.edgeFileURL = edgeFileURL;
		this.format = CSVFormat.newFormat(delimiter).withSkipHeaderRecord(true);
	}
	
	public void setCoordinatePrecision(int precision) {
		this.coordinate_precision = Math.pow(10, precision);
	}
	
	protected Reader getReader(URL url) throws IOException {
		if (url.getFile().endsWith(".gz")) {
			InputStream in = url.openStream();
			GZIPInputStream gis = new GZIPInputStream(in);
			return new InputStreamReader(gis);
		} else {
			return new InputStreamReader(url.openStream());
		}
	}
	
	protected Iterator<V> getNodeIterator() throws IOException {
		
		Reader reader = getReader(nodeFileURL);
		Iterable<CSVRecord> records = format.withHeader(getNodeHeader()).parse(reader);
		final Iterator<CSVRecord> recordIterator = records.iterator();
		
		return new Iterator<V>() {

			public boolean hasNext() {
				return recordIterator.hasNext(); 
			}

			public V next() {
				return asNode(recordIterator.next());
			}

			public void remove() {
				throw new UnsupportedOperationException();
			}
			
		};
		
	}
	
	protected String[] getNodeHeader() {
		return new String[] { "ID", "LON", "LAT" };
	}
	
	protected String[] getEdgeHeader() {
		return new String[] { "SOURCE", "TARGET", "WEIGHT" };
	}
	
	protected Iterator<EdgeData> getEdgeIterator() throws IOException {
		
		Reader reader = getReader(edgeFileURL);
		Iterable<CSVRecord> records = format.withHeader(getEdgeHeader()).parse(reader);
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
	
	protected abstract V asNode(CSVRecord record);
	
	protected EdgeData asEdgeData(CSVRecord record) {
		
		EdgeData data = new EdgeData();
		data.source = Long.parseLong(record.get("SOURCE"));
		data.target = Long.parseLong(record.get("TARGET"));
		data.weight = Double.parseDouble(record.get("WEIGHT"));
		
		return data;
		
	}

}
