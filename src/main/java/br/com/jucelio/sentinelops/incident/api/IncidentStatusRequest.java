package br.com.jucelio.sentinelops.incident.api;
import br.com.jucelio.sentinelops.incident.IncidentStatus;import jakarta.validation.constraints.NotNull;
public record IncidentStatusRequest(@NotNull IncidentStatus status){}
