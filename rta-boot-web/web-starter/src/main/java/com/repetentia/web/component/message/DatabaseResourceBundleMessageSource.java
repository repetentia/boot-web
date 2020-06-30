package com.repetentia.web.component.message;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;

import org.apache.ibatis.session.Configuration;
import org.apache.ibatis.session.SqlSession;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.support.AbstractMessageSource;

import com.repetentia.support.log.Marker;
import com.repetentia.support.reload.ReLoadable;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DatabaseResourceBundleMessageSource extends AbstractMessageSource implements InitializingBean, ReLoadable {
	private static final String id = "messageSource";
	private SqlSession sqlSession;
	private LocaleMessageHolder localeMessageHolder;

	@Override
	protected MessageFormat resolveCode(String code, Locale locale) {
		MessageFormat messageFormat = localeMessageHolder.resolveCode(code, locale);
		log.debug(Marker.MESSAGE, "message [{} : {}] - [{}]", code, locale, messageFormat.format(null));
		return messageFormat;
	}

	public void setSqlSession(SqlSession sqlSession) {
		this.sqlSession = sqlSession;
		Configuration configuration = sqlSession.getConfiguration();
		configuration.addMapper(MessageMapper.class);
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		reload();
	}

	private void load() {
		log.info(Marker.DATA_LOAD, "- Loading Messages from Database !!!");
		List<RtaMessageSource> list = sqlSession.getMapper(MessageMapper.class).findAll();
		LocaleMessageHolder localeMessageHolder = new LocaleMessageHolder();
		localeMessageHolder.load(list);
		this.localeMessageHolder = localeMessageHolder;
	}

	@Override
	public void reload() {
		load();
	}

	@Override
	public String id() {
		return id;
	}
}
