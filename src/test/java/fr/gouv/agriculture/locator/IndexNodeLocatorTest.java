package fr.gouv.agriculture.locator;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;

import org.junit.Test;

import fr.gouv.agriculture.graph.EdgeListGraphLoader;
import fr.gouv.agriculture.graph.Node;

public class IndexNodeLocatorTest {
	
	@Test
	public void testLocate() throws IOException {
		
		String nodeFile = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFile = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		EdgeListGraphLoader loader = new EdgeListGraphLoader();
		loader.loadGraph(nodeFile, edgeFile);
		IndexNodeLocator locator = new IndexNodeLocator(loader.getGraph().vertexSet());
		/*  id    |       lon        |       lat        
			-------+------------------+------------------
 			24951 | 4.83441389858932 | 45.7673043893699
		 */
		double lon = 4.834413; 
		double lat = 45.767304;
		
		long start = System.currentTimeMillis();
		Node node = locator.locate(lon, lat, 0.05);
		long duration = System.currentTimeMillis() - start;
		
		assertNotNull(node);
		assertEquals(24951, node.id);
		
		System.out.println(node);
		System.out.println("Duration : " + (duration / 1000.0));
		
		double average_duration = 0.0;
		for (int i=0; i<100; i++) {
			start = System.currentTimeMillis();
			node = locator.locate(lon, lat, 0.05);
			average_duration = (i*average_duration + System.currentTimeMillis() - start) / (i+1);
			assertNotNull(node);
		}
		
		System.out.println("Average Duration for 100*locate() : " + (average_duration / 1000.0));
		
	}

}
