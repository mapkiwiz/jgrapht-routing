package com.github.mapkiwiz.geo.algorithm;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mapkiwiz.geo.LexicographicNodeComparator;
import com.github.mapkiwiz.geo.Node;


public class ConvexHullBuilder<V extends Node> implements HullBuilder<V> {
	
	@SuppressWarnings("unchecked")
	public static <V extends Node> List<V> convexHull(List<V> nodes) {
		
		assert(nodes.size() > 0);
		
		if (nodes.size() <= 3) {
			return new ArrayList<V>(nodes);
		}
		
		Collections.sort(nodes, new LexicographicNodeComparator());
		Node[] hull = new Node[2 * nodes.size()];
		
		// lower hull
		int k = 0;
		for (V n : nodes) {
			
			while (k >= 2 && cross(hull[k-2], hull[k-1], n) <= 0) {
				k--;
			}
			hull[k++] = n;
			
		}
		
		// upper hull
		for (int i=nodes.size()-2, t = k + 1; i >= 0; i--) {
			
			V n = nodes.get(i);
			while (k >= t && cross(hull[k-2], hull[k-1], n) <= 0) {
				k--;
			}
			hull[k++] = n;
			
		}
		
		if (k > 1) {
			List<V> results = new ArrayList<V>();
			for (int i=0; i<k; i++) { // i<k : include origin twice to form ring
				results.add((V) hull[i]);
			}
			return results;
		}
		
		return Collections.emptyList();
		
	}
	
	private static double cross(Node o, Node a, Node b) {
		return o.cross(a, b);
	}

	public List<V> buildHull(List<V> nodes) {
		
		return convexHull(nodes);
		
	}

}
