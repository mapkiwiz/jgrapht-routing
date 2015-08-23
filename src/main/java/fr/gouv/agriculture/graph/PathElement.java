package fr.gouv.agriculture.graph;

public class PathElement<V> {
	
	public final V node;
	public final double distance;
	public final double weight;
	
	public PathElement(V node, double distance, double weight) {
		this.node = node;
		this.distance = distance;
		this.weight = weight;
	}

}
