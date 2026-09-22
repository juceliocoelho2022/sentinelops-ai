package br.com.jucelio.sentinelops.policy;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PolicyEvaluationRepository extends JpaRepository<PolicyEvaluationRecord, Long> {
    Optional<PolicyEvaluationRecord> findFirstByIncidentIdOrderByCreatedAtDesc(Long incidentId);
}
