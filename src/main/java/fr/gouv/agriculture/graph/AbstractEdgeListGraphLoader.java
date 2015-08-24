package fr.gouv.agriculture.graph;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;

public abstract class AbstractEdgeListGraphLoader {
	
	long loadingTime = 0;
	Map<Long, Node> nodeMap = Collections.emptyMap();
	
	static class EdgeData {
		
		Long source;
		Long target;
		double weight;
		Object data;
		
	}
	
	public abstract Iterator<Node> getNodeIterator() throws IOException;
	
	public abstract Iterator<EdgeData> getEdgeIterator() throws IOException;
	
	public SimpleWeightedGraph<Node, DefaultWeightedEdge> loadGraph()
			throws IOException {
				
		long start = System.currentTimeMillis();
		
		SimpleWeightedGraph<Node, DefaultWeightedEdge> graph = new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
		this.nodeMap = new HashMap<Long, Node>();
		
		for (Iterator<Node> nodeIterator = getNodeIterator(); nodeIterator.hasNext();) {
			
			Node node = nodeIterator.next();
			this.nodeMap.put(node.id, node);
			graph.addVertex(node);
			
		}
		
		for (Iterator<EdgeData> edgeIterator = getEdgeIterator(); edgeIterator.hasNext();) {
			
			EdgeData edgeData = edgeIterator.next();
			Node sourceNode = this.nodeMap.get(edgeData.source);
			Node targetNode = this.nodeMap.get(edgeData.target);
			DefaultWeightedEdge edge = graph.addEdge(sourceNode, targetNode);
			graph.setEdgeWeight(edge, edgeData.weight);
			
		}
		
		this.loadingTime = System.currentTimeMillis() - start;
		
		return graph;
		
	}
	
	public double getLoadingTimeSeconds() {
		return this.loadingTime / 1000.0;
	}
	
	public Node getNode(Long id) {
		
		return this.nodeMap.get(id);
		
	}

}
