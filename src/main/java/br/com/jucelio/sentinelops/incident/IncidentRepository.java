package br.com.jucelio.sentinelops.incident;
import org.springframework.data.jpa.repository.JpaRepository;
public interface IncidentRepository extends JpaRepository<Incident,Long>{long countByStatus(IncidentStatus status);long countBySeverity(Severity severity);}
