<div align="center">

<br>

# REVASTRA

### Smart Sustainable Clothing Ecosystem

*Unified platform for laundry, donation, stitching, recycling, and upcycling —*
*built on a microservice architecture that promotes circular fashion and economic inclusion.*

<br>

![Version](https://img.shields.io/badge/Version-1.0-1a1a2e?style=for-the-badge)
![Java](https://img.shields.io/badge/Java-17+-f89820?style=for-the-badge&logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.2.x-6db33f?style=for-the-badge&logo=springboot&logoColor=white)
![React](https://img.shields.io/badge/React-18+-61dafb?style=for-the-badge&logo=react&logoColor=black)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-14+-336791?style=for-the-badge&logo=postgresql&logoColor=white)
![Status](https://img.shields.io/badge/Status-In_Development-orange?style=for-the-badge)

<br>

</div>

---

## Table of Contents

1. [Project Overview](#1-project-overview)
2. [Problem Statement](#2-problem-statement)
3. [Core Modules](#3-core-modules)
4. [Technology Stack](#4-technology-stack)
5. [System Architecture](#5-system-architecture)
6. [Team](#6-team)
7. [Prerequisites](#7-prerequisites)
8. [Getting Started](#8-getting-started)
9. [Running the Application](#9-running-the-application)
10. [API Reference](#10-api-reference)
11. [Database Schema](#11-database-schema)
12. [Git Workflow](#12-git-workflow)
13. [Testing](#13-testing)
14. [Project Structure](#14-project-structure)
15. [Contributing](#15-contributing)
16. [Sprint Timeline](#16-sprint-timeline)
17. [Evaluation Criteria](#17-evaluation-criteria)
18. [Contact](#18-contact)

---

## 1. Project Overview

| | |
|---|---|
| **Program** | GlobalLogic Java Track — TE 2.0 |
| **Sprint** | 3 April – 9 April 2026 |
| **Evaluation** | 10 April 2026 |
| **Delivery Type** | Baselined End-to-End |
| **Repository** | `gl-spark-revastra` |
| **Team Size** | 4 Members |
| **Version** | 1.0 |

<br>

**Revastra** is a multi-service clothing ecosystem built on a microservice architecture. It integrates laundry management, clothing donation, recycling, stitching, and an upcycle marketplace into a single platform — driving a circular clothing lifecycle while generating income opportunities for skilled workers, students, and homemakers.

### Vision

To transform the clothing industry by creating a sustainable, trustworthy, and economically empowering ecosystem where laundry, donation, recycling, stitching, and upcycling are seamlessly unified.

### Mission

Reduce textile waste through circular fashion while enabling skilled workers to earn income by providing quality services through a verified, safe, and accessible marketplace.

---

## 2. Problem Statement

The current clothing services landscape has five critical gaps that Revastra directly addresses:

| Gap | Description |
|-----|-------------|
| **Textile Waste** | Fast fashion drives rapid waste accumulation with no accessible sustainable alternatives for consumers. |
| **Fragmented Services** | No single trusted platform exists for laundry, tailoring, and recycling together. |
| **Worker Exploitation** | Skilled workers — tailors, craftspeople — lack a dedicated platform to market and monetize their abilities. |
| **Safety Concerns** | No standardized verification exists for workers who provide home-visit services. |
| **No Sustainability Incentives** | Users receive no tangible rewards for donating or recycling clothing, reducing motivation. |

---

## 3. Core Modules

| Module | Description | Owner |
|--------|-------------|-------|
| **Laundry Management** | Washing, dry cleaning, ironing, and pickup & delivery scheduling | Sachin |
| **Donation & Recycling** | Clothing donation intake, reward point calculation, and recycling requests | Rakshita |
| **Stitching & Alteration** | Worker booking, minor fitting, and alteration order management | Samridhi |
| **Upcycle Marketplace** | Skill registration, product listing, and worker earnings dashboard | Samridhi |
| **Payment & Wallet** | Online payment processing, wallet top-up, and discount application via reward points | Rakshita |
| **Reward System** | Points earned on donation and purchase, redeemable against future orders | Rakshita |
| **Worker Verification** | Phone OTP verification, admin approval workflow, and verified badge display | Samridhi |
| **Ratings & Reviews** | Post-service ratings, written reviews, and trust scoring | Tushar |
| **Admin Dashboard** | User management, worker approval queue, and real-time order monitoring | Tushar |

---

## 4. Technology Stack

### Backend

| Component | Technology |
|-----------|------------|
| Language | Java 17+ |
| Framework | Spring Boot 3.2.x |
| Architecture | Microservices with Spring Cloud |
| API Style | REST |
| Security | Spring Security + JWT (refresh token flow) |
| Password Encoding | BCrypt |
| API Gateway | Spring Cloud Gateway |
| Service Discovery | Netflix Eureka |
| Load Balancing | Client-side (Ribbon) |

### Frontend

| Component | Technology |
|-----------|------------|
| Framework | React 18+ |
| State Management | React Hooks |
| HTTP Client | Axios |
| Styling | Tailwind CSS / CSS3 |

### Data & Persistence

| Component | Technology |
|-----------|------------|
| Database | PostgreSQL 14+ |
| ORM | JPA / Hibernate |
| Migrations | Flyway or Liquibase |

### Testing & DevOps

| Component | Technology |
|-----------|------------|
| Unit Testing | JUnit 5 |
| Mocking | Mockito |
| Integration Testing | Testcontainers *(optional)* |
| Coverage Target | 70% minimum |
| Build Tool | Maven 3.8+ |
| Version Control | Git / GitHub |
| CI/CD | GitHub Actions |
| Containerisation | Docker *(optional)* |

---

## 5. System Architecture

### Service Topology

```
                        ┌─────────────────────┐
                        │    User / Browser    │
                        └──────────┬──────────┘
                                   │
                                   ▼
                      ┌────────────────────────┐
                      │       API Gateway       │
                      │    (Spring Cloud)       │
                      │  Auth · Rate Limiting   │
                      └───────────┬────────────┘
                                  │
           ┌──────────────────────┼──────────────────────┐
           │                      │                      │
           ▼                      ▼                      ▼
   ┌──────────────┐      ┌──────────────┐      ┌──────────────┐
   │ User Service │      │Laundry Service│      │  Recycling   │
   │              │      │              │      │   Service    │
   └──────┬───────┘      └──────┬───────┘      └──────┬───────┘
          │                     │                     │
          │          ┌──────────────────────┐         │
          └──────────│   Service Registry   │─────────┘
                     │   (Netflix Eureka)   │
          ┌──────────│   Dynamic Discovery  │─────────┐
          │          └──────────────────────┘         │
          ▼                     │                     ▼
   ┌──────────────┐      ┌──────┴───────┐      ┌──────────────┐
   │   Upcycle    │      │    Order     │      │   Payment    │
   │   Service    │      │   Service    │      │   Service    │
   └──────────────┘      └─────────────┘      └──────────────┘
          │                     │                     │
          └─────────────────────┼─────────────────────┘
                                ▼
                  ┌─────────────────────────┐
                  │      PostgreSQL DB       │
                  │  (Database per Service)  │
                  └─────────────────────────┘
```

### Service Registry

| Service | Description | Priority |
|---------|-------------|----------|
| API Gateway | Single entry point — routing, auth filter, rate limiting | Required |
| Service Registry | Eureka-based service registration and discovery | Required |
| User Service | Registration, login, JWT issuance and refresh | Required |
| Laundry Service | Booking, pricing, pickup and delivery scheduling | Required |
| Recycling Service | Donation intake and reward point calculation | Required |
| Upcycle Service | Worker listings, skill registration, marketplace | Required |
| Order Service | Order lifecycle management and status tracking | Required |
| Payment Service | Payment gateway integration and wallet management | Required |
| Notification Service | Email / SMS for bookings, donations, and verification | Optional |

### Request Lifecycle

```
User Login
  → Browse Services
    → Select Service & Book
      → API Gateway routes request
        → Microservice processes booking
          → Worker Assignment
            → Payment Processing
              → Order Status Tracking
                → Service Completion
                  → Rating Submitted
                    → Reward Points Credited
```

---

## 6. Team

| Name | Role | Backend Ownership | Frontend Ownership | GitHub |
|------|------|------------------|-------------------|--------|
| **Rakshita** | Core Logic Lead | Recycling Service · Payment Service · Reward System | Donation UI · Wallet UI · Points Dashboard | [@rakshitasuyal](https://github.com/rakshitasuyal) |
| **Sachin** | Order & Laundry Lead | Laundry Service · Order Service | Laundry Booking UI · Order Tracking UI | [@vishwakarmasachin632](https://github.com/vishwakarmasachin632) |
| **Samridhi** | Marketplace Lead | Upcycle Service · Worker Verification | Worker Listing · Stitching & Alteration UI | [@Samriddhig19](https://github.com/Samriddhig19) |
| **Tushar** | Integration & UI Lead | User Service · API Gateway · Admin Endpoints | Main Dashboard · Navigation · Ratings UI | [@tushargoyal11](https://github.com/tushargoyal11) |

---

## 7. Prerequisites

Ensure the following tools are installed before setting up the project.

| Dependency | Minimum Version | Download |
|------------|----------------|----------|
| Java (JDK) | 17 | [Oracle JDK](https://www.oracle.com/java/technologies/downloads/#java17) |
| Apache Maven | 3.8 | [maven.apache.org](https://maven.apache.org/download.cgi) |
| Node.js | 16 | [nodejs.org](https://nodejs.org/) |
| Git | Any recent | [git-scm.com](https://git-scm.com/) |
| PostgreSQL | 14 | [postgresql.org](https://www.postgresql.org/download/) |

**Verify your environment:**

```bash
java -version     # Expected: 17+
mvn -version      # Expected: 3.8+
node -v           # Expected: v16+
npm -v            # Should be present
git --version     # Should be present
```

---

## 8. Getting Started

### Step 1 — Clone the Repository

```bash
git clone https://github.com/your-org/gl-spark-revastra.git
cd gl-spark-revastra
```

### Step 2 — Create the Database

Connect to PostgreSQL and run the following:

```sql
CREATE DATABASE revastra;

CREATE USER revastra_user WITH PASSWORD 'secure_password';

ALTER ROLE revastra_user SET client_encoding TO 'utf8';
ALTER ROLE revastra_user SET default_transaction_isolation TO 'read committed';
ALTER ROLE revastra_user SET timezone TO 'UTC';

GRANT ALL PRIVILEGES ON DATABASE revastra TO revastra_user;
```

### Step 3 — Configure Application Properties

Update `src/main/resources/application.properties` in each service:

```properties
# ── Database ──────────────────────────────────────────────────────────
spring.datasource.url=jdbc:postgresql://localhost:5432/revastra
spring.datasource.username=revastra_user
spring.datasource.password=secure_password
spring.datasource.driver-class-name=org.postgresql.Driver

# ── JPA / Hibernate ───────────────────────────────────────────────────
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=false

# ── Service Discovery ─────────────────────────────────────────────────
eureka.client.register-with-eureka=true
eureka.client.fetch-registry=true
eureka.client.service-url.defaultZone=http://localhost:8761/eureka

# ── Service Identity ──────────────────────────────────────────────────
spring.application.name=your-service-name
server.port=8081
```

> **Security Note:** Never commit real credentials. Use environment variables or a secrets manager for all sensitive configuration values.

### Step 4 — Build the Backend

```bash
# Build all modules (skip tests for initial setup)
mvn clean install -DskipTests=true

# Build a specific service
cd laundry-service && mvn clean package
```

### Step 5 — Set Up the Frontend

```bash
cd frontend
npm install
npm install axios react-router-dom
npm run build
```

---

## 9. Running the Application

### Development Mode

Start each service in a separate terminal session, in the order listed:

```bash
# 1. Service Registry — must start first
cd service-registry && mvn spring-boot:run

# 2. API Gateway
cd api-gateway && mvn spring-boot:run

# 3. Core Services
cd user-service      && mvn spring-boot:run
cd laundry-service   && mvn spring-boot:run
cd recycling-service && mvn spring-boot:run
cd upcycle-service   && mvn spring-boot:run
cd order-service     && mvn spring-boot:run
cd payment-service   && mvn spring-boot:run

# 9. Frontend
cd frontend && npm start
```

### Docker (Planned)

```bash
docker-compose build
docker-compose up
```

### Access Points

| Interface | URL |
|-----------|-----|
| Frontend Application | http://localhost:3000 |
| API Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |

### Health Checks

```bash
curl http://localhost:8761/
curl http://localhost:8080/api/health
curl http://localhost:8081/api/users/health
```

---

## 10. API Reference

All routes are accessed through the API Gateway at `http://localhost:8080`.

### Authentication

```
POST   /api/users/register          Register a new user account
POST   /api/users/login             Authenticate and receive a JWT
POST   /api/users/refresh           Refresh an expired JWT
```

### Laundry

```
POST   /api/laundry/book            Book a laundry service
GET    /api/laundry/slots           Get available pickup slots
GET    /api/laundry/{id}            Get booking details by ID
PUT    /api/laundry/{id}/cancel     Cancel an existing booking
```

### Donation & Recycling

```
POST   /api/recycling/donate        Submit a clothing donation request
GET    /api/recycling/points        Get current reward point balance
GET    /api/recycling/history       Get full donation history
```

### Workers & Upcycle Marketplace

```
GET    /api/workers                 List all verified workers
GET    /api/workers/{id}            Get a worker's profile
POST   /api/workers/register        Worker self-registration
POST   /api/workers/{id}/verify     Verify a worker's phone via OTP
GET    /api/marketplace/products    Browse upcycled product listings
```

### Orders

```
POST   /api/orders                  Create a new service order
GET    /api/orders/{id}             Get order details by ID
GET    /api/orders                  List all orders for current user
PUT    /api/orders/{id}/status      Update order status
```

### Payments & Wallet

```
POST   /api/payment/pay             Process a payment
GET    /api/payment/wallet          Get wallet balance
POST   /api/payment/wallet/topup    Add funds to wallet
GET    /api/payment/wallet/history  View transaction history
```

### Ratings & Reviews

```
POST   /api/reviews                 Submit a post-service review
GET    /api/reviews/order/{id}      Get all reviews for an order
GET    /api/workers/{id}/ratings    Get a worker's rating summary
```

### Admin

```
GET    /api/admin/workers/pending        List workers awaiting approval
PUT    /api/admin/workers/{id}/approve   Approve a worker profile
GET    /api/admin/orders                 Monitor all platform orders
GET    /api/admin/users                  List all registered users
```

---

## 11. Database Schema

### User

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | Auto-generated |
| `name` | String | |
| `email` | String | Unique |
| `password_hash` | String | BCrypt encoded |
| `phone` | String | |
| `role` | Enum | `USER` · `WORKER` · `ADMIN` |
| `address` | String | |
| `wallet_balance` | Decimal | |
| `created_at` | Timestamp | |
| `updated_at` | Timestamp | |

### Worker

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `user_id` | FK → User | |
| `skills` | String | |
| `experience_years` | Integer | |
| `rating` | Decimal | 0–5 scale |
| `badge_status` | String | |
| `verification_status` | Enum | `PENDING` · `VERIFIED` · `REJECTED` |
| `phone_verified` | Boolean | |
| `created_at` | Timestamp | |
| `updated_at` | Timestamp | |

### Order

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `user_id` | FK → User | |
| `worker_id` | FK → Worker | |
| `service_type` | Enum | `LAUNDRY` · `STITCHING` · `UPCYCLE` |
| `status` | Enum | `PENDING` · `CONFIRMED` · `IN_PROGRESS` · `COMPLETED` · `CANCELLED` |
| `total_amount` | Decimal | |
| `created_at` | Timestamp | |
| `completed_at` | Timestamp | |

### Payment

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `order_id` | FK → Order | |
| `user_id` | FK → User | |
| `amount` | Decimal | |
| `status` | Enum | `PENDING` · `SUCCESS` · `FAILED` · `REFUNDED` |
| `payment_method` | Enum | `CARD` · `WALLET` · `UPI` |
| `transaction_id` | String | External gateway reference |
| `created_at` | Timestamp | |

### Reward

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `user_id` | FK → User | |
| `points_earned` | Integer | |
| `source` | Enum | `DONATION` · `REFERRAL` · `PURCHASE` |
| `points_redeemed` | Integer | |
| `balance` | Integer | |
| `last_updated` | Timestamp | |

### Donation

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `user_id` | FK → User | |
| `item_count` | Integer | |
| `description` | String | |
| `status` | Enum | `PENDING` · `ACCEPTED` · `COLLECTED` |
| `points_earned` | Integer | |
| `created_at` | Timestamp | |

### Review

| Field | Type | Notes |
|-------|------|-------|
| `id` | PK | |
| `order_id` | FK → Order | |
| `rating` | Integer | 1–5 scale |
| `comment` | String | |
| `created_at` | Timestamp | |

---

## 12. Git Workflow

### Branching Strategy

| Branch | Purpose | Merge Authority |
|--------|---------|-----------------|
| `main` | Production-ready, stable releases only | Team Lead via PR |
| `develop` | Integration branch — all features merge here first | Team Lead |
| `feature/<story-id>` | One branch per user story (e.g. `feature/US-001`) | Developer via PR |
| `hotfix/<issue>` | Critical production bug fixes | Team Lead |

### Commit Message Convention

Format: `type(scope): description` — written in the imperative tense, kept concise.

```
feat(US-002):     add laundry booking endpoint
fix(auth):        resolve JWT expiry handling
test(US-003):     add unit tests for recycling service
docs:             update README setup instructions
chore:            upgrade Spring Boot to 3.2.4
refactor:         extract WorkerValidator into separate class
```

### Day-to-Day Workflow

```bash
# 1. Always start from an up-to-date develop branch
git checkout develop
git pull origin develop

# 2. Create a feature branch
git checkout -b feature/US-001

# 3. Develop, then commit in small, logical units
git add .
git commit -m "feat(US-001): implement user registration endpoint"

# 4. Push and open a Pull Request targeting develop
git push origin feature/US-001

# 5. After review approval and CI passing → merge via PR

# 6. Clean up local and remote branches
git branch -d feature/US-001
git push origin --delete feature/US-001
```

### Rules

- Direct commits to `main` are **not permitted** under any circumstance.
- All changes must be submitted via Pull Requests.
- At least **one team member** must review and approve before merge.
- All CI checks and tests must pass before a PR can be merged.
- Secrets, credentials, and API keys must **never** be committed to the repository.

---

## 13. Testing

### Running Tests

```bash
# Run all tests across all modules
mvn test

# Run tests for a specific service
mvn test -f laundry-service/pom.xml

# Generate a JaCoCo coverage report
mvn test jacoco:report

# Open the coverage report in browser
open target/site/jacoco/index.html
```

### Coverage Targets

| Level | Target |
|-------|--------|
| Minimum (required) | 70% |
| Target | 85%+ |
| Core business logic | 100% |

### Unit Test Example

```java
@SpringBootTest
class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserRepository userRepository;

    @Test
    void testRegisterUser_Success() {
        // Arrange
        UserRegistrationRequest request = new UserRegistrationRequest(
            "user@example.com", "password123"
        );

        // Act
        UserResponse response = userService.register(request);

        // Assert
        assertNotNull(response.getId());
        assertEquals("user@example.com", response.getEmail());
        verify(userRepository, times(1)).save(any());
    }
}
```

### Integration Tests

```bash
# Run integration test group only
mvn test -Dgroups=integration

# Run with Testcontainers
mvn test -Duse.testcontainers=true
```

---

## 14. Project Structure

```
gl-spark-revastra/
│
├── service-registry/                    # Eureka Service Registry
│   ├── src/main/
│   └── pom.xml
│
├── api-gateway/                         # Spring Cloud Gateway
│   ├── src/main/
│   └── pom.xml
│
├── user-service/                        # User Management & Auth
│   ├── src/
│   │   ├── main/java/com/revastra/user/
│   │   │   ├── controller/
│   │   │   ├── service/
│   │   │   ├── repository/
│   │   │   ├── entity/
│   │   │   └── dto/
│   │   └── test/
│   └── pom.xml
│
├── laundry-service/                     # Laundry Booking & Management
│   ├── src/
│   └── pom.xml
│
├── recycling-service/                   # Donation & Recycling
│   ├── src/
│   └── pom.xml
│
├── upcycle-service/                     # Upcycle Marketplace
│   ├── src/
│   └── pom.xml
│
├── order-service/                       # Order Lifecycle
│   ├── src/
│   └── pom.xml
│
├── payment-service/                     # Payments & Wallet
│   ├── src/
│   └── pom.xml
│
├── frontend/                            # React Frontend
│   ├── public/
│   ├── src/
│   │   ├── components/
│   │   │   ├── Auth/
│   │   │   ├── Laundry/
│   │   │   ├── Donation/
│   │   │   ├── Worker/
│   │   │   └── Admin/
│   │   ├── services/
│   │   │   └── api.js
│   │   ├── pages/
│   │   ├── styles/
│   │   ├── App.js
│   │   └── index.js
│   ├── package.json
│   └── .env.example
│
├── common/                              # Shared DTOs, exceptions, utilities
│   ├── src/main/java/com/revastra/common/
│   │   ├── dto/
│   │   ├── exception/
│   │   ├── util/
│   │   └── constants/
│   └── pom.xml
│
├── docs/
│   ├── API_DOCUMENTATION.md
│   ├── DATABASE_SCHEMA.md
│   ├── ARCHITECTURE.md
│   ├── USER_STORIES.md
│   └── SETUP_GUIDE.md
│
├── postman/
│   └── Revastra_API.postman_collection.json
│
├── pom.xml                              # Parent POM
├── docker-compose.yml
├── .gitignore
└── .github/
    └── workflows/
        └── ci.yml
```

---

## 15. Contributing

Follow these steps for every contribution, no exceptions:

1. **Branch** — Create a `feature/US-XXX` branch from an up-to-date `develop`.
2. **Test first** — Write unit tests before or alongside implementation (TDD).
3. **Code quality** — Follow Java naming conventions; write self-documenting code with comments for non-obvious logic.
4. **Commit atomically** — Keep commits small and focused. Use the commit convention defined in Section 12.
5. **Open a Pull Request** — Write a clear description and link to the relevant user story.
6. **Address reviews** — Respond to all review comments before requesting re-approval. Do not force-push branches under review.
7. **Merge conditions** — At least one approval required. All CI checks must pass. No merge conflicts.

---

## 16. Sprint Timeline

| Day | Date | Milestone |
|-----|------|-----------|
| Day 1 | 3 April 2026 | Project kickoff · PRD finalised · Repository created · Design slides |
| Day 2 | 4 April 2026 | Microservice scaffolding · Database schema · API contracts defined |
| Days 3–4 | 5–6 April 2026 | Backend service implementation · Endpoints coded and tested |
| Days 5–6 | 7–8 April 2026 | Frontend built and integrated with backend via API Gateway |
| Day 7 | 9 April 2026 | End-to-end testing · Bug fixes · Code freeze · Final push |
| **Evaluation** | **10 April 2026** | **Live demo — all features fully functional** |

> **Code freeze:** 9 April 2026 at 5:00 PM. No new features or refactors after this point.

---

## 17. Evaluation Criteria

| Area | Weight | Expectation |
|------|--------|-------------|
| Working Code Demo | 25% | All features functional with no crashes during the live demo |
| Architecture & Design | 20% | Clear microservice separation with API Gateway and Eureka correctly implemented |
| User Stories & PRD | 15% | 6+ user stories with acceptance criteria and full traceability |
| Testing — TDD / JUnit | 15% | Test-first approach, minimum 70% coverage, Mockito in use |
| Code Quality & Version Control | 10% | Clean commits, correct branching strategy, no committed secrets |
| Diagrams & Slides | 10% | All 7 required diagram types complete and accurate |
| Team Collaboration | 5% | Equal Git contributions, PR history visible, peer reviews documented |

---

## 18. Contact

| Name | Role | Email | GitHub |
|------|------|-------|--------|
| Rakshita | Core Logic Lead | rakshitasuyal900@gmail.com | [@rakshitasuyal](https://github.com/rakshitasuyal) |
| Sachin | Order & Laundry Lead | vishwakarmasachin632@gmail.com | [@vishwakarmasachin632](https://github.com/vishwakarmasachin632) |
| Samridhi | Marketplace Lead | sinu.secu@gmail.com | [@Samriddhig19](https://github.com/Samriddhig19) |
| Tushar | Integration & UI Lead | tgoyal2001@gmail.com | [@tushargoyal11](https://github.com/tushargoyal11) |

### Documentation Index

| Document | Path |
|----------|------|
| API Documentation | [`docs/API_DOCUMENTATION.md`](./docs/API_DOCUMENTATION.md) |
| Database Schema | [`docs/DATABASE_SCHEMA.md`](./docs/DATABASE_SCHEMA.md) |
| Architecture Guide | [`docs/ARCHITECTURE.md`](./docs/ARCHITECTURE.md) |
| User Stories | [`docs/USER_STORIES.md`](./docs/USER_STORIES.md) |
| Setup Guide | [`docs/SETUP_GUIDE.md`](./docs/SETUP_GUIDE.md) |

---

<div align="center">

**Revastra** &nbsp;·&nbsp; GlobalLogic Java Track TE 2.0 &nbsp;·&nbsp; Version 1.0 &nbsp;·&nbsp; April 2026

*This project is proprietary to the GlobalLogic Java Track TE 2.0 Program.*

</div>
