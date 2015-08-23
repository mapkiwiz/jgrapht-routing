package fr.gouv.agriculture.graph;

import java.util.ArrayList;
import java.util.List;

public class Path<V> {
	
	public final List<PathElement<V>> elements;
	
	public Path() {
		this.elements = new ArrayList<PathElement<V>>();
	}
	
	public Path(List<PathElement<V>> elements) {
		this.elements = elements;
	}
	
	public double getTotalDistance() {
		
		double distance = 0.0;
		for (PathElement<V> element : elements) {
			distance += element.distance;
		}
		
		return distance;
		
	}
	
	public double getTotalWeight() {
		
		double weight = 0.0;
		for (PathElement<V> element : elements) {
			weight += element.weight;
		}
		
		return weight;
		
	}
	
	public List<V> getNodeList() {
		
		List<V> nodes = new ArrayList<V>();
		
		for (PathElement<V> element : elements) {
			nodes.add(element.node);
		}
		
		return nodes;
		
	}

}
