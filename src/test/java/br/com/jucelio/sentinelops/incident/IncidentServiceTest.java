package br.com.jucelio.sentinelops.incident;

import br.com.jucelio.sentinelops.agent.IncidentAgent;
import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.incident.api.IncidentRequest;
import br.com.jucelio.sentinelops.incident.api.IncidentResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IncidentServiceTest {

    @Mock IncidentRepository repository;
    @Mock IncidentAgent agent;

    private IncidentService service;

    @BeforeEach
    void setUp() {
        service = new IncidentService(repository, agent);
    }

    @Test
    void shouldCreateIncidentAsOpen() {
        IncidentRequest request = new IncidentRequest(
                "High latency", "payment-service", Severity.HIGH, "Latency increased");
        when(repository.save(any(Incident.class))).thenAnswer(invocation -> invocation.getArgument(0));

        IncidentResponse response = service.create(request);

        assertThat(response.title()).isEqualTo("High latency");
        assertThat(response.serviceName()).isEqualTo("payment-service");
        assertThat(response.severity()).isEqualTo(Severity.HIGH);
        assertThat(response.status()).isEqualTo(IncidentStatus.OPEN);
        verify(repository).save(any(Incident.class));
    }

    @Test
    void shouldMoveIncidentToInvestigatingBeforeAgentRuns() {
        Incident incident = new Incident("High latency", "Timeouts", "payment-service", Severity.CRITICAL);
        InvestigationResult result = new InvestigationResult(
                "Dependency degradation", List.of("timeout"), List.of("inspect metrics"), true);

        when(repository.findById(1L)).thenReturn(Optional.of(incident));
        when(agent.investigate(incident)).thenReturn(result);

        InvestigationResult actual = service.investigate(1L);

        assertThat(incident.getStatus()).isEqualTo(IncidentStatus.INVESTIGATING);
        assertThat(actual.humanApprovalRequired()).isTrue();
        verify(agent).investigate(incident);
    }

    @Test
    void shouldThrowWhenIncidentDoesNotExist() {
        when(repository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(IncidentNotFoundException.class, () -> service.findById(404L));
    }
}
