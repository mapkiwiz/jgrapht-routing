package fr.gouv.agriculture.geojson;

import java.util.Collections;
import java.util.List;

public class LineString extends Geometry<List<List<Double>>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7461640646062837068L;

	public LineString() {
		super("LineString");
		coordinates = Collections.emptyList();
	}

}
