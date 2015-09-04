package com.github.mapkiwiz.graph.loader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.Graph;


public abstract class AbstractEdgeListGraphLoader<V, E, VID> {
	
	long loadingTime = 0;
	Map<VID, V> nodeMap = Collections.emptyMap();
	
	static class EdgeData {
		
		Long source;
		Long target;
		double weight;
		Object data;
		
	}
	
	public abstract Iterator<V> getNodeIterator() throws IOException;
	
	public abstract Iterator<EdgeData> getEdgeIterator() throws IOException;
	
	public abstract Graph<V, E> createNewGraph();
	
	public abstract VID getNodeId(V node);
	
	public abstract void addEdge(Graph<V, E> graph, V source, V target, double weight);
	
	public Graph<V, E> loadGraph()
			throws IOException {
				
		long start = System.currentTimeMillis();
		
		Graph<V, E> graph = createNewGraph();
		this.nodeMap = new HashMap<VID, V>();
		
		for (Iterator<V> nodeIterator = getNodeIterator(); nodeIterator.hasNext();) {
			
			V node = nodeIterator.next();
			this.nodeMap.put(getNodeId(node), node);
			graph.addVertex(node);
			
		}
		
		for (Iterator<EdgeData> edgeIterator = getEdgeIterator(); edgeIterator.hasNext();) {
			
			EdgeData edgeData = edgeIterator.next();
			V sourceNode = this.nodeMap.get(edgeData.source);
			V targetNode = this.nodeMap.get(edgeData.target);
			addEdge(graph, sourceNode, targetNode, edgeData.weight);
//			DefaultWeightedEdge edge = graph.addEdge(sourceNode, targetNode);
//			graph.setEdgeWeight(edge, edgeData.weight);
			
		}
		
		this.loadingTime = System.currentTimeMillis() - start;
		
		return graph;
		
	}
	
	public double getLoadingTimeSeconds() {
		return this.loadingTime / 1000.0;
	}
	
	public V getNode(VID id) {
		
		return this.nodeMap.get(id);
		
	}
	
	public Map<VID, V> getNodeMap() {
		return nodeMap;
	}

}
