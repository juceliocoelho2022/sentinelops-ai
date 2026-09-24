package br.com.jucelio.sentinelops.observability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import br.com.jucelio.sentinelops.incident.IncidentNotFoundException;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import org.junit.jupiter.api.Test;

class IncidentEvidenceServiceTest {

    private final IncidentRepository repository = mock(IncidentRepository.class);
    private final IncidentEvidenceService service = new IncidentEvidenceService(repository);

    @Test
    void shouldExposeMetricsLogsAndTracesAsEvidenceSources() {
        when(repository.existsById(42L)).thenReturn(true);
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

    @Test
    void shouldRejectEvidenceForMissingIncident() {
        assertThatThrownBy(() -> service.collect(404L))
                .isInstanceOf(IncidentNotFoundException.class)
                .hasMessage("Incident 404 was not found");
    }
}
