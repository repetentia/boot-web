package com.repetentia.web.component.property;

import java.util.List;

import org.apache.ibatis.annotations.SelectProvider;

public interface PropertyMapper {
    @SelectProvider(type = PropertySqlProvider.class,  method = "findAll")
    public List<RtaPropertySource> findAll(RtaPropertySource condition);
}
