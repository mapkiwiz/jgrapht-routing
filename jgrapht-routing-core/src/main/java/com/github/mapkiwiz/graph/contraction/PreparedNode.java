package com.github.mapkiwiz.graph.contraction;

import com.github.mapkiwiz.geo.Node;

public class PreparedNode extends Node {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 9155957246781352990L;
	
	/* package */ int level = Integer.MAX_VALUE;

	public PreparedNode(long id, double lon, double lat) {
		super(id, lon, lat);
	}
	
	@Override
	public String toString() {
		return super.toString() + " level=" + level;
	}

}
