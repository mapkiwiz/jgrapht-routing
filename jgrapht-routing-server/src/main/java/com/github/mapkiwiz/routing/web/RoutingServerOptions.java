package com.github.mapkiwiz.routing.web;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class RoutingServerOptions extends Options {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 140600159020097560L;

	public RoutingServerOptions() {
		
		Option optPort = new Option("p", "port", true, "server port");
		optPort.setType(Integer.class);
		optPort.setArgName("port");
		addOption(optPort);
		
		Option optConfig = new Option("c", "config", true, "configuration property file");
		optConfig.setArgName("propertyfile");
		optConfig.setType(String.class);
		addOption(optConfig);
		
	}

}
