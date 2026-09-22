package br.com.jucelio.sentinelops.approval.api;

import br.com.jucelio.sentinelops.approval.ApprovalDecision;
import br.com.jucelio.sentinelops.approval.ApprovalRecord;

import java.time.Instant;

public record ApprovalResponse(
        Long id,
        Long incidentId,
        ApprovalDecision decision,
        String decidedBy,
        String reason,
        Instant decidedAt
) {
    public static ApprovalResponse from(ApprovalRecord record) {
        return new ApprovalResponse(
                record.getId(),
                record.getIncidentId(),
                record.getDecision(),
                record.getDecidedBy(),
                record.getReason(),
                record.getDecidedAt()
        );
    }
}
