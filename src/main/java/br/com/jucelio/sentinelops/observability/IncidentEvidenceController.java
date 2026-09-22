package br.com.jucelio.sentinelops.observability;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents/{incidentId}/evidence")
public class IncidentEvidenceController {

    private final IncidentEvidenceService service;

    public IncidentEvidenceController(IncidentEvidenceService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<IncidentEvidence> collect(@PathVariable Long incidentId) {
        return ResponseEntity.ok(service.collect(incidentId));
    }
}
