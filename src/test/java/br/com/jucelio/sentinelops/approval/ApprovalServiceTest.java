package br.com.jucelio.sentinelops.approval;

import br.com.jucelio.sentinelops.approval.api.ApprovalRequest;
import br.com.jucelio.sentinelops.approval.api.ApprovalResponse;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

    private ApprovalRecordRepository approvalRepository;
    private IncidentRepository incidentRepository;
    private ApprovalService service;

    @BeforeEach
    void setUp() {
        approvalRepository = mock(ApprovalRecordRepository.class);
        incidentRepository = mock(IncidentRepository.class);
        service = new ApprovalService(approvalRepository, incidentRepository);
    }

    @Test
    void shouldPersistHumanApprovalWithActorAndReason() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(approvalRepository.save(any(ApprovalRecord.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        ApprovalResponse response = service.decide(
                10L,
                new ApprovalRequest(ApprovalDecision.APPROVED, "sre.lead", "Validated evidence")
        );

        ArgumentCaptor<ApprovalRecord> captor = ArgumentCaptor.forClass(ApprovalRecord.class);
        verify(approvalRepository).save(captor.capture());

        assertThat(response.incidentId()).isEqualTo(10L);
        assertThat(response.decision()).isEqualTo(ApprovalDecision.APPROVED);
        assertThat(captor.getValue().getDecidedBy()).isEqualTo("sre.lead");
        assertThat(captor.getValue().getDecidedAt()).isNotNull();
    }

    @Test
    void shouldReturnApprovalHistoryForIncident() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(approvalRepository.findByIncidentIdOrderByDecidedAtDesc(10L))
                .thenReturn(List.of(new ApprovalRecord(
                        10L, ApprovalDecision.REJECTED, "security.lead", "Insufficient evidence"
                )));

        List<ApprovalResponse> history = service.history(10L);

        assertThat(history).hasSize(1);
        assertThat(history.getFirst().decision()).isEqualTo(ApprovalDecision.REJECTED);
    }
}
