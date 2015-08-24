package fr.gouv.agriculture.test;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.Test;

public class HsqlDbTest extends AbstractHsqlDbTest {
	
	@Test
	public void testHsqlDb() throws IOException {
		
		int numberOfNodes = template.queryForObject("SELECT count(*) FROM nodes", Integer.class);
		int numberOfEdges = template.queryForObject("SELECT count(*) FROM edges", Integer.class);
		assertTrue(numberOfNodes > 0);
		assertTrue(numberOfEdges > 0);
		System.out.println("Number of nodes : " + numberOfNodes);
		System.out.println("Number of edges : " + numberOfEdges);
	
	}

}
