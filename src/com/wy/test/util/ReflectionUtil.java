package com.wy.test.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@SuppressWarnings("unchecked")
public class ReflectionUtil {

	private static Object[] EMPTY_PARAMS = new Object[0];

	private static Class<?>[] EMPTY_PARAMS_TYPE = new Class[0];

	public static <T> T getValue(Object obj, String name) {
		try {
			int first;
			if ((first = name.indexOf('.')) > -1) {
				Field f = getField(obj.getClass(), name.substring(0, first));
				f.setAccessible(true);
				return getValue(f.get(obj), name.substring(first + 1));
			} else {
				Field f = getField(obj.getClass(), name);
				f.setAccessible(true);
				return (T) f.get(obj);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static <T> T getValue(Class<?> clazz, String name) {
		try {
			Field f = getField(clazz, name);
			f.setAccessible(true);
			return (T) f.get(null);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void setValue(Object obj, String name, Object value) {
		try {
			Field f = getField(obj.getClass(), name);
			f.setAccessible(true);
			f.set(obj, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setValue(Class<?> clazz, String name, Object value) {
		try {
			Field f = getField(clazz, name);
			f.setAccessible(true);
			f.set(null, value);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static Field getField(Class<?> clazz, String name) {
		try {
			return clazz.getDeclaredField(name);
		} catch (Exception e) {
		}
		Class<?> superClass = clazz.getSuperclass();
		return superClass != null ? getField(superClass, name) : null;
	}

	public static Object invoke(Object obj, String name, Class<?>... paramsType) {
		return invoke(obj, name, paramsType, EMPTY_PARAMS);
	}

	public static <T> T invoke(Object obj, String name, Class<?>[] paramsType, Object... params) {
		try {
			Method m = getMethod(obj.getClass(), name, paramsType);
			m.setAccessible(true);
			return (T) m.invoke(obj, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static void invoke(Object obj, String name, Object value) {
		invoke(obj, name, EMPTY_PARAMS_TYPE, value);
	}

	private static Method getMethod(Class<?> clazz, String name, Class<?>... paramsType) {
		try {
			return clazz.getDeclaredMethod(name, paramsType);
		} catch (Exception e) {
		}
		Class<?> superClass = clazz.getSuperclass();
		return superClass != null ? getMethod(superClass, name, paramsType) : null;
	}
}
