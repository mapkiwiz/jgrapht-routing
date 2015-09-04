package com.github.mapkiwiz.locator;

import java.util.Collection;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.index.quadtree.QuadTree;


public class IndexNodeLocator<V extends Node> implements NodeLocator<V> {
	
	private QuadTree<V> index;
	
	public IndexNodeLocator(Collection<V> nodes) {
		
		index = new QuadTree<V>(-180.0, -90.0, 180.0, 90.0);
		for (V node : nodes) {
			index.set(node.lon, node.lat, node);
		}
		
	}

	public V locate(double lon, double lat, double maxDistance) {
		
		return index.nearest(lon, lat, maxDistance);
		
	}

}
