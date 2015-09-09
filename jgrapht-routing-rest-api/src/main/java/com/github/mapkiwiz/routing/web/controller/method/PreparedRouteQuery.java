package com.github.mapkiwiz.routing.web.controller.method;

import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.contraction.PreparedNode;
import com.github.mapkiwiz.routing.web.controller.ApiController;

public class PreparedRouteQuery implements Callable<Path<Node>> {
	
private static final Logger LOGGER = LoggerFactory.getLogger(RouteQuery.class);
	
private PreparedNode sourceNode;
private PreparedNode targetNode;
private ApiController controller;
	
	public PreparedRouteQuery(ApiController controller, Node source, Node target) {
		this.controller = controller;
		this.sourceNode = (PreparedNode) source;
		this.targetNode = (PreparedNode) target;
	}
	
	@SuppressWarnings("unchecked")
	public Path<Node> call() throws Exception {
		
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("[route] Entering thread pool");
		}
		
		long startTime = System.currentTimeMillis();
		Path<?> path = controller.getPreparedGraph().shortestPath(sourceNode, targetNode);
		controller.getStats().recordExecutionTimeToNow("shortest_path.prepared.execution", startTime);
		
		if (LOGGER.isDebugEnabled()) {
			long duration = System.currentTimeMillis() - startTime;
			LOGGER.debug("[route] Exiting thread pool, execution time {} ms.", duration);
		}
		
		return (Path<Node>) path;
		
	}

}
