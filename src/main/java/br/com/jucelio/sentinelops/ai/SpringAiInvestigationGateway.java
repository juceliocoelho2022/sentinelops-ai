package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import java.util.stream.Collectors;
import org.springframework.ai.chat.client.ChatClient;

public class SpringAiInvestigationGateway implements AiInvestigationGateway {

    private static final String SYSTEM_PROMPT = """
            You are the investigation component of SentinelOps AI.
            Analyze only the supplied incident evidence.
            Do not claim that a remediation was executed.
            Do not authorize, approve, or bypass policy decisions.
            Every recommendation must require deterministic policy evaluation.
            Prefer bounded hypotheses and explicit evidence references.
            """;

    private final ChatClient chatClient;

    public SpringAiInvestigationGateway(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    @Override
    public AiInvestigationRecommendation investigate(IncidentEvidence evidence) {
        String signals = evidence.signals().stream()
                .map(signal -> "%s | %s | %s | %s".formatted(
                        signal.source(), signal.kind(), signal.reference(), signal.summary()))
                .collect(Collectors.joining("\n"));

        AiInvestigationRecommendation recommendation = chatClient.prompt()
                .system(SYSTEM_PROMPT)
                .user(user -> user.text("""
                        Investigate incident {incidentId} using only these evidence signals:
                        {signals}

                        Return a bounded hypothesis, evidence references and safe next steps.
                        requiresPolicyEvaluation must be true.
                        """)
                        .param("incidentId", evidence.incidentId())
                        .param("signals", signals))
                .call()
                .entity(AiInvestigationRecommendation.class);

        if (recommendation == null) {
            throw new IllegalStateException("AI provider returned no investigation recommendation");
        }
        return recommendation;
    }
}
