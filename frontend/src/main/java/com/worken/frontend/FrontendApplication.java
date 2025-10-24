package com.worken.frontend;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Servidor HTTP extremadamente simple que entrega activos estáticos y ofrece
 * un punto de partida para la aplicación web sin depender de frameworks externos.
 */
public class FrontendApplication {

    public static final int PORT = 5173;
    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", FrontendApplication::handleRequest);
        server.setExecutor(null);
        System.out.printf("Frontend disponible en http://localhost:%d/%n", PORT);
        server.start();
    }

    private static void handleRequest(HttpExchange exchange) throws IOException {
        addCachingHeaders(exchange.getResponseHeaders());
        String path = exchange.getRequestURI().getPath();
        if (path.equals("/")) {
            serve(exchange, "static/index.html", "text/html; charset=UTF-8");
        } else if (path.equals("/styles.css")) {
            serve(exchange, "static/styles.css", "text/css; charset=UTF-8");
        } else if (path.equals("/app.js")) {
            serve(exchange, "static/app.js", "application/javascript; charset=UTF-8");
        } else {
            exchange.sendResponseHeaders(404, -1);
            exchange.close();
        }
    }

    private static void serve(HttpExchange exchange, String resourcePath, String contentType) throws IOException {
        byte[] body = CACHE.computeIfAbsent(resourcePath, FrontendApplication::readResource);
        if (body == null) {
            exchange.sendResponseHeaders(404, -1);
        } else {
            Headers headers = exchange.getResponseHeaders();
            headers.add("Content-Type", contentType);
            exchange.sendResponseHeaders(200, body.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(body);
            }
        }
        exchange.close();
    }

    private static byte[] readResource(String resourcePath) {
        try (InputStream in = FrontendApplication.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (in == null) {
                return null;
            }
            return in.readAllBytes();
        } catch (IOException ex) {
            return null;
        }
    }

    private static void addCachingHeaders(Headers headers) {
        headers.add("Cache-Control", "no-store, max-age=0");
    }
}
