CREATE TABLE incidents (id BIGSERIAL PRIMARY KEY,title VARCHAR(150) NOT NULL,description VARCHAR(2000),service_name VARCHAR(120) NOT NULL,severity VARCHAR(20) NOT NULL,status VARCHAR(30) NOT NULL,created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP);
CREATE INDEX idx_incidents_service ON incidents(service_name);
CREATE INDEX idx_incidents_status ON incidents(status);
CREATE INDEX idx_incidents_severity ON incidents(severity);
