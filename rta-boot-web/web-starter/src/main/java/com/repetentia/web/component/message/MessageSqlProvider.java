package com.repetentia.web.component.message;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.ibatis.jdbc.SQL;

import kr.co.ydns.grip.bootstrap.support.sql.SqlProviderUtils;

public class MessageSqlProvider {
    private static final Class<GripMessageSource> clazz = GripMessageSource.class;
	public String findAll() {
		SQL sql = new SQL();
		String table = SqlProviderUtils.table(clazz);
		sql.FROM(table);
		List<String> list = SqlProviderUtils.columns(clazz);
		sql.SELECT(list.toArray(new String[list.size()]));
		return sql.toString();
	}

	public String update() {
		SQL sql = new SQL();
		String table = SqlProviderUtils.table(clazz);
		sql.UPDATE(table);
		List<String> sets = SqlProviderUtils.keyColumns(clazz, false);
		for (String set:sets) {
			sql.SET(set);	
		}
		List<String> conditions = SqlProviderUtils.keyColumns(clazz, true);
		sql.WHERE(conditions.toArray(new String[conditions.size()]));
		return sql.toString();		
	}
	
	public String insert() {
		SQL sql = new SQL();
		String table = SqlProviderUtils.table(clazz);
		sql.INSERT_INTO(table);
		Map<String, String> insertColumns = SqlProviderUtils.insertColumns(clazz);
		Set<Entry<String, String>> entrySet = insertColumns.entrySet();
		for (Entry<String, String> entry:entrySet) {
			sql.VALUES(entry.getKey(), "#{" + entry.getValue() + "}");
		}
		return sql.toString();
	}
}
