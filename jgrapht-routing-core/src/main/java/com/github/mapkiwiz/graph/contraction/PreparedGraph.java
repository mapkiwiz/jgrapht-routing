package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

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
		
		PreparedGraphIterator forwardIterator =
				new PreparedGraphIterator(this, source);
		PreparedGraphIterator reverseIterator =
				new PreparedGraphIterator(this, target);
		
		PreparedNode middleNode = null;
		
		while(forwardIterator.hasNext() || reverseIterator.hasNext()) {
			
			if (forwardIterator.hasNext()) {
				PreparedNode next = forwardIterator.next();
				if (reverseIterator.isSettled(next)) {
					middleNode = next;
					break;
				}
			}
			
			if (reverseIterator.hasNext()) {
				PreparedNode next = reverseIterator.next();
				if (forwardIterator.isSettled(next)) {
					middleNode = next;
					break;
				}
			}
			
		}
		
		double distance = Double.POSITIVE_INFINITY;
		if (middleNode != null) {
			distance = forwardIterator.getShortestPathLength(middleNode) +
					reverseIterator.getShortestPathLength(middleNode);
		}
		
		return distance;
		
	}

}
