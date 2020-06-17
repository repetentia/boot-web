package com.repetentia.web.initializer;

import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Collections;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.builder.ParentContextApplicationContextInitializer;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.context.AnnotationConfigServletWebServerApplicationContext;
import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.boot.web.servlet.support.ServletContextApplicationContextInitializer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.MergedAnnotations;
import org.springframework.core.annotation.MergedAnnotations.SearchStrategy;
import org.springframework.util.Assert;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.WebApplicationContext;


public abstract class RtaServletInitializer implements WebApplicationInitializer {

	protected Log logger; // Don't initialize early

	private boolean registerErrorPageFilter = true;

	/**
	 * Set if the {@link ErrorPageFilter} should be registered. Set to {@code false} if
	 * error page mappings should be handled via the server and not Spring Boot.
	 * @param registerErrorPageFilter if the {@link ErrorPageFilter} should be registered.
	 */
	protected final void setRegisterErrorPageFilter(boolean registerErrorPageFilter) {
		this.registerErrorPageFilter = registerErrorPageFilter;
	}

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		// Logger initialization is deferred in case an ordered
		// LogServletContextInitializer is being used
		this.logger = LogFactory.getLog(getClass());
		WebApplicationContext rootAppContext = createRootApplicationContext(servletContext);
		if (rootAppContext != null) {
			servletContext.addListener(new ContextLoaderListener(rootAppContext) {

				@Override
				public void contextInitialized(ServletContextEvent event) {
					// no-op because the application context is already initialized
				}

				@Override
				public void contextDestroyed(ServletContextEvent event) {
					try {
						super.contextDestroyed(event);
					}
					finally {
						deregisterJdbcDrivers(event.getServletContext());
					}
				}

			});

		}
		else {
			this.logger.debug("No ContextLoaderListener registered, as createRootApplicationContext() did not "
					+ "return an application context");
		}
	}

	/**
	 * Deregisters the JDBC drivers that were registered by the application represented by
	 * the given {@code servletContext}. The default implementation
	 * {@link DriverManager#deregisterDriver(Driver) deregisters} every {@link Driver}
	 * that was loaded by the {@link ServletContext#getClassLoader web application's class
	 * loader}.
	 * @param servletContext the web application's servlet context
	 * @since 2.3.0
	 */
	protected void deregisterJdbcDrivers(ServletContext servletContext) {
		for (Driver driver : Collections.list(DriverManager.getDrivers())) {
			if (driver.getClass().getClassLoader() == servletContext.getClassLoader()) {
				try {
					DriverManager.deregisterDriver(driver);
				}
				catch (SQLException ex) {
					// Continue
				}
			}
		}
	}

	protected WebApplicationContext createRootApplicationContext(ServletContext servletContext) {
		SpringApplicationBuilder builder = createSpringApplicationBuilder();
		builder.main(getClass());
		ApplicationContext parent = getExistingRootWebApplicationContext(servletContext);
		if (parent != null) {
			this.logger.info("Root context already created (using as parent).");
			servletContext.setAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE, null);
			builder.initializers(new ParentContextApplicationContextInitializer(parent));
		}
		builder.initializers(new ServletContextApplicationContextInitializer(servletContext));
		builder.contextClass(AnnotationConfigServletWebServerApplicationContext.class);
		builder = configure(builder);
		builder.listeners(new WebEnvironmentPropertySourceInitializer());
		SpringApplication application = builder.build();
		if (application.getAllSources().isEmpty()
				&& MergedAnnotations.from(getClass(), SearchStrategy.TYPE_HIERARCHY).isPresent(Configuration.class)) {
			application.addPrimarySources(Collections.singleton(getClass()));
		}
		Assert.state(!application.getAllSources().isEmpty(),
				"No SpringApplication sources have been defined. Either override the "
						+ "configure method or add an @Configuration annotation");
		// Ensure error pages are registered
		if (this.registerErrorPageFilter) {
			application.addPrimarySources(Collections.singleton(ErrorPageFilterConfiguration.class));
		}
		application.setRegisterShutdownHook(false);
		return run(application);
	}

	/**
	 * Returns the {@code SpringApplicationBuilder} that is used to configure and create
	 * the {@link SpringApplication}. The default implementation returns a new
	 * {@code SpringApplicationBuilder} in its default state.
	 * @return the {@code SpringApplicationBuilder}.
	 * @since 1.3.0
	 */
	protected SpringApplicationBuilder createSpringApplicationBuilder() {
		return new SpringApplicationBuilder();
	}

	/**
	 * Called to run a fully configured {@link SpringApplication}.
	 * @param application the application to run
	 * @return the {@link WebApplicationContext}
	 */
	protected WebApplicationContext run(SpringApplication application) {
		return (WebApplicationContext) application.run();
	}

	private ApplicationContext getExistingRootWebApplicationContext(ServletContext servletContext) {
		Object context = servletContext.getAttribute(WebApplicationContext.ROOT_WEB_APPLICATION_CONTEXT_ATTRIBUTE);
		if (context instanceof ApplicationContext) {
			return (ApplicationContext) context;
		}
		return null;
	}

	/**
	 * Configure the application. Normally all you would need to do is to add sources
	 * (e.g. config classes) because other settings have sensible defaults. You might
	 * choose (for instance) to add default command line arguments, or set an active
	 * Spring profile.
	 * @param builder a builder for the application context
	 * @return the application builder
	 * @see SpringApplicationBuilder
	 */
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder;
	}

}
