package com.repetentia.web.component.property;

import java.util.List;

import org.apache.ibatis.jdbc.SQL;

import com.repetentia.support.sql.SqlProviderUtils;

public class PropertySqlProvider {
    private static final Class<RtaPropertySource> clazz = RtaPropertySource.class;
    public String findAll(RtaPropertySource condition) {
        SQL sql = new SQL();
        String table = SqlProviderUtils.table(clazz);
        sql.FROM(table);
        List<String> list = SqlProviderUtils.columns(clazz);
        sql.SELECT(list.toArray(new String[list.size()]));
        List<String> conditions = SqlProviderUtils.where(condition);
        sql.WHERE(conditions.toArray(new String[conditions.size()]));
        return sql.toString();
    }
}
