package com.github.mapkiwiz.routing.web.config;

import java.io.IOException;
import java.net.URL;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.graph.DijsktraIteratorFactory;
import com.github.mapkiwiz.graph.PriorityQueueDijkstraIterator;
import com.github.mapkiwiz.graph.contraction.CSVPreparedGraphLoader;
import com.github.mapkiwiz.graph.loader.CSVEdgeListGraphLoader;
import com.github.mapkiwiz.locator.IndexNodeLocator;
import com.github.mapkiwiz.locator.NodeLocator;
import com.github.mapkiwiz.routing.web.controller.GraphHolder;


@Configuration
public class RoadGraphConfig {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoadGraphConfig.class);
	
	@Bean
	public DataSource dataSource() {
		
		String url = "jdbc:postgresql://localhost:5432/refgeo";
		DriverManagerDataSource dataSource =
				new DriverManagerDataSource(url);
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername("refgeo");
		dataSource.setPassword("refgeo");

		return dataSource;
		
	}
	
	@Bean
	public DijsktraIteratorFactory dijsktraIteratorFactory() {
		return new PriorityQueueDijkstraIterator.Factory();
	}
	
	@Bean
	public GraphHolder roadGraph(
			@Value("${data.node.file.url}") URL nodeFileURL,
			@Value("${data.edge.file.url}") URL edgeFileURL,
			@Value("${data.shortcut.file.url}") URL shortcutFileURL,
			@Value("${data.prepared}") boolean prepared,
			@Value("${data.coordinate.precision}") int precision) throws IOException {
		
//		LOGGER.info("Loading road graph from JDBC connection");
//		
//		String nodeTemplateQuery = "SELECT n.id, st_x(n.geom) AS lon, st_y(n.geom) AS lat " +
//				" FROM (SELECT id, st_transform(the_geom, 4326) AS geom" +
//				" FROM bdtopo.routes_vertices_pgr ORDER BY id) n;";
//		
//		String edgeTemplateQuery = "SELECT source, target, cost AS weight FROM bdtopo.routes ORDER BY source, target";
//		
//		JdbcEdgeListGraphLoader loader = new JdbcEdgeListGraphLoader(
//				dataSource,
//				nodeTemplateQuery,
//				edgeTemplateQuery);
		
		Graph<? extends Node, ?> graph;
		long startTime = System.currentTimeMillis();
		
		if (prepared) {
			LOGGER.info("Loading prepared graph from TSV files");
			CSVPreparedGraphLoader loader;
			if (shortcutFileURL != null) {
				loader = new CSVPreparedGraphLoader(nodeFileURL, edgeFileURL, shortcutFileURL);
			} else {
				loader = new CSVPreparedGraphLoader(nodeFileURL, edgeFileURL, true);
			}
			loader.setCoordinatePrecision(precision);
			graph = loader.loadGraph();
		} else {
			LOGGER.info("Loading road graph from TSV files");
			CSVEdgeListGraphLoader loader = new CSVEdgeListGraphLoader(nodeFileURL, edgeFileURL);
			loader.setCoordinatePrecision(precision);
			graph = loader.loadGraph();
		}
		
		double loadingTimeSeconds = (System.currentTimeMillis() - startTime) / 1000.0;
		
		LOGGER.info("Loading time : {} s.", loadingTimeSeconds);
		LOGGER.info("Loaded {} nodes", graph.vertexSet().size());
		LOGGER.info("Loaded {} edges", graph.edgeSet().size());
		
		return new GraphHolder(graph);
		
	}
	
	@Bean
	public NodeLocator<?> nodeLocator(GraphHolder holder) {
		
		LOGGER.info("Building node index ...");
		NodeLocator<Node> locator = new IndexNodeLocator<Node>(holder.getGraph(Node.class).vertexSet());
		LOGGER.info("Index built.");
		
		return locator;
		
	}

}
