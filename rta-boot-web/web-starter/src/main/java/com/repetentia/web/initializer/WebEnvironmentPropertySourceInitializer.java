package com.repetentia.web.initializer;

import javax.servlet.ServletContext;

import org.springframework.boot.context.event.ApplicationEnvironmentPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.core.Ordered;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.web.context.ConfigurableWebEnvironment;
import org.springframework.web.context.ServletContextAware;

public class WebEnvironmentPropertySourceInitializer
		implements ApplicationListener<ApplicationEnvironmentPreparedEvent>, Ordered, ServletContextAware {

	private ServletContext servletContext;

	public WebEnvironmentPropertySourceInitializer() {
		this.servletContext = null;

	}

	@Override
	public void onApplicationEvent(ApplicationEnvironmentPreparedEvent event) {
		ConfigurableEnvironment environment = event.getEnvironment();

		if (environment instanceof ConfigurableWebEnvironment) {
			((ConfigurableWebEnvironment) environment).initPropertySources(this.servletContext, null);
		}
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE;
	}

	@Override
	public void setServletContext(ServletContext servletContext) {
		this.servletContext = servletContext;
	}

}