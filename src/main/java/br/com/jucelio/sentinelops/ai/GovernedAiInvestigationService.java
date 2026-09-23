package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import org.springframework.stereotype.Service;

@Service
public class GovernedAiInvestigationService {

    private final AiInvestigationGateway gateway;

    public GovernedAiInvestigationService(AiInvestigationGateway gateway) {
        this.gateway = gateway;
    }

    public AiInvestigationRecommendation investigate(IncidentEvidence evidence) {
        AiInvestigationRecommendation recommendation = gateway.investigate(evidence);

        if (!recommendation.requiresPolicyEvaluation()) {
            throw new IllegalStateException("AI investigation cannot bypass deterministic policy evaluation");
        }

        return recommendation;
    }
}
