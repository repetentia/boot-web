package com.repetentia.web.component.property;

import java.util.Map;

import org.springframework.core.env.MapPropertySource;

public class DatabasePropertySource extends MapPropertySource {
    public DatabasePropertySource(String name, Map<String, Object> source) {
        super(name, source);
    }
}