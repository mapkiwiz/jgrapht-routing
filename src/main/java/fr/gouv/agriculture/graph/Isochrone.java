package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;

import fr.gouv.agriculture.hull.ConvexHullBuilder;

public class Isochrone {
	
	private final DijsktraIteratorFactory iteratorFactory;
	private final ShortestPath shortestPath;
	
	public Isochrone(DijsktraIteratorFactory factory) {
		this.iteratorFactory = factory;
		this.shortestPath = new ShortestPath(factory);
	}
	
	public <E> List<Node> isochrone(Graph<Node, E> graph, Node source, double distance) {
		
		assert(distance > 0);
		
		List<Node> nodes = shortestPath.searchByDistance(graph, source, distance);
		List<Node> convexHull = ConvexHullBuilder.convexHull(nodes);
		return convexHull;
		
	}
	
	public <E> List<Node> isochroneRaw(Graph<Node, E> graph, Node source, double distance) {
		
		assert(distance > 0);
		
		List<Node> nodes = shortestPath.searchByDistance(graph, source, distance);
		return nodes;
		
	}
	
	public <E> List<List<Node>> isochrones(Graph<Node, E> graph, Node source, double... distances) {
		
		for (double distance : distances) {
			assert(distance > 0);
		}
		
		Arrays.sort(distances);
		
		DijkstraIterator<Node> iterator =
				this.iteratorFactory.create(graph, source);
		
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

}
