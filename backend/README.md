# Backend Worken

API REST construida con Spring Boot para listar oportunidades laborales informales.

## Requisitos
- JDK 17+
- Maven 3.9+

## Ejecutar
```bash
./mvnw spring-boot:run
```

La API quedará disponible en `http://localhost:8080`.

### Endpoints principales
- `GET /api/jobs`: lista todos los trabajos cargados.
- `GET /api/jobs/{id}`: obtiene un trabajo por su identificador.
- `POST /api/jobs`: crea un trabajo (requiere JSON con `title`, `description`, `location`, `hourlyRate`, `category`).
- `PUT /api/jobs/{id}`: actualiza un trabajo existente.
- `DELETE /api/jobs/{id}`: elimina un trabajo.

## Datos de ejemplo
Al iniciar la aplicación se precargan trabajos representativos para facilitar pruebas rápidas. La base usa H2 en memoria.
