package com.github.mapkiwiz.graph.loader;

import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.graph.SimpleWeightedGraph;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;

import com.github.mapkiwiz.geo.Node;



public class JdbcEdgeListGraphLoader extends AbstractEdgeListGraphLoader<Node, DefaultWeightedEdge, Long> {

	private final DataSource dataSource;
	private final String nodeTemplateQuery;
	private final String edgeTemplateQuery;

	public JdbcEdgeListGraphLoader(DataSource dataSource, String nodeTemplateQuery, String edgeTemplateQuery) {
		this.dataSource = dataSource;
		this.nodeTemplateQuery = nodeTemplateQuery;
		this.edgeTemplateQuery = edgeTemplateQuery;
	}

	@Override
	public Iterator<Node> getNodeIterator() throws IOException {
		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.query(nodeTemplateQuery, new RowIterator<Node>() {

			@Override
			public Node next(ResultSet results) throws DataAccessException,
					SQLException {
				
				Long id = results.getLong("id");
				double lon = results.getDouble("lon");
				double lat = results.getDouble("lat");
				return new Node(id, lon, lat);
				
			}
			
		});
		
	}

	@Override
	public Iterator<EdgeData> getEdgeIterator() throws IOException {

		JdbcTemplate template = new JdbcTemplate(dataSource);
		return template.query(edgeTemplateQuery, new RowIterator<EdgeData>() {

			@Override
			public EdgeData next(ResultSet results) throws DataAccessException,
					SQLException {
				
				EdgeData edgeData = new EdgeData();
				edgeData.source = results.getLong("source");
				edgeData.target = results.getLong("target");
				edgeData.weight = results.getDouble("weight");
				return edgeData;
				
			}
			
		});

	}
	
	static abstract class RowIterator<T> implements ResultSetExtractor<Iterator<T>> {
		
		public Iterator<T> extractData(ResultSet rs)
				throws SQLException, DataAccessException {
			
			List<T> results = new ArrayList<T>();
			while (rs.next()) {
				results.add(next(rs));
			}
			
			return results.iterator();
			
		}
		
		public abstract T next(ResultSet results) throws DataAccessException, SQLException;
		
	}

	@Override
	public Map<Long, Node> getNodeMap() {
		return nodeMap;
	}
	
	@Override
	public Graph<Node, DefaultWeightedEdge> createNewGraph() {
		return new SimpleWeightedGraph<Node, DefaultWeightedEdge>(DefaultWeightedEdge.class);
	}

	@Override
	public Long getNodeId(Node node) {
		return node.id;
	}

	@Override
	public void addEdge(Graph<Node, DefaultWeightedEdge> graph, Node source, Node target, double weight) {
		
		DefaultWeightedEdge edge = graph.addEdge(source, target);
		((SimpleWeightedGraph<Node, DefaultWeightedEdge>) graph).setEdgeWeight(edge, weight);
		
	}

}
