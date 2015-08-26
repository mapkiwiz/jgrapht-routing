package com.github.mapkiwiz.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;

import javax.sql.DataSource;

import org.hsqldb.lib.InOutUtil;
import org.junit.After;
import org.junit.Before;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public abstract class AbstractHsqlDbTest {
	
	protected DataSource dataSource;
	protected JdbcTemplate template;
	
	@Before
	public void setUp() throws IOException {
		
		String nodeFilename = getClass().getClassLoader().getResource("bdtopo.nodes.tsv.gz").getFile();
		String edgeFilename = getClass().getClassLoader().getResource("bdtopo.edges.tsv.gz").getFile();
		File container = new File(nodeFilename).getParentFile();
		assertTrue(container.isDirectory());
		
		File nodeFile = new File(container, "hsqldb.nodes.tsv");
		if (!nodeFile.exists()) {
			gunzip(nodeFilename, nodeFile.getAbsolutePath());
		}
		File edgeFile = new File(container, "hsqldb.edges.tsv");
		if (!edgeFile.exists()) {
			gunzip(edgeFilename, edgeFile.getAbsolutePath());
		}
		
		String testDb = getClass().getClassLoader().getResource("test.db").getFile();
		String connectionString = "jdbc:hsqldb:file:" + testDb;
		DriverManagerDataSource dataSource = new DriverManagerDataSource(connectionString);
		dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
		this.dataSource = dataSource;
		
		template = new JdbcTemplate(dataSource);
//		template.execute("SET DATABASE TEXT TABLE DEFAULTS 'allow_full_path=true'");
		template.execute("CREATE TEXT TABLE nodes (id integer, lon integer, lat integer)");
		template.execute("SET TABLE nodes SOURCE 'hsqldb.nodes.tsv;fs=\\t;ignore_first=true' DESC");
		template.execute("CREATE TEXT TABLE edges (source integer, target integer, weight double precision, data integer)");
		template.execute("SET TABLE edges SOURCE 'hsqldb.edges.tsv;fs=\\t;ignore_first=true' DESC");
		
	}
	
	public void gunzip(String sourceFilename, String destFilename) throws IOException {
		
		FileInputStream fis = new FileInputStream(sourceFilename);
		GZIPInputStream zis = new GZIPInputStream(fis);
		FileOutputStream fos = new FileOutputStream(destFilename);
		InOutUtil.copy(zis, fos);
		fis.close();
		fos.close();
		
	}
	
	@After
	public void tearDown() {
		
		template.execute("DROP TABLE nodes");
		template.execute("DROP TABLE edges");
		
	}

}
