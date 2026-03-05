# TMS — Transportation Management System

A modular **Transportation Management System (TMS)** backend built with **Java, Spring Boot, and GraphQL**.

The system manages the complete **shipment lifecycle**, including shipment creation, validation, pricing calculations, authentication, and event-driven integrations with external systems.

The architecture follows a **modular monolith design** with clear domain boundaries and extensible rule engines, enabling the platform to scale and evolve as new logistics workflows, pricing strategies, and carrier integrations are introduced.

---

# Tech Stack

| Layer                   | Technology            |
| ----------------------- | --------------------- |
| Backend Framework       | Spring Boot           |
| API Layer               | GraphQL               |
| Security                | Spring Security + JWT |
| Database                | PostgreSQL            |
| ORM                     | Hibernate / JPA       |
| Messaging               | RabbitMQ              |
| Caching & Rate Limiting | Redis                 |
| Build Tool              | Gradle                |

---

# Core Features

## Shipment Management

* Create shipments
* Update shipment details
* Track shipment lifecycle
* Status transition management
* Shipment filtering and pagination

Shipments support multiple attributes including:

* Dimensions
* Weight
* Delivery type
* Tracking information
* Shipment status lifecycle

---

# Pricing Engine

Pricing logic is implemented using a **rule-based pipeline**.

Each rule operates on a shared context object and modifies pricing data incrementally.

Example pricing rules:

* Chargeable weight calculation
* Slab pricing
* Fuel surcharge
* GST calculation
* International shipment handling
* Delivery percentage adjustments

Rules are modular and easily extendable.

Example rules:

* `ChargeableWeightRule`
* `FuelSurchargeRule`
* `GstRule`
* `InternationalRule`
* `SlabPricingRule`

All rules implement a shared interface:

```
CalculationRule
```

The rules operate on a shared:

```
PricingContext
```

This allows rules to be chained and executed sequentially.

---

# Shipment Validation Engine

Before a shipment is created or updated, it passes through a **validation rule pipeline**.

Each validation rule validates a specific business constraint.

Example validation rules:

* Backdating prevention
* Status transition validation
* Terminal state immutability
* Tracking number immutability
* Physical attribute validation
* Delivery type validation
* Date consistency checks

Rules operate on:

```
ShipmentValidationContext
```

and implement:

```
ShipmentValidationRule
```

This allows validation logic to remain **modular, reusable, and easy to extend**.

---

# Authentication & Security

Authentication includes:

* User signup
* Login
* JWT access token generation
* Refresh token rotation
* Email verification
* Password reset

Security is implemented using **Spring Security filters and JWT tokens**.

Key components:

* `JwtAuthenticationFilter`
* `JwtService`
* `CustomUserDetailsService`
* `CustomUserPrincipal`

---

# GraphQL API

The system exposes a **GraphQL API** for all operations.

Example operations include:

### Authentication

```
signup
login
verifyEmail
resetPassword
```

### Shipments

```
createShipment
updateShipment
shipment
shipments
```

GraphQL DTOs are organized into:

* Input DTOs
* Output DTOs
* Filters
* Pagination models

---

# Redis Rate Limiting

GraphQL resolvers are protected using **Redis-based rate limiting**.

Rate limiting supports:

* IP-based limiting for unauthenticated requests
* User-based limiting for authenticated users
* Per-resolver rate limit configuration

Redis is hosted using **Upstash Redis**.

Example configuration:

```
redis.host=${REDIS_HOST}
redis.port=${REDIS_PORT}
redis.username=${REDIS_USERNAME}
redis.password=${REDIS_PASSWORD}
redis.ssl=true
```

Environment variables:

```
REDIS_HOST
REDIS_PORT
REDIS_USERNAME
REDIS_PASSWORD
```

---

# Messaging Architecture (RabbitMQ)

The system uses **RabbitMQ** for asynchronous messaging and event-driven processing.

Messaging is used for:

* Shipment tracking updates
* Event-driven workflows
* Future integrations with external systems

Messaging components include:

* Publishers
* Consumers
* Message models
* RabbitMQ configuration

This allows decoupled services and improved scalability.

---

# Project Architecture

The project follows a modular layered architecture.

```
tms
│
├── config
│   ├── RedisConfig
│   ├── RedisProperties
│   ├── SecurityConfig
│   └── WebConfig
│
├── domain
│   Core business entities
│   Enums and value objects
│
├── graphql
│   ├── controller
│   ├── model
│   └── utils
│
├── mapper
│   Entity ↔ DTO mappings
│
├── repository
│   Data access layer
│
├── service
│   Business logic layer
│
├── rule
│   Measurement processing and Pricing rule engine
│
├── validation
│   Shipment validation rules
│
├── security
│   JWT authentication and filters
│
├── messaging
│   ├── config
│   ├── publisher
│   ├── consumer
│   └── model
│
├── utils
│   Shared utility classes
│
└── exception
    Centralized GraphQL error handling
```

---

# Environment Configuration

Create a `.env` file:

```
REDIS_HOST=your-host
REDIS_PORT=6379
REDIS_USERNAME=default
REDIS_PASSWORD=your-password
```

Other configuration can be set inside `application.properties`.

---

# Running the Project

### Clone repository

```
git clone <repository-url>
cd tms
```

---

### Build project

```
./gradlew build
```

---

### Run application

```
./gradlew bootRun
```

Application will start on:

```
http://localhost:8080
```

---

# Development Principles

The system follows several architectural principles:

* Single Responsibility Principle
* Rule-based business logic
* Layered architecture
* Stateless authentication
* Event-driven messaging
* Distributed rate limiting
* Modular validation pipelines

---

# Future Improvements

Possible future enhancements:

* Multi-carrier integrations
* Shipment tracking APIs
* GraphQL subscriptions for live tracking
* Carrier pricing configuration
* Multi-tenant architecture
* Observability and metrics

---

# Author

**Suvodip Mondal**
