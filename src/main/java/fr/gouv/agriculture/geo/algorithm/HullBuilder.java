package fr.gouv.agriculture.geo.algorithm;

import java.util.List;

import fr.gouv.agriculture.geo.Node;
import fr.gouv.agriculture.geojson.Polygon;

public interface HullBuilder {
	
	public Polygon buildHull(List<Node> nodes);

}
