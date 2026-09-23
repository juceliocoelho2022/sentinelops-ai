package br.com.jucelio.sentinelops.ai;

import static org.assertj.core.api.Assertions.assertThat;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class AiInvestigationDiagnosticsServiceTest {

    @Test
    void shouldCaptureGatewayAndLatencyWithoutChangingGovernance() {
        IncidentEvidence evidence = new IncidentEvidence(
                42L, Instant.parse("2026-09-23T00:00:00Z"), List.of());

        AiInvestigationGateway gateway = ignored -> new AiInvestigationRecommendation(
                42L, "TEST", "Bounded hypothesis", List.of(),
                List.of("Inspect evidence"), true);

        GovernedAiInvestigationService governedService =
                new GovernedAiInvestigationService(gateway);

        AiInvestigationDiagnostics result =
                new AiInvestigationDiagnosticsService(governedService, gateway)
                        .investigate(evidence);

        assertThat(result.recommendation().requiresPolicyEvaluation()).isTrue();
        assertThat(result.gateway()).contains("AiInvestigationDiagnosticsServiceTest");
        assertThat(result.latencyMs()).isGreaterThanOrEqualTo(0L);
        assertThat(result.completedAt()).isNotNull();
    }
}
