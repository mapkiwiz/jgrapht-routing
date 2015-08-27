package com.github.mapkiwiz.locator;

import java.sql.ResultSet;
import java.sql.SQLException;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

import com.github.mapkiwiz.geo.Node;


public class DatabaseNodeLocator implements NodeLocator {
	
	private final DataSource dataSource;
	private final String templateQuery;
	
	public DatabaseNodeLocator(DataSource dataSource, String templateQuery) {
		assert(dataSource != null);
		this.dataSource = dataSource;
		this.templateQuery = templateQuery;
	}
	
	public Node locate(double lon, double lat, double maxDistance) {
		
		JdbcTemplate template = new JdbcTemplate(dataSource);
		Node node = template.queryForObject(templateQuery, new NodeMapper(), lon, lat);
		return node;
		
	}
	
	public static class NodeMapper implements RowMapper<Node> {

		public Node mapRow(ResultSet rs, int rowNum) throws SQLException {
			int id = rs.getInt("id");
			double lon = rs.getDouble("lon");
			double lat = rs.getDouble("lat");
			return new Node(id, lon, lat);
		}
		
	}

}
