package com.repetentia.web.component.property;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.sql.DataSource;

import org.apache.ibatis.mapping.Environment;
import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;
import org.apache.ibatis.transaction.TransactionFactory;
import org.apache.ibatis.transaction.jdbc.JdbcTransactionFactory;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertySource;

import com.repetentia.web.config.data.DataSourceConfig;


public class DatabasePropertyLoader {
    private final static String RTA_PROPERTY_SOURCE = "RtaPropertySource";
    private final static String DEFAULT_SITE = "default";
    private final ConfigurableEnvironment env;

    public DatabasePropertyLoader(org.springframework.core.env.Environment env) {
        this.env = (ConfigurableEnvironment) env;
    }

    public RtaPropertySource conditions(String profile) {
        String site = env.getProperty("rta.site", DEFAULT_SITE);
        RtaPropertySource conditions = new RtaPropertySource();
        conditions.setProfile(profile);
        conditions.setSite(site);
        return conditions;
    }

    public List<String> activeProfiles() {
        String[] profiles = env.getActiveProfiles();
        
        List<String> list = new ArrayList<String>();
        boolean hasDefault = false;
        for (String profile:profiles) {
            list.add(profile);
            if (DEFAULT_SITE.equals(profile)) hasDefault = true;
        }
        if (!hasDefault) list.add(0, DEFAULT_SITE);
        
        return list;
    }

    public void loadDataBasePropertySource() {
        Map<String, Object> sourceMap = listToSourceMap();
        PropertySource<?> propertySource = new DatabasePropertySource(RTA_PROPERTY_SOURCE, sourceMap);
        this.env.getPropertySources().addFirst(propertySource);
    }

    public Map<String, Object> listToSourceMap() {
        Map<String, Object> sourceMap = new HashMap<>();
        List<String> profiles = activeProfiles();

        for (String profile : profiles) {
            List<RtaPropertySource> list = loadPropertySource(profile);
            for (RtaPropertySource propertySource : list) {
                String key = propertySource.getPropertyKey();
                String value = propertySource.getPropertyValue();
                sourceMap.put(key, value);
            }
        }

        return sourceMap;
    }

    public List<RtaPropertySource> loadPropertySource(String profile) {
        DataSourceConfig dsc = new DataSourceConfig(env);
        DataSource dataSource = dsc.dataSource();
        TransactionFactory transactionFactory = new JdbcTransactionFactory();
        Environment environment = new Environment("pre", transactionFactory, dataSource);
        Configuration configuration = new Configuration(environment);
        configuration.addMapper(PropertyMapper.class);
        SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(configuration);
        SqlSession sqlSession = sqlSessionFactory.openSession();
        RtaPropertySource conditions = conditions(profile);
        List<RtaPropertySource> list = sqlSession.getMapper(PropertyMapper.class).findAll(conditions);
        sqlSession.close();
        return list;
    }
}