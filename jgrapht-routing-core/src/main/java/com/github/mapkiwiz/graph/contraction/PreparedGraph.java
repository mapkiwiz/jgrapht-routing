package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.ShortestPath;

public class PreparedGraph extends SimpleDirectedGraph<PreparedNode, PreparedEdge> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6554678051132943473L;
	
	/* package */ boolean contracted = false;

	public PreparedGraph() {
		super(PreparedEdge.class);
	}
	
	public boolean isContracted() {
		return contracted;
	}

	@Override
	public double getEdgeWeight(PreparedEdge e) {
		return e.weight;
	}

	@Override
	public void setEdgeWeight(PreparedEdge e, double weight) {
		e.weight = weight;
	}
	
	public List<PreparedNode> getOutgoingNodesOf(PreparedNode node) {
		
		List<PreparedNode> result = new ArrayList<PreparedNode>();
		for (PreparedEdge edge : edgesOf(node)) {
			PreparedNode n = Graphs.getOppositeVertex(this, edge, node);
			result.add(n);
		}
		
		return result;
		
	}
	
	public List<PreparedNode> getIncomingNodesOf(PreparedNode node) {
		return getOutgoingNodesOf(node);
	}
	
	public FilteredGraph filter() {
		
		return new FilteredGraph(this);
		
	}
	
	public double shortestPathLength(PreparedNode source, PreparedNode target) {
		
		DijsktraIteratorFactory factory = new PreparedGraphIterator.Factory();
		ShortestPath shortestPath = new ShortestPath(factory);
		return shortestPath.shortestPathLength(this, source, target);
		
	}
	
	public List<PreparedEdge> shortestPathEdges(PreparedNode source, PreparedNode target) {
		
		PreparedGraphIterator forwardIterator = new PreparedGraphIterator(this, source);
		PreparedGraphIterator reverseIterator = new PreparedGraphIterator(this, target);
		
		PreparedNode middlePoint = ShortestPath.bidirectionalDijkstra(forwardIterator, reverseIterator);
	
		if (middlePoint == null) {
			return Collections.emptyList();
		}
		
		PreparedEdge edge;
		List<PreparedEdge> edges = new ArrayList<PreparedEdge>();
		PreparedNode currentNode = middlePoint;
		
		while ((edge = forwardIterator.getParentEdge(currentNode)) != null) {
			edges.add(edge);
			currentNode = Graphs.getOppositeVertex(this, edge, currentNode);
		}
		
		Collections.reverse(edges);
		currentNode = middlePoint;
		
		while ((edge = reverseIterator.getParentEdge(currentNode)) != null) {
			edges.add(edge);
			currentNode = Graphs.getOppositeVertex(this, edge, currentNode);
		}
		
		return edges;
		
	}
	
	public Path<PreparedNode> shortestPath(PreparedNode source, PreparedNode target) {
		
		List<PreparedEdge> packedEdges = shortestPathEdges(source, target);
		return PathUnpacker.unpack(packedEdges, source, target);
		
	}

}
