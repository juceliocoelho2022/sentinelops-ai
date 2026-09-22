package br.com.jucelio.sentinelops.incident.api;
import br.com.jucelio.sentinelops.incident.Severity;import jakarta.validation.constraints.*;
public record IncidentRequest(@NotBlank @Size(max=150) String title,@NotBlank @Size(max=120) String serviceName,@NotNull Severity severity,@Size(max=2000) String description){}
