# 📐 PG Premium Suite - Architectural Blueprint

This document details the software design patterns, package topologies, and security architectures applied across the PG Premium Suite backend service.


## 1. Domain-Driven Package Topology (Feature-First)

The application discards the traditional monolithic "Layered Architecture" (which fragments controllers, entities, and repositories into massive global folders) in favor of a **Domain-Driven, Feature-First Design**. 

Technical layers are completely bound inside self-contained feature boundaries to maximize cohesion, reduce cognitive friction, and ensure microservice readiness.

### 📁 Core Package Blueprints

```text
com.jash.taskservice/
│
├── TaskserviceApplication.java     # Application Bootstrapper
│
├── core/                           # Cross-Cutting Infrastructure (Domain Agnostic)
│   ├── config/                     # System Engines & Security Beans
│   ├── filter/                     # Request Interception Pipeline
│   └── exception/                  # Unified Error Handling & Global Interceptors
│
└── domain/                         # Bounded Business Feature Streams (Self-Contained)
    ├── user/                       # Auth Context, Seeding, Profiles, & User Data
    ├── property/                   # Real Estate Maps, Buildings, Rooms, & Locations
    ├── task/                       # Ticket Lifecycle, Complaints, & Workflows
    ├── financial/                  # Automated Rental Ledgers, Utility Billing, & Yields
    ├── dashboard/                  # Specialized Mobile API Data Optimization DTOs
    └── audit/                      # Read-Only Security Trails & Infrastructure Tracing
```


##🔑 Key Architectural Advantages:

High Functional Cohesion: Modifying a feature's behavior requires operating inside a single, dedicated package directory.

## Low Coupling Factor: 

Business features do not spill into adjacent logical packages, making code modifications highly predictable.

## Microservice Extraction Ready:

If a business domain scales drastically in complexity, its bounded folder can be isolated and extracted into a standalone microservice with near-zero friction.

## Cognitive Clarity: 

Elimination of monolithic, global directory walls containing dozens of unrelated class definitions.



#🛡️ 2. Request Processing & Security Pipeline

All incoming HTTP requests pass through an immutable execution matrix before being handed off to the respective Domain Feature Controllers. The engine forces strict stateless operations.

```
  [ Incoming HTTP Request ]
              │
              ▼
    [ MDCLoggingFilter ]             <─ Drops Unique Correlation Trace IDs into Context
              │
              ▼
 [ JwtAuthenticationFilter ]         <─ Parses Bearer Token Payload / Verifies State
              │
              ▼
   [ AuthorizationFilter ]           <─ Validates Method-Level Security Rules (@PreAuthorize)
              │
              ▼
  [ Domain Controller Layer ]        <─ Invokes Bounded Feature Code (e.g., TaskController)

```

##🔑 Token Engine Operations

Access Tokens: Short-lived JWT identifiers (15-minute validity window) passed directly via authorization headers to sign stateless transactions.



##Refresh Tokens: 

Long-lived state tracking rows (7-day validity window) transmitted strictly through encrypted HttpOnly cookie layers to effectively eliminate Cross-Site Scripting (XSS) compromise pathways. Session authenticity is verified continuously against active PostgreSQL rows.


