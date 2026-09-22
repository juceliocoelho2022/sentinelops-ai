package br.com.jucelio.sentinelops.approval;

import br.com.jucelio.sentinelops.approval.api.ApprovalRequest;
import br.com.jucelio.sentinelops.approval.api.ApprovalResponse;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import br.com.jucelio.sentinelops.policy.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ApprovalServiceTest {

    private ApprovalRecordRepository approvalRepository;
    private IncidentRepository incidentRepository;
    private PolicyEvaluationRepository policyRepository;
    private ApprovalService service;

    @BeforeEach
    void setUp() {
        approvalRepository = mock(ApprovalRecordRepository.class);
        incidentRepository = mock(IncidentRepository.class);
        policyRepository = mock(PolicyEvaluationRepository.class);
        service = new ApprovalService(approvalRepository, incidentRepository, policyRepository);
    }

    @Test
    void shouldPersistApprovalOnlyWhenPolicyRequiresApproval() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(policyRepository.findFirstByIncidentIdOrderByCreatedAtDesc(10L))
                .thenReturn(Optional.of(new PolicyEvaluationRecord(10L, PolicyDecision.REQUIRE_APPROVAL, "high risk")));
        when(approvalRepository.save(any(ApprovalRecord.class))).thenAnswer(i -> i.getArgument(0));

        ApprovalResponse response = service.decide(
                10L, new ApprovalRequest(ApprovalDecision.APPROVED, "Validated evidence"), "sre.lead");

        ArgumentCaptor<ApprovalRecord> captor = ArgumentCaptor.forClass(ApprovalRecord.class);
        verify(approvalRepository).save(captor.capture());
        assertThat(response.decision()).isEqualTo(ApprovalDecision.APPROVED);
        assertThat(captor.getValue().getDecidedBy()).isEqualTo("sre.lead");
    }

    @Test
    void shouldBlockApprovalWhenPolicyDeniesAction() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(policyRepository.findFirstByIncidentIdOrderByCreatedAtDesc(10L))
                .thenReturn(Optional.of(new PolicyEvaluationRecord(10L, PolicyDecision.DENY_ACTION, "critical risk")));

        assertThrows(ApprovalNotAllowedException.class, () ->
                service.decide(10L, new ApprovalRequest(ApprovalDecision.APPROVED, "override"), "sre.lead"));

        verify(approvalRepository, never()).save(any());
    }

    @Test
    void shouldBlockApprovalWhenPolicyAllowsRecommendation() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(policyRepository.findFirstByIncidentIdOrderByCreatedAtDesc(10L))
                .thenReturn(Optional.of(new PolicyEvaluationRecord(10L, PolicyDecision.ALLOW_RECOMMENDATION, "low risk")));

        assertThrows(ApprovalNotAllowedException.class, () ->
                service.decide(10L, new ApprovalRequest(ApprovalDecision.APPROVED, "not needed"), "sre.lead"));

        verify(approvalRepository, never()).save(any());
    }

    @Test
    void shouldBlockApprovalWithoutPolicyEvaluation() {
        when(incidentRepository.existsById(10L)).thenReturn(true);
        when(policyRepository.findFirstByIncidentIdOrderByCreatedAtDesc(10L)).thenReturn(Optional.empty());

        assertThrows(ApprovalNotAllowedException.class, () ->
                service.decide(10L, new ApprovalRequest(ApprovalDecision.APPROVED, "missing policy"), "sre.lead"));
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
