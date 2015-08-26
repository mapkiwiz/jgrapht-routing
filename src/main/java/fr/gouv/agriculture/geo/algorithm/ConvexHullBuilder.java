package fr.gouv.agriculture.geo.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.gouv.agriculture.geo.LexicographicNodeComparator;
import fr.gouv.agriculture.geo.Node;
import fr.gouv.agriculture.geo.NodeUtils;
import fr.gouv.agriculture.geojson.Polygon;

public class ConvexHullBuilder implements HullBuilder {
	
	public static List<Node> convexHull(List<Node> nodes) {
		
		assert(nodes.size() > 0);
		
		if (nodes.size() <= 3) {
			return new ArrayList<Node>(nodes);
		}
		
		Collections.sort(nodes, new LexicographicNodeComparator());
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
	
	private static double cross(Node o, Node a, Node b) {
		return o.cross(a, b);
	}

	public Polygon buildHull(List<Node> nodes) {
		
		List<Node> hullNodes = convexHull(nodes);
		return NodeUtils.asPolygon(hullNodes);
		
	}

}
