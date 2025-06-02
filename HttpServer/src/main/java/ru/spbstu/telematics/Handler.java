package ru.spbstu.telematics;

@FunctionalInterface
interface Handler {
    void handle(HttpRequest request, HttpResponse response) throws InterruptedException;
}