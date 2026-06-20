# 🏢 PG Premium Suite

An enterprise-grade, mobile-first ecosystem designed to manage premium Paying Guest (PG) accommodations, co-living facilities, and property operations seamlessly.

---

## 🏗️ System Architecture & Code Design

The backend service is strictly built using a **Domain-Driven, Feature-First Architecture**. Instead of traditional layer-based separation (which scatters controllers, services, and entities across global folders), everything related to a business feature is unified inside a single bounded domain context.

### 📁 Core Package Blueprints

- **`core/`**: Cross-cutting platform mechanisms independent of business features.
  - `core/config/`: System engines (Security parameters, Quartz job schedulers, Business Calendars).
  - `core/filter/`: Pipeline interceptors (`JwtAuthenticationFilter`, `MDCLoggingFilter`).
  - `core/exception/`: Global unified error interceptors and runtime rules.
- **`domain/`**: Bounded business modules containing their own controllers, entities, and repositories.
  - `domain/user/`: Authentication, session token tracking, profile configurations, and secure data access.
  - `domain/property/`: Buildings, room maps, configurations, and location registries.
  - `domain/task/`: Tenant complaint lifecycles, maintenance operations, and auto-progression workflows.
  - `domain/financial/`: Utility billing, rent matrices, landlord yields, and background ledger processing.
  - `domain/dashboard/`: Specialized analytics metrics computed specifically for mobile applications.
  - `domain/audit/`: Read-only historical logs tracing developer actions and system event records.

---

## 🛡️ Core Security Architecture

The authentication ecosystem utilizes a strict stateless dual-token approach:
1. **Access Tokens (JWT):** Short-lived tokens (15-minute validity) passed directly inside the client payload to authorize incoming REST calls.
2. **Refresh Tokens:** Long-lived tokens (7-day validity) transmitted securely via `HttpOnly` cookies to protect sessions against Cross-Site Scripting (XSS) intercept risks. Session authenticity is continuously matched against live PostgreSQL database token layers.

---

## ⚡ Quick Start & Run Commands

To spin up the service locally under the active development profile, execute the Maven wrapper from the root workspace directory:

```powershell
.\pg_backend_service\mvnw.cmd clean spring-boot:run -f pg_backend_service/pom.xml "-Dspring-boot.run.profiles=dev"