package fr.gouv.agriculture.graph;

import java.util.Iterator;

public interface DijkstraIterator<V> extends Iterator<V> {
	
	public V getParent(V vertex);
	
	public double getShortestPathLength(V vertex);
	
	public double getPathElementWeight(V vertex);
	
	public PathElement<V> getPathElement(V vertex);
	
	public Path<V> getPath(V vertex);

}
