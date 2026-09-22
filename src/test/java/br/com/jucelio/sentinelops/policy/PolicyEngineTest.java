package br.com.jucelio.sentinelops.policy;

import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.security.SecurityFinding;
import br.com.jucelio.sentinelops.security.SecurityRiskLevel;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class PolicyEngineTest {

    private final PolicyEngine policyEngine = new PolicyEngine();

    @Test
    void shouldDenyAutonomousActionForCriticalSecurityRisk() {
        PolicyEvaluation evaluation = policyEngine.evaluate(
                incident(false),
                security(SecurityRiskLevel.CRITICAL, true)
        );

        assertThat(evaluation.decision()).isEqualTo(PolicyDecision.DENY_ACTION);
        assertThat(evaluation.reasons()).isNotEmpty();
    }

    @Test
    void shouldRequireApprovalForHighRisk() {
        PolicyEvaluation evaluation = policyEngine.evaluate(
                incident(false),
                security(SecurityRiskLevel.HIGH, true)
        );

        assertThat(evaluation.decision()).isEqualTo(PolicyDecision.REQUIRE_APPROVAL);
    }

    @Test
    void shouldAllowRecommendationForLowRiskWithoutApprovalRequirement() {
        PolicyEvaluation evaluation = policyEngine.evaluate(
                incident(false),
                security(SecurityRiskLevel.LOW, false)
        );

        assertThat(evaluation.decision()).isEqualTo(PolicyDecision.ALLOW_RECOMMENDATION);
    }

    private InvestigationResult incident(boolean approval) {
        return new InvestigationResult(
                "Dependency degradation",
                List.of("timeout"),
                List.of("inspect metrics"),
                approval
        );
    }

    private SecurityFinding security(SecurityRiskLevel risk, boolean approval) {
        return new SecurityFinding(
                risk,
                "INCIDENT_SECURITY_REVIEW",
                "Security review",
                List.of("security evidence"),
                List.of("review before action"),
                approval
        );
    }
}
