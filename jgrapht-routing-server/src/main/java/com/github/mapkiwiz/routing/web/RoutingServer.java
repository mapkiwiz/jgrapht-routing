package com.github.mapkiwiz.routing.web;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.Properties;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.servlet.FilterHolder;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.util.ClassUtils;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.github.mapkiwiz.routing.web.config.WebMvcConfig;
import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.AppInfo;
import com.github.mapkiwiz.web.filter.RequestLoggerFilter;

public class RoutingServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoutingServer.class);
	private Server server;
	private int port = 8080;
	private Properties properties;
	
	public RoutingServer(CommandLine options) throws IOException {
		
		AppInfo appInfo = new AppInfo();
		LOGGER.info("{} Version {} API v{}", getClass().getSimpleName(), "0.5-SNAPSHOT", appInfo.version);
		setProperties(options);
		
	}
	
	public void start() throws Exception {
		
		ServletContextHandler servletContextHandler = new ServletContextHandler(
				ServletContextHandler.NO_SECURITY |
				ServletContextHandler.NO_SESSIONS);
		servletContextHandler.setContextPath("/routing");
		
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.scan(ClassUtils.getPackageName(WebMvcConfig.class));
		context.registerShutdownHook();
		
		PropertySourcesPlaceholderConfigurer placeholderConfigurer = new PropertySourcesPlaceholderConfigurer();
		placeholderConfigurer.setProperties(properties);
		context.addBeanFactoryPostProcessor(placeholderConfigurer);
		
		DispatcherServlet servlet = new DispatcherServlet(context);
		servletContextHandler.addServlet(new ServletHolder("dispatcherServlet", servlet), "/*");
		
		RequestLoggerFilter filter = new RequestLoggerFilter();
		filter.setLogger(LOGGER);
		servletContextHandler.addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
		
		HandlerList handlers = new HandlerList();
		handlers.addHandler(servletContextHandler);
		
		server = new Server(port);
		server.setHandler(handlers);
		server.start();
		
	}
	
	public void stop() {
		
		if (server == null) return;
		
		try {
			LOGGER.info("Server shutting down ...");
			server.stop();
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		}
		
	}
	
	protected void setProperties(CommandLine options) throws IOException {
		
		properties = new Properties();
		LOGGER.info("Loading default configuration properties");
		properties.load(ClassUtils.getDefaultClassLoader().getResourceAsStream("routing.default.properties"));
		
		String config;
		
		if ((config = options.getOptionValue("c", null)) != null) {
			LOGGER.info("Reading configuration from file {}", config);
			properties = new Properties(properties);
			properties.load(new FileInputStream(config));
		}
		
		port = Integer.valueOf(options.getOptionValue("p", properties.getProperty("http.port")));
		
	}
	
	public static void main(String[] args) {
		
		try {
			
			DefaultParser commandLineParser = new DefaultParser();
			CommandLine options = commandLineParser.parse(
					new RoutingServerOptions(), args);
			
			final RoutingServer server = new RoutingServer(options);
			server.start();
			LOGGER.info("Server started on port {}", server.port);
			
			Runtime.getRuntime().addShutdownHook(new Thread() {

				@Override
				public void run() {
					server.stop();
				}
				
			});
			
		} catch (ParseException e) {
			
			LOGGER.error(e.getMessage());
			HelpFormatter helpFormatter = new HelpFormatter();
			helpFormatter.printHelp(RoutingServer.class.getSimpleName(), new RoutingServerOptions());
			
			LOGGER.debug(e.getMessage(), e);
			
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			
		}
		
	}

}
