package br.com.jucelio.sentinelops.security;

import br.com.jucelio.sentinelops.incident.Incident;
import br.com.jucelio.sentinelops.incident.Severity;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class SecurityAgentTest {

    private final SecurityAgent agent = new SecurityAgent();

    @Test
    void shouldRequireHumanApprovalForCriticalIncident() {
        Incident incident = new Incident(
                "Suspicious access",
                "Repeated authorization failures",
                "payment-service",
                Severity.CRITICAL
        );

        SecurityFinding finding = agent.analyze(incident);

        assertThat(finding.riskLevel()).isEqualTo(SecurityRiskLevel.CRITICAL);
        assertThat(finding.humanApprovalRequired()).isTrue();
        assertThat(finding.evidence()).contains("Affected service: payment-service");
        assertThat(finding.recommendations()).isNotEmpty();
    }

    @Test
    void shouldNotRequireHumanApprovalForLowRiskReview() {
        Incident incident = new Incident(
                "Minor anomaly",
                "Low impact anomaly",
                "catalog-service",
                Severity.LOW
        );

        SecurityFinding finding = agent.analyze(incident);

        assertThat(finding.riskLevel()).isEqualTo(SecurityRiskLevel.LOW);
        assertThat(finding.humanApprovalRequired()).isFalse();
    }
}
