package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.jgrapht.Graphs;
import org.jgrapht.graph.SimpleDirectedGraph;

import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathElement;
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
		return shortestPath.bidirectionalShortestPathLength(this, source, target);
		
	}
	
	public List<PreparedEdge> shortestPathEdges(PreparedNode source, PreparedNode target) {
		
		PreparedGraphIterator forwardIterator = new PreparedGraphIterator(this, source);
		PreparedGraphIterator reverseIterator = new PreparedGraphIterator(this, target);
		
		PreparedNode middlePoint = null;
		
		while (forwardIterator.hasNext() && reverseIterator.hasNext()) {
			
			if (forwardIterator.hasNext()) {
				PreparedNode next = forwardIterator.next();
				if (reverseIterator.isSettled(next)) {
					middlePoint = next;
					break;
				}
			}
			
			if (reverseIterator.hasNext()) {
				PreparedNode next = reverseIterator.next();
				if (forwardIterator.isSettled(next)) {
					middlePoint = next;
					break;
				}
			}
			
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
		
		List<PreparedEdge> edges = shortestPathEdges(source, target);
		List<PreparedEdge> unpackedEdges = new ArrayList<PreparedEdge>();
		
		PreparedNode currentNode = source;
		
		for (PreparedEdge edge : edges) {
			
			PreparedNode nextNode = Graphs.getOppositeVertex(this, edge, currentNode);
			List<PreparedEdge> unpacked = unpack(edge, currentNode, nextNode);
			
			if (unpacked.size() > 0 && edge.target.equals(currentNode)) {
				Collections.reverse(unpacked);
			}
			
			unpackedEdges.addAll(unpacked);
			currentNode = nextNode;
			
		}
		
		List<PathElement<PreparedNode>> pathElements = new ArrayList<PathElement<PreparedNode>>();
		currentNode = source;
		
		for (PreparedEdge edge : unpackedEdges) {
			pathElements.add(new PathElement<PreparedNode>(currentNode, edge.weight, edge.weight));
			currentNode = Graphs.getOppositeVertex(this, edge, currentNode);
		}
		
		pathElements.add(new PathElement<PreparedNode>(target, 0.0, 0.0));
		
		return new Path<PreparedNode>(pathElements);
		
	}
	
	public List<PreparedEdge> unpack(PreparedEdge edge, PreparedNode source, PreparedNode target) {
		
		if (edge.data == null || !edge.data.shortcut) {
			return Collections.singletonList(edge);
		}
		
		List<PreparedEdge> edges = new ArrayList<PreparedEdge>();
		
		if (edge.data.inEdge.data.shortcut) {
			List<PreparedEdge> inEdges = unpack(edge.data.inEdge, source, edge.data.viaNode);
			if (edge.data.inEdge.source.equals(edge.data.viaNode)) {
				Collections.reverse(inEdges);
			}
			edges.addAll(inEdges);
		} else {
			edges.add(edge.data.inEdge);
		}
		
		if (edge.data.outEdge.data.shortcut) {
			List<PreparedEdge> outEdges = unpack(edge.data.outEdge, edge.data.viaNode, target);
			if (edge.data.outEdge.target.equals(edge.data.viaNode)) {
				Collections.reverse(outEdges);
			}
			edges.addAll(outEdges);
		} else {
			edges.add(edge.data.outEdge);
		}
		
		return edges;
		
	}

}
