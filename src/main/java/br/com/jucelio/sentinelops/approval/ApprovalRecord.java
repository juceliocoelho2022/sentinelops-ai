package br.com.jucelio.sentinelops.approval;

import jakarta.persistence.*;

import java.time.Instant;

@Entity
@Table(name = "approval_records")
public class ApprovalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "incident_id", nullable = false)
    private Long incidentId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ApprovalDecision decision;

    @Column(name = "decided_by", nullable = false, length = 120)
    private String decidedBy;

    @Column(length = 500)
    private String reason;

    @Column(name = "decided_at", nullable = false, updatable = false)
    private Instant decidedAt;

    protected ApprovalRecord() {
    }

    public ApprovalRecord(Long incidentId, ApprovalDecision decision, String decidedBy, String reason) {
        this.incidentId = incidentId;
        this.decision = decision;
        this.decidedBy = decidedBy;
        this.reason = reason;
        this.decidedAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getIncidentId() { return incidentId; }
    public ApprovalDecision getDecision() { return decision; }
    public String getDecidedBy() { return decidedBy; }
    public String getReason() { return reason; }
    public Instant getDecidedAt() { return decidedAt; }
}
