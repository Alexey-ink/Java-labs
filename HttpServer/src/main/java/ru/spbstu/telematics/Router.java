package ru.spbstu.telematics;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class Router {
    private final Map<RouteKey, Handler> routes = new ConcurrentHashMap<>();

    public void addRoute(String method, String path, Handler handler) {
        routes.put(new RouteKey(method, path), handler);
    }

    public Handler findHandler(String method, String path) {
        return routes.get(new RouteKey(method, path));
    }

    private record RouteKey(String method, String path) {}
}