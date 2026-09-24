package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.time.Instant;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class AiInvestigationDiagnosticsService {

    private static final Logger log = LoggerFactory.getLogger(AiInvestigationDiagnosticsService.class);

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
        String gatewayName = gateway.getClass().getSimpleName();
        String outcome = "ERROR";
        try {
            AiInvestigationRecommendation recommendation = investigationService.investigate(evidence);
            outcome = "SUCCESS";
            return new AiInvestigationDiagnostics(
                    recommendation,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt),
                    gatewayName,
                    Instant.now());
        } finally {
            log.info("AI investigation completed incidentId={} gateway={} latencyMs={} outcome={}",
                    evidence.incidentId(), gatewayName,
                    TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt), outcome);
        }
    }
}
