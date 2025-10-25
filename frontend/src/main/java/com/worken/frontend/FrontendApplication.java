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
import java.nio.charset.StandardCharsets;

/**
 * Servidor HTTP extremadamente simple que entrega activos estáticos y ofrece
 * un punto de partida para la aplicación web sin depender de frameworks externos.
 */
public class FrontendApplication {

    public static final int DEFAULT_PORT = 5173;
    private static final String FRONTEND_PORT_ENV = "FRONTEND_PORT";
    private static final String FRONTEND_PORT_PROPERTY = "frontend.port";
    private static final String BACKEND_URL_ENV = "BACKEND_URL";
    private static final String BACKEND_URL_PROPERTY = "backend.url";
    private static final String DEFAULT_BACKEND_URL = "http://localhost:8080/api/jobs";
    private static final Map<String, byte[]> CACHE = new ConcurrentHashMap<>();

    public static void main(String[] args) throws IOException {
        int port = resolveFrontendPort();

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", FrontendApplication::handleRequest);
        server.createContext("/config.js", FrontendApplication::handleConfigRequest);
        server.setExecutor(null);
        System.out.printf("Frontend disponible en http://localhost:%d/%n", port);
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

    private static void handleConfigRequest(HttpExchange exchange) throws IOException {
        addCachingHeaders(exchange.getResponseHeaders());
        if (!"GET".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(405, -1);
            exchange.close();
            return;
        }

        String backendUrl = resolveBackendUrl();
        String payload = "window.__BACKEND_URL__ = '" + escapeForJavaScript(backendUrl) + "';\n";
        byte[] body = payload.getBytes(StandardCharsets.UTF_8);

        Headers headers = exchange.getResponseHeaders();
        headers.add("Content-Type", "application/javascript; charset=UTF-8");
        exchange.sendResponseHeaders(200, body.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(body);
        }
        exchange.close();
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

    private static int resolveFrontendPort() {
        String env = System.getenv(FRONTEND_PORT_ENV);
        String property = System.getProperty(FRONTEND_PORT_PROPERTY);
        return parsePort(env, parsePort(property, DEFAULT_PORT));
    }

    private static int parsePort(String candidate, int fallback) {
        if (candidate == null || candidate.isBlank()) {
            return fallback;
        }
        try {
            return Integer.parseInt(candidate.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }

    private static String resolveBackendUrl() {
        String env = System.getenv(BACKEND_URL_ENV);
        if (env != null && !env.isBlank()) {
            return env.trim();
        }
        String property = System.getProperty(BACKEND_URL_PROPERTY);
        if (property != null && !property.isBlank()) {
            return property.trim();
        }
        return DEFAULT_BACKEND_URL;
    }

    private static String escapeForJavaScript(String value) {
        return value
                .replace("\\", "\\\\")
                .replace("'", "\\'");
    }
}
