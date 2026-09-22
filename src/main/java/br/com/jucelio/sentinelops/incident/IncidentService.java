package br.com.jucelio.sentinelops.incident;

import br.com.jucelio.sentinelops.agent.IncidentAgent;
import br.com.jucelio.sentinelops.agent.IncidentInvestigation;
import br.com.jucelio.sentinelops.agent.InvestigationResult;
import br.com.jucelio.sentinelops.incident.api.IncidentRequest;
import br.com.jucelio.sentinelops.incident.api.IncidentResponse;
import br.com.jucelio.sentinelops.security.SecurityAgent;
import br.com.jucelio.sentinelops.security.SecurityFinding;
import br.com.jucelio.sentinelops.policy.PolicyEngine;
import br.com.jucelio.sentinelops.policy.PolicyEvaluation;
import br.com.jucelio.sentinelops.policy.PolicyEvaluationRecord;
import br.com.jucelio.sentinelops.policy.PolicyEvaluationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IncidentService {
    private final IncidentRepository repository;
    private final IncidentAgent agent;
    private final SecurityAgent securityAgent;
    private final PolicyEngine policyEngine;
    private final PolicyEvaluationRepository policyEvaluationRepository;

    public IncidentService(IncidentRepository repository, IncidentAgent agent, SecurityAgent securityAgent,
                           PolicyEngine policyEngine, PolicyEvaluationRepository policyEvaluationRepository) {
        this.repository = repository;
        this.agent = agent;
        this.securityAgent = securityAgent;
        this.policyEngine = policyEngine;
        this.policyEvaluationRepository = policyEvaluationRepository;
    }

    @Transactional
    public IncidentResponse create(IncidentRequest request) {
        return IncidentResponse.from(repository.save(new Incident(
                request.title(), request.description(), request.serviceName(), request.severity())));
    }

    @Transactional(readOnly = true)
    public List<IncidentResponse> findAll() {
        return repository.findAll().stream().map(IncidentResponse::from).toList();
    }

    @Transactional(readOnly = true)
    public IncidentResponse findById(Long id) {
        return IncidentResponse.from(get(id));
    }

    @Transactional
    public IncidentResponse changeStatus(Long id, IncidentStatus status) {
        Incident incident = get(id);
        incident.changeStatus(status);
        return IncidentResponse.from(incident);
    }

    @Transactional
    public IncidentInvestigation investigate(Long id) {
        Incident incident = get(id);
        incident.changeStatus(IncidentStatus.INVESTIGATING);

        InvestigationResult incidentAnalysis = agent.investigate(incident);
        SecurityFinding securityAnalysis = securityAgent.analyze(incident);

        PolicyEvaluation policyEvaluation = policyEngine.evaluate(incidentAnalysis, securityAnalysis);
        policyEvaluationRepository.save(new PolicyEvaluationRecord(
                id, policyEvaluation.decision(), String.join("; ", policyEvaluation.reasons())));

        return new IncidentInvestigation(incidentAnalysis, securityAnalysis, policyEvaluation);
    }

    private Incident get(Long id) {
        return repository.findById(id).orElseThrow(() -> new IncidentNotFoundException(id));
    }
}
