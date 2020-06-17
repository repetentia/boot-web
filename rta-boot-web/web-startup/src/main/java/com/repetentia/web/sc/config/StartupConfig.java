package com.repetentia.web.sc.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.repetentia.web.config.data.DataSourceConfig;
import com.repetentia.web.config.essential.BasicConfig;

@Configuration
@Import({ DataSourceConfig.class, BasicConfig.class })
public class StartupConfig {

}
