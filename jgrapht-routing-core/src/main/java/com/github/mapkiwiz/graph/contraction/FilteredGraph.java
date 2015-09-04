package com.github.mapkiwiz.graph.contraction;

import java.util.HashSet;
import java.util.Set;

import org.jgrapht.graph.GraphDelegator;

/* package */ class FilteredGraph extends GraphDelegator<PreparedNode, PreparedEdge> {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8145224603088557653L;
	
	private int minLevel = Integer.MIN_VALUE;
	private PreparedNode removedNode;
	private boolean noShortcuts = false;

	public FilteredGraph(PreparedGraph graph) {
		super(graph);
	}
	
	@Override
	public Set<PreparedEdge> edgesOf(PreparedNode vertex) {
	
		Set<PreparedEdge> edges = new HashSet<PreparedEdge>();
		for (PreparedEdge edge : super.edgesOf(vertex)) {
			if (!isFilteredOut(edge)) {
				edges.add(edge);
			}
		}
		return edges;
	
	}
	
	protected boolean isFilteredOut(PreparedEdge edge) {
		
		return (edge.level < minLevel) ||
			   (noShortcuts && edge.shortcut) ||
			   edge.source.equals(removedNode) ||
			   edge.target.equals(removedNode);
		
	}
	
	public FilteredGraph minLevel(int minLevel) {
		this.minLevel = minLevel;
		return this;
	}
	
	public FilteredGraph ignore(PreparedNode node) {
		this.removedNode = node;
		return this;
	}
	
	public FilteredGraph shortcuts(boolean on) {
		this.noShortcuts = !on;
		return this;
	}

}
