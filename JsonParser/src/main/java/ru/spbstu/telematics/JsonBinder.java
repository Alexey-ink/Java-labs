package ru.spbstu.telematics;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

class JsonBinder {
    public static <T> T bind(Object node, Class<T> cls) {
        if (node==null) return null;
        if (isPrimitiveOrWrapper(cls) || cls == String.class || cls == Character.class) {
            return castPrimitive(node, cls);
        }

        if (cls.isArray() && node instanceof List) {
            List<?> list = (List<?>) node;
            Class<?> comp = cls.getComponentType();
            Object arr = Array.newInstance(comp, list.size());
            for (int i=0;i<list.size();i++) {
                Array.set(arr, i, bind(list.get(i), comp));
            }
            return cls.cast(arr);
        }
        if (Collection.class.isAssignableFrom(cls) && node instanceof List) {
            try {
                @SuppressWarnings("unchecked")
                Collection<Object> col;
                if (cls.isInterface()) {
                    col = new ArrayList<>();
                } else {
                    col = (Collection<Object>) cls.getDeclaredConstructor().newInstance();
                }

                for (Object e : (List<?>) node) col.add(e);
                return cls.cast(col);
            } catch (Exception e) { throw new JsonException(e.getMessage()); }
        }

        if (Map.class.isAssignableFrom(cls) && node instanceof Map) {
            try {
                @SuppressWarnings("unchecked")
                Map<Object, Object> mapInstance = cls.isInterface()
                        ? new HashMap<>()
                        : (Map<Object, Object>) cls.getDeclaredConstructor().newInstance();

                Map<?, ?> inputMap = (Map<?, ?>) node;
                for (Map.Entry<?, ?> entry : inputMap.entrySet()) {
                    Object key = entry.getKey();
                    Object val = entry.getValue();
                    mapInstance.put(key, val); // optionally: bind(val, Object.class)
                }
                return (T) mapInstance;
            } catch (Exception e) {
                throw new JsonException(e.getMessage());
            }
        }

        if (node instanceof Map) {
            try {
                T obj = cls.getDeclaredConstructor().newInstance();
                Map<?,?> map = (Map<?,?>) node;
                for (Field f : getAllFields(cls)) {
                    if (Modifier.isStatic(f.getModifiers())) continue;
                    f.setAccessible(true);
                    Object val = map.get(f.getName());
                    if (val!=null) {
                        f.set(obj, bind(val, f.getType()));
                    }
                }
                return obj;
            } catch (Exception e) { throw new JsonException(e.getMessage()); }
        }
        throw new JsonException("Cannot bind " + node + " to " + cls);
    }

    private static boolean isPrimitiveOrWrapper(Class<?> c) {
        return c.isPrimitive() || c==Integer.class || c==Long.class || c==Boolean.class || c==Double.class
                || c==Float.class || c==Short.class || c==Byte.class || c==Character.class;
    }

    @SuppressWarnings("unchecked")
    private static <T> T castPrimitive(Object value, Class<T> cls) {
        if (value == null) return null;

        if (cls == boolean.class || cls == Boolean.class) {
            if (!(value instanceof Boolean)) throw new JsonException("Expected boolean, got: " + value.getClass());
            return (T) value;
        }

        if (cls == char.class || cls == Character.class) {
            if (value instanceof String && ((String) value).length() == 1)
                return (T) Character.valueOf(((String) value).charAt(0));
            if (value instanceof Number)
                return (T) Character.valueOf((char) ((Number) value).intValue());
            throw new JsonException("Expected char, got: " + value);
        }

        if (cls == String.class) return (T) value.toString();

        if (cls.isPrimitive() || Number.class.isAssignableFrom(cls)) {
            if (!(value instanceof Number)) throw new JsonException("Expected number, got: " + value.getClass());
            Number num = (Number) value;
            if (cls == int.class || cls == Integer.class) return (T) Integer.valueOf(num.intValue());
            if (cls == long.class || cls == Long.class) return (T) Long.valueOf(num.longValue());
            if (cls == double.class || cls == Double.class) return (T) Double.valueOf(num.doubleValue());
            if (cls == float.class || cls == Float.class) return (T) Float.valueOf(num.floatValue());
            if (cls == short.class || cls == Short.class) return (T) Short.valueOf(num.shortValue());
            if (cls == byte.class || cls == Byte.class) return (T) Byte.valueOf(num.byteValue());
        }

        throw new JsonException("Cannot cast to " + cls + ": " + value);
    }

    public static Field[] getAllFields(Class<?> cls) {
        List<Field> fields = new java.util.ArrayList<>();
        while (cls != null && cls != Object.class) {
            fields.addAll(Arrays.asList(cls.getDeclaredFields()));
            cls = cls.getSuperclass();
        }
        return fields.toArray(new Field[0]);
    }
}