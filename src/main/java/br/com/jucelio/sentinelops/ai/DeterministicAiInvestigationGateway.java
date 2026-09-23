package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.util.List;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnMissingBean(AiInvestigationGateway.class)
public class DeterministicAiInvestigationGateway implements AiInvestigationGateway {

    @Override
    public AiInvestigationRecommendation investigate(IncidentEvidence evidence) {
        List<String> references = evidence.signals().stream()
                .map(signal -> signal.source() + ":" + signal.reference())
                .toList();

        return new AiInvestigationRecommendation(
                evidence.incidentId(),
                "DETERMINISTIC_FALLBACK",
                "Correlate runtime metrics, structured logs and traces before proposing remediation.",
                references,
                List.of(
                        "Inspect correlated evidence for the affected request path.",
                        "Form a bounded incident hypothesis.",
                        "Submit any remediation recommendation to deterministic policy evaluation."),
                true);
    }
}
