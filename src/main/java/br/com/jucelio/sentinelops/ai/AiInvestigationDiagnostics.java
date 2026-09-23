package br.com.jucelio.sentinelops.ai;

import java.time.Instant;

public record AiInvestigationDiagnostics(
        AiInvestigationRecommendation recommendation,
        long latencyMs,
        String gateway,
        Instant completedAt) {
}
