package com.github.mapkiwiz.graph;

import java.util.ArrayList;
import java.util.List;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.LineString;

public class PathUtils {
	
	public static Feature<LineString> toFeature(Path<Node> path) {
		
		List<List<Double>> coordinates = new ArrayList<List<Double>>();
		double distance = 0.0;
		double time = 0.0;
		
		for (PathElement<Node> segment : path.elements) {
			coordinates.add(segment.node.asCoordinatePair());
			distance += segment.distance;
			time += segment.weight;
		}
		
		LineString geometry = new LineString();
		geometry.coordinates = coordinates;
		Feature<LineString> feature = new Feature<LineString>();
		feature.geometry = geometry;
		feature.properties.put("source", path.elements.get(0).node.id);
		feature.properties.put("target", path.elements.get(path.elements.size() - 1 ).node.id);
		feature.properties.put("distance", distance);
		feature.properties.put("time", time);
		
		return feature;
		
	}

}
