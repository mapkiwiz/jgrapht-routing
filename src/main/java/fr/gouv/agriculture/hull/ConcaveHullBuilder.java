package fr.gouv.agriculture.hull;

import java.util.ArrayList;
import java.util.List;

import com.github.davidmoten.rtree.Entry;
import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Point;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.graph.utils.NodeUtils;

public class ConcaveHullBuilder implements HullBuilder {
	
	private long duration_index;
	private long duration_hull;

	public Polygon buildHull(List<Node> nodes) {
		
		if (nodes.size() <= 4) {
			return null;
		}
		
		long start = System.currentTimeMillis();
		
		RTree<Node, Point> rtree = RTree.create(); //.minChildren(3).maxChildren(6).create();
		for (Node node : nodes) {
			Point p = Point.create(node.lon, node.lat);
			rtree = rtree.add(node, p);
		}
		
		duration_index = System.currentTimeMillis() - start;
		
		List<Node> result = new ArrayList<Node>();
		List<Node> convexHull = ConvexHullBuilder.convexHull(nodes);
		double convexHullLength = NodeUtils.length(convexHull);
		double maxSegmentLength = convexHullLength / Math.min(2 * convexHull.size(), 1000);
		double searchDistance = convexHullLength / Math.PI / 4;
		
		for (Double[] p : segmentize(convexHull, maxSegmentLength)) {
			List<Entry<Node, Point>> r = rtree.nearest(Point.create(p[0], p[1]), searchDistance, 1)
					.toList()
					.toBlocking()
					.single();
			if (!r.isEmpty()) {
				Node n = r.get(0).value();
				result.add(n);
			}
			
		}
		
		result.add(convexHull.get(0));
		
		duration_hull = System.currentTimeMillis() - start - duration_index;
		
		return NodeUtils.asPolygon(result);
		
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
