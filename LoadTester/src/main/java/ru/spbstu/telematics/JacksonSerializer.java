package ru.spbstu.telematics;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JacksonSerializer {
    private static final ObjectMapper mapper = new ObjectMapper();

    public static String serialize(Object data) throws JsonProcessingException {
        return mapper.writeValueAsString(data);
    }
}