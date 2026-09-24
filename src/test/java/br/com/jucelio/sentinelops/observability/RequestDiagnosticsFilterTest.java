package br.com.jucelio.sentinelops.observability;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import java.io.IOException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.LoggerFactory;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

class RequestDiagnosticsFilterTest {

    private final RequestDiagnosticsFilter filter = new RequestDiagnosticsFilter();
    private final Logger logger = (Logger) LoggerFactory.getLogger(RequestDiagnosticsFilter.class);
    private ListAppender<ILoggingEvent> logs;

    @BeforeEach
    void captureDiagnostics() {
        logs = new ListAppender<>();
        logs.start();
        logger.addAppender(logs);
    }

    @AfterEach
    void stopCapturingDiagnostics() {
        logger.detachAppender(logs);
        logs.stop();
    }

    @Test
    void preservesSuccessfulResponseStatus() throws Exception {
        MockHttpServletRequest request = request();
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(204);
        FilterChain chain = mock(FilterChain.class);

        filter.doFilter(request, response, chain);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(204);
        assertDiagnostics(204, "SUCCESS");
    }

    @Test
    void logsNormalizedStatusAndPropagatesRuntimeFailure() throws Exception {
        assertFailure(new IllegalStateException("provider unavailable"), 200, 500);
    }

    @Test
    void logsNormalizedStatusAndPropagatesIOException() throws Exception {
        assertFailure(new IOException("connection failed"), 200, 500);
    }

    @Test
    void logsNormalizedStatusAndPropagatesServletException() throws Exception {
        assertFailure(new ServletException("servlet failed"), 200, 500);
    }

    @Test
    void preservesExistingErrorStatusInDiagnostics() throws Exception {
        assertFailure(new IllegalStateException("provider unavailable"), 503, 503);
    }

    private void assertFailure(Exception failure, int responseStatus, int loggedStatus) throws Exception {
        MockHttpServletRequest request = request();
        MockHttpServletResponse response = new MockHttpServletResponse();
        response.setStatus(responseStatus);
        FilterChain chain = mock(FilterChain.class);

        doThrow(failure).when(chain).doFilter(request, response);

        assertThatThrownBy(() -> filter.doFilter(request, response, chain)).isSameAs(failure);

        verify(chain).doFilter(request, response);
        assertThat(response.getStatus()).isEqualTo(responseStatus);
        assertDiagnostics(loggedStatus, "ERROR");
    }

    private MockHttpServletRequest request() {
        return new MockHttpServletRequest("POST", "/api/v1/incidents/42/ai-investigation");
    }

    private void assertDiagnostics(int status, String outcome) {
        assertThat(logs.list).hasSize(1);
        Object[] arguments = logs.list.get(0).getArgumentArray();
        assertThat(arguments).hasSize(5);
        assertThat(arguments[0]).isEqualTo("POST");
        assertThat(arguments[1]).isEqualTo("/api/v1/incidents/42/ai-investigation");
        assertThat(arguments[2]).isEqualTo(status);
        assertThat(arguments[3]).isInstanceOf(Long.class);
        assertThat(arguments[4]).isEqualTo(outcome);
    }
}
