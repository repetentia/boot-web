package com.repetentia.web.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.scheduling.annotation.EnableAsync;

import com.repetentia.web.initializer.RtaServletInitializer;
import com.repetentia.web.initializer.WebEnvironmentPropertySourceInitializer;
import com.repetentia.web.support.ComponentScanExcludeFilter;
import com.repetentia.web.support.ComponentScanIncludeFilter;

@SpringBootApplication
@EnableAsync
@ComponentScan(basePackages = { "com.repetentia.web.sc" }
	, includeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = ComponentScanIncludeFilter.class)
	, excludeFilters = @ComponentScan.Filter(type = FilterType.CUSTOM, classes = ComponentScanExcludeFilter.class))
public class RtaStartupApplication extends RtaServletInitializer {
	public static void main(String[] args) {
		args = new String[1];
		args[0] = "--spring.profiles.active=local";
		SpringApplication sa = new SpringApplication(RtaStartupApplication.class);
		sa.addListeners(new WebEnvironmentPropertySourceInitializer());
		sa.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(RtaStartupApplication.class);
	}

	public void empty() {
	}
}