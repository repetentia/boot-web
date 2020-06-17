package com.repetentia.web.config.data;

import javax.sql.DataSource;

import org.apache.commons.dbcp2.BasicDataSource;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import com.repetentia.support.log.Marker;
import com.repetentia.utils.common.StringUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class DataSourceConfig implements InitializingBean {
	@Autowired
	private DataSourceProperties dataSourceProperties;
	private static final String LICENSE = "rta.server.license";
	private static final String LICENSE_INFO = "rta.server.license.info";

	private Environment env;

	public DataSourceConfig(Environment env) {
		this.env = env;
		log.info(Marker.BEAN_INIT, "- Creating Bean DataSourceConfig");
	}

	@Bean
	public DataSource dataSource() {
		BasicDataSource bds = new BasicDataSource();
		bds.setDriverClassName(dataSourceProperties.getDriverClassName());
		bds.setUrl(dataSourceProperties.getUrl());
		bds.setUsername(dataSourceProperties.getUsername());
		bds.setPassword(dataSourceProperties.getPassword());
		String validationQuery = "SELECT 1";
		Integer initialSize = 2;
		Integer maxTotal = 2;
		bds.setInitialSize(initialSize);
		bds.setMaxTotal(maxTotal);
		bds.setValidationQuery(validationQuery);
		log.info(Marker.BOOT_CONFIG, "- Creating DataSource - {}", dataSourceProperties.getDriverClassName());
		return bds;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info(Marker.AFTER_PROP, "- DataSourceConfig after properties set - {}",
				dataSourceProperties.getDriverClassName());
		if (StringUtils.isEmpty(dataSourceProperties.getDriverClassName())) {
			String licenseKey = env.getProperty(LICENSE);
			setDataSourceProperties(licenseKey);
		}
	}

	private boolean setDataSourceProperties(String licenseKey) {
		if (StringUtils.isEmpty(licenseKey))
			return false;
		String licenseInfo = env.getProperty(LICENSE_INFO);

		return false;
	}
}