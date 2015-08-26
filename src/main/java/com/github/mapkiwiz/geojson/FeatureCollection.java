package com.github.mapkiwiz.geojson;

import java.util.ArrayList;
import java.util.List;

public class FeatureCollection<G extends Geometry<?>> extends GeoJsonObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6346865049909104273L;
	
	public final String type = "FeatureCollection";
	public final List<Feature<G>> features;
	
	public FeatureCollection() {
		this.features = new ArrayList<Feature<G>>();
	}
	
	public FeatureCollection(List<Feature<G>> features) {
		this.features = features;
	}

}
