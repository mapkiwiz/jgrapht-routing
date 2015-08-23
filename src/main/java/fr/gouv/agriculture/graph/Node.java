package fr.gouv.agriculture.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2627482846992484129L;
	
	public final int id;
	public final double lon;
	public final double lat;
	
	public Node(int id, double lon, double lat) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + id;
		return result;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Node other = (Node) obj;
		if (id != other.id)
			return false;
		return true;
	}
	
	public List<Double> asCoordinatePair() {
		
		List<Double> pair = new ArrayList<Double>();
		pair.add(lon);
		pair.add(lat);
		return pair;
		
	}
	
	public String toString() {
		return "ID: " + id + " (" + lon + "," + lat + ")";
	}

}
