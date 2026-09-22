package br.com.jucelio.sentinelops.approval;

public class ApprovalNotAllowedException extends RuntimeException {
    public ApprovalNotAllowedException(Long incidentId, String reason) {
        super("Approval is not allowed for incident " + incidentId + ": " + reason);
    }
}
