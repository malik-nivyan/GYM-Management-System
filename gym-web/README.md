# Titan Forge Gym Web App

Modernized migration of the desktop gym app into a Spring Boot REST API + Vue frontend.

## Architecture decisions

- Backend uses a layered structure (`controller -> service -> repository`) so business logic stays out of controllers.
- API contracts use DTOs instead of exposing JPA entities directly for safer, stable payloads.
- Authentication is stateless JWT with Spring Security and role-based access (`ADMIN`, `STAFF`).
- MySQL is configured through environment variables with safe local defaults.
- Frontend uses Vue Router and modular views per domain (`Members`, `Memberships`, `Payments`, `Trainers`).

## Project structure

```text
gym-web/
├─ backend/
│  ├─ mvnw, mvnw.cmd
│  ├─ pom.xml
│  ├─ .mvn/wrapper/maven-wrapper.properties
│  └─ src/main/
│     ├─ java/com/titanforge/gym/
│     │  ├─ config/        (security + cors)
│     │  ├─ controller/    (auth + CRUD endpoints)
│     │  ├─ dto/           (request/response models)
│     │  ├─ exception/     (REST error handling)
│     │  ├─ init/          (seed data)
│     │  ├─ model/         (JPA entities)
│     │  ├─ repository/    (Spring Data)
│     │  ├─ security/      (JWT filter/service)
│     │  └─ service/       (business logic)
│     └─ resources/application.yml
└─ frontend/
   ├─ package.json
   └─ src/
      ├─ api/
      ├─ components/
      ├─ router/
      ├─ stores/
      └─ views/
```

## Backend setup (MySQL)

Create DB once:

```sql
CREATE DATABASE IF NOT EXISTS dump CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
```

Set optional environment variables:

- `GYM_DB_URL` (default `jdbc:mysql://127.0.0.1:3306/dump?serverTimezone=UTC`)
- `GYM_DB_USER` (default `root`)
- `GYM_DB_PASSWORD` (default `nivyan`)
- `GYM_JWT_SECRET` (recommended in production)
- `GYM_FRONTEND_ORIGIN` (default `http://localhost:5173`)

Run backend (with Maven Wrapper):

```powershell
cd F:\IdeaProjects\JDBC\gym-web\backend
.\mvnw.cmd spring-boot:run
```

## Frontend setup

```powershell
cd F:\IdeaProjects\JDBC\gym-web\frontend
npm install
npm run dev
```

## Auth and default users

On first backend run, sample users are upserted (BCrypt hashed):

- `admin / nivyan` -> `ADMIN`
- `staff / staffpass` -> `STAFF`

## API summary

- Public: `POST /api/auth/login`, `GET /api/health`
- Staff/Admin: members + payments
- Admin only (write), staff/admin (read): memberships + trainers
