package com.github.mapkiwiz.geo.algorithm;

import java.util.List;

import com.github.mapkiwiz.geo.Node;


public interface HullBuilder {
	
	public List<Node> buildHull(List<Node> nodes);

}
