package com.github.mapkiwiz.geo.algorithm;

import java.util.List;

import com.github.mapkiwiz.geo.Node;


public interface HullBuilder<V extends Node> {
	
	public List<V> buildHull(List<V> nodes);

}
