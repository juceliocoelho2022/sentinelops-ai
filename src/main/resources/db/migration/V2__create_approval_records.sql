CREATE TABLE approval_records (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL,
    decision VARCHAR(20) NOT NULL,
    decided_by VARCHAR(120) NOT NULL,
    reason VARCHAR(500),
    decided_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_approval_records_incident
        FOREIGN KEY (incident_id) REFERENCES incidents(id)
);

CREATE INDEX idx_approval_records_incident
    ON approval_records(incident_id);

CREATE INDEX idx_approval_records_decided_at
    ON approval_records(decided_at);
