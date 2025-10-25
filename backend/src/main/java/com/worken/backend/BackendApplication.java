package com.worken.backend;

import com.worken.backend.http.JobsHttpHandler;
import com.worken.backend.job.InMemoryJobRepository;
import com.worken.backend.job.JobService;
import com.worken.backend.job.JobInput;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

/**
 * Arranque principal de la API HTTP. Utiliza el servidor ligero que incluye el JDK
 * para evitar dependencias externas y facilitar la ejecución en entornos restringidos.
 */
public class BackendApplication {

    public static final int DEFAULT_PORT = 8080;
    private static final String PORT_ENV = "BACKEND_PORT";
    private static final String PORT_PROPERTY = "backend.port";

    public static void main(String[] args) throws IOException {
        int port = resolvePort();

        InMemoryJobRepository repository = new InMemoryJobRepository();
        JobService jobService = new JobService(repository);
        seed(jobService);

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/jobs", new JobsHttpHandler(jobService));
        server.setExecutor(null); // usa un pool por defecto
        System.out.printf("Backend escuchando en http://localhost:%d/api/jobs%n", port);
        server.start();
    }

    private static void seed(JobService jobService) {
        List<JobInput> samples = Arrays.asList(
                new JobInput("Pintor de interiores", "Pintado de departamentos y casas", "Construcción", "Ciudad de México", 1500.0, "555-123-4567", "pintor@worken.com", LocalDate.now().minusDays(1)),
                new JobInput("Niñera de fin de semana", "Cuidado de dos niños de 4 y 7 años", "Cuidado personal", "Guadalajara", 900.0, "333-987-6543", "nineras@worken.com", LocalDate.now().minusDays(2)),
                new JobInput("Tutor de matemáticas", "Apoyo escolar para secundaria", "Educación", "Monterrey", 800.0, "818-555-9090", "tutores@worken.com", LocalDate.now().minusDays(3))
        );

        for (JobInput input : samples) {
            jobService.create(input);
        }
    }

    private static int resolvePort() {
        String env = System.getenv(PORT_ENV);
        String property = System.getProperty(PORT_PROPERTY);
        return parsePort(env, parsePort(property, DEFAULT_PORT));
    }

    private static int parsePort(String candidate, int fallback) {
        if (candidate == null || candidate.trim().isEmpty()) {
            return fallback;
        }
        try {
            return Integer.parseInt(candidate.trim());
        } catch (NumberFormatException ex) {
            return fallback;
        }
    }
}
