package fr.gouv.agriculture.geojson;

import java.util.Collections;
import java.util.List;

public class Polygon extends Geometry<List<List<List<Double>>>> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9139980808072579511L;

	public Polygon() {
		super("Polygon");
		coordinates = Collections.emptyList();
	}

}
