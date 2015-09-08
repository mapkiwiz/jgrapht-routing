package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathElement;

public class PathUnpacker {

	public static Path<PreparedNode> unpack(
			List<PreparedEdge> edges,
			PreparedNode source, PreparedNode target) {

		List<PreparedEdge> unpackedEdges = new ArrayList<PreparedEdge>();

		PreparedNode currentNode = source;

		for (PreparedEdge edge : edges) {

			PreparedNode nextNode = getOppositeVertex(edge, currentNode);
			List<PreparedEdge> unpacked = unpack(edge, currentNode, nextNode);

			if (unpacked.size() > 0 && edge.target.equals(currentNode)) {
				Collections.reverse(unpacked);
			}

			unpackedEdges.addAll(unpacked);
			currentNode = nextNode;

		}

//		for (PreparedEdge ue : unpackedEdges) {
//			System.out.println(ue);
//		}

		List<PathElement<PreparedNode>> pathElements = new ArrayList<PathElement<PreparedNode>>();
		currentNode = source;

		for (PreparedEdge edge : unpackedEdges) {
			pathElements.add(new PathElement<PreparedNode>(currentNode, edge.weight, edge.weight));
			currentNode = getOppositeVertex(edge, currentNode);
		}

		pathElements.add(new PathElement<PreparedNode>(target, 0.0, 0.0));

		return new Path<PreparedNode>(pathElements);

	}

	public static List<PreparedEdge> unpack(PreparedEdge edge, PreparedNode source, PreparedNode target) {

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
	
	private static PreparedNode getOppositeVertex(PreparedEdge edge, PreparedNode vertex) {
		
		if (edge.source.equals(vertex)) {
			return edge.target;
		} else if (edge.target.equals(vertex)){
			return edge.source;
		} else {
			throw new IllegalArgumentException("Edge does not contain vertex " + vertex);
		}
		
	}

}
