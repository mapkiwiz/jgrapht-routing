package com.github.mapkiwiz.graph.loader;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.jgrapht.Graph;


public abstract class AbstractEdgeListGraphLoader<V, E, VID> {
	
	long loadingTime = 0;
	protected Map<VID, V> nodeMap = Collections.emptyMap();
	
	protected static class EdgeData {
		
		public int id;
		public Long source;
		public Long target;
		public double weight;
		public Object data;
		
	}
	
	protected abstract Iterator<V> getNodeIterator() throws IOException;
	
	protected abstract Iterator<EdgeData> getEdgeIterator() throws IOException;
	
	protected abstract Graph<V, E> createNewGraph();
	
	protected abstract VID getNodeId(V node);
	
	public abstract void addEdge(Graph<V, E> graph, V source, V target, EdgeData data);
	
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
			addEdge(graph, sourceNode, targetNode, edgeData);
			
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
