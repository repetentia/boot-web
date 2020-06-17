package com.repetentia.utils.common;

public class StringUtils {
	public static boolean isEmpty(String str) {
		return (str == null || "".equals(str));
	}

	public static boolean hasLength(String str) {
		return (str != null && !str.isEmpty());
	}
}
