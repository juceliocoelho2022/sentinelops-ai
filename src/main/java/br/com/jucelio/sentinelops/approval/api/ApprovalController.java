package br.com.jucelio.sentinelops.approval.api;

import br.com.jucelio.sentinelops.approval.ApprovalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/incidents/{incidentId}/approvals")
public class ApprovalController {

    private final ApprovalService service;

    public ApprovalController(ApprovalService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApprovalResponse decide(
            @PathVariable Long incidentId,
            @Valid @RequestBody ApprovalRequest request
    ) {
        return service.decide(incidentId, request);
    }

    @GetMapping
    public List<ApprovalResponse> history(@PathVariable Long incidentId) {
        return service.history(incidentId);
    }
}
