package com.repetentia.commons.web.component.mybatis;

import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.ibatis.binding.BindingException;
import org.apache.ibatis.binding.MapperMethod.ParamMap;
import org.apache.ibatis.executor.Executor;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.ParameterMapping;
import org.apache.ibatis.mapping.SqlSource;
import org.apache.ibatis.plugin.Interceptor;
import org.apache.ibatis.plugin.Intercepts;
import org.apache.ibatis.plugin.Invocation;
import org.apache.ibatis.plugin.Plugin;
import org.apache.ibatis.plugin.Signature;
import org.apache.ibatis.session.ResultHandler;
import org.apache.ibatis.session.RowBounds;

import com.repetentia.commons.web.component.SqlFormatter;
import com.repetentia.support.log.Marker;
import com.repetentia.utils.common.ReflectionUtils;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Intercepts({ @Signature(type = Executor.class, method = "query", args = { MappedStatement.class, Object.class,
		RowBounds.class, ResultHandler.class }) })
public class QueryLogInterceptor implements Interceptor {

	@Override
	public Object intercept(Invocation invocation) throws Throwable {

		Object[] args = invocation.getArgs();

		MappedStatement ms = (MappedStatement) args[0];
		String id = ms.getId();
		log.debug(Marker.SQL, "[ ID] {}", id);
		Object param = (Object) args[1];
		SqlSource sqlSource = ms.getSqlSource();
		BoundSql boundSql = sqlSource.getBoundSql(param);
		List<ParameterMapping> parameterMappings = boundSql.getParameterMappings();
		Pattern pattern = Pattern.compile("\\?");
		Matcher matcher = pattern.matcher(boundSql.getSql());
		StringBuffer result = new StringBuffer();

		if (param == null) {
			// do nothing
		} else if (param instanceof ParamMap) {
			int idx = 0;
			for (ParameterMapping parameterMapping : parameterMappings) {
				matcher.find();

				String property = parameterMapping.getProperty();
				ParamMap<?> paramMap = (ParamMap<?>) param;

				Object value = null;
				try {
					value = paramMap.get(property);
				} catch (BindingException e) {
					log.trace(Marker.SQL, "{}", e);
					idx++;
					value = paramMap.get("param" + idx);
				}
				if (value != null) {
					Class<?> clazz = value.getClass();
					if (clazz.equals(java.lang.String.class)) {
						value = "'" + value + "'";
					}
					matcher.appendReplacement(result, value.toString());
				} else {
					matcher.appendReplacement(result, "null");
				}

			}
		} else if (param instanceof String) {
			matcher.find();
			Object value = param;
			if (value != null) {
				Class<?> clazz = value.getClass();
				if (clazz.equals(java.lang.String.class)) {
					value = "'" + value + "'";
				}
				matcher.appendReplacement(result, value.toString());
			}
		} else {
			for (ParameterMapping parameterMapping : parameterMappings) {
				String property = parameterMapping.getProperty();

				matcher.find();

				Object value = ReflectionUtils.getPropertyObject(param, property);
				if (value != null) {
					Class<?> clazz = value.getClass();
					if (clazz.equals(java.lang.String.class)) {
						value = "'" + value + "'";
					}
					matcher.appendReplacement(result, value.toString());
				} else {
					matcher.appendReplacement(result, "null");
				}
			}
		}
		matcher.appendTail(result);
		String formattedSQL = new SqlFormatter().format(result);
		log.debug(Marker.SQL, "[query] {}", formattedSQL);
		return invocation.proceed();
	}

	@Override
	public Object plugin(Object target) {
		return Plugin.wrap(target, this);
	}

	@Override
	public void setProperties(Properties properties) {
	}

}
