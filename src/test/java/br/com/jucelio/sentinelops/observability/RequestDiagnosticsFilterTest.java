package br.com.jucelio.sentinelops.observability;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.verify;

import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestDiagnosticsFilterTest {

    private final RequestDiagnosticsFilter filter = new RequestDiagnosticsFilter();

    @Test
    void preservesSuccessfulResponseStatus() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("GET", "/actuator/health");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
    }

    @Test
    void propagatesRuntimeFailureInsteadOfMaskingIt() throws Exception {
        MockHttpServletRequest request = new MockHttpServletRequest("POST", "/api/v1/incidents/42/ai-investigation");
        MockHttpServletResponse response = new MockHttpServletResponse();
        FilterChain chain = org.mockito.Mockito.mock(FilterChain.class);

        org.mockito.Mockito.doThrow(new IllegalStateException("provider unavailable"))
                .when(chain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, chain))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("provider unavailable");

        verify(chain).doFilter(request, response);
    }
}
