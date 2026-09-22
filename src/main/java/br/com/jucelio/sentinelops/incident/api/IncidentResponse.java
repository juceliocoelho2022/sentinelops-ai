package br.com.jucelio.sentinelops.incident.api;
import br.com.jucelio.sentinelops.incident.*;import java.time.Instant;
public record IncidentResponse(Long id,String title,String description,String serviceName,Severity severity,IncidentStatus status,Instant createdAt,Instant updatedAt){public static IncidentResponse from(Incident i){return new IncidentResponse(i.getId(),i.getTitle(),i.getDescription(),i.getServiceName(),i.getSeverity(),i.getStatus(),i.getCreatedAt(),i.getUpdatedAt());}}
