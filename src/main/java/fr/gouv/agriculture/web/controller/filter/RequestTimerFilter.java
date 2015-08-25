package fr.gouv.agriculture.web.controller.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.filter.OncePerRequestFilter;

import com.timgroup.statsd.StatsDClient;

public class RequestTimerFilter extends OncePerRequestFilter {
	
	private final static Logger LOGGER = LoggerFactory.getLogger(RequestTimerFilter.class);

	@Autowired
	private StatsDClient statsdClient;
	
	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		long start = System.currentTimeMillis();
		filterChain.doFilter(request, response);
		long duration = System.currentTimeMillis() - start;
		
		String method = request.getRequestURI();
		LOGGER.info("{}, {} ms.", method, duration);
		statsdClient.incrementCounter(method);
		statsdClient.recordExecutionTime(method, duration);
		
	}

}
