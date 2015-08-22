package fr.gouv.agriculture.geojson;

import java.util.HashMap;
import java.util.Map;

public class Feature<G extends Geometry<?>> extends GeoJsonObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -5277119639886605198L;
	
	public final String type = "Feature";
	public Map<String, Object> properties = new HashMap<String, Object>();
	public G geometry = null;

}
