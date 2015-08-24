package fr.gouv.agriculture.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import fr.gouv.agriculture.geojson.Feature;
import fr.gouv.agriculture.geojson.LineString;
import fr.gouv.agriculture.geojson.Point;
import fr.gouv.agriculture.geojson.Polygon;
import fr.gouv.agriculture.graph.Isochrone;
import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.graph.Path;
import fr.gouv.agriculture.graph.PathElement;
import fr.gouv.agriculture.graph.ShortestPath;
import fr.gouv.agriculture.hull.ConcaveHullBuilder;
import fr.gouv.agriculture.locator.NodeLocator;

@RestController
public class ApiController {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(ApiController.class);
	
	public double isochroneMinDistance = 10000.0; // 10 km
	public double isochroneMaxDistance = 60000.0; // 60 km
	public double searchDistance = 0.05;
	
	@Autowired
	private Graph<Node, DefaultWeightedEdge> graph;
	
	@Autowired
	private NodeLocator nodeLocator;
	
	@RequestMapping("/api/v1/info")
	public AppInfo getAppInfo() {
		return new AppInfo();
	}
	
	@RequestMapping("/api/v1/locate")
	public Feature<Point> locate(@RequestParam("lon") double lon, @RequestParam("lat") double lat) throws NearestNodeNotFound {
		
		Node node = nodeLocator.locate(lon, lat, searchDistance);
		if (node == null) {
			throw new NearestNodeNotFound("No node near point (" + lon + "," + lat + ")");
		}
		
		Feature<Point> feature = new Feature<Point>();
		Point p = asPoint(node);
		feature.geometry = p;
		feature.properties.put("request_lon", lon);
		feature.properties.put("request_lat", lat);
		feature.properties.put("id", node.id);
		
		return feature;
		
	}
	
	@RequestMapping("/api/v1/distance")
	public DistanceInfo distance(@RequestParam("source") List<Double> source, @RequestParam("target") List<Double> target) throws NotFoundException, InvalidRequestException {
				
		Node sourceNode = getNodeFromLocParameter(source);
		Node targetNode = getNodeFromLocParameter(target);
		
		Path<Node> path = ShortestPath.shortestPath(graph, sourceNode, targetNode);
		
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
		
		return info;
		
	}
	
	public Node getNodeFromLocParameter(List<Double> loc) throws InvalidParameterFormat, NearestNodeNotFound {
		
		if (loc.size() != 2) {
			throw new InvalidParameterFormat("source and target parameters must be in the form lon,lat");
		}
		
		double lon = loc.get(0);
		double lat = loc.get(1);
		
		Node node = nodeLocator.locate(lon, lat, searchDistance);
		
		if (node == null) {
			throw new NearestNodeNotFound("No node near point (" + lon + "," + lat + ")");
		}
		
		return node;
		
	}
	
	@RequestMapping("/api/v1/route")
	public Feature<LineString> route(@RequestParam("source") List<Double> source, @RequestParam("target") List<Double> target) throws NotFoundException, InvalidRequestException {
		
		Node sourceNode = getNodeFromLocParameter(source);
		Node targetNode = getNodeFromLocParameter(target);
		
		Path<Node> path = ShortestPath.shortestPath(graph, sourceNode, targetNode);
		
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
		
		return feature;
		
	}
	
	@RequestMapping("/api/v1/isochrone")
	public Polygon isochrone(@RequestParam(value="lon") double lon, @RequestParam("lat") double lat, @RequestParam(value="distance") double distance) throws NotFoundException, InvalidRequestException {
		
		if (distance < isochroneMinDistance || distance > isochroneMaxDistance) {
			throw new DistanceNotInRange("Distance must between " + isochroneMinDistance + " and " + isochroneMaxDistance + " meters.");
		}
		
		Node node = nodeLocator.locate(lon, lat, 0.05);
		
		if (node == null) {
			throw new NearestNodeNotFound("No node near point (" + lon + "," + lat + ")");
		}
		
		List<Node> nodes = Isochrone.isochroneRaw(graph, node, distance);
		ConcaveHullBuilder builder = new ConcaveHullBuilder();
		Polygon polygon = builder.buildHull(nodes);
		
		return polygon;
		
	}
	
	@ExceptionHandler({ NotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionReport reportNotFound(NotFoundException ex) {
		
		ExceptionReport report = new ExceptionReport();
		report.error = ex.getClass().getSimpleName();
		report.message = ex.getMessage();
		return report;
		
	}
	
	@ExceptionHandler({ InvalidRequestException.class, MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionReport reportInvalidRequest(Exception ex) {
		
		ExceptionReport report = new ExceptionReport();
		report.error = ex.getClass().getSimpleName();
		report.message = ex.getMessage();
		return report;
		
	}
	
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionReport reportServerError(Exception e) {
		
		LOGGER.error(e.getMessage(), e);
		
		ExceptionReport report = new ExceptionReport();
		report.error = "ApplicationError";
		report.message = "Oops :( There was some error and your request could not be processed.";
		return report;
		
	}
	
	public Point asPoint(Node node) {
		
		Point p = new Point();
		p.coordinates = node.asCoordinatePair();
		return p;
		
	}
	
	public static class AppInfo {
		
		public final String version = "1.0";
		public final String app = "Routing Service";
		public final String documentation = "/path/to/the/doc";
		
	}
	
	public static class ExceptionReport {
		
		public String error;
		public String message;
		
	}
	
	public static class DistanceInfo {
		
		public Point source;
		public Point target;
		public double distance;
		public double time;
		public String distance_unit = "meters";
		public String time_unit = "minutes";
		
	}
	
	public static class NearestNodeNotFound extends NotFoundException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3540311395574147483L;

		public NearestNodeNotFound(String message) {
			super(message);
		}
		
	}
	
	public static class DistanceNotInRange extends InvalidRequestException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6820250961234003784L;

		public DistanceNotInRange(String message) {
			super(message);
		}
		
	}

}
