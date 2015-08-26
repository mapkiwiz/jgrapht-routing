package com.github.mapkiwiz.geo.algorithm;

import java.util.ArrayList;
import java.util.List;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geojson.Polygon;
import com.github.mapkiwiz.index.quadtree.QuadTree;


public class ConcaveHullBuilder implements HullBuilder {
	
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

	public Polygon buildHull(List<Node> nodes) {
		
		if (nodes.size() <= 4) {
			return null;
		}
		
		long start = System.currentTimeMillis();
		
		QuadTree<Node> tree = new QuadTree<Node>(-180.0, -90.0, 180.0, 90.0);
		for (Node n : nodes) {
			tree.set(n.lon, n.lat, n);
		}
		
		duration_index = System.currentTimeMillis() - start;
		
		List<Node> convexHull = ConvexHullBuilder.convexHull(nodes);
		List<Node> concaveHull = convexHull;
		int i = 0;
		int numberOfNodes;
		int currentNumberOfNodes = concaveHull.size()-1;
		
		while (i++ < maxIteration && (numberOfNodes = concaveHull.size()) > currentNumberOfNodes) {
			currentNumberOfNodes = numberOfNodes;
			concaveHull = refine(concaveHull, tree);
		}
		
		duration_hull = System.currentTimeMillis() - start - duration_index;
//		System.out.println("Iterations : " + (i-1));
		
		return NodeUtils.asPolygon(concaveHull);
		
	}
	
	private List<Node> refine(List<Node> ring, QuadTree<Node> tree) {
		
		double convexHullLength = NodeUtils.length(ring);
		double maxSegmentLength = convexHullLength / Math.min(2 * ring.size(), 1000);
		double searchDistance = convexHullLength / ring.size() * 2;
//		System.out.println("Search distance : " + searchDistance);
		
		List<Node> result = new ArrayList<Node>();
		
		for (Double[] p : segmentize(ring, maxSegmentLength)) {
			Node nearest = tree.nearest(p[0], p[1], searchDistance);
			if (nearest != null) {
				result.add(nearest);
			}
			
		}
		
		result.add(ring.get(0));
		
		return result;
		
	}
	
	private List<Double[]> segmentize(List<Node> ring, double maxSegmentLength) {
		
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
