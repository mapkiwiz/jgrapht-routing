package com.github.mapkiwiz.routing.web.controller.method;

import java.util.concurrent.Callable;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathElement;
import com.github.mapkiwiz.graph.ShortestPath;
import com.github.mapkiwiz.routing.web.controller.ApiController;
import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.DistanceInfo;

public class DistanceQuery implements Callable<DistanceInfo> {
	
	private Node sourceNode;
	private Node targetNode;
	private ApiController controller;
	
	public DistanceQuery(ApiController controller, Node source, Node target) {
		this.sourceNode = source;
		this.targetNode = target;
		this.controller = controller;
	}

	public DistanceInfo call() throws Exception {
		
		long spStartTime = System.currentTimeMillis();
		ShortestPath shortestPath = new ShortestPath(controller.getIteratorFactory());
		Path<Node> path = shortestPath.shortestPath(controller.getGraph(), sourceNode, targetNode);
		controller.getStats().recordExecutionTimeToNow("shortest_path.execution", spStartTime);
		
		double distance = 0.0;
		double time = 0.0;
		
		for (PathElement<Node> segment : path.elements) {
			distance += segment.distance;
			time += segment.weight;
		}
		
		DistanceInfo info = new DistanceInfo();
		info.distance = distance;
		info.cost = time;
		info.source = controller.asPoint(sourceNode);
		info.target = controller.asPoint(targetNode);
		
		return info;
		
	}

}
