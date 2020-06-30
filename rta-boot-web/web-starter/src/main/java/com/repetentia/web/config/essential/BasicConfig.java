package com.repetentia.web.config.essential;

import org.apache.ibatis.session.SqlSession;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import com.repetentia.support.log.Marker;
import com.repetentia.web.component.message.DatabaseResourceBundleMessageSource;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class BasicConfig {
    private SqlSession sqlSession;
    BasicConfig(SqlSession sqlSession) {
    	this.sqlSession = sqlSession;
    }

    @Bean(name = "messageSource")
    public MessageSource messagSource() {
        DatabaseResourceBundleMessageSource databaseMessageSource = new DatabaseResourceBundleMessageSource();
        databaseMessageSource.setSqlSession(sqlSession);
        log.info(Marker.BOOT_CONFIG, "- Initializing DatabaseResourceBundleMessageSource !!!");
        return databaseMessageSource;
    }
    
    @Bean
    public TaskExecutor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);
        executor.setMaxPoolSize(10);
        executor.setQueueCapacity(25);
        log.info(Marker.BOOT_CONFIG, "- Initializing ThreadPoolTaskExecutor !!!");
        return executor;
    }
}
