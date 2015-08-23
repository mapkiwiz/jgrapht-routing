package fr.gouv.agriculture.graph.utils;

import java.util.ArrayList;
import java.util.List;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.Node;

public class NodeUtils {

	public static double length(List<Node> ring) {

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
	
	public static Polygon asPolygon(List<Node> nodes) {
		
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

}
