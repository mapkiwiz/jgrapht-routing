package com.github.mapkiwiz.routing.web.controller;

import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;

import org.jgrapht.Graph;
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
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.LineString;
import com.github.mapkiwiz.geojson.Point;
import com.github.mapkiwiz.geojson.Polygon;
import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.Path;
import com.github.mapkiwiz.graph.PathUtils;
import com.github.mapkiwiz.graph.contraction.PreparedGraph;
import com.github.mapkiwiz.locator.NodeLocator;
import com.github.mapkiwiz.routing.web.controller.method.DistanceMatrixQuery;
import com.github.mapkiwiz.routing.web.controller.method.DistanceQuery;
import com.github.mapkiwiz.routing.web.controller.method.IsochroneQuery;
import com.github.mapkiwiz.routing.web.controller.method.PreparedDistanceQuery;
import com.github.mapkiwiz.routing.web.controller.method.PreparedRouteQuery;
import com.github.mapkiwiz.routing.web.controller.method.RouteQuery;
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
		
		Node sourceNode = getNodeFromLocParameter(source);
		Node targetNode = getNodeFromLocParameter(target);
		
		stats.recordExecutionTime("locate.execution", (System.currentTimeMillis() - startTime) / 2);
		
		Future<DistanceInfo> promise;
		
		if (graphHolder.isPrepared()) {
			promise = this.worker.submit(
					new PreparedDistanceQuery(this, sourceNode, targetNode));
		} else {
			promise = this.worker.submit(
					new DistanceQuery(this, sourceNode, targetNode));
		}
		
		try {
			
			DistanceInfo info = promise.get();
			stats.recordExecutionTimeToNow("distance.request.execution.total", startTime);
			stats.increment("distance.request.count");
			
			return info;
			
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		} catch (ExecutionException e) {
			throw new RuntimeException(e);
		}
		
		
	}
	
	public Node getNodeFromLocParameter(List<Double> loc) throws InvalidParameterFormat, NearestNodeNotFound {
		
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
		
		Future<Path<Node>> promise;
		
		if (graphHolder.isPrepared()) {
			promise = this.worker.submit(new PreparedRouteQuery(this, sourceNode, targetNode));
		} else {
			promise = this.worker.submit(new RouteQuery(this, sourceNode, targetNode));
		}
		
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
		
		Feature<LineString> feature = PathUtils.toFeature(path);
		
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
				this.worker.submit(new IsochroneQuery(this, node, distance, concaveHull));
		
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
				this.worker.submit(new DistanceMatrixQuery(this, source, targets));
		
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
	
	public Graph<Node, ?> getGraph() {
		return graphHolder.getGraph(Node.class);
	}
	
	public PreparedGraph getPreparedGraph() {
		
		if (graphHolder.isPrepared()) {
			return (PreparedGraph) graphHolder.getGraph();
		} else {
			return null;
		}
		
	}
	
	public StatsDClient getStats() {
		return stats;
	}
	
	public DijsktraIteratorFactory getIteratorFactory() {
		return iteratorFactory;
	}
	
	Logger getLogger() {
		return LOGGER;
	}

	public void destroy() throws Exception {
		this.worker.shutdownNow();
	}

}
