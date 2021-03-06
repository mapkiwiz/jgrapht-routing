package com.github.mapkiwiz.routing.web.controller;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geojson.Feature;
import com.github.mapkiwiz.geojson.Point;


public abstract class ApiControllerBase {
	
	abstract Logger getLogger();
	
	@ExceptionHandler({ NotFoundException.class })
	@ResponseStatus(HttpStatus.NOT_FOUND)
	public ExceptionReport reportNotFound(NotFoundException ex) {
		
		getLogger().info("404 - ", ex.getMessage());
		
		ExceptionReport report = new ExceptionReport();
		report.error = ex.getClass().getSimpleName();
		report.message = ex.getMessage();
		return report;
		
	}
	
	@ExceptionHandler({ InvalidRequestException.class, MissingServletRequestParameterException.class })
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ExceptionReport reportInvalidRequest(Exception ex) {
		
		getLogger().info("400 - {}", ex.getMessage());
		
		ExceptionReport report = new ExceptionReport();
		report.error = ex.getClass().getSimpleName();
		report.message = ex.getMessage();
		return report;
		
	}
	
	@ExceptionHandler({ Exception.class })
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	public ExceptionReport reportServerError(Exception e) {
		
		getLogger().error(e.getMessage(), e);
		
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
	
	public Feature<Point> asFeature(Node node) {
		
		Feature<Point> feature = new Feature<Point>();
		feature.geometry = asPoint(node);
		feature.properties.put("id", node.id);
		return feature;
		
	}
	
	public static class AppInfo {
		
		public final String version = "5.0-SNAPSHOT";
		public final String apiVersion = "1";
		public final String app = "Routing Service";
		public final String documentation = "/path/to/the/doc";
		public final String type = "AppInfo";
		
	}
	
	public static class ExceptionReport {
		
		public String error;
		public String message;
		public final String type = "ExceptionReport";
		
	}
	
	public static class DistanceInfo {
		
		public Point source;
		public Point target;
		public double distance;
		public double cost;
		public String distance_unit = "meters";
		public String cost_unit = "meters";
		public final String type = "Distance";
		
	}
	
	public static class DistanceMatrixInfo {
		
		public Feature<Point> source;
		public String distance_unit = "meters";
		// public String time_unit = "minutes";
		public List<DistanceMatrixResult> distances = new ArrayList<DistanceMatrixResult>();
		public final String type = "DistanceMatrix";
		
	}
	
	public static class DistanceMatrixResult {
		
		public Feature<Point> target;
		public double distance;
		public final String type = "TargetDistance";
		
	}
	
	public static class NearestNodeNotFound extends NotFoundException {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -3540311395574147483L;

		public NearestNodeNotFound(double lon, double lat) {
			super("No node near point (" + lon + "," + lat + ")");
		}
		
	}
	
	public static class DistanceNotInRange extends InvalidRequestException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6820250961234003784L;

		public DistanceNotInRange(double min, double max) {
			super("Distance must be between " + min + " and " + max);
		}
		
	}
	
	public static class NoPathBetweenPoints extends NotFoundException {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5860120457753330436L;

		public NoPathBetweenPoints(Node source, Node target) {
			super("No path found between points (" + source.lon
					+ "," + source.lat
					+ ") and (" + target.lon
					+ "," + target.lat + ")");
		}
		
	}

}
