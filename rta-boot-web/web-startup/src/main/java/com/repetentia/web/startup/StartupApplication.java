package com.repetentia.web.startup;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

import com.repetentia.web.initializer.GripServletInitializer;
import com.repetentia.web.initializer.WebEnvironmentPropertySourceInitializer;

@SpringBootApplication
@EnableAsync
@ComponentScan({ "com.repetentia.web" })
public class StartupApplication extends GripServletInitializer {
	public static void main(String[] args) {
		args = new String[1];
		args[0] = "--spring.profiles.active=local";
		SpringApplication sa = new SpringApplication(StartupApplication.class);
		sa.addListeners(new WebEnvironmentPropertySourceInitializer());
		sa.run(args);
	}

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
		return application.sources(StartupApplication.class);
	}

	public void empty() {
	}
}
