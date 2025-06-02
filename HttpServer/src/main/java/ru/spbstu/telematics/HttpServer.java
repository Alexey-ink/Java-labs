package ru.spbstu.telematics;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;

public class HttpServer {
    private final String host;
    private final int port;
    private ServerSocketChannel serverChannel;
    private ExecutorService threadPool;
    private final Router router = new Router();
    private volatile boolean isRunning;

    public HttpServer(String host, int port) {
        this.host = host;
        this.port = port;
    }

    public void setThreadPool(int threads, boolean isVirtual) {
        this.threadPool = ThreadPool.create(threads, isVirtual);
    }

    public void addRoute(String method, String path, Handler handler) {
        router.addRoute(method, path, handler);
    }

    public void start() throws IOException {
        isRunning = true;
        serverChannel = ServerSocketChannel.open();
        serverChannel.bind(new InetSocketAddress(host, port));
        serverChannel.configureBlocking(true);

        System.out.println("Server started on " + host + ":" + port);

        threadPool.execute(() -> {
            while (isRunning) {
                try {
                    SocketChannel clientChannel = serverChannel.accept();
                    handleConnection(clientChannel); // Обработка подключения
                } catch (IOException e) {
                    if (isRunning) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private HttpRequest parseRequest(SocketChannel channel) throws IOException {
        ByteBuffer buffer = ByteBuffer.allocate(8192);
        StringBuilder headerSb = new StringBuilder();
        int bytesRead;
        while ((bytesRead = channel.read(buffer)) > 0) {
            buffer.flip();
            // ASCII -- стандарт для headers
            headerSb.append(StandardCharsets.US_ASCII.decode(buffer));
            buffer.clear();
            if (headerSb.indexOf("\r\n\r\n") >= 0) {
                break;
            }
        }
        if (bytesRead == -1 && headerSb.length() == 0) {
            throw new IOException("Connection closed by client before headers");
        }

        // 2) Разбираем стартовую строку и заголовки
        String headerBlock = headerSb.toString();
        String[] parts = headerBlock.split("\r\n\r\n", 2);
        String[] headerLines = parts[0].split("\r\n");

        if (headerLines.length == 0) throw new IOException("No headers found");
        String[] requestLine = headerLines[0].split(" +");
        if (requestLine.length < 3) throw new IOException("Invalid request line: " + headerLines[0]);

        // Собираем HttpRequest (method, path, headersMap)
        HttpRequest request = new HttpRequest();
        request.setMethod(requestLine[0]);
        request.setPath(requestLine[1]);
        Map<String, String> headersMap = new HashMap<>();
        for (int i = 1; i < headerLines.length; i++) {
            String[] kv = headerLines[i].split(": ", 2);
            if (kv.length == 2) headersMap.put(kv[0], kv[1]);
        }
        request.setHeaders(headersMap);

        // 3) Читаем тело по Content-Length
        int contentLength = headersMap.containsKey("Content-Length")
                ? Integer.parseInt(headersMap.get("Content-Length"))
                : 0;
        byte[] body = new byte[contentLength];
        int offset = 0;

        // 3a) Если после заголовков уже есть кусок тела — копируем его
        if (parts.length == 2) {
            byte[] leftover = parts[1].getBytes(StandardCharsets.ISO_8859_1);
            int toCopy = Math.min(leftover.length, contentLength);
            System.arraycopy(leftover, 0, body, 0, toCopy);
            offset = toCopy;
        }

        // 3b) Читаем остальное из канала
        while (offset < contentLength) {
            buffer.clear();
            int r = channel.read(buffer);
            if (r == -1) break;
            buffer.flip();
            int len = Math.min(buffer.remaining(), contentLength - offset);
            buffer.get(body, offset, len);
            offset += len;
        }

        request.setBody(body);
        return request;
    }

    private void handleConnection(SocketChannel clientChannel) {
        threadPool.execute(() -> {
            try {
                HttpRequest request = parseRequest(clientChannel);
                HttpResponse response = new HttpResponse();

                Handler handler = router.findHandler(request.getMethod(), request.getPath());
                if (handler != null) {
                    handler.handle(request, response);
                } else {
                    response.setStatus(404);
                    response.setBody("Not Found");
                }

                response.send(clientChannel);
                clientChannel.close();
            } catch (Exception e) {
                e.printStackTrace();
                try {
                    clientChannel.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void stop() throws IOException {
        isRunning = false;
        serverChannel.close();
    }
}