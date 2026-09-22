package br.com.jucelio.sentinelops.policy;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "policy_evaluations")
public class PolicyEvaluationRecord {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false) private Long incidentId;
    @Enumerated(EnumType.STRING) @Column(nullable = false, length = 40) private PolicyDecision decision;
    @Column(nullable = false, length = 1000) private String reasons;
    @Column(nullable = false, updatable = false) private Instant createdAt;

    protected PolicyEvaluationRecord() {}

    public PolicyEvaluationRecord(Long incidentId, PolicyDecision decision, String reasons) {
        this.incidentId = incidentId;
        this.decision = decision;
        this.reasons = reasons;
        this.createdAt = Instant.now();
    }
    public Long getId(){ return id; }
    public Long getIncidentId(){ return incidentId; }
    public PolicyDecision getDecision(){ return decision; }
    public String getReasons(){ return reasons; }
    public Instant getCreatedAt(){ return createdAt; }
}
