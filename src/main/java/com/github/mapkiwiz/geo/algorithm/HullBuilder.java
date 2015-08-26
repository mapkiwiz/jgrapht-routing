package com.github.mapkiwiz.geo.algorithm;

import java.util.List;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geojson.Polygon;


public interface HullBuilder {
	
	public Polygon buildHull(List<Node> nodes);

}
