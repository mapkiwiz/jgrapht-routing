package com.github.mapkiwiz.locator;

import com.github.mapkiwiz.geo.Node;

public interface NodeLocator {
	
	public Node locate(double lon, double lat, double maxDistance);

}
