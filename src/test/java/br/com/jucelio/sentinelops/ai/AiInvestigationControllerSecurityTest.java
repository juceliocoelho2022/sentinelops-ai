package br.com.jucelio.sentinelops.ai;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import br.com.jucelio.sentinelops.observability.IncidentEvidenceService;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AiInvestigationController.class)
class AiInvestigationControllerSecurityTest {

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    IncidentEvidenceService evidenceService;

    @MockitoBean
    GovernedAiInvestigationService investigationService;

    @Test
    void shouldRejectUnauthenticatedInvestigation() throws Exception {
        mockMvc.perform(post("/api/v1/incidents/42/ai-investigation"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldRejectJwtWithoutInvestigationScope() throws Exception {
        mockMvc.perform(post("/api/v1/incidents/42/ai-investigation")
                        .with(jwt()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowJwtWithInvestigationScope() throws Exception {
        IncidentEvidence evidence = new IncidentEvidence(42L, Instant.now(), List.of());
        when(evidenceService.collect(42L)).thenReturn(evidence);
        when(investigationService.investigate(any())).thenReturn(
                new AiInvestigationRecommendation(
                        42L, "DETERMINISTIC_FALLBACK", "Bounded hypothesis",
                        List.of(), List.of("Inspect evidence"), true));

        mockMvc.perform(post("/api/v1/incidents/42/ai-investigation")
                        .with(jwt().authorities(() -> "SCOPE_incident:investigate")))
                .andExpect(status().isOk());
    }
}
