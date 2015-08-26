package com.github.mapkiwiz.locator;

import java.util.Collection;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.index.quadtree.QuadTree;


public class IndexNodeLocator implements NodeLocator {
	
	private QuadTree<Node> index;
	
	public IndexNodeLocator(Collection<Node> nodes) {
		
		index = new QuadTree<Node>(-180.0, -90.0, 180.0, 90.0);
		for (Node node : nodes) {
			index.set(node.lon, node.lat, node);
		}
		
	}

	public Node locate(double lon, double lat, double maxDistance) {
		
		return index.nearest(lon, lat, maxDistance);
		
	}

}
