package com.github.mapkiwiz.geo.algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.geojson.GeoJsonFormatException;
import com.github.mapkiwiz.geojson.GeoJsonObject;
import com.github.mapkiwiz.geojson.Polygon;


public class PostgisConcaveHullBuilder implements HullBuilder<Node> {
	
	private final DataSource dataSource;
	private long duration_loading = 0L;
	private long duration_hull = 0L;
	
	public PostgisConcaveHullBuilder(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Polygon getHullAsPolygon(List<Node> nodes) {
		
		duration_loading = duration_hull = 0L;
		
		TransactionDefinition txDefinition =
				new DefaultTransactionDefinition(TransactionDefinition.ISOLATION_DEFAULT);
		DataSourceTransactionManager txManager =
				new DataSourceTransactionManager(dataSource);
		TransactionStatus tx = txManager.getTransaction(txDefinition);
		JdbcTemplate template = new JdbcTemplate(dataSource);
		
		long start = System.currentTimeMillis();
		template.execute("CREATE TEMP TABLE isochrone_points (geom geometry(Point))");
		List<Object[]> points = new ArrayList<Object[]>();
		for (Node node : nodes) {
			points.add(node.asCoordinatePair().toArray());
		}
		template.batchUpdate("INSERT INTO isochrone_points (geom) VALUES (st_makepoint(?, ?))", points);
		duration_loading = System.currentTimeMillis() - start;
		String json = template.queryForObject("SELECT st_asgeojson(st_concavehull(st_collect(geom), .98, false), 6) FROM isochrone_points", String.class);
		duration_hull = System.currentTimeMillis() - start - duration_loading;
		
		txManager.rollback(tx);
		try {
			Polygon polygon = (Polygon) GeoJsonObject.parse(json);
			return polygon;
		} catch (GeoJsonFormatException e) {
			return null;
		}
		
	}
	
	public List<Node> buildHull(List<Node> nodes) {
		
		Polygon polygon = getHullAsPolygon(nodes);
		
		List<Node> hull = new ArrayList<Node>();
		for (List<Double> pair : polygon.coordinates.get(0)) {
			Node node = new Node(0, pair.get(0), pair.get(1));
			hull.add(node);
		}
		
		return hull;
		
	}
	
	public double getLoadingDurationSeconds() {
		return (duration_loading / 1000.0);
	}
	
	public double getHullDurationSeconds() {
		return (duration_hull / 1000.0);
	}
	
}
