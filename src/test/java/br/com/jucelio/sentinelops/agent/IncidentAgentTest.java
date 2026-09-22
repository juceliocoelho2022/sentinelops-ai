package br.com.jucelio.sentinelops.agent;
import br.com.jucelio.sentinelops.incident.*;import org.junit.jupiter.api.Test;import static org.assertj.core.api.Assertions.assertThat;
class IncidentAgentTest{@Test void shouldRequireHumanApproval(){Incident i=new Incident("Database latency","timeouts","payment-service",Severity.CRITICAL);InvestigationResult r=new IncidentAgent().investigate(i);assertThat(r.humanApprovalRequired()).isTrue();assertThat(r.evidence()).isNotEmpty();assertThat(r.recommendations()).isNotEmpty();}}
