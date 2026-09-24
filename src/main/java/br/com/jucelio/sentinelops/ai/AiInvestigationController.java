package br.com.jucelio.sentinelops.ai;

import br.com.jucelio.sentinelops.observability.IncidentEvidence;
import br.com.jucelio.sentinelops.observability.IncidentEvidenceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/incidents")
public class AiInvestigationController {

    private final IncidentEvidenceService evidenceService;
    private final AiInvestigationDiagnosticsService investigationService;

    public AiInvestigationController(
            IncidentEvidenceService evidenceService,
            AiInvestigationDiagnosticsService investigationService) {
        this.evidenceService = evidenceService;
        this.investigationService = investigationService;
    }

    @PostMapping("/{incidentId}/ai-investigation")
    public ResponseEntity<AiInvestigationRecommendation> investigate(@PathVariable Long incidentId) {
        IncidentEvidence evidence = evidenceService.collect(incidentId);
        return ResponseEntity.ok(investigationService.investigate(evidence).recommendation());
    }
}
