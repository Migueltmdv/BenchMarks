# BenchMarks

API REST para valorar bancos públicos: bancos, valoraciones, fotos y usuarios, con autenticación JWT.

## Requisitos

- **JDK 17 o superior** para lanzar Gradle. El proyecto compila con Java 25; si no lo tienes, Gradle lo descarga solo.
- **Docker** (Docker Desktop abierto). Postgres y Redis se levantan automáticamente desde `compose.yaml`.
- Puertos libres: `18080` (API), `15432` (Postgres) y `6379` (Redis).

## Ejecutar

```bash
./gradlew bootRun        # en Windows: gradlew.bat bootRun
```

Al arrancar se levantan los contenedores y se cargan los datos de prueba (`src/main/resources/sql`).
La base de datos se reinicia con esos datos en cada arranque.

## Usar la API

- Base: `http://localhost:18080`
- Documentación: `http://localhost:18080/docs/swagger` (o `/docs/scalar`)
- Usuarios de prueba: `user` / `1234` (rol USER) y `admin` / `1234` (rol ADMIN)

`POST /auth/login` con `{"username": "user", "password": "1234"}` devuelve el token en la cabecera
`Authorization`; envíalo como `Authorization: Bearer <token>` en el resto de peticiones.

| Recurso | Rutas |
|---|---|
| Autenticación | `/auth/login`, `/auth/register`, `/auth/refresh`, `/auth/logout` |
| Bancos | `/benches`, `/benches/{id}`, `POST /benches/{id}/like` |
| Valoraciones | `/valoraciones`, `/valoraciones/{id}` |
| Fotos | `/photos`, `/photos/{id}` |
| Usuarios | `/users`, `/users/{id}` |

Las modificaciones parciales (`PATCH`) usan JSON Patch (`Content-Type: application/json-patch+json`).
