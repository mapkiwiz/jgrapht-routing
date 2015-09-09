package com.github.mapkiwiz.graph.contraction;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class ContractorListener extends NodePriorityCalculator {

	private final Collection<PreparedEdge> contractedEdge;
	private final Map<NodeTuple, PreparedEdge> shortcuts;
	
	public ContractorListener() {
		
		this.contractedEdge = new ArrayList<PreparedEdge>();
		this.shortcuts = new HashMap<NodeTuple, PreparedEdge>();
	
	}
	
	@Override
	public void considerShortcut(
			PreparedNode source, PreparedNode target, double weight,
			PreparedEdge inEdge, PreparedEdge outEdge, PreparedNode viaNode) {
		
		shortcutCount++;
		
		NodeTuple tuple = new NodeTuple(target, source);
		PreparedEdge shortcut;
		
		if (shortcuts.containsKey(tuple)
				&& (shortcut = shortcuts.get(tuple)).source.equals(target)
				&& shortcut.weight == weight) {
			
			shortcut.data.direction = EdgeDirection.BIDIRECTIONAL;
	
		} else {
		
			shortcut = new PreparedEdge(source, target, weight);
			shortcut.data.direction = EdgeDirection.FORWARD;
			shortcut.data.shortcut = true;
			shortcut.data.viaNode = viaNode;
			shortcut.data.inEdge = inEdge;
			shortcut.data.outEdge = outEdge;
			shortcuts.put(new NodeTuple(source, target), shortcut);
			
		}
	
	}

	@Override
	public void onContractedEdge(PreparedEdge edge) {
		
		this.contractedEdge.add(edge);
		
	}
	
	public Collection<PreparedEdge> getContractedEdges() {
		return contractedEdge;
	}
	
	public Collection<PreparedEdge> getShortcuts() {
		return shortcuts.values();
	}
	
	static class NodeTuple {
		
		final PreparedNode source;
		final PreparedNode target;
		
		public NodeTuple(PreparedNode source, PreparedNode target) {
			this.source = source;
			this.target = target;
		}
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result
					+ ((source == null) ? 0 : source.hashCode());
			result = prime * result
					+ ((target == null) ? 0 : target.hashCode());
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			NodeTuple other = (NodeTuple) obj;
			if (source == null) {
				if (other.source != null)
					return false;
			} else if (!source.equals(other.source))
				return false;
			if (target == null) {
				if (other.target != null)
					return false;
			} else if (!target.equals(other.target))
				return false;
			return true;
		}
		
		
		
	}

}
