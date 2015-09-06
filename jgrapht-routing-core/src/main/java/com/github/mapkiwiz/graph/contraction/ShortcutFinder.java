package com.github.mapkiwiz.graph.contraction;

import java.util.HashMap;
import java.util.Map;

import org.jgrapht.Graph;
import org.jgrapht.Graphs;

import com.github.mapkiwiz.graph.DijkstraIterator;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;

public class ShortcutFinder {
	
	public static final int DEFAULT_MAX_NODE_LIMIT = 1000;
	public int maxNodeLimit = DEFAULT_MAX_NODE_LIMIT;

	public void findShortcuts(PreparedGraph graph, PreparedNode node, int minLevel, ShortcutFinderListener listener) {

		Map<PreparedNode, PreparedEdge> in = getIncomingEdgesOf(graph, node);
		Map<PreparedNode, PreparedEdge> out = getOutgoingEdgesOf(graph, node);
		listener.setOriginalEdgeCount(in.size() + out.size());
		int contractedNeighborCount = 0;
		int incidentEdgeCount = 0;

		for (Map.Entry<PreparedNode, PreparedEdge> entry : in.entrySet()) {

			PreparedNode inNode = entry.getKey();
			
			if (inNode.level < minLevel) {
				contractedNeighborCount++;
				continue;
			}

			PriorityQueueDijkstraIterator<PreparedNode, PreparedEdge> iterator =
					new PriorityQueueDijkstraIterator<PreparedNode, PreparedEdge>(
							graph.filter().ignore(node).minLevel(minLevel),
							inNode);
			
			iterator.setMaxNodeLimit(maxNodeLimit);

			for (PreparedNode outNode : out.keySet()) {

				if (inNode.equals(outNode)) {
					continue;
				}

				if (outNode.level < minLevel) {
					continue;
				}

				// distance(in,out) via node
				double via_distance =
						in.get(inNode).weight + out.get(outNode).weight;
				// shortest distance(in, out) without node
				double shortest_distance = distance(outNode, iterator);
				// add shortcut if shortest > via distance
				if (shortest_distance > via_distance) {
					listener.considerShortcut(inNode, outNode, via_distance);
				}

			}
			
			incidentEdgeCount++;
			listener.onContractedEdge(entry.getValue());
			
		}
		
		for (Map.Entry<PreparedNode, PreparedEdge> entry : out.entrySet()) {
			PreparedNode outNode = entry.getKey();
			if (outNode.level < minLevel) {
				contractedNeighborCount++;
			} else {
				incidentEdgeCount++;
				listener.onContractedEdge(entry.getValue());
			}
		}
		
		listener.setContractedNeighborCount(contractedNeighborCount);
		listener.setIncidentNodeCount(incidentEdgeCount);

	}

	public double distance(PreparedNode target, DijkstraIterator<PreparedNode> iterator) {

		if (iterator.isSettled(target)) {
			return iterator.getShortestPathLength(target);
		}

		while (iterator.hasNext()) {
			PreparedNode next = iterator.next();
			if (next.equals(target)) {
				break;
			}
		}

		return iterator.getShortestPathLength(target);

	}

	public Map<PreparedNode, PreparedEdge> getIncomingEdgesOf(Graph<PreparedNode, PreparedEdge> graph, PreparedNode node) {

		Map<PreparedNode, PreparedEdge> edges = new HashMap<PreparedNode, PreparedEdge>();
		for (PreparedEdge edge : graph.edgesOf(node)) {
			PreparedNode origin = Graphs.getOppositeVertex(graph, edge, node);
			edges.put(origin, edge);
		}
		return edges;

	}

	public Map<PreparedNode, PreparedEdge> getOutgoingEdgesOf(Graph<PreparedNode, PreparedEdge> graph, PreparedNode node) {
		return getIncomingEdgesOf(graph, node);
	}

}
