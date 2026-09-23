package br.com.jucelio.sentinelops.observability;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RequestDiagnosticsFilter extends OncePerRequestFilter {

    private static final Logger log = LoggerFactory.getLogger(RequestDiagnosticsFilter.class);

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        long startedAt = System.nanoTime();
        Throwable failure = null;

        try {
            filterChain.doFilter(request, response);
        } catch (IOException | ServletException | RuntimeException ex) {
            failure = ex;
            throw ex;
        } finally {
            long durationMs = (System.nanoTime() - startedAt) / 1_000_000;
            int status = failure == null
                    ? response.getStatus()
                    : normalizeFailureStatus(response.getStatus());

            log.info("HTTP request completed method={} path={} status={} durationMs={} outcome={}",
                    request.getMethod(),
                    request.getRequestURI(),
                    status,
                    durationMs,
                    failure == null ? "SUCCESS" : "ERROR");
        }
    }

    private int normalizeFailureStatus(int responseStatus) {
        return responseStatus >= 400
                ? responseStatus
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
    }
}
