package fr.gouv.agriculture.geo.algorithm;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import fr.gouv.agriculture.geo.Node;
import fr.gouv.agriculture.geojson.GeoJsonFormatException;
import fr.gouv.agriculture.geojson.GeoJsonObject;
import fr.gouv.agriculture.geojson.Polygon;

public class PostgisConcaveHullBuilder implements HullBuilder {
	
	private final DataSource dataSource;
	private long duration_loading = 0L;
	private long duration_hull = 0L;
	
	public PostgisConcaveHullBuilder(DataSource dataSource) {
		this.dataSource = dataSource;
	}
	
	public Polygon buildHull(List<Node> nodes) {
		
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
		Polygon hull;
		try {
			hull = (Polygon) GeoJsonObject.parse(json);
			return hull;
		} catch (GeoJsonFormatException e) {
			return null;
		}
		
	}
	
	public double getLoadingDurationSeconds() {
		return (duration_loading / 1000.0);
	}
	
	public double getHullDurationSeconds() {
		return (duration_hull / 1000.0);
	}
	
}
