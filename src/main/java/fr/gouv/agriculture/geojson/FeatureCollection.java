package fr.gouv.agriculture.geojson;

import java.util.Collections;
import java.util.List;

public class FeatureCollection<G extends Geometry<?>> extends GeoJsonObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6346865049909104273L;
	
	public final String type = "FeatureCollection";
	public List<Feature<G>> features = Collections.emptyList();

}
