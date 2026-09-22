package br.com.jucelio.sentinelops.agent;
import br.com.jucelio.sentinelops.incident.Incident;import org.springframework.stereotype.Component;import java.util.List;
@Component public class IncidentAgent{public InvestigationResult investigate(Incident i){return new InvestigationResult("Possible dependency degradation or resource saturation",List.of("Incident received from "+i.getServiceName(),"Severity classified as "+i.getSeverity()),List.of("Inspect service metrics","Correlate application logs","Inspect distributed traces","Review recent deployments"),true);}}
