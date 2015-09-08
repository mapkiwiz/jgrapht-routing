package com.github.mapkiwiz.graph.contraction;

public class NodePriorityCalculator implements ShortcutFinderListener {
	
	private int originalEdgeCount = 0;
	private int incidentEdgeCount = 0;
	private int contractedNeighborCount = 0;
	/* package */ int shortcutCount = 0;
	
	public int edgeDifferenceWeight = 10;
	public int contractedNeighborCountWeight = 1;
	public int originalEdgeCountWeight = 1;
	
	public void setOriginalEdgeCount(int count) {
		this.originalEdgeCount = count;
	}

	public void setContractedNeighborCount(int count) {
		this.contractedNeighborCount = count;
	}

	public void setIncidentNodeCount(int count) {
		this.incidentEdgeCount = count;
	}

	public void considerShortcut(
			PreparedNode source, PreparedNode target, double weight,
			PreparedEdge inEdge, PreparedEdge outEdge, PreparedNode viaNode) {
		this.shortcutCount++;
	}

	public void onContractedEdge(PreparedEdge edge) {
		// no-op
	}
	
	public int getEdgeDifference() {
		return shortcutCount - incidentEdgeCount;
	}
	
	public int getNodePriority() {
		
		return edgeDifferenceWeight * getEdgeDifference() +
				contractedNeighborCountWeight * contractedNeighborCount +
				originalEdgeCountWeight * originalEdgeCount;
	
	}

}
