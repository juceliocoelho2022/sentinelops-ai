package br.com.jucelio.sentinelops.observability;

import java.time.Instant;
import java.util.List;

public record IncidentEvidence(
        Long incidentId,
        Instant collectedAt,
        List<EvidenceSignal> signals) {

    public record EvidenceSignal(
            String source,
            String kind,
            String reference,
            String summary) {
    }
}
