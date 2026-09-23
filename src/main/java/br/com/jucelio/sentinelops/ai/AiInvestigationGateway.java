package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;

public interface AiInvestigationGateway {
    AiInvestigationRecommendation investigate(IncidentEvidence evidence);
}
