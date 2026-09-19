# 🛡️ SentinelOps AI

> **Autonomous Incident Intelligence for Java Systems**

[![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)](https://openjdk.org/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=springboot&logoColor=white)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)](https://www.postgresql.org/)
[![Docker](https://img.shields.io/badge/Docker-ready-2496ED?logo=docker&logoColor=white)](https://www.docker.com/)
[![AI](https://img.shields.io/badge/AI-Agentic%20Architecture-8A2BE2)](#-ai-agents)
[![Status](https://img.shields.io/badge/status-MVP%20v0.1-blue)](#-roadmap)

**SentinelOps AI** é uma plataforma de engenharia de confiabilidade orientada por IA para **observar, investigar e responder a incidentes em sistemas Java distribuídos**.

A proposta combina **Java + Spring Boot + observabilidade + Agentic AI + governança**, permitindo que agentes especializados correlacionem métricas, logs, traces, banco de dados e alterações de código para gerar hipóteses de causa raiz e recomendações — mantendo ações críticas sob **Policy Engine e Human-in-the-Loop**.

---

## 🎯 Problema

Em arquiteturas distribuídas, descobrir por que um serviço falhou pode exigir navegar manualmente entre:

- métricas no Prometheus;
- logs no Loki;
- traces distribuídos no Tempo;
- eventos Kafka;
- banco PostgreSQL;
- Kubernetes;
- deploys, commits e Pull Requests.

O SentinelOps AI pretende transformar esses sinais dispersos em uma **investigação de incidente correlacionada e auditável**.

## 💡 Exemplo

Imagine que o `payment-service` passe de **300 ms para 2,4 s** de latência.

O SentinelOps AI poderá:

1. detectar a degradação;
2. consultar métricas do serviço;
3. correlacionar exceções e timeouts nos logs;
4. analisar traces distribuídos;
5. verificar saturação ou lentidão no PostgreSQL;
6. relacionar o início do problema a deploys recentes;
7. produzir uma hipótese de causa raiz;
8. sugerir uma correção;
9. solicitar aprovação humana antes de qualquer ação crítica.

> **A IA investiga e recomenda. A plataforma governa o que pode ser executado.**

---

## 🏗️ Arquitetura alvo

```text
 Java / Spring Services
          │
          ├──── Prometheus ─── Metrics
          ├──── Loki ───────── Logs
          ├──── Tempo ──────── Traces
          ├──── Kafka ──────── Events
          ├──── PostgreSQL ─── Data
          └──── GitHub ─────── Code / PRs
                     │
                     ▼
              ┌───────────────┐
              │ SentinelOps AI│
              └───────┬───────┘
                      │
        ┌─────────────┼──────────────┐
        ▼             ▼              ▼
 IncidentAgent  LogAnalyzerAgent  PerformanceAgent
        │             │              │
        └─────────────┼──────────────┘
                      ▼
              SecurityAgent
                      │
                      ▼
                  CodeAgent
                      │
                      ▼
             Spring AI / LLM
                      │
               Tool Calling / MCP
                      │
                      ▼
                Policy Engine
                      │
              Human-in-the-Loop
                      │
                      ▼
              Controlled Actions
```

## 🤖 AI Agents

| Agente | Responsabilidade |
|---|---|
| **IncidentAgent** | Coordena a investigação e consolida evidências |
| **LogAnalyzerAgent** | Analisa logs, exceções e padrões anormais |
| **PerformanceAgent** | Investiga latência, CPU, memória, JVM e banco |
| **SecurityAgent** | Identifica riscos, anomalias e configurações suspeitas |
| **CodeAgent** | Relaciona incidentes a commits/PRs e propõe correções |

Na **v0.1**, o `IncidentAgent` já possui um fluxo determinístico de investigação. A integração real com LLM e ferramentas será adicionada progressivamente para evitar autonomia prematura.

---

## ⚙️ Stack

### Backend
- Java 21
- Spring Boot 3.5.5
- Spring Web
- Spring Data JPA
- Bean Validation
- Spring Boot Actuator
- JUnit 5

### Dados
- PostgreSQL 17
- H2 para desenvolvimento inicial
- Redis *(roadmap)*
- Kafka *(roadmap)*

### Observabilidade
- Prometheus *(roadmap)*
- Grafana *(roadmap)*
- Loki *(roadmap)*
- Tempo *(roadmap)*

### AI & Governance
- Spring AI *(roadmap)*
- LLM integration *(roadmap)*
- Tool Calling / MCP *(roadmap)*
- RAG para runbooks *(roadmap)*
- Policy Engine *(roadmap)*
- Human-in-the-Loop *(roadmap)*

### Cloud & DevOps
- Docker
- Kubernetes *(roadmap)*
- AWS *(roadmap)*
- GitHub Actions *(roadmap)*

---

## 🚀 MVP v0.1

O primeiro incremento implementa o núcleo do domínio de incidentes.

### Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/incidents` | Registra um incidente |
| `GET` | `/api/v1/incidents` | Lista incidentes |
| `POST` | `/api/v1/incidents/{id}/investigate` | Executa investigação inicial |
| `GET` | `/api/v1/dashboard/summary` | Resumo operacional |
| `GET` | `/actuator/health` | Health check |

### Exemplo de incidente

```json
{
  "serviceName": "payment-service",
  "description": "Latência subiu de 300ms para 2400ms e existem timeouts no PostgreSQL",
  "severity": "CRITICAL"
}
```

A investigação retorna uma estrutura semelhante a:

```json
{
  "probableCause": "Possível degradação de dependência ou saturação de recursos",
  "evidence": [
    "Incidente recebido do serviço payment-service",
    "Severidade classificada como CRITICAL"
  ],
  "recommendations": [
    "Consultar métricas do Prometheus",
    "Correlacionar logs no Loki",
    "Inspecionar traces no Tempo",
    "Verificar deploys e commits recentes"
  ],
  "humanApprovalRequired": true
}
```

---

## ▶️ Executando localmente

### Pré-requisitos

- JDK 21+
- Maven 3.9+
- IntelliJ IDEA ou IDE compatível
- Docker Desktop para PostgreSQL

### Execução rápida

```bash
mvn spring-boot:run
```

Depois:

```text
http://localhost:8080/actuator/health
```

Resposta esperada:

```json
{"status":"UP"}
```

Por padrão, o MVP pode utilizar **H2 em memória**, permitindo iniciar o projeto sem infraestrutura externa.

---

## 🧪 Testando no PowerShell

### Criar incidente

```powershell
$body = @{
  serviceName = "payment-service"
  description = "Latência subiu de 300ms para 2400ms e existem timeouts no PostgreSQL"
  severity = "CRITICAL"
} | ConvertTo-Json

Invoke-RestMethod `
  -Method Post `
  -Uri "http://localhost:8080/api/v1/incidents" `
  -ContentType "application/json" `
  -Body $body
```

### Investigar

```powershell
Invoke-RestMethod -Method Post `
  http://localhost:8080/api/v1/incidents/1/investigate
```

### Dashboard

```powershell
Invoke-RestMethod `
  http://localhost:8080/api/v1/dashboard/summary
```

---

## 🗺️ Roadmap

### ✅ v0.1 — Foundation
- domínio de incidentes;
- REST API;
- `IncidentAgent`;
- investigação determinística;
- JPA;
- Actuator;
- testes iniciais.

### 🔨 v0.2 — Production-ready API
- PostgreSQL;
- Flyway;
- DTOs;
- ProblemDetail + `@RestControllerAdvice`;
- OpenAPI / Swagger;
- testes unitários e integração;
- dashboard web inicial.

### 📊 v0.3 — Observability Intelligence
- Prometheus;
- Grafana;
- Loki;
- Tempo;
- `LogAnalyzerAgent`;
- `PerformanceAgent`;
- correlação real de telemetria.

### 🧠 v0.4 — Agentic AI
- Spring AI;
- integração com LLM;
- Tool Calling;
- MCP;
- RAG para runbooks;
- memória de incidentes.

### 🛡️ v0.5 — Governance
- Policy Engine;
- Human-in-the-Loop;
- trilha de auditoria;
- integração GitHub;
- geração controlada de Pull Requests.

### 🚀 v1.0 — Cloud-native
- Kafka;
- Redis;
- Kubernetes;
- AWS;
- `SecurityAgent`;
- `CodeAgent`;
- CI/CD;
- observabilidade completa;
- testes de resiliência.

---

## 🔐 Princípios de segurança

O SentinelOps AI segue um princípio central:

> **Nenhuma ação crítica deve depender exclusivamente da decisão de um LLM.**

A arquitetura evoluirá com:
- least privilege;
- ferramentas explicitamente autorizadas;
- validação de parâmetros;
- Policy Engine;
- aprovação humana para operações sensíveis;
- auditoria de decisões e execuções;
- separação entre **sugerir** e **executar**.

---

## 📈 Objetivo de engenharia

Este projeto não pretende ser apenas uma demonstração de chatbot.

O objetivo é explorar como **agentes de IA podem participar de operações reais de software sem abandonar fundamentos de engenharia**, como segurança, observabilidade, resiliência, testes, rastreabilidade e governança.

```text
Observe → Detect → Investigate → Correlate → Recommend → Approve → Act → Learn
```

---

## 👨‍💻 Autor

**Jucelio Farias Coelho**

Desenvolvimento Backend • Java • Spring Boot • Dados • Cloud • AI Engineering

---

## ⭐ Projeto em evolução

O SentinelOps AI será desenvolvido incrementalmente, com cada versão adicionando capacidade operacional e mantendo decisões arquiteturais documentadas.

Se o projeto for útil como referência de **Java + Observability + Agentic AI**, considere deixar uma ⭐ no repositório.

---

**SentinelOps AI — Build reliable systems. Empower people.**
