# Worken Java Starter (Backend + Frontend)

Dos proyectos Java puros pensados para funcionar sin dependencias externas ni acceso a Maven Central.

- `backend/` → API HTTP en el puerto 8080 que gestiona trabajos informales.
- `frontend/` → Servidor web en el puerto 5173 que consume la API y ofrece interfaz en el navegador.

## Requisitos
- JDK 17+

## Cómo arrancar
1. **Backend**
   ```bash
   cd backend
   javac $(find src/main/java -name '*.java')
   java -cp src/main/java com.worken.backend.BackendApplication
   ```
2. **Frontend** (en otra terminal)
   ```bash
   cd frontend
   javac $(find src/main/java -name '*.java')
   java -cp src/main/java:src/main/resources com.worken.frontend.FrontendApplication
   ```

Visita `http://localhost:5173/` y utiliza el formulario para crear trabajos o revisar la lista existente, la cual se sincroniza con la API en `http://localhost:8080/api/jobs`.

## Configuración

Ambos servidores permiten ajustar puertos y URL del backend sin recompilar:

- **Backend**: define `BACKEND_PORT` o la propiedad `-Dbackend.port` para escoger otro puerto.
- **Frontend**: define `FRONTEND_PORT`/`-Dfrontend.port` para mover el servidor web y `BACKEND_URL`/`-Dbackend.url` para apuntar a otra instancia del backend.
