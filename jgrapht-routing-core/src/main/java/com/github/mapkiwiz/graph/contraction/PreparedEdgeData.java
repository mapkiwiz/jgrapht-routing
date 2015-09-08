package com.github.mapkiwiz.graph.contraction;



public class PreparedEdgeData {
	
	EdgeDirection direction = EdgeDirection.BIDIRECTIONAL;
	int level = Integer.MAX_VALUE;
	boolean shortcut = false;
	PreparedNode viaNode;
	PreparedEdge inEdge;
	PreparedEdge outEdge;

}
