# Worken Java Starter (Backend + Frontend)

Dos proyectos separados — ambos en **Java**:

- `backend/` → API REST con Spring Boot + H2 (puerto 8080)
- `frontend/` → Web MVC con Spring Boot + Thymeleaf (puerto 5173)

## Requisitos
- JDK 17+
- Maven (o usa `mvnw` que llama a `mvn` instalado)

## Cómo arrancar
1) Backend
```bash
cd backend
./mvnw spring-boot:run
```
2) Frontend
```bash
cd ../frontend
./mvnw spring-boot:run
```
Abre `http://localhost:5173` y navega a **Empleos**. Los datos se cargan desde `http://localhost:8080/api/jobs`.

> Esqueleto inspirado en una app con navegación por pantallas similar a la que ya subiste; aquí se ofrece versión 100% Java para que puedas seguir creciendo luego con React si lo deseas.
