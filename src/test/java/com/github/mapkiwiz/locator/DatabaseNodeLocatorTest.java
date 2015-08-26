package com.github.mapkiwiz.locator;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.github.mapkiwiz.geo.Node;
import com.github.mapkiwiz.locator.DatabaseNodeLocator;
import com.github.mapkiwiz.test.DatabaseTestHelper;
import com.github.mapkiwiz.test.PostgisTest;


@PostgisTest
@Category(PostgisTest.class)
public class DatabaseNodeLocatorTest {
	
	@Test
	public void testLocate() {
		
		String query = "SELECT * FROM bdtopo.nearest_vertices_lonlat(?, ?)";
		DataSource dataSource = DatabaseTestHelper.getDataSource();
		DatabaseNodeLocator locator = new DatabaseNodeLocator(dataSource, query);
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
		assertTrue(24951L == node.id);
		
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
