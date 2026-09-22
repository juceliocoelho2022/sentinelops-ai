package br.com.jucelio.sentinelops.incident;

import br.com.jucelio.sentinelops.agent.IncidentAgent;
import br.com.jucelio.sentinelops.agent.IncidentInvestigation;
import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.incident.api.IncidentRequest;
import br.com.jucelio.sentinelops.incident.api.IncidentResponse;
import br.com.jucelio.sentinelops.security.SecurityAgent;
import br.com.jucelio.sentinelops.security.SecurityFinding;
import br.com.jucelio.sentinelops.security.SecurityRiskLevel;
import br.com.jucelio.sentinelops.policy.PolicyEngine;
import br.com.jucelio.sentinelops.policy.PolicyEvaluation;
import br.com.jucelio.sentinelops.policy.PolicyDecision;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock IncidentRepository repository;
    @Mock IncidentAgent agent;
    @Mock SecurityAgent securityAgent;
    @Mock PolicyEngine policyEngine;

    private IncidentService service;

    @BeforeEach
    void setUp() {
        service = new IncidentService(repository, agent, securityAgent, policyEngine);
    }

    @Test
    void shouldCreateIncidentAsOpen() {
        IncidentRequest request = new IncidentRequest(
                "High latency", "payment-service", Severity.HIGH, "Latency increased");
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncidentResponse response = service.create(request);

        assertThat(response.title()).isEqualTo("High latency");
        assertThat(response.serviceName()).isEqualTo("payment-service");
        assertThat(response.severity()).isEqualTo(Severity.HIGH);
        assertThat(response.status()).isEqualTo(IncidentStatus.OPEN);
        verify(repository).save(any(Incident.class));
    }

    @Test
    void shouldRunIncidentAndSecurityAnalysisAsOnePipeline() {
        Incident incident = new Incident("High latency", "Timeouts", "payment-service", Severity.CRITICAL);
        InvestigationResult incidentResult = new InvestigationResult(
                "Dependency degradation", List.of("timeout"), List.of("inspect metrics"), false);
        SecurityFinding securityFinding = new SecurityFinding(
                SecurityRiskLevel.CRITICAL,
                "INCIDENT_SECURITY_REVIEW",
                "Critical security review",
                List.of("authorization failures"),
                List.of("require human approval"),
                true);

        when(repository.findById(1L)).thenReturn(Optional.of(incident));
        when(agent.investigate(incident)).thenReturn(incidentResult);
        when(securityAgent.analyze(incident)).thenReturn(securityFinding);
        when(policyEngine.evaluate(incidentResult, securityFinding))
                .thenReturn(new PolicyEvaluation(PolicyDecision.DENY_ACTION, List.of("critical risk")));

        IncidentInvestigation actual = service.investigate(1L);

        assertThat(incident.getStatus()).isEqualTo(IncidentStatus.INVESTIGATING);
        assertThat(actual.incidentAnalysis()).isEqualTo(incidentResult);
        assertThat(actual.securityAnalysis()).isEqualTo(securityFinding);
        assertThat(actual.policyEvaluation().decision()).isEqualTo(PolicyDecision.DENY_ACTION);
        verify(agent).investigate(incident);
        verify(securityAgent).analyze(incident);
        verify(policyEngine).evaluate(incidentResult, securityFinding);
    }

    @Test
    void shouldThrowWhenIncidentDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.findById(404L));
    }
}
