package fr.gouv.agriculture.locator;

import fr.gouv.agriculture.graph.Node;

public interface NodeLocator {
	
	public Node locate(double lon, double lat, double maxDistance);

}
