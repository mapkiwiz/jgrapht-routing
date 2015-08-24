package fr.gouv.agriculture.locator;

import java.util.Collection;

import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.index.quadtree.QuadTree;

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
