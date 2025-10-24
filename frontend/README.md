# Frontend Worken

Cliente web minimalista sin dependencias externas. Sirve HTML, CSS y JavaScript mediante el servidor HTTP ligero del JDK y consume la API del backend (`http://localhost:8080/api/jobs`).

## Requisitos
- JDK 17+

## Compilación
```bash
javac $(find src/main/java -name '*.java')
```

## Ejecución
```bash
java -cp src/main/java:src/main/resources com.worken.frontend.FrontendApplication
```

Abre `http://localhost:5173/` en tu navegador una vez que el backend esté en marcha.
