package br.com.jucelio.sentinelops.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ActuatorSecurityTest.SecurityProbeController.class)
@Import(SecurityConfig.class)
class ActuatorSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void shouldAllowPrometheusScrapeWithoutJwt() throws Exception {
        mockMvc.perform(get("/actuator/prometheus"))
                .andExpect(status().isOk())
                .andExpect(content().string("prometheus-probe"));
    }

    @Test
    void shouldRequireAuthenticationForOtherActuatorPaths() throws Exception {
        mockMvc.perform(get("/actuator/metrics"))
                .andExpect(status().isUnauthorized());
    }

    @RestController
    static class SecurityProbeController {

        @GetMapping("/actuator/prometheus")
        String prometheus() {
            return "prometheus-probe";
        }

        @GetMapping("/actuator/metrics")
        String metrics() {
            return "metrics-probe";
        }
    }
}
