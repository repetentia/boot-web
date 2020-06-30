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
        String value = getProperty(instance, name);
        return value == null;
    }

    public static boolean isNotNull(Object instance, String name) {
        return !isNull(instance, name);
    }
    
    public static String getProperty(Object instance, String name) {
    	Object value = getPropertyObject(instance, name);
        return value==null?null:value.toString();
    }
    
    public static Object getPropertyObject(Object instance, String name) {
        Object value = null;
        try {
            Field field = getFieldRecursive(instance.getClass(), name);
            if (field != null) {
                field.setAccessible(true);
                value = field.get(instance);
                field.setAccessible(false);            	
            }
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return value;
    }
    
    private static Field getFieldRecursive(Class<?> clazz, String name) {
    	Field field = null;
		try {
			field = clazz.getDeclaredField(name);
		} catch (NoSuchFieldException e) {
			Class<?> superClazz = clazz.getSuperclass();
			if (!Object.class.equals(superClazz)) {
				field = getFieldRecursive(superClazz, name);
			}
		} catch (SecurityException e) {
			e.printStackTrace();
		}
    	return field;
    }
}