package com.github.mapkiwiz.routing.web.controller.method;

import java.util.List;
import java.util.concurrent.Callable;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.ConcaveHullBuilder;
import com.github.mapkiwiz.geo.algorithm.ConvexHullBuilder;
import com.github.mapkiwiz.geojson.Polygon;
import com.github.mapkiwiz.graph.Isochrone;
import com.github.mapkiwiz.routing.web.controller.ApiController;

public class IsochroneQuery implements Callable<Polygon> {
	
	private final Node sourceNnode;
	private final ApiController controller;
	private final double distance;
	private final boolean concaveHull;
	
	public IsochroneQuery(ApiController controller, Node sourceNode, double distance, boolean concaveHull) {
		this.controller = controller;
		this.sourceNnode = sourceNode;
		this.distance = distance;
		this.concaveHull = concaveHull;
	}
	
	public Polygon call() throws Exception {
		
		List<Node> hull;
		Isochrone processor = new Isochrone(controller.getIteratorFactory());
		List<Node> nodes = processor.isochrone(controller.getGraph(), sourceNnode, distance);
		
		if (concaveHull) {
			ConcaveHullBuilder<Node> builder = new ConcaveHullBuilder<Node>();
			hull = builder.buildHull(nodes);
		} else {
			ConvexHullBuilder<Node> builder = new ConvexHullBuilder<Node>();
			hull = builder.buildHull(nodes);
		}
		
		return NodeUtils.asPolygon(hull);
		
	}

}
