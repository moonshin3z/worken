# Backend Worken

API HTTP ligera (sin dependencias externas) para listar oportunidades laborales informales. Utiliza el servidor `com.sun.net.httpserver.HttpServer` incluido en el JDK, por lo que funciona sin necesidad de descargar librerías.

## Requisitos
- JDK 17+

## Compilación
Desde este directorio:

```bash
javac $(find src/main/java -name '*.java')
```

## Ejecución

```bash
java -cp src/main/java com.worken.backend.BackendApplication
```

Por defecto escuchará en `http://localhost:8080/api/jobs`, aunque puedes definir otra configuración:

- Variable de entorno `BACKEND_PORT`.
- Propiedad JVM `-Dbackend.port=<puerto>`.

## Endpoints
- `GET /api/jobs`: lista todos los trabajos cargados.
- `GET /api/jobs/{id}`: obtiene un trabajo por su identificador numérico.
- `POST /api/jobs`: crea un trabajo (JSON con `title`, `description`, `category`, `city`, `payment`, `contactPhone`, `contactEmail`, `publishedAt`).
- `PUT /api/jobs/{id}`: actualiza un trabajo existente.
- `DELETE /api/jobs/{id}`: elimina un trabajo.

Se inicializan datos de ejemplo al levantar la aplicación para facilitar las pruebas manuales.
