package com.repetentia.utils.common;

import java.lang.reflect.Field;

import org.springframework.util.Assert;

public class ReflectionUtils {
	public static Field[] getDeclaredFields(Class<?> clazz) {
		Assert.notNull(clazz, "Class must not be null");
		Field[] result = null;
		try {
			result = clazz.getDeclaredFields();
		} catch (Throwable ex) {
			throw new IllegalStateException("Failed to introspect Class [" + clazz.getName() + "] from ClassLoader [" + clazz.getClassLoader() + "]", ex);
		}
		return result;
	}

	public static boolean isNull(Object instance, String name) {
		Object value = getProperty(instance, name);
		return value == null;
	}

	public static boolean isNotNull(Object instance, String name) {
		return !isNull(instance, name);
	}

	public static Object getProperty(Object instance, String name) {
		Object value = null;
		try {
			Field field = instance.getClass().getField(name);
			value = field.get(instance);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}
		return value;
	}
}
