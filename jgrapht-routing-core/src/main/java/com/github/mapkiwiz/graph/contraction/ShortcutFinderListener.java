package com.github.mapkiwiz.graph.contraction;

public interface ShortcutFinderListener {
	
	public void setOriginalEdgeCount(int count);
	
	public void setContractedNeighborCount(int count);
	
	public void setIncidentNodeCount(int count);
	
	public void considerShortcut(PreparedNode source, PreparedNode target, double weight, PreparedEdge inEdge, PreparedEdge outEdge, PreparedNode viaNode);

//	public void onContractedNode(PreparedNode node);
	
	public void onContractedEdge(PreparedEdge edge);
	
}
