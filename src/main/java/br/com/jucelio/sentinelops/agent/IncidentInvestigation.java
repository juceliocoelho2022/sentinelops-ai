package br.com.jucelio.sentinelops.agent;

import br.com.jucelio.sentinelops.security.SecurityFinding;

public record IncidentInvestigation(
        InvestigationResult incidentAnalysis,
        SecurityFinding securityAnalysis,
        boolean humanApprovalRequired
) {
}
