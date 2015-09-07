package com.github.mapkiwiz.routing.web.controller;

import org.jgrapht.Graph;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.contraction.PreparedGraph;

public class GraphHolder {
	
	private Graph<?,?> graph;
	private boolean prepared;
	
	public GraphHolder(Graph<?,?> graph) {
		this.graph = graph;
		this.prepared = (graph instanceof PreparedGraph) && ((PreparedGraph) graph).isContracted();
	}
	
	public Graph<?, ?> getGraph() {
		return graph;
	}
	
	@SuppressWarnings("unchecked")
	public <V extends Node> Graph<V, ?> getGraph(Class<V> nodeClass) {
		return (Graph<V, ?>) graph;
	}
	
	public boolean isPrepared() {
		return prepared;
	}

}
