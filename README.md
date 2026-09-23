# 🛡️ SentinelOps AI

> **Governed Incident Intelligence for Java Systems**

![Java](https://img.shields.io/badge/Java-21-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5.5-6DB33F?logo=springboot&logoColor=white)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-17-4169E1?logo=postgresql&logoColor=white)
![Docker](https://img.shields.io/badge/Docker-CI%20validated-2496ED?logo=docker&logoColor=white)
![Flyway](https://img.shields.io/badge/Flyway-migrations-red)
![Status](https://img.shields.io/badge/status-v0.5%20in%20progress-blue)
[![CI](https://github.com/juceliocoelho2022/sentinelops-ai/actions/workflows/ci.yml/badge.svg)](https://github.com/juceliocoelho2022/sentinelops-ai/actions/workflows/ci.yml)

**SentinelOps AI** é um projeto de engenharia backend voltado à investigação governada de incidentes em sistemas Java. A v0.5 está em desenvolvimento sobre a fundação de governança e observabilidade das versões anteriores: o projeto já possui um gateway de investigação por IA, integração opt-in com Spring AI/OpenAI, endpoint protegido por JWT e fallback determinístico. A integração real com a API da OpenAI foi alcançada em runtime; o teste externo ficou bloqueado por `HTTP 429 / credit_balance_exhausted`, sem alteração do código para mascarar a limitação do provider.

## 💼 O que este projeto demonstra

Para avaliação técnica e recrutamento, este repositório demonstra competências aplicadas em:

- **Java Backend:** Java 21, Spring Boot, REST APIs, DTOs, Bean Validation e tratamento padronizado de erros.
- **Arquitetura e design:** separação de responsabilidades, pipeline de investigação e componentes especializados.
- **Dados:** PostgreSQL 17, Spring Data JPA/Hibernate, migrations com Flyway e integridade referencial.
- **Testes:** JUnit 5, Mockito, MockMvc, Testcontainers com PostgreSQL real e JaCoCo.
- **DevOps:** Docker, Docker Compose e GitHub Actions com build, testes e validação da imagem.
- **Governança de IA:** Policy Engine determinístico, Policy Enforcement persistido, Human-in-the-Loop e Audit Trail.
- **Segurança:** Spring Security, OAuth2 Resource Server/JWT, autorização por scope e identidade de auditoria derivada do principal autenticado.
- **Engenharia segura:** separação explícita entre recomendação, autorização/aprovação e futura execução privilegiada.

## 🧭 Princípios de engenharia: autonomia e decisão técnica

Este projeto também funciona como laboratório de evolução profissional em **Java Backend**. O objetivo não é acumular frameworks no repositório, mas demonstrar capacidade de receber um problema, analisar alternativas, tomar decisões técnicas, implementar, testar e preparar a solução para operação.

Cada evolução relevante do SentinelOps AI procura responder quatro perguntas:

1. **Qual problema estamos resolvendo?**
2. **Por que esta solução foi escolhida?**
3. **Quais trade-offs e limites ela introduz?**
4. **Como validamos que a decisão funciona?**

Exemplos já aplicados:

| Problema | Decisão de engenharia | Trade-off / limite | Validação |
|---|---|---|---|
| Identidade de quem aprova | JWT + principal autenticado | exige um Identity Provider confiável em runtime | testes de autenticação/autorização |
| Aprovação indevida | scope `incident:approve` | autorização por endpoint ainda pode evoluir para políticas mais granulares | cenários 401, 403 e autorizado |
| Humano tentando contornar política | Policy Evaluation persistida + enforcement | somente `REQUIRE_APPROVAL` aceita decisão humana | testes de `ALLOW_RECOMMENDATION`, `REQUIRE_APPROVAL` e `DENY_ACTION` |
| Diferença entre teste e PostgreSQL real | Testcontainers | maior custo de execução do pipeline | `mvn verify` com PostgreSQL em container |
| Mudanças implícitas no banco | Flyway + `ddl-auto=validate` | migrations precisam ser mantidas explicitamente | startup e testes de integração |
| Artefato Java funcionar mas container falhar | Docker build no CI | pipeline leva mais tempo | imagem construída a cada CI |

Essa abordagem direciona o roadmap: **mensageria, observabilidade, resiliência e cloud só entram quando houver um problema técnico claro que justifique a tecnologia.**

### Cenário de negócio

Imagine uma API Java de pagamentos apresentando degradação. O SentinelOps recebe o incidente, executa análises especializadas, classifica o risco, aplica uma política determinística e registra uma eventual decisão humana. O objetivo é reduzir o tempo de investigação sem entregar controle irrestrito da infraestrutura a um agente de IA.

### Fluxo demonstrável

```text
Create Incident
      ↓
IncidentAgent
      ↓
SecurityAgent
      ↓
PolicyEngine
      ↓
ALLOW_RECOMMENDATION | REQUIRE_APPROVAL | DENY_ACTION
                           ↓
                    Human Approval
                           ↓
                      Audit Trail
```

> A v0.5 adiciona investigação assistida por LLM de forma opt-in. A IA produz recomendações; autorização, política e futura execução privilegiada permanecem fora do controle do modelo.

> **RECOMMEND != EXECUTE** — análise, política e aprovação são etapas distintas. Aprovação humana não executa automaticamente infraestrutura, banco, cloud ou Kubernetes.

## ✅ Estado atual — v0.5 em desenvolvimento

Implementado:

- Java 21 + Spring Boot 3.5.5
- REST API, DTOs, Bean Validation e ProblemDetail
- Spring Data JPA / Hibernate + PostgreSQL 17
- Flyway V1 para incidentes, V2 para trilha de aprovação e V3 para avaliações de política
- Actuator + OpenAPI / Swagger
- `IncidentAgent` determinístico
- `SecurityAgent` determinístico
- `PolicyEngine` determinístico
- decisões `ALLOW_RECOMMENDATION`, `REQUIRE_APPROVAL` e `DENY_ACTION`
- Spring Security + OAuth2 Resource Server/JWT
- autorização `incident:approve` para decisões humanas
- Human Approval com `APPROVED` / `REJECTED` e ator derivado do JWT
- Policy Enforcement: somente `REQUIRE_APPROVAL` aceita decisão humana; `DENY_ACTION` não pode ser sobrescrito pelo endpoint normal
- Audit Trail persistente com ator, justificativa e timestamp
- JUnit 5, Mockito, MockMvc, Testcontainers e JaCoCo
- GitHub Actions com `mvn verify`
- build da imagem Docker validado pelo CI
- execução da aplicação como usuário não-root no container
- Micrometer + Prometheus para métricas de runtime
- Grafana provisionado com dashboard de diagnóstico
- logs JSON estruturados enviados por Promtail ao Loki
- Micrometer Tracing + OpenTelemetry OTLP + Tempo
- correlação de logs e traces por `traceId` / `spanId`
- `IncidentEvidence` como contrato backend-neutral para investigação automatizada
- `AiInvestigationGateway` como abstração de provider
- `DeterministicAiInvestigationGateway` como fallback local e testável
- `SpringAiInvestigationGateway` com `ChatClient` para investigação assistida por LLM
- integração opt-in com OpenAI via Spring AI 1.1.x, compatível com Spring Boot 3.5.x
- endpoint `POST /api/v1/incidents/{id}/ai-investigation` protegido por `SCOPE_incident:investigate`
- modo JWT local opt-in para testes, sem enfraquecer a configuração padrão baseada em JWK
- `GovernedAiInvestigationService` rejeita recomendações que não exijam avaliação determinística de política
- diagnóstico de investigação com latência, gateway selecionado e timestamp
- chamada real ao provider OpenAI comprovada em runtime; validação final da resposta está pendente por indisponibilidade de créditos da API

Ainda estão no roadmap: adapters que consultem Prometheus/Loki/Tempo em tempo real, integração efetiva da recomendação de IA com o `PolicyEngine`, RAG para runbooks, Tool Calling/MCP, memória de incidentes, Kafka, execução controlada, Kubernetes e AWS.

## 🏗️ Arquitetura atual

```text
Incident
   │
   ▼
IncidentService
   │
   ├── IncidentAgent
   │      └── causa + evidências + recomendações
   │
   └── SecurityAgent
          └── risco + evidências + recomendações
                   │
                   ▼
              PolicyEngine
                   │
       ┌───────────┼────────────┐
       ▼           ▼            ▼
ALLOW_          REQUIRE_      DENY_
RECOMMENDATION  APPROVAL      ACTION
                   │
                   ▼
             Human Approval
                   │
             APPROVED/REJECTED
                   │
                   ▼
               Audit Trail
```

Nenhuma etapa acima executa ações privilegiadas.

## 🎯 Arquitetura alvo

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
   ┌──────────┼──────────────┐
   ▼          ▼              ▼
Incident   LogAnalyzer   Performance
Agent      Agent         Agent
   │          │              │
   └──────────┼──────────────┘
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
        Audit Trail
              │
     Controlled Actions
```

## 🤖 Componentes de inteligência

| Componente | Estado | Papel |
|---|---|---|
| **IncidentAgent** | Implementado | Investigação determinística inicial |
| **SecurityAgent** | Implementado | Avaliação determinística de risco do incidente |
| **PolicyEngine** | Implementado | Governa o resultado antes de qualquer futura ação |
| **Human Approval** | Implementado na v0.3 | Registra aprovação/rejeição humana |
| **LogAnalyzerAgent** | Roadmap | Logs, exceções e padrões |
| **PerformanceAgent** | Roadmap | Latência, CPU, memória, JVM e banco |
| **CodeAgent** | Roadmap | Commits/PRs e propostas de correção |
| **Spring AI / LLM** | Em desenvolvimento | Gateway opt-in para investigação assistida sobre evidências; chamada ao provider validada até a API externa |

## ⚙️ Stack

**Backend:** Java 21, Spring Boot 3.5.5, Spring Web, Spring Data JPA, Bean Validation e Actuator.

**Dados:** PostgreSQL 17 + Flyway.

**Qualidade:** JUnit 5, Mockito, MockMvc, Testcontainers, JaCoCo e GitHub Actions.

**API:** OpenAPI / Swagger via springdoc.

**Infra local:** Docker + Docker Compose. O CI também executa `docker build` para detectar incompatibilidades entre o artefato Maven e a imagem.

## 🚀 Executando localmente

Pré-requisitos: JDK 21+, Maven e Docker Desktop.

```bash
docker compose up -d
mvn clean verify
mvn spring-boot:run
```

PostgreSQL do projeto:

```text
localhost:5433
jdbc:postgresql://localhost:5433/sentinelops
```

Health check:

```text
GET http://localhost:8080/actuator/health
```

Swagger UI:

```text
http://localhost:8080/swagger-ui.html
```

## 🔌 Endpoints

| Método | Endpoint | Descrição |
|---|---|---|
| `POST` | `/api/v1/incidents` | Cria incidente |
| `GET` | `/api/v1/incidents` | Lista incidentes |
| `GET` | `/api/v1/incidents/{id}` | Consulta incidente |
| `PATCH` | `/api/v1/incidents/{id}/status` | Altera status |
| `POST` | `/api/v1/incidents/{id}/investigate` | Executa pipeline IncidentAgent → SecurityAgent → PolicyEngine |
| `POST` | `/api/v1/incidents/{id}/approvals` | Registra decisão humana |
| `GET` | `/api/v1/incidents/{id}/approvals` | Consulta histórico auditável |
| `GET` | `/api/v1/dashboard/summary` | Resumo operacional |
| `POST` | `/api/v1/incidents/{id}/ai-investigation` | Executa investigação assistida por IA; exige `incident:investigate` |
| `GET` | `/actuator/health` | Health check |

## 🔎 Investigação governada

A investigação retorna três blocos conceituais:

```text
incidentAnalysis
securityAnalysis
policyEvaluation
```

O `PolicyEngine` aplica regras determinísticas:

- `ALLOW_RECOMMENDATION`: análise de baixo risco pode ser apresentada como recomendação.
- `REQUIRE_APPROVAL`: a continuidade exige decisão humana explícita.
- `DENY_ACTION`: risco crítico bloqueia remediação autônoma.

Exemplo de decisão humana:

```json
{
  "decision": "APPROVED",
  "reason": "Evidence validated before remediation"
}
```

O ator (`decidedBy`) é obtido do principal autenticado no JWT, e não do payload enviado pelo cliente.

A aprovação é registrada para auditoria, mas **não dispara execução automática**.

## 🗃️ Banco e migrações

Flyway mantém o schema versionado:

```text
V1__create_incidents.sql
V2__create_approval_records.sql
V3__create_policy_evaluations.sql
```

A tabela `approval_records` mantém vínculo por foreign key com `incidents` e registra decisão, ator, justificativa e instante da decisão. `policy_evaluations` persiste a decisão determinística usada para aplicar a regra de aprovação.

O Hibernate utiliza `ddl-auto=validate`, mantendo alterações de schema sob responsabilidade das migrations.

Configurações podem ser sobrescritas por `DB_URL`, `DB_USER` e `DB_PASSWORD`.

## 🎬 Demo rápida

Com a aplicação em execução, um avaliador pode percorrer o fluxo principal pela API/Swagger:

```text
1. POST /api/v1/incidents
2. POST /api/v1/incidents/{id}/investigate
3. Inspecionar incidentAnalysis + securityAnalysis + policyEvaluation
4. POST /api/v1/incidents/{id}/approvals
5. GET  /api/v1/incidents/{id}/approvals
6. GET  /api/v1/dashboard/summary
```

Esse percurso evidencia a separação entre investigação, decisão de política e auditoria humana.

## 🤖 Investigação assistida por IA — v0.5

A v0.5 introduz IA sem transferir autoridade operacional ao modelo:

```text
IncidentEvidence
      ↓
AiInvestigationGateway
      ├── Deterministic fallback
      └── Spring AI / OpenAI (opt-in)
                    ↓
        AiInvestigationRecommendation
                    ↓
      requiresPolicyEvaluation = true
                    ↓
              Policy Engine
                    ↓
        Human Approval / Audit
```

**Limite atual importante:** `requiresPolicyEvaluation=true` é um guardrail aplicado pelo serviço, mas a recomendação de IA ainda não dispara diretamente o `PolicyEngine`. Essa integração permanece como próximo incremento para preservar a separação entre recomendação probabilística e decisão determinística.

### Validação real do provider

O caminho Spring AI → OpenAI foi exercitado em runtime. A API externa respondeu com `HTTP 429`, `insufficient_quota` / `credit_balance_exhausted`. Isso confirma que autenticação local, endpoint protegido, seleção do gateway Spring AI e chamada ao provider foram alcançados; não confirma ainda uma resposta LLM bem-sucedida. O projeto mantém o fallback determinístico para desenvolvimento e testes sem dependência de API paga.

### Próximo hardening identificado em runtime

Durante o teste do provider, o `RequestDiagnosticsFilter` registrou `status=200` antes de a exceção downstream resultar em erro para o cliente. Esse comportamento foi registrado como dívida técnica da v0.5 e será corrigido junto com tratamento explícito de indisponibilidade/quota do provider, métricas de IA e proveniência do gateway.

## 🧠 Decisões de engenharia

**Policy Engine determinístico:** decisões críticas de governança não ficam exclusivamente sob responsabilidade de um modelo probabilístico.

**Human-in-the-Loop:** a aprovação é persistida como evento auditável e não representa execução automática.

**Flyway + `ddl-auto=validate`:** evolução de schema é explícita, reproduzível e validada pela aplicação.

**Testcontainers:** testes de integração usam PostgreSQL real em container, reduzindo diferenças entre teste e runtime.

**Docker validado no CI:** o pipeline verifica não apenas o código Java, mas também se o artefato produzido gera uma imagem executável.

**Roadmap honesto:** componentes ainda não implementados permanecem identificados como roadmap.

## 🧪 Qualidade e CI

O pipeline executa:

```text
Checkout
   ↓
Java 21
   ↓
mvn verify
   ↓
Unit / MockMvc / Security Authorization / Integration Tests
   ↓
Testcontainers + PostgreSQL
   ↓
JaCoCo
   ↓
docker build
```

Isso valida tanto o artefato Java quanto a capacidade de gerar a imagem de container antes do merge.

## 🗺️ Roadmap

**v0.2.1 — Quality & CI:** persistência PostgreSQL, Flyway, validação, ProblemDetail, testes, Testcontainers e CI.

**v0.3 — Governance Foundation:** SecurityAgent, pipeline de investigação, Policy Engine, Human Approval, Audit Trail e validação da imagem Docker no CI.

**v0.3.2 — Security & Governance Hardening:** JWT, autorização por scope, identidade autenticada no Audit Trail, persistência da PolicyEvaluation, enforcement de `REQUIRE_APPROVAL` e bloqueio de `DENY_ACTION`.

**v0.4 — Observability Intelligence — concluída:** métricas com Prometheus, dashboard Grafana, logs JSON com Loki/Promtail, tracing OpenTelemetry/Tempo, correlação por `traceId`/`spanId` e contrato `IncidentEvidence`. O contrato ainda não consulta os backends em tempo real; adapters de coleta permanecem como evolução incremental.

**v0.5 — Agentic AI — em desenvolvimento:** abstração `AiInvestigationGateway`, fallback determinístico, Spring AI `ChatClient`, provider OpenAI opt-in, endpoint governado protegido por JWT e diagnósticos de latência/gateway já implementados. Próximos incrementos: resiliência do provider, correção da telemetria em exceções, integração efetiva com `PolicyEngine`, RAG para runbooks, Tool Calling/MCP e memória de incidentes.

**v0.6 — Controlled Remediation:** catálogo de ações permitidas, autorização, idempotência, dry-run, execução controlada e auditoria completa.

**v1.0 — Cloud-native:** Kafka, Redis, Kubernetes, AWS, CodeAgent, CI/CD avançado e testes de resiliência.

## 🔐 Segurança e governança

O projeto adota uma separação explícita entre:

```text
ANALYZE → RECOMMEND → POLICY → APPROVE → AUDIT → EXECUTE
```

Na v0.3.2, o fluxo termina em **AUDIT**. A aprovação exige JWT com `incident:approve`, e o backend também valida a decisão persistida do Policy Engine antes de aceitar a decisão humana.

A futura etapa `EXECUTE` deverá usar least privilege, ferramentas explicitamente autorizadas, validação de parâmetros, autorização independente do LLM, idempotência, limites operacionais e trilha completa de auditoria.

Credenciais reais não devem ser commitadas. Ambientes externos devem usar secrets e variáveis de ambiente.

## 📈 Objetivo de engenharia

O SentinelOps AI explora como agentes e modelos de IA podem participar de operações de software sem substituir controles determinísticos de engenharia:

```text
Observe → Detect → Investigate → Correlate → Recommend → Govern → Approve → Act → Learn
```

O objetivo não é criar um agente com acesso irrestrito à infraestrutura, mas uma plataforma de investigação e automação governada.

## 👨‍💻 Autor

**Jucelio Farias Coelho**

Java Backend • Spring Boot • Dados • Cloud • AI Engineering

---

**SentinelOps AI — Build reliable systems. Empower people.**


## 🔭 Observability Intelligence — v0.4

```text
HTTP Request
   ├── Metrics ──→ Prometheus ─┐
   ├── JSON Logs → Loki ────────┼──→ Grafana
   │                │           │
   │             traceId        │
   └── OTel ─────→ Tempo ───────┘
                         │
                         ▼
                  IncidentEvidence
```

**Decisões e trade-offs:** sampling de traces em 100% é deliberado no ambiente local para demonstrabilidade e deve ser reduzido/ajustado em produção; Loki/Tempo usam configuração local de demonstração; `traceId` permite correlação operacional, mas sua alta cardinalidade exige estratégia diferente em escala; `IncidentEvidence` cria um contrato estável sem fingir que os adapters de consulta aos backends já existem.

### Runtime diagnostics dashboard (v0.4)

The observability stack includes a provisioned Grafana dashboard backed by Prometheus. It is intentionally diagnostic: availability, HTTP request volume and 5xx errors, JVM heap/CPU/threads, and HikariCP connection pressure.

Local validation: run `docker compose up -d` and the Spring Boot application on port 8080. Prometheus scrapes `/actuator/prometheus`; Grafana is exposed on port 3000.

> Security note: local Grafana credentials are development-only. Production must use secret management and network-level protection for management and metrics endpoints.
