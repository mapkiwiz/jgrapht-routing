package com.github.mapkiwiz.routing.web.controller.method;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.ShortestPath;
import com.github.mapkiwiz.routing.web.controller.ApiController;

public class RouteQuery implements Callable<Path<Node>> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RouteQuery.class);
	
	private final ApiController controller;
	private final Node sourceNode;
	private final Node targetNode;
	
	public RouteQuery(ApiController controller, Node source, Node target) {
		this.controller = controller;
		this.sourceNode = source;
		this.targetNode = target;
	}
	
	public Path<Node> call() throws Exception {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[route] Entering thread pool");
		}
		
		long startTime = System.currentTimeMillis();
		ShortestPath shortestPath = new ShortestPath(controller.getIteratorFactory());
		Path<Node> path = shortestPath.shortestPath(controller.getGraph(), sourceNode, targetNode);
		controller.getStats().recordExecutionTimeToNow("shortest_path.execution", startTime);
		
		if (LOGGER.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - startTime;
			LOGGER.debug("[route] Exiting thread pool, execution time {} ms.", duration);
		}
		
		return path;
		
	}

}
