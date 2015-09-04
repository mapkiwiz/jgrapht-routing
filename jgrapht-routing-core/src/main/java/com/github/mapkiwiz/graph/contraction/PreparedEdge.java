package com.github.mapkiwiz.graph.contraction;


public class PreparedEdge {

	public final PreparedNode source;
	public final PreparedNode target;
	double weight;
	
	EdgeDirection direction = EdgeDirection.BIDIRECTIONAL;
	int level = Integer.MAX_VALUE;
	boolean shortcut = false;
	PreparedNode viaNode = null;
	
	public PreparedEdge(PreparedNode source, PreparedNode target) {
		this.source = source;
		this.target = target;
		this.weight = 1.0;
	}
	
	public PreparedEdge(PreparedNode source, PreparedNode target, double weight) {
		this.source = source;
		this.target = target;
		this.weight = weight;
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(source.id);
		if (shortcut) {
			buffer.append(" -s-> ");
		} else {
			buffer.append(" ---> ");
		}
		buffer.append(target.id);
		buffer.append(" (");
		buffer.append(weight);
		buffer.append(", ");
		buffer.append(direction);
		buffer.append(", level=");
		buffer.append(level);
		buffer.append(")");
		return buffer.toString();
	}

}
