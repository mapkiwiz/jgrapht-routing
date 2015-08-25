package fr.gouv.agriculture.web.config;

import java.io.IOException;

import javax.sql.DataSource;

import org.jgrapht.Graph;
import org.jgrapht.graph.DefaultWeightedEdge;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import fr.gouv.agriculture.graph.DijsktraIteratorFactory;
import fr.gouv.agriculture.graph.JdbcEdgeListGraphLoader;
import fr.gouv.agriculture.graph.Node;
import fr.gouv.agriculture.graph.PriorityQueueDijkstraIterator;
import fr.gouv.agriculture.locator.IndexNodeLocator;
import fr.gouv.agriculture.locator.NodeLocator;

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
	public Graph<Node, DefaultWeightedEdge> roadGraph(DataSource dataSource) throws IOException {
		
		LOGGER.info("Loading road graph from JDBC connection");
		
		String nodeTemplateQuery = "SELECT n.id, st_x(n.geom) AS lon, st_y(n.geom) AS lat " +
				" FROM (SELECT id, st_transform(the_geom, 4326) AS geom" +
				" FROM bdtopo.routes_vertices_pgr ORDER BY id) n;";
		
		String edgeTemplateQuery = "SELECT source, target, cost AS weight FROM bdtopo.routes ORDER BY source, target";
		
		JdbcEdgeListGraphLoader loader = new JdbcEdgeListGraphLoader(
				dataSource,
				nodeTemplateQuery,
				edgeTemplateQuery);
		
		Graph<Node, DefaultWeightedEdge> graph = loader.loadGraph();
		
		LOGGER.info("Loading time : {} s.", loader.getLoadingTimeSeconds());
		LOGGER.info("Loaded {} nodes", graph.vertexSet().size());
		LOGGER.info("Loaded {} edges", graph.edgeSet().size());
		
		return graph;
		
	}
	
	@Bean
	public NodeLocator nodeLocator(Graph<Node, DefaultWeightedEdge> graph) {
		
		LOGGER.info("Building node index ...");
		NodeLocator locator = new IndexNodeLocator(graph.vertexSet());
		LOGGER.info("Index built.");
		
		return locator;
		
	}

}
