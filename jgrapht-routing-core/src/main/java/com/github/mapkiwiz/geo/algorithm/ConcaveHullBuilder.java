package com.github.mapkiwiz.geo.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.index.quadtree.QuadTree;


public class ConcaveHullBuilder<V extends Node> implements HullBuilder<V> {
	
	private long duration_index;
	private long duration_hull;
	private int maxIteration;
	
	public ConcaveHullBuilder() {
		this.maxIteration = 2;
	}
	
	public ConcaveHullBuilder(int maxIteration) {
		this.maxIteration = maxIteration;
	}
	
	public void setMaxIteration(int maxIteration) {
		this.maxIteration = maxIteration;
	}
	
	public int getMaxIteration() {
		return this.maxIteration;
	}

	public List<V> buildHull(List<V> nodes) {
		
		if (nodes.size() <= 4) {
			return null;
		}
		
		long start = System.currentTimeMillis();
		
		QuadTree<V> tree = new QuadTree<V>(-180.0, -90.0, 180.0, 90.0);
		for (V n : nodes) {
			tree.set(n.lon, n.lat, n);
		}
		
		duration_index = System.currentTimeMillis() - start;
		
		List<V> convexHull = ConvexHullBuilder.convexHull(nodes);
		List<V> concaveHull = convexHull;
		int i = 0;
		int numberOfNodes;
		int currentNumberOfNodes = concaveHull.size()-1;
		
		while (i++ < maxIteration && (numberOfNodes = concaveHull.size()) > currentNumberOfNodes) {
			currentNumberOfNodes = numberOfNodes;
			concaveHull = refine(concaveHull, tree);
		}
		
		duration_hull = System.currentTimeMillis() - start - duration_index;
//		System.out.println("Iterations : " + (i-1));
		
		return concaveHull;
		
	}
	
	private List<V> refine(List<V> ring, QuadTree<V> tree) {
		
		double convexHullLength = NodeUtils.length(ring);
		double maxSegmentLength = convexHullLength / Math.min(2 * ring.size(), 1000);
		double searchDistance = convexHullLength / ring.size() * 2;
//		System.out.println("Search distance : " + searchDistance);
		
		List<V> result = new ArrayList<V>();
		
		for (Double[] p : segmentize(ring, maxSegmentLength)) {
			V nearest = tree.nearest(p[0], p[1], searchDistance);
			if (nearest != null) {
				result.add(nearest);
			}
			
		}
		
		result.add(ring.get(0));
		
		return result;
		
	}
	
	private List<Double[]> segmentize(List<V> ring, double maxSegmentLength) {
		
//		System.out.println("Segmentize input size = " + ring.size());
		List<Double[]> points = new ArrayList<Double[]>();
		
		for (int i=0; i<ring.size()-1; i++) {
			Node p1 = ring.get(i);
			Node p2 = ring.get(i+1);
			double length = NodeUtils.length(p1, p2);
			if (length == 0.0) {
				points.add(new Double[] { p1.lon, p1.lat });
				continue;
			}
			double x0 = p1.lon;
			double y0 = p1.lat;
			double a = p2.lon - x0;
			double b = p2.lat - y0;
			int k = 0;
			double j;
			while ((j = (k*maxSegmentLength / length)) < 1) {
				double x = x0 + j * a;
				double y = y0 + j * b;
//				System.out.println("(" + x + "," + y + ")");
				points.add(new Double[] {x, y});
				k++;
			}
		}
		
//		System.out.println("Segmentized ring size = " + points.size());
		return points;
		
	}
	
	public double getHullDurationSeconds() {
		return (duration_hull / 1000.0);
	}
	
	public double getIndexDurationSeconds() {
		return (duration_index / 1000.0);
	}

}
