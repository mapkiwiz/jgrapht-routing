package fr.gouv.agriculture.graph;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Node implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2627482846992484129L;
	
	public final Long id;
	public final double lon;
	public final double lat;
	
	public Node(long id, double lon, double lat) {
		this.id = id;
		this.lon = lon;
		this.lat = lat;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
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
