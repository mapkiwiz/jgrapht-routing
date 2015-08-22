package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;

import fr.gouv.agriculture.geojson.Polygon;

public class Isochrone {
	
	public static <E> List<Node> isochrone(Graph<Node, E> graph, Node source, double distance) {
		
		assert(distance > 0);
		
		List<Node> nodes = ShortestPath.searchByDistance(graph, source, distance);
		List<Node> convexHull = ConvexHull.convexHull(nodes);
		return convexHull;
		
	}
	
	public static Polygon asPolygon(List<Node> isochrone) {
		
		Polygon geometry = new Polygon();
		List<List<List<Double>>> coordinates = new ArrayList<List<List<Double>>>();
		List<List<Double>> exteriorRing = new ArrayList<List<Double>>();
		for (Node n : isochrone) {
			exteriorRing.add(n.asCoordinatePair());
		}
		coordinates.add(exteriorRing);
		geometry.coordinates = coordinates;
		return geometry;
		
	}

}
