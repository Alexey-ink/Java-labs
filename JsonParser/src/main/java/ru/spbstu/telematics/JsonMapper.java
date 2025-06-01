package ru.spbstu.telematics;

import java.util.Map;

public class JsonMapper {
    private JsonMapper() {}

    /** Преобразовать Java-объект в JSON-строку */
    public static String toJson(Object obj) {
        return new JsonSerializer().serialize(obj);
    }

    /** Преобразовать JSON-строку в Map<String, Object> */
    public static Map<String,Object> toMap(String json) {
        Object parsed = new JsonParser().parse(json);
        if (!(parsed instanceof Map)) {
            throw new JsonException("JSON root is not an object");
        }
        //noinspection unchecked
        return (Map<String,Object>) parsed;
    }

    /** Преобразовать JSON-строку в указанный класс */
    public static <T> T fromJson(String json, Class<T> cls) {
        Object parsed = new JsonParser().parse(json);
        return JsonBinder.bind(parsed, cls);
    }
}
