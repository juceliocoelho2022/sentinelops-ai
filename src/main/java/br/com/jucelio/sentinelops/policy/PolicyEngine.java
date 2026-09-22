package br.com.jucelio.sentinelops.policy;

import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.security.SecurityFinding;
import br.com.jucelio.sentinelops.security.SecurityRiskLevel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PolicyEngine {

    public PolicyEvaluation evaluate(
            InvestigationResult incidentAnalysis,
            SecurityFinding securityAnalysis
    ) {
        List<String> reasons = new ArrayList<>();

        if (securityAnalysis.riskLevel() == SecurityRiskLevel.CRITICAL) {
            reasons.add("Critical security risk cannot trigger autonomous remediation");
            reasons.add("Human approval is mandatory before any privileged action");
            return new PolicyEvaluation(PolicyDecision.DENY_ACTION, List.copyOf(reasons));
        }

        if (incidentAnalysis.humanApprovalRequired()
                || securityAnalysis.humanApprovalRequired()
                || securityAnalysis.riskLevel() == SecurityRiskLevel.HIGH) {
            reasons.add("Investigation requires explicit human approval");
            return new PolicyEvaluation(PolicyDecision.REQUIRE_APPROVAL, List.copyOf(reasons));
        }

        reasons.add("Analysis may be presented as a recommendation only");
        return new PolicyEvaluation(PolicyDecision.ALLOW_RECOMMENDATION, List.copyOf(reasons));
    }
}
