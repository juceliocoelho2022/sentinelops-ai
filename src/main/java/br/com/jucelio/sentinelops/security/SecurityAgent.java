package br.com.jucelio.sentinelops.security;

import br.com.jucelio.sentinelops.incident.Incident;
import br.com.jucelio.sentinelops.incident.Severity;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SecurityAgent {

    public SecurityFinding analyze(Incident incident) {
        SecurityRiskLevel riskLevel = mapRisk(incident.getSeverity());

        return new SecurityFinding(
                riskLevel,
                "INCIDENT_SECURITY_REVIEW",
                "Deterministic security review for " + incident.getServiceName(),
                List.of(
                        "Incident severity: " + incident.getSeverity(),
                        "Affected service: " + incident.getServiceName(),
                        "Current incident status: " + incident.getStatus()
                ),
                List.of(
                        "Review authentication and authorization failures",
                        "Inspect recent privilege and configuration changes",
                        "Correlate suspicious access with application logs and traces",
                        "Require human approval before any containment action"
                ),
                riskLevel == SecurityRiskLevel.HIGH || riskLevel == SecurityRiskLevel.CRITICAL
        );
    }

    private SecurityRiskLevel mapRisk(Severity severity) {
        return switch (severity) {
            case LOW -> SecurityRiskLevel.LOW;
            case MEDIUM -> SecurityRiskLevel.MEDIUM;
            case HIGH -> SecurityRiskLevel.HIGH;
            case CRITICAL -> SecurityRiskLevel.CRITICAL;
        };
    }
}
