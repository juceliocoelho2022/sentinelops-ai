# SentinelOps AI — Technical Demo

This walkthrough is designed for recruiters, tech leads and software engineers evaluating the project.

## Goal

Demonstrate a governed incident-investigation workflow without granting an AI agent unrestricted infrastructure access.

## Prerequisites

- Java 21+
- Maven
- Docker Desktop

## Run

```bash
docker compose up -d
mvn clean verify
mvn spring-boot:run
```

Open Swagger UI at `http://localhost:8080/swagger-ui.html`.

## Scenario

Create a high-severity incident for a Java payment service.

```json
{
  "title": "High latency on payment-service",
  "serviceName": "payment-service",
  "severity": "HIGH",
  "description": "Payment API latency increased during transaction processing."
}
```

### 1. Create the incident

`POST /api/v1/incidents`

Save the returned incident ID.

### 2. Investigate

`POST /api/v1/incidents/{id}/investigate`

Observe the three responsibilities in the response:

- `incidentAnalysis`
- `securityAnalysis`
- `policyEvaluation`

### 3. Register a human decision

`POST /api/v1/incidents/{id}/approvals`

```json
{
  "decision": "APPROVED",
  "reason": "Evidence reviewed for portfolio demonstration"
}
```

The actor is derived from the authenticated JWT principal. The token must carry the `incident:approve` scope.

### 4. Inspect the audit trail

`GET /api/v1/incidents/{id}/approvals`

The API returns the persisted decision history.

### 5. Inspect the dashboard

`GET /api/v1/dashboard/summary`

## Engineering boundary

The current version does **not** execute infrastructure actions. Approval is an auditable human decision, not permission for an LLM to directly change Kubernetes, AWS, databases or source code.

The approval endpoint is protected by JWT authentication and the `incident:approve` scope. The audit actor is derived from the authenticated principal, not from request input. A persisted `DENY_ACTION` cannot be overridden through the normal approval endpoint.

## What to review in the code

- `IncidentService` — investigation orchestration
- `IncidentAgent` — deterministic incident analysis
- `SecurityAgent` — deterministic security analysis
- `PolicyEngine` — governance rules
- `ApprovalService` — human decision persistence
- Flyway migrations — database evolution
- tests — unit, MVC and PostgreSQL integration coverage
- GitHub Actions — Maven verification and Docker build
