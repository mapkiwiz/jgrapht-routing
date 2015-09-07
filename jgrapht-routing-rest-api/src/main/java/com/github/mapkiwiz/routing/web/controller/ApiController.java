package com.github.mapkiwiz.routing.web.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geo.NodeUtils;
import com.github.mapkiwiz.geo.algorithm.ConcaveHullBuilder;
import com.github.mapkiwiz.geo.algorithm.ConvexHullBuilder;
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.LineString;
import com.github.mapkiwiz.geojson.Point;
import com.github.mapkiwiz.geojson.Polygon;
import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.DistanceMatrix;
import com.github.mapkiwiz.graph.Isochrone;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathElement;
import com.github.mapkiwiz.graph.ShortestPath;
import com.github.mapkiwiz.locator.NodeLocator;
import com.timgroup.statsd.StatsDClient;



@RestController
@RequestMapping("/api/v1")
@CrossOrigin(
		methods={ RequestMethod.OPTIONS, RequestMethod.GET, RequestMethod.POST },
		allowCredentials="false",
		origins={ "*" })
public class ApiController extends ApiControllerBase implements DisposableBean {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
	
	@Value("${search.isochrone.min.distance}")
	private double isochroneMinDistance;
	
	@Value("${search.isochrone.max.distance}")
	private double isochroneMaxDistance;
	
	@Value("${search.max.distance}")
	private double searchDistance;
	
	@Autowired
	private DijsktraIteratorFactory iteratorFactory;
	
	@Autowired
	private GraphHolder graphHolder;
	
	@Autowired
	private NodeLocator<?> nodeLocator;
	
	@Autowired
	private StatsDClient stats;
	
	private final ThreadPoolExecutor worker;
	
	public ApiController(ThreadPoolExecutor worker) {
		this.worker = worker;
	}
	
	@RequestMapping("/info")
	public AppInfo getAppInfo() {
		return new AppInfo();
	}
	
	@RequestMapping("/locate")
	public Feature<Point> locate(
			@RequestParam("lon") double lon,
			@RequestParam("lat") double lat) throws NearestNodeNotFound {
		
		final long startTime = System.currentTimeMillis();
		
		Node node = nodeLocator.locate(lon, lat, searchDistance);
		
		stats.recordExecutionTimeToNow("locate.execution", startTime);
		
		if (node == null) {
			throw new NearestNodeNotFound(lon, lat);
		}
		
		Feature<Point> feature = new Feature<Point>();
		Point p = asPoint(node);
		feature.geometry = p;
		feature.properties.put("request_lon", lon);
		feature.properties.put("request_lat", lat);
		feature.properties.put("id", node.id);
		
		stats.increment("locate.request.count");
		
		return feature;
		
	}
	
	@RequestMapping("/distance")
	public DistanceInfo distance(
			@RequestParam("source") List<Double> source,
			@RequestParam("target") List<Double> target) throws NotFoundException, InvalidRequestException {
				
		final long startTime = System.currentTimeMillis();
		
		final Node sourceNode = getNodeFromLocParameter(source);
		final Node targetNode = getNodeFromLocParameter(target);
		
		stats.recordExecutionTime("locate.execution", (System.currentTimeMillis() - startTime) / 2);
		
		Future<Path<Node>> promise =
				this.worker.submit(new Callable<Path<Node>>() {

			public Path<Node> call() throws Exception {
				
				long spStartTime = System.currentTimeMillis();
				ShortestPath shortestPath = new ShortestPath(iteratorFactory);
				Path<Node> path = shortestPath.shortestPath(graphHolder.getGraph(Node.class), sourceNode, targetNode);
				stats.recordExecutionTimeToNow("shortest_path.execution", spStartTime);
				return path;
				
			}
			
		});
		
		Path<Node> path;
		try {
			path = promise.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		double distance = 0.0;
		double time = 0.0;
		
		for (PathElement<Node> segment : path.elements) {
			distance += segment.distance;
			time += segment.weight;
		}
		
		DistanceInfo info = new DistanceInfo();
		info.distance = distance;
		info.time = time;
		info.source = asPoint(sourceNode);
		info.target = asPoint(targetNode);
		
		stats.recordExecutionTimeToNow("distance.request.execution.total", startTime);
		stats.increment("distance.request.count");
		
		return info;
		
	}
	
	private Node getNodeFromLocParameter(List<Double> loc) throws InvalidParameterFormat, NearestNodeNotFound {
		
		if (loc.size() != 2) {
			throw new InvalidParameterFormat("source and target parameters must be in the form lon,lat");
		}
		
		double lon = loc.get(0);
		double lat = loc.get(1);
		
		Node node = nodeLocator.locate(lon, lat, searchDistance);
		
		if (node == null) {
			throw new NearestNodeNotFound(lon, lat);
		}
		
		return node;
		
	}
	
	@RequestMapping("/route")
	public Feature<LineString> route(
			@RequestParam("source") List<Double> source,
			@RequestParam("target") List<Double> target) throws NotFoundException, InvalidRequestException {
		
		final long startTime = System.currentTimeMillis();
		
		final Node sourceNode = getNodeFromLocParameter(source);
		final Node targetNode = getNodeFromLocParameter(target);
		
		stats.recordExecutionTime("locate.execution", (System.currentTimeMillis() - startTime) / 2);
		
		Future<Path<Node>> promise = this.worker.submit(
				new Callable<Path<Node>>() {

					public Path<Node> call() throws Exception {
		
						if (LOGGER.isDebugEnabled()) {
							LOGGER.debug("[route] Entering thread pool");
						}
						
						long startTime = System.currentTimeMillis();
						ShortestPath shortestPath = new ShortestPath(iteratorFactory);
						Path<Node> path = shortestPath.shortestPath(graphHolder.getGraph(Node.class), sourceNode, targetNode);
						stats.recordExecutionTimeToNow("shortest_path.execution", startTime);
						
						if (LOGGER.isDebugEnabled()) {
							long duration = System.currentTimeMillis() - startTime;
							LOGGER.debug("[route] Exiting thread pool, execution time {} ms.", duration);
						}
						
						return path;
						
					}
		
		});
		
		Path<Node> path;
		
		try {
			path = promise.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			if (e.getCause() instanceof NotFoundException) {
				throw (NotFoundException) e.getCause();
			}
			throw new RuntimeException(e);
		}
		
		if (path.elements.isEmpty()) {
			throw new NoPathBetweenPoints(sourceNode, targetNode);
		}
		
		List<List<Double>> coordinates = new ArrayList<List<Double>>();
		double distance = 0.0;
		double time = 0.0;
		
		for (PathElement<Node> segment : path.elements) {
			coordinates.add(segment.node.asCoordinatePair());
			distance += segment.distance;
			time += segment.weight;
		}
		
		LineString geometry = new LineString();
		geometry.coordinates = coordinates;
		Feature<LineString> feature = new Feature<LineString>();
		feature.geometry = geometry;
		feature.properties.put("source", sourceNode.id);
		feature.properties.put("target", targetNode.id);
		feature.properties.put("distance", distance);
		feature.properties.put("time", time);
		
		stats.recordExecutionTimeToNow("route.request.execution.total", startTime);
		stats.increment("route.request.count");
		return feature;
		
	}
	
	@RequestMapping("/isochrone")
	public Polygon isochrone(
			@RequestParam("lon") double lon,
			@RequestParam("lat") double lat,
			@RequestParam("distance") final double distance,
			@RequestParam(value="concave", defaultValue="false") final boolean concaveHull)
					throws NotFoundException, InvalidRequestException {
		
		if (distance < isochroneMinDistance || distance > isochroneMaxDistance) {
			throw new DistanceNotInRange(isochroneMinDistance, isochroneMaxDistance);
		}
		
		final long startTime = System.currentTimeMillis();
		final Node node = nodeLocator.locate(lon, lat, searchDistance);
		stats.recordExecutionTimeToNow("locate.execution", startTime);
		
		if (node == null) {
			throw new NearestNodeNotFound(lon, lat);
		}
		
		Future<Polygon> promise =
				this.worker.submit(new Callable<Polygon>() {

			public Polygon call() throws Exception {
				
				List<Node> hull;
				Isochrone processor = new Isochrone(iteratorFactory);
				List<Node> nodes = processor.isochrone(graphHolder.getGraph(Node.class), node, distance);
				
				if (concaveHull) {
					ConcaveHullBuilder<Node> builder = new ConcaveHullBuilder<Node>();
					hull = builder.buildHull(nodes);
				} else {
					ConvexHullBuilder<Node> builder = new ConvexHullBuilder<Node>();
					hull = builder.buildHull(nodes);
				}
				
				return NodeUtils.asPolygon(hull);
				
			}
			
		});
		
		Polygon polygon;
		try {
			polygon = promise.get();
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		stats.recordExecutionTimeToNow("isochrone.request.execution.total", startTime);
		stats.increment("isochrone.request.count");
		
		return polygon;
		
	}
	
	@RequestMapping("/matrix")
	public DistanceMatrixInfo distanceMatrix(
			@RequestParam("source") final List<Double> source,
			@RequestParam("target") final List<List<Double>> targets) throws InvalidParameterFormat, NearestNodeNotFound {
		
		
		Future<DistanceMatrixInfo> promise =
				this.worker.submit(new Callable<DistanceMatrixInfo>() {

					public DistanceMatrixInfo call() throws Exception {
						
						Node sourceNode = getNodeFromLocParameter(source);
						
						Node[] nodes;
						
						if (targets.size() == 2 && targets.get(0).size() == 1 && targets.get(1).size() == 1) {
							
							double lon = targets.get(0).get(0);
							double lat = targets.get(1).get(0);
							nodes = new Node[1];
							nodes[0] = nodeLocator.locate(lon, lat, searchDistance);
							
						} else {
						
							nodes = new Node[targets.size()];
							for (int i=0; i<nodes.length; i++) {
								nodes[i] = getNodeFromLocParameter(targets.get(i));
							}
						
						}
						
						DistanceMatrix<Node> matrix = new DistanceMatrix<Node>();
						double[] distances =
								matrix.distances(graphHolder.getGraph(Node.class), sourceNode, nodes);
						
						DistanceMatrixInfo matrixInfo = new DistanceMatrixInfo();
						matrixInfo.source = asFeature(sourceNode);
						
						for (int i=0; i<nodes.length; i++) {
							DistanceMatrixResult result = new DistanceMatrixResult();
							result.target = asFeature(nodes[i]);
							result.distance = distances[i];
							matrixInfo.distances.add(result);
						}
						
						return matrixInfo;
						
					}
				
				});
		
		try {
			
			return promise.get();
			
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			
			if (e.getCause() instanceof InvalidParameterFormat) {
				throw (InvalidParameterFormat) e.getCause();
			} else if (e.getCause() instanceof NearestNodeNotFound) {
				throw (NearestNodeNotFound) e.getCause();
			}
			
			throw new RuntimeException(e);
			
		}
		
	}
	
	Logger getLogger() {
		return LOGGER;
	}

	public void destroy() throws Exception {
		this.worker.shutdownNow();
	}

}
