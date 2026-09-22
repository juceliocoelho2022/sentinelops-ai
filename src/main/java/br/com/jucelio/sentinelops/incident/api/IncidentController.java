package br.com.jucelio.sentinelops.incident.api;

import br.com.jucelio.sentinelops.agent.IncidentInvestigation;
import br.com.jucelio.sentinelops.incident.IncidentService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents")
public class IncidentController {
    private final IncidentService service;

    public IncidentController(IncidentService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<IncidentResponse> create(@Valid @RequestBody IncidentRequest request) {
        IncidentResponse created = service.create(request);
        return ResponseEntity.created(URI.create("/api/v1/incidents/" + created.id())).body(created);
    }

    @GetMapping
    public List<IncidentResponse> all() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public IncidentResponse one(@PathVariable Long id) {
        return service.findById(id);
    }

    @PatchMapping("/{id}/status")
    public IncidentResponse status(@PathVariable Long id, @Valid @RequestBody IncidentStatusRequest request) {
        return service.changeStatus(id, request.status());
    }

    @PostMapping("/{id}/investigate")
    public IncidentInvestigation investigate(@PathVariable Long id) {
        return service.investigate(id);
    }
}
