package fr.gouv.agriculture.test;

import javax.sql.DataSource;

import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class DatabaseTestHelper {

	public static DataSource getDataSource() {

		String url = "jdbc:postgresql://localhost:5432/refgeo";
		DriverManagerDataSource dataSource =
				new DriverManagerDataSource(url);
		dataSource.setDriverClassName("org.postgresql.Driver");
		dataSource.setUsername("refgeo");
		dataSource.setPassword("refgeo");

		return dataSource;

	}

}
