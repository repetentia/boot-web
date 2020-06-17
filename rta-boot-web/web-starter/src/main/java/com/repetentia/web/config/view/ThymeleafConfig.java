package com.repetentia.web.config.view;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.web.servlet.ViewResolver;
import org.thymeleaf.spring5.SpringTemplateEngine;
import org.thymeleaf.spring5.templateresolver.SpringResourceTemplateResolver;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;
import org.thymeleaf.templatemode.TemplateMode;

import com.repetentia.support.log.Marker;

import lombok.extern.slf4j.Slf4j;
import nz.net.ultraq.thymeleaf.LayoutDialect;

@Slf4j
@Configuration
public class ThymeleafConfig {
	private final String prefix = "classpath:templates/";
	private final String suffix = ".html";
	private final String encoding = "UTF-8";
	private final boolean useCache = false;
	private final int order = 1;
	private final Environment env;

	public ThymeleafConfig(Environment env) {
		this.env = env;
		env.getProperty("prefix");
	}

	@Bean
	public SpringResourceTemplateResolver templateResolver() {
		SpringResourceTemplateResolver templateResolver = new SpringResourceTemplateResolver();
		templateResolver.setPrefix(prefix);
		templateResolver.setCharacterEncoding(encoding);
		templateResolver.setSuffix(suffix);
		templateResolver.setTemplateMode(TemplateMode.HTML);
		templateResolver.setCacheable(useCache);
		return templateResolver;
	}

	@Bean
	public SpringTemplateEngine templateEngine(MessageSource messageSource) {
		SpringTemplateEngine templateEngine = new SpringTemplateEngine();
		templateEngine.setTemplateResolver(templateResolver());
		templateEngine.setTemplateEngineMessageSource(messageSource);
		templateEngine.addDialect(layoutDialect());
		return templateEngine;
	}

	@Bean
	public LayoutDialect layoutDialect() {
		return new LayoutDialect();
	}

	@Bean
	@Autowired
	public ViewResolver viewResolver(MessageSource messageSource) {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine(messageSource));
		viewResolver.setCharacterEncoding("UTF-8");
		viewResolver.setOrder(order);
		viewResolver.setCache(useCache);
		log.info(Marker.BOOT_CONFIG, "- initializing ThymeleafViewResolver - order : {}", order);
		return viewResolver;
	}

}
