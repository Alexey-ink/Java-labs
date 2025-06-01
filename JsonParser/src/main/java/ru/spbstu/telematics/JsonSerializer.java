package ru.spbstu.telematics;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.Map;

import static ru.spbstu.telematics.JsonBinder.getAllFields;

public class JsonSerializer {
    public String serialize(Object obj) {
        if (obj == null) return "null";
        if (obj instanceof Number || obj instanceof Boolean) return obj.toString();
        if (obj instanceof String) return quote((String) obj);
        if (obj.getClass().isArray()) return serializeArray(obj);
        if (obj instanceof Collection) return serializeCollection((Collection<?>) obj);
        if (obj instanceof Map) return serializeMap((Map<?,?>) obj);
        return serializeObject(obj);
    }

    private String serializeArray(Object array) {
        int len = Array.getLength(array);
        StringBuilder sb = new StringBuilder().append('[');
        for (int i = 0; i < len; i++) {
            if (i>0) sb.append(',');
            sb.append(serialize(Array.get(array, i)));
        }
        return sb.append(']').toString();
    }

    private String serializeCollection(Collection<?> col) {
        StringBuilder sb = new StringBuilder().append('[');
        boolean first = true;
        for (Object o : col) {
            if (!first) sb.append(',');
            first = false;
            sb.append(serialize(o));
        }
        return sb.append(']').toString();
    }

    private String serializeMap(Map<?,?> map) {
        StringBuilder sb = new StringBuilder().append('{');
        boolean first = true;
        for (Map.Entry<?,?> e : map.entrySet()) {
            if (!first) sb.append(',');
            first = false;
            sb.append(serialize(e.getKey().toString()));
            sb.append(':');
            sb.append(serialize(e.getValue()));
        }
        return sb.append('}').toString();
    }

    private String serializeObject(Object obj) {
        StringBuilder sb = new StringBuilder().append('{');
        boolean first = true;
        for (Field f : getAllFields(obj.getClass())) {
            if (Modifier.isStatic(f.getModifiers())) continue;
            f.setAccessible(true);
            try {
                Object value = f.get(obj);
                if (!first) sb.append(',');
                first = false;
                sb.append(quote(f.getName())).append(':').append(serialize(value));
            } catch (IllegalAccessException e) {
                throw new JsonException(e.getMessage());
            }
        }
        return sb.append('}').toString();
    }

    private String quote(String s) {
        return '"' + s.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t") + '"';
    }
}