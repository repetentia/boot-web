package com.repetentia.web.component.message;

import java.text.MessageFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class LocaleMessageHolder {
	public final static Locale DEFAULT_LOCALE = Locale.KOREA;
	// locale, code
	private final ConcurrentMap<String, ConcurrentMap<String, MessageFormat>> localeMessagesHolder = new ConcurrentHashMap<>();

	public MessageFormat resolveCode(String code, Locale locale) {
		final ConcurrentMap<String, MessageFormat> messagesHolder = resolveLocale(locale);
		MessageFormat messageFormat = resolveCode(code, messagesHolder);
		if (messageFormat == null) return new MessageFormat("빈칸");
		return messageFormat;
	}
	
	private MessageFormat resolveCode(final String code, final ConcurrentMap<String, MessageFormat> messagesHolder) {
		if (messagesHolder == null) return null;
		return messagesHolder.get(code);
	}
	
	private ConcurrentMap<String, MessageFormat> resolveLocale(final Locale locale) {
		if (locale == null) {
			final ConcurrentMap<String, MessageFormat> defaultMessagesHolder = localeMessagesHolder.get(DEFAULT_LOCALE.toLanguageTag());
			return defaultMessagesHolder;
		}
		final ConcurrentMap<String, MessageFormat> messagesHolder = localeMessagesHolder.get(locale.toLanguageTag());
		if (messagesHolder == null) {
			final ConcurrentMap<String, MessageFormat> defaultMessagesHolder = localeMessagesHolder.get(DEFAULT_LOCALE.toLanguageTag());
			return defaultMessagesHolder;
		}
		return messagesHolder;
	}
	
	public void load(List<GripMessageSource> list) {
		localeMessagesHolder.clear();
		for (GripMessageSource message : list) {
			final String locale = message.getLocale();
			final ConcurrentMap<String, MessageFormat> messagesHolder = getMessagesHolderByLocale(locale);
			MessageFormat messageFormat = new MessageFormat(message.getMessage());
			messagesHolder.put(message.getCode(), messageFormat);
		}
	}

	private ConcurrentMap<String, MessageFormat> getMessagesHolderByLocale(final String locale) {
		final ConcurrentMap<String, MessageFormat> messagesHolder = localeMessagesHolder.get(locale);
		if (messagesHolder == null) {
			final ConcurrentMap<String, MessageFormat> newMessagesHolder = createMessagesHolder();
			localeMessagesHolder.put(locale, newMessagesHolder);
			return newMessagesHolder;
		}
		return messagesHolder;
	}

	private ConcurrentMap<String, MessageFormat> createMessagesHolder() {
		final ConcurrentMap<String, MessageFormat> messagesHolder = new ConcurrentHashMap<>();
		return messagesHolder;
	}
}
