package br.com.jucelio.sentinelops.approval;

import br.com.jucelio.sentinelops.approval.api.ApprovalRequest;
import br.com.jucelio.sentinelops.approval.api.ApprovalResponse;
import br.com.jucelio.sentinelops.incident.IncidentNotFoundException;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import br.com.jucelio.sentinelops.policy.PolicyDecision;
import br.com.jucelio.sentinelops.policy.PolicyEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRecordRepository approvalRepository;
    private final IncidentRepository incidentRepository;
    private final PolicyEvaluationRepository policyEvaluationRepository;

    public ApprovalService(ApprovalRecordRepository approvalRepository, IncidentRepository incidentRepository,
                           PolicyEvaluationRepository policyEvaluationRepository) {
        this.approvalRepository = approvalRepository;
        this.incidentRepository = incidentRepository;
        this.policyEvaluationRepository = policyEvaluationRepository;
    }

    @Transactional
    public ApprovalResponse decide(Long incidentId, ApprovalRequest request, String authenticatedActor) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new IncidentNotFoundException(incidentId);
        }

        var policy = policyEvaluationRepository.findFirstByIncidentIdOrderByCreatedAtDesc(incidentId)
                .orElseThrow(() -> new ApprovalNotAllowedException(incidentId, "no policy evaluation exists"));

        if (policy.getDecision() != PolicyDecision.REQUIRE_APPROVAL) {
            throw new ApprovalNotAllowedException(
                    incidentId, "policy decision is " + policy.getDecision());
        }

        ApprovalRecord record = new ApprovalRecord(
                incidentId,
                request.decision(),
                authenticatedActor,
                request.reason()
        );

        return ApprovalResponse.from(approvalRepository.save(record));
    }

    @Transactional(readOnly = true)
    public List<ApprovalResponse> history(Long incidentId) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new IncidentNotFoundException(incidentId);
        }

        return approvalRepository.findByIncidentIdOrderByDecidedAtDesc(incidentId)
                .stream()
                .map(ApprovalResponse::from)
                .toList();
    }
}
