package com.github.mapkiwiz.graph.contraction;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

public class GraphContractorOptions extends Options {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3972991360149125345L;

	public GraphContractorOptions() {
		
		super();
		
		Option optInputNodeFile = 
				Option.builder("n")
				.longOpt("node-file")
				.hasArg().argName("filename")
				.desc("input node file")
				.required()
				.build();
		addOption(optInputNodeFile);
		
		Option optInputEdgeFile = Option.builder("e")
				.longOpt("edge-file")
				.hasArg().argName("filename")
				.desc("input edge file")
				.required()
				.build();
		addOption(optInputEdgeFile);
		
		Option optOutputDir =
				Option.builder("d")
				.longOpt("output-dir")
				.desc("output directory")
				.hasArg().argName("dirname")
				.required()
				.build();
		addOption(optOutputDir);
		
		Option optMaxNodes =
				Option.builder()
				.longOpt("max-nodes")
				.desc("search max nodes for each source node")
				.hasArg().argName("max")
				.type(Integer.class)
				.build();
		addOption(optMaxNodes);
		
		Option optCoordinatePrecision =
				Option.builder()
				.longOpt("precision")
				.hasArg().argName("precision")
				.type(Integer.class)
				.desc("input coordinate precision")
				.build();
		addOption(optCoordinatePrecision);
		
	}

}
