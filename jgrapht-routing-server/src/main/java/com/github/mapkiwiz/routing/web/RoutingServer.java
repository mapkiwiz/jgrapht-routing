package com.github.mapkiwiz.routing.web;

import java.util.EnumSet;

import javax.servlet.DispatcherType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.ParseException;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import com.github.mapkiwiz.routing.web.controller.ApiControllerBase.AppInfo;
import com.github.mapkiwiz.web.filter.RequestLoggerFilter;

public class RoutingServer {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RoutingServer.class);
	private Server server;
	private int port = 8080;
	
	public RoutingServer() {
		AppInfo appInfo = new AppInfo();
		LOGGER.info("{} Version {} API v{}", getClass().getSimpleName(), "0.5-SNAPSHOT", appInfo.version);
	}
	
	public void start() throws Exception {
		
		ResourceHandler resourceHandler = new ResourceHandler();
		resourceHandler.setDirectoriesListed(false);
		resourceHandler.setResourceBase("./src/main/webapp");
		resourceHandler.setWelcomeFiles(new String[] {
			"index.html"
		});
		
		ServletContextHandler servletContextHandler = new ServletContextHandler(
				ServletContextHandler.NO_SECURITY |
				ServletContextHandler.NO_SESSIONS);
		servletContextHandler.setContextPath("/routing");
		AnnotationConfigWebApplicationContext context = new AnnotationConfigWebApplicationContext();
		context.scan("com.github.mapkiwiz.routing.web.config");
		context.registerShutdownHook();
		DispatcherServlet servlet = new DispatcherServlet(context);
		servletContextHandler.addServlet(new ServletHolder(servlet), "/*");
		
		//registerProxyFilter(servletContextHandler, "requestLoggerFilter");
		servletContextHandler.addFilter(RequestLoggerFilter.class, "/*", EnumSet.of(DispatcherType.REQUEST));
		
		HandlerList handlers = new HandlerList();
		handlers.addHandler(resourceHandler);
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
	
//	private void registerProxyFilter(ServletContextHandler contextHandler, String name) {
//		DelegatingFilterProxy filter = new DelegatingFilterProxy(name);
//		filter.setContextAttribute("org.springframework.web.servlet.FrameworkServlet.CONTEXT.dispatcher");
//		contextHandler.addFilter(new FilterHolder(filter), "/*", EnumSet.of(DispatcherType.REQUEST));
//	}
	
	public static void main(String[] args) {
		
		try {
			
			DefaultParser commandLineParser = new DefaultParser();
			CommandLine options = commandLineParser.parse(
					new RoutingServerOptions(), args);
			
			final RoutingServer server = new RoutingServer();
			server.port = Integer.valueOf(options.getOptionValue("p", "8080"));
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
			helpFormatter.printHelp("server", new RoutingServerOptions());
			
			LOGGER.debug(e.getMessage(), e);
			
		} catch (Exception e) {
			
			LOGGER.error(e.getMessage(), e);
			
		}
		
	}

}
