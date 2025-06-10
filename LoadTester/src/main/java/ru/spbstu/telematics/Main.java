package ru.spbstu.telematics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {
    private static DatabaseManager database;
    public static final boolean useJackson = false;
    private static final boolean isVirtual = false;

    private static final Map<String, CalculationResult> storage = new ConcurrentHashMap<>();

    public static void main(String[] args) throws InterruptedException {
        try {

            database = new DatabaseManager("test.db");

            HttpServer server = new HttpServer("localhost", 80);
            server.setThreadPool(4, isVirtual);

            // Обработчики запросов
            server.addRoute("POST", "/request1", Main::handleRequest1);
            server.addRoute("POST", "/request2", Main::handleRequest2);

            server.start();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
            System.exit(1);
        }

        Thread.sleep(Long.MAX_VALUE);
    }

    private static void handleRequest1(HttpRequest request, HttpResponse response) {
        try {
            byte[] rawBody = request.getBody();
            String jsonBody = new String(rawBody, StandardCharsets.UTF_8);
            Map<String, Object> data = parseJson(jsonBody);

            String id = UUID.randomUUID().toString();
            String serializedData = serializeData(data);
            database.saveData(id, serializedData);

            String storedData = database.getData(id);
            response.setBody(storedData);
            response.setHeader("Content-Type", "application/json");
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private static void handleRequest2(HttpRequest request, HttpResponse response) {
        try {
            byte[] rawBody = request.getBody();
            String jsonBody = new String(rawBody, StandardCharsets.UTF_8);
            Map<String, Object> data = parseJson(jsonBody);

            CalculationResult result = calculate(data);
            String resultId = UUID.randomUUID().toString();
            storage.put(resultId, result);

            response.setBody(serializeData(result.toMap()));
            response.setHeader("Content-Type", "application/json");
        } catch (Exception e) {
            handleError(response, e);
        }
    }

    private static Map<String, Object> parseJson(String json) throws IOException {
        if (useJackson) {
            return JacksonParser.parse(json);
        } else {
            JsonParser parser = new JsonParser();
            Object result = parser.parse(json);

            if (result instanceof Map) {
                return (Map<String, Object>) result;
            }
            throw new IOException("Invalid JSON structure");
        }
    }

    private static String serializeData(Object data) throws IOException {
        if (useJackson) {
            return JacksonSerializer.serialize(data);
        } else {
            JsonSerializer serializer = new JsonSerializer();
            return serializer.serialize(data);
        }
    }

    private static CalculationResult calculate(Map<String, Object> data) {
        CalculationResult result = new CalculationResult();
        data.forEach((key, value) -> processValue(value, result));
        return result;
    }

    private static void processValue(Object value, CalculationResult result) {
        if (value instanceof Number num) {
            result.numbersCount++;
            result.numbersSum += num.doubleValue();
        } else if (value instanceof String str) {
            result.stringsCount++;
            result.concatenatedStrings.append(str);
        } else if (value instanceof Iterable<?> iterable) {
            // Обработка массивов и коллекций
            iterable.forEach(item -> processValue(item, result));
        } else if (value instanceof Map<?, ?> map) {
            // Обработка ТОЛЬКО значений (ключи игнорируются)
            map.values().forEach(val -> processValue(val, result));
        }
    }

    private static void handleError(HttpResponse response, Exception e) {
        response.setStatus(500);
        response.setBody(String.format("""
            {
                "error": "%s",
                "details": "%s"
            }""", e.getClass().getSimpleName(), e.getMessage()));
    }

    static class CalculationResult {
        int numbersCount;
        double numbersSum;
        int stringsCount;
        StringBuilder concatenatedStrings = new StringBuilder();

        Map<String, Object> toMap() {
            return Map.of(
                    "numbersCount", numbersCount,
                    "numbersSum", numbersSum,
                    "stringsCount", stringsCount,
                    "concatenatedStrings", concatenatedStrings.toString()
            );
        }
    }
}