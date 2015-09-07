package com.github.mapkiwiz.geo;

import java.util.ArrayList;
import java.util.List;

import com.github.mapkiwiz.geojson.Polygon;


public class NodeUtils {
	
	/**
     * mean radius of the earth
     */
    public final static double R = 6371000; // m

	public static <V extends Node> double length(List<V> ring) {

		double length = 0.0;
		for (int i=0; i<ring.size()-1; i++) {
			Node p1 = ring.get(i);
			Node p2 = ring.get(i+1);
			length += length(p1, p2);
		}

		return length;

	}

	public static double length(Node p1, Node p2) {
		return Math.sqrt(
				Math.pow(p2.lon - p2.lon, 2) +
				Math.pow(p2.lat - p1.lat, 2));
	}
	
	public static <V extends Node> Polygon asPolygon(List<V> nodes) {
		
		assert(nodes.get(0).equals(nodes.get(nodes.size() -1)));
		
		List<List<Double>> exteriorRing = new ArrayList<List<Double>>();
		for (Node node : nodes) {
			exteriorRing.add(node.asCoordinatePair());
		}
		
		List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>();
		coordinates.add(exteriorRing);
		Polygon polygon = new Polygon();
		polygon.coordinates = coordinates;
		
		return polygon;
		
	}
    
    public static <V extends Node> double sphericalDistance(V a, V b) {
    	
    	double sinDeltaLat = Math.sin(Math.toRadians(b.lat - a.lat) / 2);
        double sinDeltaLon = Math.sin(Math.toRadians(b.lon - a.lon) / 2);
        double normedDist =
        		sinDeltaLat * sinDeltaLat
                + sinDeltaLon * sinDeltaLon * Math.cos(Math.toRadians(a.lat)) * Math.cos(Math.toRadians(b.lat));
        
        return R * 2 * Math.asin(Math.sqrt(normedDist));
        
    }

}
