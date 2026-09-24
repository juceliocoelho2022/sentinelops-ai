package br.com.jucelio.sentinelops.ai;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;

class AiInvestigationDiagnosticsServiceTest {

    @Test
    void recordsFailedInvestigationWithoutChangingTheException() {
        IncidentEvidence evidence = new IncidentEvidence(
                42L, Instant.parse("2026-09-23T00:00:00Z"), List.of());
        IllegalStateException failure = new IllegalStateException("provider unavailable");
        AiInvestigationGateway gateway = ignored -> { throw failure; };
        Logger logger = (Logger) LoggerFactory.getLogger(AiInvestigationDiagnosticsService.class);
        ListAppender<ILoggingEvent> logs = new ListAppender<>();
        logs.start();
        logger.addAppender(logs);
        try {
            AiInvestigationDiagnosticsService service = new AiInvestigationDiagnosticsService(
                    new GovernedAiInvestigationService(gateway), gateway);

            assertThatThrownBy(() -> service.investigate(evidence)).isSameAs(failure);

            assertThat(logs.list).hasSize(1);
            Object[] arguments = logs.list.get(0).getArgumentArray();
            assertThat(arguments).hasSize(4);
            assertThat(arguments[0]).isEqualTo(42L);
            assertThat(arguments[2]).isInstanceOf(Long.class);
            assertThat(arguments[3]).isEqualTo("ERROR");
        } finally {
            logger.detachAppender(logs);
            logs.stop();
        }
    }

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
