package br.com.jucelio.sentinelops.ai;

import java.util.List;

public record AiInvestigationRecommendation(
        Long incidentId,
        String mode,
        String hypothesis,
        List<String> evidenceReferences,
        List<String> recommendedNextSteps,
        boolean requiresPolicyEvaluation) {
}
