package br.com.jucelio.sentinelops.security;

import java.util.List;

public record SecurityFinding(
        SecurityRiskLevel riskLevel,
        String category,
        String summary,
        List<String> evidence,
        List<String> recommendations,
        boolean humanApprovalRequired
) {
}
