package com.github.mapkiwiz.routing.web.controller.method;

import java.util.concurrent.Callable;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.contraction.PreparedGraph;
import com.github.mapkiwiz.graph.contraction.PreparedNode;
import com.github.mapkiwiz.routing.web.controller.ApiController;
import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.DistanceInfo;

public class PreparedDistanceQuery implements Callable<DistanceInfo> {
	
	private PreparedNode sourceNode;
	private PreparedNode targetNode;
	private ApiController controller;
	
	public PreparedDistanceQuery(ApiController controller, Node source, Node target) {
		this.sourceNode = (PreparedNode) source;
		this.targetNode = (PreparedNode) target;
		this.controller = controller;
	}

	public DistanceInfo call() throws Exception {
		
		long spStartTime = System.currentTimeMillis();
		PreparedGraph graph = controller.getPreparedGraph();
		double time = graph.shortestPathLength(sourceNode, targetNode);
		controller.getStats().recordExecutionTimeToNow("shortest_path.prepared.execution", spStartTime);
		
		double distance = 0.0;
		
		DistanceInfo info = new DistanceInfo();
		info.distance = distance;
		info.cost = time;
		info.source = controller.asPoint(sourceNode);
		info.target = controller.asPoint(targetNode);
		
		return info;
		
	}

}
