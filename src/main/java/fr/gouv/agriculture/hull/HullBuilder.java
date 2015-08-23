package fr.gouv.agriculture.hull;

import java.util.List;

import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.Node;

public interface HullBuilder {
	
	public Polygon buildHull(List<Node> nodes);

}
