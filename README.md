# 🪪 Credential Manager Service

Backend service built with **Java 21 + Spring WebFlux + R2DBC + PostgreSQL + JWT**, designed as a **code challenge** to demonstrate clean architecture, reactive programming, Docker, CI, and good engineering practices.

---

## 🚀 Tech Stack

- ☕ **Java 21**
- 🌱 **Spring Boot (WebFlux)**
- 🧵 **Reactive stack (Project Reactor)**
- 🐘 **PostgreSQL + R2DBC**
- 🗃 **Flyway** (DB migrations)
- 🔐 **JWT authentication**
- 🧪 **JUnit 5 + Mockito + WebTestClient**
- 🐳 **Docker & Docker Compose**
- ⚙️ **GitHub Actions (CI)**

---

## 📦 Project Structure (High level)

src/
├─ main/
│ ├─ api/ # Controllers & DTOs
│ ├─ application/ # Use cases & application logic
│ ├─ domain/ # Domain models & business rules
│ ├─ infrastructure/ # Persistence, security, config
│ └─ shared/ # Error handling, shared contracts
└─ test/ # Unit & slice tests

Architecture follows **clean layering**:
- Domain is framework-agnostic
- Application orchestrates use cases
- Infrastructure handles technical concerns
- API layer exposes HTTP endpoints

---

## ▶️ Run locally (without Docker)

### Requirements
- Java 21
- Maven
- PostgreSQL running locally

### 1️⃣ Create database
```sql
    CREATE DATABASE appdb;
    CREATE USER app WITH PASSWORD 'app';
    GRANT ALL PRIVILEGES ON DATABASE appdb TO app;


### 2️⃣ Run application
    mvn spring-boot:run
The API will be available at:
    http://localhost:8080/


🐳 Run with Docker (recommended)
    Requirements
    Docker
    Docker Compose
    
1️⃣ Create env file
    cp .env.example .env
2️⃣ Build & run
    docker compose up --build
    
This will:
    start PostgreSQL
    run Flyway migrations
    start the API
API available at:
    http://localhost:8080/
    
    
🔐 Authentication Flow
1.-Register a user
2.-Login to receive a JWT
3.-Use the JWT in Authorization: Bearer <token> header

📡 Available API Endpoints:

🔑 Auth

➕ Register
POST /api/v1/auth/register

curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "password123"
  }'


🔓 Login
POST /api/v1/auth/login

curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "email": "user@test.com",
    "password": "password123"
  }'

Response:
{
  "accessToken": "jwt-token",
  "tokenType": "Bearer"
}

🪪 Credentials
All endpoints below require:
Authorization: Bearer <JWT>


➕ Create credential
POST /api/v1/credentials
Allowed types:
HVAC_LICENSE
EPA_608
INSURANCE
STATE_LICENSE

curl -X POST http://localhost:8080/api/v1/credentials \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer <JWT>" \
  -d '{
    "type": "HVAC_LICENSE",
    "issuer": "State Licensing Board",
    "license_number": "HV-2026-001",
    "expiry_date": "2027-12-31"
  }'


📄 List credentials (with filters & pagination)
GET /api/v1/credentials
Query params:
    status (optional)
    type (optional)
    cursor (optional)
    limit (optional)
    
curl -X GET "http://localhost:8080/api/v1/credentials?type=HVAC_LICENSE&status=PENDING" \
  -H "Authorization: Bearer <JWT>"


🔍 Get credential by ID
GET /api/v1/credentials/{id}

curl -X GET http://localhost:8080/api/v1/credentials/1 \
  -H "Authorization: Bearer <JWT>"


🗑 Soft delete credential
Allowed only when status is PENDING or REJECTED
DELETE /api/v1/credentials/{id}

curl -X DELETE http://localhost:8080/api/v1/credentials/1 \
  -H "Authorization: Bearer <JWT>"


🛠 Admin (no auth for this challenge)
(In real production this endpoint would be secured it was specified un documentation)
✅ Update credential status
Allowed values:
APPROVED
REJECTED
PUT /api/v1/admin/credentials/{id}/status

curl -X PUT http://localhost:8080/api/v1/admin/credentials/1/status \
  -H "Content-Type: application/json" \
  -d '{
    "status": "APPROVED"
  }'
Invalid values return 400 BAD_REQUEST.


🧪 Tests
Run unit tests:
mvn test

Tests include:
-Domain rules
-Application services
-Controller slice tests
-Global error handling


⚙️ CI
GitHub Actions workflow:
Runs unit tests with PostgreSQL service
Builds Docker image
Uses environment variables and secrets safely


🔒 Security Notes
JWT secret injected via environment variables
No credentials committed to repository
.env ignored, .env.example provided


📌 Notes for Reviewers
1.-Focus is on architecture, correctness, and clarity
2.-Reactive programming used end-to-end
3.-Docker & CI included to demonstrate real-world readiness
4.-Error handling standardized via enums & global handler

👨‍💻 Author
Developed by Luis Gutierrez (devPull Developer 😉) Thank you for your time to review this project!

