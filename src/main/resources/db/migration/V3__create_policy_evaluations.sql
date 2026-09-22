CREATE TABLE policy_evaluations (
    id BIGSERIAL PRIMARY KEY,
    incident_id BIGINT NOT NULL REFERENCES incidents(id),
    decision VARCHAR(40) NOT NULL,
    reasons VARCHAR(1000) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL
);

CREATE INDEX idx_policy_evaluations_incident_created
    ON policy_evaluations (incident_id, created_at DESC);
