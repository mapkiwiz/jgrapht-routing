package fr.gouv.agriculture.hull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.Node;

public class ConvexHullBuilder implements HullBuilder {
	
	public static List<Node> convexHull(List<Node> nodes) {
		
		assert(nodes.size() > 0);
		
		if (nodes.size() <= 3) {
			return new ArrayList<Node>(nodes);
		}
		
		Collections.sort(nodes, new ConvexHullBuilder.NodeComparator());
		Node[] hull = new Node[2 * nodes.size()];
		
		// lower hull
		int k = 0;
		for (Node n : nodes) {
			
			while (k >= 2 && cross(hull[k-2], hull[k-1], n) <= 0) {
				k--;
			}
			hull[k++] = n;
			
		}
		
		// upper hull
		for (int i=nodes.size()-2, t = k + 1; i >= 0; i--) {
			
			Node n = nodes.get(i);
			while (k >= t && cross(hull[k-2], hull[k-1], n) <= 0) {
				k--;
			}
			hull[k++] = n;
			
		}
		
		if (k > 1) {
			List<Node> results = new ArrayList<Node>();
			for (int i=0; i<k; i++) { // i<k : include origin twice to form ring
				results.add(hull[i]);
			}
			return results;
		}
		
		return Collections.emptyList();
		
	}
	
	protected static double cross(Node o, Node a, Node b) {
		double ax = a.lon - o.lon;
		double ay = a.lat - o.lat;
		double bx = b.lon - o.lon;
		double by = b.lat - o.lat;
		return (ax * by - ay * bx);
	}
	
	public static class NodeComparator implements Comparator<Node> {

		public int compare(Node o1, Node o2) {
			
			if (o1.lon == o2.lon) {
				if (o1.lat > o2.lat) {
					return 1;
				} else if (o1.lat == o2.lat) {
					return 0;
				} else {
					return -1;
				}
			} else {
				if (o1.lon > o2.lon) {
					return 1;
				} else {
					return -1;
				}
			}
			
		}

	}

	public Polygon buildHull(List<Node> nodes) {
		
		List<Node> hullNodes = convexHull(nodes);
		List<List<Double>> exteriorRing = new ArrayList<List<Double>>();
		for (Node node : hullNodes) {
			exteriorRing.add(node.asCoordinatePair());
		}
		
		List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>();
		coordinates.add(exteriorRing);
		Polygon polygon = new Polygon();
		polygon.coordinates = coordinates;
		
		return polygon;
		
	}

}
