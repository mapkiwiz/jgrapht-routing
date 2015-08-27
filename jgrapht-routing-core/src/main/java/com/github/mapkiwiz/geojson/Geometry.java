package com.github.mapkiwiz.geojson;


public abstract class Geometry<T> extends GeoJsonObject {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3956929515809059521L;
	
	public final String type;
	public T coordinates;
	
	protected Geometry(String type) {
		this.type = type;
	}

}
