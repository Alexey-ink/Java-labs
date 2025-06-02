package ru.spbstu.telematics;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        HttpServer server = new HttpServer("localhost", 80);

        // GET
        server.addRoute("GET", "/hello", (req, res) -> {
            Thread.sleep(10000);
            res.setStatus(200);
            res.setBody("Hello from Server");
        });

        // POST
        server.addRoute("POST", "/data", (req, res) -> {
            byte[] raw = req.getBody();
            String bodyText = new String(raw, StandardCharsets.UTF_8);
            res.setStatus(201);
            res.setBody("POST: Received data: " + bodyText);
        });

        // PUT
        server.addRoute("PUT", "/update", (req, res) -> {
            String bodyText = new String(req.getBody(), StandardCharsets.UTF_8);
            res.setStatus(200);
            res.setBody("PUT: Updated with: " + bodyText);
        });

        // PATCH
        server.addRoute("PATCH", "/patch", (req, res) -> {
            String bodyText = new String(req.getBody(), StandardCharsets.UTF_8);
            res.setStatus(200);
            res.setBody("PATCH: Partial update: " + bodyText);
        });

        // DELETE
        server.addRoute("DELETE", "/delete", (req, res) -> {
            res.setStatus(204);
            res.setBody("");
        });

        server.setThreadPool(4, true);
        server.start();

        Thread.sleep(Long.MAX_VALUE);
    }
}