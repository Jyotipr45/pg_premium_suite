# High-Class AI-Driven PG Management System: Technical Blueprint

## 1. Architectural Philosophy
* **Pattern:** Domain-Driven Design (DDD) & Clean Architecture.
* **Paradigm:** Asynchronous, non-blocking, and event-driven.
* **Goal:** Maximum execution speed, zero redundant database calls, and ultra-low mobile internet data consumption.

## 2. Backend Tech Stack (Spring Boot)
* **Framework:** Spring Boot 3.x (Java 17/21).
* **Security:** Spring Security + Stateless JWT + Role-Based Access Control (RBAC) + Attribute-Based Access Control (ABAC).
* **Databases:**
  - PostgreSQL (ACID compliant: Users, Bookings, Room Inventory, Financial Invoices).
  - MongoDB (Polymorphic documents: AI Chat logs, System Audit Trails, Maintenance Tickets).
  - Redis (Cache Layer: In-memory storage for active dashboards).

## 3. Strict Backend Folder Structure
All Java code must map strictly to this package blueprint inside pg_backend_service:
- com.jash.taskservice.config       # Security Filters, Redis/Mongo configurations
- com.jash.taskservice.controller   # REST Entry points & compressed DTO data shapes
- com.jash.taskservice.domain       # Business Logic Layer (Models, Core Services)
- com.jash.taskservice.infrastructure # Repositories, AI connections, and WhatsApp API
