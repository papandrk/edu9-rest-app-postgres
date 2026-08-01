# EduApp REST API (PostgreSQL)

A Spring Boot REST API for managing teachers and users in an educational context, featuring JWT-based authentication, role-based access control, soft-delete, file uploads, and async report generation. This version of the project uses **PostgreSQL** as the relational database.

---

## 📌 Repository Origin & Fork Notice

This repository is built upon [edu9-rest-app-pro](https://github.com/papandrk/edu9-rest-app-pro).

Specifically, this repository shares the same commit history up to commit [`73a9b53e1df689b574b67e2f8e13fc4186e3c05b`](https://github.com/papandrk/edu9-rest-app-pro/commit/73a9b53e1df689b574b67e2f8e13fc4186e3c05b), after which development diverged to transition the persistence layer to PostgreSQL.

---

## Features

* **RESTful API Services:** Full CRUD capabilities for teachers, users, attachments, and personal information.
* **Database & Migrations:** PostgreSQL backend managed with Flyway migrations (`src/main/resources/db/migration`).
* **Authentication & Authorization:** Spring Security integration with stateless JWT authentication, role-based access control (RBAC), and custom user capabilities.
* **OpenAPI / Swagger Documentation:** Interactive API documentation via springdoc-openapi.
* **Multi-Environment Configuration:** Separate configuration profiles (`dev`, `staging`, `prod`).
* **Logging & MDC Filter:** Integrated logging with Spring/Logback and MDC tracing context (request-scoped context).

---

## 🛠️ Tech Stack

* **Java 21** / **Spring Boot 3**
* **Spring Security** - stateless JWT authentication
* **Spring Data JPA** / **Hibernate** + **Flyway** (schema migrations)
* **PostgreSQL 18**
* **Lombok**, **Jakarta Validation**
* **OpenAPI** / **Swagger UI** — interactive API docs
* **Logback** — structured logging with MDC (request-scoped context)
* **Build Tool:** Gradle

---

## 🚀 Getting Started

### Prerequisites

* **JDK 21** or higher installed.
* **PostgreSQL** instance running locally or accessible over the network.

### Database Setup

Create a PostgreSQL database (default settings match `application-dev.properties`):

```sql
CREATE DATABASE cf9db;
CREATE USER cf9user WITH PASSWORD '12345';
ALTER DATABASE cf9db OWNER TO cf9user;

-- Execute the next command AFTER connecting to cf9db as cf9user:
CREATE SCHEMA eduappschema AUTHORIZATION cf9user;
```

Flyway runs all migrations automatically on startup from `src/main/resources/db/migration/`.

---

## ⚙️ Configuration & Profiles

Configuration parameters are stored in `src/main/resources/`. The application supports three profiles:

| Profile | Config File | Default Port | Description |
| :--- | :--- | :--- | :--- |
| `dev` | `application-dev.properties` | `8080` | Local development environment |
| `staging` | `application-staging.properties` | `8080` | Staging environment |
| `prod` | `application-prod.properties` | `8080` | Production environment |

The active profile is `dev` by default.

| Property        | Default (dev)            | Override via                 |
|-----------------|--------------------------|------------------------------|
| DB host         | `localhost`              | `PG_HOST`                    |
| DB port         | `5432`                   | `PG_PORT`                    |
| DB name         | `cf9db`                  | `PG_DB`                      |
| DB user         | `cf9user`                | `PG_USER`                    |
| DB password     | `12345`                  | `PG_PASSWORD`                |
| DB schema       | `eduappschema`           | `PG_SCHEMA`                  |
| JWT secret      | *(dev value)*            | `app.security.secret-key`    |
| JWT expiration  | `600000` ms (10 minutes) | `app.security.jwt-expiration` |
| CORS origins    | *(dev value)*            | `allowed.origins` (comma-separated) |
| BCrypt strength | `12`                     | `security.bcrypt.strength`   |

> **Production**: never commit real secrets. Inject `app.security.secret-key` via environment variable.

### Sample Database Credentials (`application-dev.properties`)

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/cf9db
spring.datasource.username=cf9user
spring.datasource.password=12345
spring.jpa.properties.hibernate.default_schema=eduappschema
```

---

## 🏃 Building and Running

### 1. Build the Project

Using the Gradle wrapper:

```bash
# Works on Linux, macOS & Windows (PowerShell)
./gradlew build
```

### 2. Run the Application

Run with the default `dev` profile:

```bash
./gradlew bootRun
```

To run with, for example, the 'staging' profile:

```bash
./gradlew bootRun --args='--spring.profiles.active=staging'
```

Alternatively, run the packaged JAR directly (after building the project, seen here with the 'prod' profile selected):

```bash
java -jar build/libs/edu9-rest-app-postgres-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

The server starts on port **8080**.

---

## 📖 API Documentation & Swagger UI

Once the application is running, access the interactive API documentation at:

* **Swagger UI:** `http://localhost:8080/swagger-ui.html`
* **OpenAPI Specs:** `http://localhost:8080/v3/api-docs`

---

## 🧪 Running Tests

Execute the unit and integration test suite:

```bash
./gradlew test
```

## API Overview

Base path: `/api/v1`

### Authentication

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/auth/authenticate` | Public | Log in and receive a JWT |

**Request body:**
```json
{ "username": "alice", "password": "Secret1!" }
```
**Response:**
```json
{ "token": "<jwt>" }
```

Use the token in subsequent requests:
```
Authorization: Bearer <token>
```

### Users

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/users` | Public | Register a new user |
| GET | `/users/{uuid}` | Bearer | Get user by UUID |

### Teachers

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/teachers` | Public | Create a teacher |
| GET | `/teachers` | Bearer | List teachers (paginated + filtered) |
| GET | `/teachers/{uuid}` | Bearer | Get teacher by UUID |
| PUT | `/teachers/{uuid}` | Bearer | Update a teacher |
| DELETE | `/teachers/{uuid}` | Bearer | Soft-delete a teacher |
| POST | `/teachers/{uuid}/amka-file` | Public | Upload AMKA document file |

**Pagination defaults:** `page=0`, `size=5`. Pass `page`, `size`, and `sort` as query params.

### Eligible Report (async)

| Method | Endpoint | Auth | Description |
|---|---|---|---|
| POST | `/eligible/report` | Public | Start async report generation; returns `jobId` |
| GET | `/eligible/report/{jobId}` | Public | Poll for report status/result |

## Password Policy

Passwords must be at least 8 characters and contain:
- One digit
- One lowercase letter
- One uppercase letter
- One special character (`!@#$%^&+=`)

## Error Responses

All errors return a JSON body. Validation failures include per-field messages.

| HTTP Status | Cause |
|---|---|
| 400 | Validation error |
| 401 | Missing or invalid JWT |
| 403 | Insufficient permissions |
| 404 | Resource not found |
| 409 | Resource already exists |
| 500 | File upload failure or unexpected error |

## Data Model

```
roles ──< roles_capabilities >── capabilities
  |
users ──── teachers ──── personal_information ──── attachments
                |
             regions
```

All entities extend `AbstractEntity` and support **soft delete** (`deleted` flag + `deletedAt`). Records are never hard-deleted. External identifiers are UUIDs — internal `id` columns are never exposed in the API.

## API Docs (Swagger UI)

Once the application is running, open:

```
http://localhost:8080/swagger-ui.html
```

## Project Structure

```
src/main/java/gr/aueb/cf/eduapp/
  api/           REST controllers
  authentication/  JWT logic + UserDetails
  core/          Error handler, exceptions, filters, OpenAPI config
  dto/           Request/response records (Java records + Jakarta validation)
  mapper/        Entity <-> DTO conversion
  model/         JPA entities
  repository/    Spring Data repositories
  security/      Filter chain, JWT filter, CORS, entry points
  service/       Business logic (interface + implementation)
  validator/     Custom Spring Validators
```
