package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jgrapht.Graph;

public class Isochrone {
	
	private final DijsktraIteratorFactory iteratorFactory;
	private final ShortestPath shortestPath;
	
	public Isochrone(DijsktraIteratorFactory factory) {
		this.iteratorFactory = factory;
		this.shortestPath = new ShortestPath(factory);
	}
	
	public <E,V> List<V> isochrone(Graph<V, E> graph, V source, double distance) {
		
		assert(distance > 0);
		
		List<V> nodes = shortestPath.searchByDistance(graph, source, distance);
		return nodes;
		
	}
	
	public <E, V> List<List<V>> isochrones(Graph<V, E> graph, V source, double... distances) {
		
		for (double distance : distances) {
			assert(distance > 0);
		}
		
		Arrays.sort(distances);
		
		DijkstraIterator<V> iterator =
				this.iteratorFactory.create(graph, source);
		
		List<List<V>> isochrones = new ArrayList<List<V>>();
		V currentNode = null;
		
		for (int k = 0; k < distances.length; k++) {
			
			double distance = distances[k];
			
			List<V> isochrone = new ArrayList<V>();
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
			
			isochrones.add(isochrone);
		
		}
		
		return isochrones;
		
	}

}
