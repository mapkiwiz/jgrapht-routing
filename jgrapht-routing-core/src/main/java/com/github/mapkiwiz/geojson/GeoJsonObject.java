package com.github.mapkiwiz.geojson;

import java.io.Serializable;

import com.google.gson.GsonBuilder;

public abstract class GeoJsonObject implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -2550801215786364861L;

	public String toGeoJSON() {
		GsonBuilder gson = new GsonBuilder();
		return gson.create().toJson(this);
	}
	
	public static GeoJsonObject parse(String json) throws GeoJsonFormatException {
		
		GeoJsonParser parser = new GeoJsonParser();
		return parser.parse(json);
		
	}

}
