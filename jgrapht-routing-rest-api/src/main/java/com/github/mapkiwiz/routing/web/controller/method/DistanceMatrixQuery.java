package com.github.mapkiwiz.routing.web.controller.method;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.DistanceMatrix;
import com.github.mapkiwiz.routing.web.controller.ApiController;
import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.DistanceMatrixInfo;
import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.DistanceMatrixResult;

public class DistanceMatrixQuery implements Callable<DistanceMatrixInfo> {
	
	private final ApiController controller;
	private final List<Double> source;
	private final List<List<Double>> targets;
	
	public DistanceMatrixQuery(ApiController controller, List<Double> source, List<List<Double>> targets) {
		this.controller = controller;
		this.source = source;
		this.targets = targets;
	}

	public DistanceMatrixInfo call() throws Exception {
		
		Node sourceNode = controller.getNodeFromLocParameter(source);
		
		Node[] nodes;
		
		if (targets.size() == 2 && targets.get(0).size() == 1 && targets.get(1).size() == 1) {
			
			double lon = targets.get(0).get(0);
			double lat = targets.get(1).get(0);
			List<Double> loc = new ArrayList<Double>();
			loc.add(lon);
			loc.add(lat);
			
			nodes = new Node[1];
			nodes[0] = controller.getNodeFromLocParameter(loc);
			
		} else {
		
			nodes = new Node[targets.size()];
			for (int i=0; i<nodes.length; i++) {
				nodes[i] = controller.getNodeFromLocParameter(targets.get(i));
			}
		
		}
		
		DistanceMatrix<Node> matrix = new DistanceMatrix<Node>();
		double[] distances =
				matrix.distances(controller.getGraph(), sourceNode, nodes);
		
		DistanceMatrixInfo matrixInfo = new DistanceMatrixInfo();
		matrixInfo.source = controller.asFeature(sourceNode);
		
		for (int i=0; i<nodes.length; i++) {
			DistanceMatrixResult result = new DistanceMatrixResult();
			result.target = controller.asFeature(nodes[i]);
			result.distance = distances[i];
			matrixInfo.distances.add(result);
		}
		
		return matrixInfo;
		
	}

}
