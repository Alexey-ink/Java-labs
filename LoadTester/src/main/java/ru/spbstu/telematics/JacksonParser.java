package ru.spbstu.telematics;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.Map;

public class JacksonParser {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static Map<String, Object> parse(String json) throws IOException {
        return mapper.readValue(json, new TypeReference<Map<String, Object>>() {});
    }
}