package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.traverse.ClosestFirstIterator;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.hull.ConvexHullBuilder;

public class Isochrone {
	
	public static <E> List<Node> isochrone(Graph<Node, E> graph, Node source, double distance) {
		
		assert(distance > 0);
		
		List<Node> nodes = ShortestPath.searchByDistance(graph, source, distance);
		List<Node> convexHull = ConvexHullBuilder.convexHull(nodes);
		return convexHull;
		
	}
	
	public static <E> List<Node> isochroneRaw(Graph<Node, E> graph, Node source, double distance) {
		
		assert(distance > 0);
		
		List<Node> nodes = ShortestPath.searchByDistance(graph, source, distance);
		return nodes;
		
	}
	
	public static <E> List<List<Node>> isochrones(Graph<Node, E> graph, Node source, double... distances) {
		
		for (double distance : distances) {
			assert(distance > 0);
		}
		
		Arrays.sort(distances);
		
		ClosestFirstIterator<Node, E> iterator =
				new ClosestFirstIterator<Node, E>(graph, source);
		
		List<List<Node>> isochrones = new ArrayList<List<Node>>();
		Node currentNode = null;
		
		for (int k = 0; k < distances.length; k++) {
			
			double distance = distances[k];
			
			List<Node> isochrone = new ArrayList<Node>();
			if (currentNode != null) {
				isochrone.add(currentNode);
			}
			
			while (iterator.hasNext()) {
				
				currentNode = iterator.next();
				double currentDistance = iterator.getShortestPathLength(currentNode);
				
				if (currentDistance > distance) {
					break;
				}
				
				isochrone.add(currentNode);
				
			}
			
			isochrones.add(ConvexHullBuilder.convexHull(isochrone));
		
		}
		
		return isochrones;
		
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
