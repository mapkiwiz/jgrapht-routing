package fr.gouv.agriculture.web.controller;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

import fr.gouv.agriculture.geo.Node;
import fr.gouv.agriculture.geojson.Point;

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
