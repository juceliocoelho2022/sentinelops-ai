package br.com.jucelio.sentinelops.approval.api;

import br.com.jucelio.sentinelops.approval.ApprovalDecision;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ApprovalRequest(
        @NotNull ApprovalDecision decision,
        @NotBlank @Size(max = 120) String decidedBy,
        @Size(max = 500) String reason
) {
}
