package com.repetentia.web.initializer;

import org.springframework.boot.web.servlet.support.ErrorPageFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class ErrorPageFilterConfiguration {

	@Bean
	ErrorPageFilter errorPageFilter() {
		return new ErrorPageFilter();
	}

}
