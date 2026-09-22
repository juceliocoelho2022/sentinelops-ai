package br.com.jucelio.sentinelops.approval;

import br.com.jucelio.sentinelops.approval.api.ApprovalRequest;
import br.com.jucelio.sentinelops.approval.api.ApprovalResponse;
import br.com.jucelio.sentinelops.incident.IncidentNotFoundException;
import br.com.jucelio.sentinelops.incident.IncidentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ApprovalService {

    private final ApprovalRecordRepository approvalRepository;
    private final IncidentRepository incidentRepository;

    public ApprovalService(ApprovalRecordRepository approvalRepository, IncidentRepository incidentRepository) {
        this.approvalRepository = approvalRepository;
        this.incidentRepository = incidentRepository;
    }

    @Transactional
    public ApprovalResponse decide(Long incidentId, ApprovalRequest request, String authenticatedActor) {
        if (!incidentRepository.existsById(incidentId)) {
            throw new IncidentNotFoundException(incidentId);
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
