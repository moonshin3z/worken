package com.worken.backend.http;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.worken.backend.job.Job;
import com.worken.backend.job.JobRequest;
import com.worken.backend.job.JobService;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class JobsHttpHandler implements HttpHandler {

    private final JobService jobService;

    public JobsHttpHandler(JobService jobService) {
        this.jobService = jobService;
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        addCors(exchange.getResponseHeaders());
        if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
            exchange.sendResponseHeaders(204, -1);
            exchange.close();
            return;
        }

        String path = exchange.getRequestURI().getPath();
        String method = exchange.getRequestMethod();

        if ("/api/jobs".equals(path) || "/api/jobs/".equals(path)) {
            handleCollection(exchange, method);
        } else if (path.startsWith("/api/jobs/")) {
            handleSingle(exchange, method, path.substring("/api/jobs/".length()));
        } else {
            respond(exchange, 404, JsonUtil.error("Ruta no encontrada"));
        }
    }

    private void handleCollection(HttpExchange exchange, String method) throws IOException {
        switch (method) {
            case "GET":
                respond(exchange, 200, JsonUtil.toJson(jobService.findAll()));
                break;
            case "POST":
                String body = readBody(exchange.getRequestBody());
                JobRequest request = JsonUtil.parseJobRequest(body);
                String validation = validate(request);
                if (validation != null) {
                    respond(exchange, 400, JsonUtil.error(validation));
                    return;
                }
                Job created = jobService.create(request.toInput());
                respond(exchange, 201, JsonUtil.toJson(created));
                break;
            default:
                respond(exchange, 405, JsonUtil.error("Método no permitido"));
                break;
        }
    }

    private void handleSingle(HttpExchange exchange, String method, String idPart) throws IOException {
        long id = parseId(idPart);
        if (id <= 0) {
            respond(exchange, 400, JsonUtil.error("Identificador inválido"));
            return;
        }
        switch (method) {
            case "GET":
                Optional<Job> job = jobService.findById(id);
                if (job.isPresent()) {
                    respond(exchange, 200, JsonUtil.toJson(job.get()));
                } else {
                    respond(exchange, 404, JsonUtil.error("Trabajo no encontrado"));
                }
                break;
            case "PUT":
                String body = readBody(exchange.getRequestBody());
                JobRequest request = JsonUtil.parseJobRequest(body);
                String validation = validate(request);
                if (validation != null) {
                    respond(exchange, 400, JsonUtil.error(validation));
                    return;
                }
                Optional<Job> updated = jobService.update(id, request.toInput());
                if (updated.isPresent()) {
                    respond(exchange, 200, JsonUtil.toJson(updated.get()));
                } else {
                    respond(exchange, 404, JsonUtil.error("Trabajo no encontrado"));
                }
                break;
            case "DELETE":
                boolean deleted = jobService.delete(id);
                if (deleted) {
                    respond(exchange, 204, "");
                } else {
                    respond(exchange, 404, JsonUtil.error("Trabajo no encontrado"));
                }
                break;
            default:
                respond(exchange, 405, JsonUtil.error("Método no permitido"));
                break;
        }
    }

    private long parseId(String idPart) {
        try {
            if (idPart.endsWith("/")) {
                idPart = idPart.substring(0, idPart.length() - 1);
            }
            return Long.parseLong(idPart);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }

    private String readBody(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private void respond(HttpExchange exchange, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        if (status == 204) {
            exchange.sendResponseHeaders(status, -1);
        } else {
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
            exchange.sendResponseHeaders(status, bytes.length);
            try (OutputStream os = exchange.getResponseBody()) {
                os.write(bytes);
            }
        }
        exchange.close();
    }

    private void addCors(Headers headers) {
        headers.add("Access-Control-Allow-Origin", "*");
        headers.add("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
        headers.add("Access-Control-Allow-Headers", "Content-Type");
    }

    private String validate(JobRequest input) {
        if (input.getTitle() == null || input.getTitle().trim().isEmpty()) {
            return "El título es obligatorio";
        }
        if (input.getDescription() == null || input.getDescription().trim().isEmpty()) {
            return "La descripción es obligatoria";
        }
        if (input.getCategory() == null || input.getCategory().trim().isEmpty()) {
            return "La categoría es obligatoria";
        }
        if (input.getCity() == null || input.getCity().trim().isEmpty()) {
            return "La ciudad es obligatoria";
        }
        if (input.getPayment() <= 0) {
            return "El pago debe ser mayor a 0";
        }
        return null;
    }
}
