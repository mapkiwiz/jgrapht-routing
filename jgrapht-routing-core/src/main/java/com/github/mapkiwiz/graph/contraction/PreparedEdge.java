package com.github.mapkiwiz.graph.contraction;

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
	
	public List<PreparedEdge> unpack() {
		
		return PathUnpacker.unpack(this, source, target);
		
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
		buffer.append(")");
		return buffer.toString();
	}
	
	public static void setIdNextValue(int value) {
		sequence.set(value);
	}

}
