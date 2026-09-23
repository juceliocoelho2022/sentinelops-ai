package br.com.jucelio.sentinelops.observability;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IncidentEvidenceServiceTest {

    private final IncidentEvidenceService service = new IncidentEvidenceService();

    @Test
    void shouldExposeMetricsLogsAndTracesAsEvidenceSources() {
        IncidentEvidence evidence = service.collect(42L);

        assertThat(evidence.incidentId()).isEqualTo(42L);
        assertThat(evidence.collectedAt()).isNotNull();
        assertThat(evidence.signals())
                .extracting(IncidentEvidence.EvidenceSignal::source)
                .containsExactly("prometheus", "loki", "tempo");
        assertThat(evidence.signals())
                .extracting(IncidentEvidence.EvidenceSignal::kind)
                .containsExactly("METRIC", "LOG", "TRACE");
    }
}
