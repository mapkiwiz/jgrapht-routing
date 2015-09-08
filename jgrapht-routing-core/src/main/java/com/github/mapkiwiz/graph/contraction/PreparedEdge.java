package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


public class PreparedEdge {
	
	private static final AtomicInteger sequence = new AtomicInteger(0);

	public final int id;
	public final PreparedNode source;
	public final PreparedNode target;
	double weight;
	PreparedEdgeData data;
	
	public PreparedEdge(PreparedNode source, PreparedNode target) {
		this(sequence.incrementAndGet(), source, target, 1.0);
	}
	
	public PreparedEdge(PreparedNode source, PreparedNode target, double weight) {
		this(sequence.incrementAndGet(), source, target, weight);
	}
	
	public PreparedEdge(int id, PreparedNode source, PreparedNode target, double weight) {
		this.id = id;
		this.source = source;
		this.target = target;
		this.weight = weight;
		this.data = new PreparedEdgeData();
	}
	
	public List<PreparedNode> unpack() {
		
		if (data == null || !data.shortcut) {
			return Collections.emptyList();
		}
		
		List<PreparedNode> viaNodes = new ArrayList<PreparedNode>();
		
		if (data.inEdge.data.shortcut) {
			List<PreparedNode> inViaNodes = data.inEdge.unpack();
			if (data.inEdge.source.equals(data.viaNode)) {
				Collections.reverse(inViaNodes);
			}
			viaNodes.addAll(inViaNodes);
		}
		
		viaNodes.add(data.viaNode);
		
		if (data.outEdge.data.shortcut) {
			List<PreparedNode> outViaNodes = data.outEdge.unpack();
			if (data.outEdge.target.equals(data.viaNode)) {
				Collections.reverse(outViaNodes);
			}
			viaNodes.addAll(outViaNodes);
		}
		
		return viaNodes;
		
	}
	
	public String toString() {
		StringBuffer buffer = new StringBuffer();
		buffer.append(source.id);
		if (data.shortcut) {
			buffer.append(" -s-> ");
		} else {
			buffer.append(" ---> ");
		}
		buffer.append(target.id);
		buffer.append(" (");
		buffer.append(weight);
		buffer.append(", ");
		buffer.append(data.direction);
		buffer.append(", level=");
		buffer.append(data.level);
		buffer.append(")");
		return buffer.toString();
	}
	
	public static void setIdNextValue(int value) {
		sequence.set(value);
	}

}
