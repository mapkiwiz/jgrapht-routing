package com.github.mapkiwiz.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.OncePerRequestFilter;

import com.github.mapkiwiz.routing.web.RoutingServer;

public class RequestLoggerFilter extends OncePerRequestFilter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RoutingServer.class);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		long startTime = System.currentTimeMillis();
		filterChain.doFilter(request, response);
		long duration = System.currentTimeMillis() - startTime;
		
		String method = request.getRequestURI().replaceFirst("/", "");
		LOGGER.info("[{}] {} t={} ms.", response.getStatus(), method, duration);
		
	}

}
