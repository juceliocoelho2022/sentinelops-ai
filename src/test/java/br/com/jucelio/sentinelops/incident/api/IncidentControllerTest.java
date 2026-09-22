package br.com.jucelio.sentinelops.incident.api;

import br.com.jucelio.sentinelops.agent.IncidentInvestigation;
import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.incident.IncidentService;
import br.com.jucelio.sentinelops.incident.IncidentStatus;
import br.com.jucelio.sentinelops.incident.Severity;
import br.com.jucelio.sentinelops.security.SecurityFinding;
import br.com.jucelio.sentinelops.security.SecurityRiskLevel;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class IncidentControllerTest {

    private IncidentService service;
    private MockMvc mockMvc;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setUp() {
        service = mock(IncidentService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new IncidentController(service)).build();
    }

    @Test
    void shouldCreateIncidentAndReturn201() throws Exception {
        IncidentRequest request = new IncidentRequest(
                "High latency", "payment-service", Severity.HIGH, "Latency increased");
        IncidentResponse response = new IncidentResponse(
                1L, "High latency", "Latency increased", "payment-service",
                Severity.HIGH, IncidentStatus.OPEN, Instant.now(), Instant.now());

        when(service.create(request)).thenReturn(response);

        mockMvc.perform(post("/api/v1/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/v1/incidents/1"))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.status").value("OPEN"));
    }

    @Test
    void shouldReturnCombinedInvestigationPipeline() throws Exception {
        InvestigationResult incidentAnalysis = new InvestigationResult(
                "Dependency degradation", List.of("timeout"), List.of("inspect metrics"), false);
        SecurityFinding securityAnalysis = new SecurityFinding(
                SecurityRiskLevel.CRITICAL,
                "INCIDENT_SECURITY_REVIEW",
                "Critical security review",
                List.of("authorization failures"),
                List.of("require human approval"),
                true);
        IncidentInvestigation result = new IncidentInvestigation(
                incidentAnalysis, securityAnalysis, true);

        when(service.investigate(1L)).thenReturn(result);

        mockMvc.perform(post("/api/v1/incidents/1/investigate"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.incidentAnalysis.probableCause").value("Dependency degradation"))
                .andExpect(jsonPath("$.securityAnalysis.riskLevel").value("CRITICAL"))
                .andExpect(jsonPath("$.humanApprovalRequired").value(true));
    }
}
