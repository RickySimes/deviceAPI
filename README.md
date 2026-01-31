# Device API

## Overview
REST API for managing device inventory with state-based business rules.

## Features
  - CRUD operations for devices
  - Filter by brand or state (AVAILABLE, IN_USE, INACTIVE)
  - Business rules: IN_USE devices cannot be deleted or renamed
  - Validation with detailed error responses
  - OpenAPI documentation

## Tech Stack

 - Java 21
 - Spring Boot 3.5.4
 - Spring Data JPA
 - PostgreSQL 16
 - Maven
 - Docker & Docker Compose

 ## Prerequisites

 - Java 21+
 - Maven 3.9+ (or use included Maven Wrapper)
 - Docker & Docker Compose (for containerized deployment)

 ## Quick Start
  docker-compose up

  ## API Endpoints
  | Method | Endpoint | Description |
  |--------|----------|-------------|
  | POST   | /api/devices | Create device |
  | GET    | /api/devices | List devices (?brand=&state=) |
  | GET    | /api/devices/{id} | Get device |
  | PATCH  | /api/devices/{id} | Update device |
  | DELETE | /api/devices/{id} | Delete device |

## API Docs
  http://localhost:8080/swagger-ui.html


## Running Tests
./mvnw test

## Future Improvements
- Add caching (Redis)
- Add rate limiting
- Add audit logging
- Add API versioning strategy
