package br.com.jucelio.sentinelops.agent;
import java.util.List;
public record InvestigationResult(String probableCause,List<String> evidence,List<String> recommendations,boolean humanApprovalRequired){}
