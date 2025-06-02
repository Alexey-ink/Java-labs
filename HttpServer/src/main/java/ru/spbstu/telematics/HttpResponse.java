package ru.spbstu.telematics;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

class HttpResponse {
    private int status = 200;
    private final Map<String, String> headers = new HashMap<>();
    private byte[] body;

    public void setStatus(int status) { this.status = status; }
    public void setHeader(String name, String value) { headers.put(name, value); }
    public void setBody(String bodyText) { this.body = bodyText.getBytes(); }
    public void setBody(byte[] data) {
        this.body = data;
    }

    public void send(SocketChannel channel) throws IOException {
        if (body == null) {
            body = new byte[0];
        }

        byte[] bodyBytes = body;
        headers.put("Content-Length", String.valueOf(bodyBytes.length));
        String statusMessage = HttpStatus.getReasonPhrase(status);
        String response = "HTTP/1.1 " + status + " " + statusMessage + "\r\n";

        if (!headers.isEmpty()) {
            response += headers.entrySet().stream()
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(Collectors.joining("\r\n")) + "\r\n";
        }

        response += "\r\n";

        ByteBuffer headerBuffer = ByteBuffer.wrap(response.getBytes(StandardCharsets.UTF_8));
        while (headerBuffer.hasRemaining()) {
            channel.write(headerBuffer);
        }

        ByteBuffer bodyBuffer = ByteBuffer.wrap(bodyBytes);
        while (bodyBuffer.hasRemaining()) {
            channel.write(bodyBuffer);
        }
    }
}