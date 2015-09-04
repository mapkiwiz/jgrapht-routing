package com.github.mapkiwiz.locator;

import com.github.mapkiwiz.geo.Node;

public interface NodeLocator<V extends Node> {
	
	public V locate(double lon, double lat, double maxDistance);

}
