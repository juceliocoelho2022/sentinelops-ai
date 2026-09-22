# 🛡️ SentinelOps AI

> **Autonomous Incident Intelligence for Java Systems**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-red)
![Status](https://img.shields.io/badge/status-v0.2.1-blue)
[![CI](https://github.com/juceliocoelho2022/sentinelops-ai/actions/workflows/ci.yml/badge.svg)](https://github.com/juceliocoelho2022/sentinelops-ai/actions/workflows/ci.yml)

**SentinelOps AI** é uma plataforma em evolução para investigação governada de incidentes em sistemas Java. O projeto combina fundamentos de backend, persistência, observabilidade e uma arquitetura alvo de agentes especializados.

> **RECOMMEND != EXECUTE** — a IA investiga e recomenda; ações críticas devem permanecer sob políticas determinísticas, autorização, aprovação humana e auditoria.

## ✅ Estado atual — v0.2.1

Implementado e validado localmente:

- Java 21 + Spring Boot 3.5.5
- REST API com DTOs e Bean Validation
- Service Layer + Spring Data JPA / Hibernate
- PostgreSQL 17 em Docker
- Flyway com migração versionada
- ProblemDetail + `@RestControllerAdvice`
- Actuator
- OpenAPI / Swagger
- `IncidentAgent` determinístico
- transição de incidente para `INVESTIGATING`
- resumo operacional do dashboard
- JUnit 5 + Mockito + MockMvc + JaCoCo
- Testcontainers com PostgreSQL 17 para integração
- GitHub Actions executando `mvn verify` em push e pull request

A integração com Prometheus, Loki, Tempo, LLMs e demais agentes permanece no roadmap; o README não apresenta essas capacidades como já implementadas.

## 🏗️ Arquitetura alvo

```text
Java / Spring Services
        │
        ├── Metrics / Prometheus
        ├── Logs / Loki
        ├── Traces / Tempo
        ├── Kafka
        ├── PostgreSQL
        └── GitHub
              │
              ▼
       SentinelOps AI
              │
   ┌──────────┼──────────┐
   ▼          ▼          ▼
Incident   LogAnalyzer  Performance
Agent      Agent        Agent
   │          │          │
   └──────────┼──────────┘
              ▼
        SecurityAgent
              │
          CodeAgent
              │
       Spring AI / LLM
              │
      Tool Calling / MCP
              │
        Policy Engine
              │
      Human Approval
              │
     Controlled Actions
```

## 🤖 Agentes planejados

| Agente | Papel |
|---|---|
| **IncidentAgent** | Implementado inicialmente; coordena a investigação determinística |
| **LogAnalyzerAgent** | Roadmap: análise de logs, exceções e padrões |
| **PerformanceAgent** | Roadmap: latência, CPU, memória, JVM e banco |
| **SecurityAgent** | Roadmap: riscos, anomalias e configurações |
| **CodeAgent** | Roadmap: correlação com commits/PRs e propostas de correção |

## ⚙️ Stack

**Backend:** Java 21, Spring Boot 3.5.5, Spring Web, Spring Data JPA, Bean Validation, Actuator.

**Dados:** PostgreSQL 17, Flyway.

**Qualidade:** JUnit 5, Mockito, MockMvc, Testcontainers e JaCoCo. GitHub Actions executa `mvn verify` em push e pull request.

**API:** OpenAPI / Swagger via springdoc.

**Infra local:** Docker + Docker Compose.

**Roadmap:** Prometheus, Grafana, Loki, Tempo, Kafka, Redis, Spring AI, LLM, Tool Calling/MCP, RAG, Kubernetes e AWS.

## 🚀 Executando localmente

Pré-requisitos: JDK 21+, Maven e Docker Desktop.

### 1. Suba o PostgreSQL

```bash
docker compose up -d
```

O container usa internamente a porta `5432` e publica o banco do SentinelOps em:

```text
localhost:5433
```

Isso evita conflito com uma instalação PostgreSQL local que já utilize `5432`.

### 2. Execute os testes

```bash
mvn clean test
```

### 3. Inicie a aplicação

```bash
mvn spring-boot:run
```

### 4. Verifique a saúde

```text
GET http://localhost:8080/actuator/health
```

Resposta esperada:

```json
{"status":"UP"}
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## 🔌 Endpoints atuais

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/incidents` | Cria incidente |
| `GET` | `/api/v1/incidents` | Lista incidentes |
| `GET` | `/api/v1/incidents/{id}` | Consulta incidente |
| `PATCH` | `/api/v1/incidents/{id}/status` | Altera status |
| `POST` | `/api/v1/incidents/{id}/investigate` | Executa investigação determinística |
| `GET` | `/api/v1/dashboard/summary` | Resumo operacional |
| `GET` | `/actuator/health` | Health check |

## 🧪 Fluxo validado

Criar incidente:

```json
{
  "title": "Latência elevada no payment-service",
  "serviceName": "payment-service",
  "severity": "HIGH",
  "description": "Latência da API aumentou durante processamento de pagamentos."
}
```

Exemplo PowerShell:

```powershell
$body = @{
    title       = "Latência elevada no payment-service"
    serviceName = "payment-service"
    severity    = "HIGH"
    description = "Latência da API aumentou durante processamento de pagamentos."
} | ConvertTo-Json

$incident = Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/api/v1/incidents" `
    -ContentType "application/json" `
    -Body $body
```

Investigar:

```powershell
Invoke-RestMethod `
    -Method Post `
    -Uri "http://localhost:8080/api/v1/incidents/$($incident.id)/investigate"
```

A implementação atual retorna uma causa provável, evidências, recomendações e exige aprovação humana. O incidente passa para `INVESTIGATING`.

Dashboard:

```powershell
Invoke-RestMethod "http://localhost:8080/api/v1/dashboard/summary"
```

## 🗃️ Banco e migrações

A aplicação utiliza PostgreSQL como banco padrão:

```text
jdbc:postgresql://localhost:5433/sentinelops
```

Configurações podem ser sobrescritas por:

- `DB_URL`
- `DB_USER`
- `DB_PASSWORD`

A estrutura inicial é criada pelo Flyway:

```text
V1__create_incidents.sql
```

O Hibernate está configurado com `ddl-auto=validate`, mantendo o versionamento de schema sob responsabilidade das migrations.

## 🗺️ Roadmap

**v0.2.1 — Quality & CI:** PostgreSQL, Flyway, DTOs, validação, ProblemDetail, Swagger, Actuator, agente determinístico, testes unitários/MockMvc, integração PostgreSQL com Testcontainers e CI com GitHub Actions.

**v0.3 — Observability Intelligence:** Prometheus, Grafana, Loki, Tempo, `LogAnalyzerAgent` e `PerformanceAgent`.

**v0.4 — Agentic AI:** Spring AI, integração LLM, Tool Calling/MCP, RAG para runbooks e memória de incidentes.

**v0.5 — Governance & Security:** `SecurityAgent`, Policy Engine, Human-in-the-Loop, auditoria e integração controlada com GitHub.

**v1.0 — Cloud-native:** Kafka, Redis, Kubernetes, AWS, `CodeAgent`, CI/CD avançado e testes de resiliência.

## 🔐 Segurança e governança

Nenhuma ação crítica deve depender exclusivamente da decisão de um LLM.

A evolução do projeto seguirá princípios de least privilege, ferramentas explicitamente autorizadas, validação de parâmetros, separação entre recomendação e execução, aprovação humana para operações sensíveis e trilha de auditoria.

Credenciais reais não devem ser commitadas no repositório. Para ambientes além do desenvolvimento local, use secrets e variáveis de ambiente.

## 📈 Objetivo de engenharia

O objetivo é explorar como agentes de IA podem participar de operações de software sem abandonar fundamentos de engenharia:

```text
Observe → Detect → Investigate → Correlate → Recommend → Approve → Act → Learn
```

## 👨‍💻 Autor

**Jucelio Farias Coelho**

Java Backend • Spring Boot • Dados • Cloud • AI Engineering

---

**SentinelOps AI — Build reliable systems. Empower people.**
