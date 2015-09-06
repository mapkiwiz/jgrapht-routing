package com.github.mapkiwiz.routing.web.config;

import java.util.Arrays;
import java.util.concurrent.ThreadPoolExecutor;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.http.MediaType;
import org.springframework.scheduling.concurrent.ThreadPoolExecutorFactoryBean;
import org.springframework.web.accept.ContentNegotiationManagerFactoryBean;
import org.springframework.web.servlet.View;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.DefaultServletHandlerConfigurer;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.view.ContentNegotiatingViewResolver;
import org.springframework.web.servlet.view.InternalResourceViewResolver;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import com.github.mapkiwiz.routing.web.controller.ApiController;
import com.timgroup.statsd.NonBlockingStatsDClient;
import com.timgroup.statsd.StatsDClient;


/**
 *
 */
@Configuration
@EnableWebMvc
public class WebMvcConfig extends WebMvcConfigurerAdapter {
	
	@Bean
	public StatsDClient statsdClient() {
		return new NonBlockingStatsDClient("routing", "localhost", 8125);
	}

	@Bean
	public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
		return new PropertySourcesPlaceholderConfigurer();
	}
	
	@Bean
	public ContentNegotiatingViewResolver contentViewResolver() throws Exception {
		ContentNegotiationManagerFactoryBean contentNegotiationManager = new ContentNegotiationManagerFactoryBean();
		contentNegotiationManager.addMediaType("json", MediaType.APPLICATION_JSON);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		MappingJackson2JsonView defaultView = new MappingJackson2JsonView();
		defaultView.setExtractValueFromSingleKeyModel(true);

		ContentNegotiatingViewResolver contentViewResolver = new ContentNegotiatingViewResolver();
		contentViewResolver.setContentNegotiationManager(contentNegotiationManager.getObject());
		contentViewResolver.setViewResolvers(Arrays.<ViewResolver> asList(viewResolver));
		contentViewResolver.setDefaultViews(Arrays.<View> asList(defaultView));
		return contentViewResolver;
	}
	
	@Bean
	public ThreadPoolExecutorFactoryBean threadPoolExecutorFactoryBean() {
		ThreadPoolExecutorFactoryBean bean = new ThreadPoolExecutorFactoryBean();
		bean.setThreadGroupName("routing");
		return bean;
	}
	
	@Bean
	public ApiController apiController(ThreadPoolExecutor executor) {
		executor.setCorePoolSize(4);
		executor.setMaximumPoolSize(4);
		ApiController controller = new ApiController(executor);
		return controller;
	}
	
//	@Bean
//	public StatelessRestController serviceController() {
//		return new StatelessRestController();
//	}
//	
//	@Bean
//	public StatefulWebController accessTokenController() {
//		return new StatefulWebController();
//	}
//	
//	@Bean
//	public AdminController adminController() {
//		return new AdminController();
//	}
//	
//	@Bean
//	public ApplicationController applicationController() {
//		return new ApplicationController();
//	}
	
//	@Bean
//	public BeanNameViewResolver beanNameViewResolver() {
//		return new BeanNameViewResolver();
//	}
	
	@Override
	public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//		configurer.enable();
	}
	
}
