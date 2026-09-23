package br.com.jucelio.sentinelops.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;

class GovernedAiInvestigationServiceTest {

    private final IncidentEvidence evidence = new IncidentEvidence(
            42L,
            Instant.parse("2026-09-23T00:00:00Z"),
            List.of(new IncidentEvidence.EvidenceSignal(
                    "loki", "LOG", "traceId", "Correlated request log")));

    @Test
    void shouldKeepAiRecommendationBehindPolicyEvaluation() {
        AiInvestigationGateway gateway = ignored -> new AiInvestigationRecommendation(
                42L, "TEST", "Possible latency issue", List.of("loki:traceId"),
                List.of("Inspect trace"), true);

        AiInvestigationRecommendation result =
                new GovernedAiInvestigationService(gateway).investigate(evidence);

        assertThat(result.requiresPolicyEvaluation()).isTrue();
        assertThat(result.incidentId()).isEqualTo(42L);
    }

    @Test
    void shouldRejectGatewayThatAttemptsToBypassPolicyEvaluation() {
        AiInvestigationGateway gateway = ignored -> new AiInvestigationRecommendation(
                42L, "TEST", "Unsafe recommendation", List.of(), List.of(), false);

        GovernedAiInvestigationService service = new GovernedAiInvestigationService(gateway);

        assertThatThrownBy(() -> service.investigate(evidence))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("cannot bypass deterministic policy evaluation");
    }
}
