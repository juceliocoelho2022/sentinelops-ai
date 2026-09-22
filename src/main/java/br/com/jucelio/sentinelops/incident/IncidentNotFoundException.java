package br.com.jucelio.sentinelops.incident;
public class IncidentNotFoundException extends RuntimeException{public IncidentNotFoundException(Long id){super("Incident "+id+" was not found");}}
