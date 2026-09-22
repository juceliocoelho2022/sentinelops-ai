package br.com.jucelio.sentinelops.approval.api;

import br.com.jucelio.sentinelops.approval.ApprovalDecision;
import br.com.jucelio.sentinelops.approval.ApprovalService;
import br.com.jucelio.sentinelops.security.SecurityConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ApprovalController.class)
@Import(SecurityConfig.class)
class ApprovalControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ApprovalService service;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldReturn401WhenApprovalHasNoJwt() throws Exception {
        mockMvc.perform(post("/api/v1/incidents/10/approvals")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approvalBody()))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void shouldReturn403WhenJwtDoesNotHaveApprovalScope() throws Exception {
        mockMvc.perform(post("/api/v1/incidents/10/approvals")
                        .with(jwt().jwt(jwt -> jwt.subject("sre.viewer"))
                                .authorities(() -> "SCOPE_incident:read"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approvalBody()))
                .andExpect(status().isForbidden());
    }

    @Test
    void shouldAllowApprovalWithRequiredScopeAndUseAuthenticatedSubject() throws Exception {
        when(service.decide(eq(10L), any(ApprovalRequest.class), eq("sre.lead")))
                .thenReturn(new ApprovalResponse(
                        1L, 10L, ApprovalDecision.APPROVED,
                        "sre.lead", "Validated evidence", Instant.now()));

        mockMvc.perform(post("/api/v1/incidents/10/approvals")
                        .with(jwt().jwt(jwt -> jwt.subject("sre.lead"))
                                .authorities(() -> "SCOPE_incident:approve"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(approvalBody()))
                .andExpect(status().isCreated());

        verify(service).decide(eq(10L), any(ApprovalRequest.class), eq("sre.lead"));
    }

    private String approvalBody() throws Exception {
        return objectMapper.writeValueAsString(
                new ApprovalRequest(ApprovalDecision.APPROVED, "Validated evidence")
        );
    }
}
