package br.com.jucelio.sentinelops.observability;

import br.com.jucelio.sentinelops.incident.IncidentNotFoundException;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class IncidentEvidenceService {

    private final IncidentRepository incidentRepository;

    public IncidentEvidenceService(IncidentRepository incidentRepository) {
        this.incidentRepository = incidentRepository;
    }

    public IncidentEvidence collect(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new IncidentNotFoundException(incidentId);
        }

        List<IncidentEvidence.EvidenceSignal> signals = new ArrayList<>();

        signals.add(new IncidentEvidence.EvidenceSignal(
                "prometheus",
                "METRIC",
                "http_server_requests_seconds",
                "Runtime HTTP metrics are available for incident diagnosis."));

        signals.add(new IncidentEvidence.EvidenceSignal(
                "loki",
                "LOG",
                "traceId",
                "Structured logs can be correlated by trace identifier."));

        signals.add(new IncidentEvidence.EvidenceSignal(
                "tempo",
                "TRACE",
                "traceId",
                "Request traces are available for latency and execution-path analysis."));

        return new IncidentEvidence(incidentId, Instant.now(), List.copyOf(signals));
    }
}
