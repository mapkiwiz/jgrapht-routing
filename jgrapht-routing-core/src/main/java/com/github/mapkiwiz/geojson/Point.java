package com.github.mapkiwiz.geojson;

import java.util.Collections;
import java.util.List;

public class Point extends Geometry<List<Double>> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5222329032002293908L;

	public Point() {
		super("Point");
		coordinates = Collections.emptyList();
	}
	
}
