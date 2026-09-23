package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.springframework.stereotype.Service;

@Service
public class AiInvestigationDiagnosticsService {

    private final GovernedAiInvestigationService investigationService;
    private final AiInvestigationGateway gateway;

    public AiInvestigationDiagnosticsService(
            GovernedAiInvestigationService investigationService,
            AiInvestigationGateway gateway) {
        this.investigationService = investigationService;
        this.gateway = gateway;
    }

    public AiInvestigationDiagnostics investigate(IncidentEvidence evidence) {
        long startedAt = System.nanoTime();
        AiInvestigationRecommendation recommendation = investigationService.investigate(evidence);
        long latencyMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);

        return new AiInvestigationDiagnostics(
                recommendation,
                latencyMs,
                gateway.getClass().getSimpleName(),
                Instant.now());
    }
}
