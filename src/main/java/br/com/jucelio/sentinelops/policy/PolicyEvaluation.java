package br.com.jucelio.sentinelops.policy;

import java.util.List;

public record PolicyEvaluation(
        PolicyDecision decision,
        List<String> reasons
) {
}
