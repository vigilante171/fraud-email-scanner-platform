package com.fraudscanner.auditservice.service;

import com.fraudscanner.auditservice.dto.AuditLogRequest;
import com.fraudscanner.auditservice.dto.AuditLogResponse;
import com.fraudscanner.auditservice.entity.AuditLog;
import com.fraudscanner.auditservice.enums.AuditEventType;
import com.fraudscanner.auditservice.repository.AuditLogRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public AuditLogResponse createAuditLog(AuditLogRequest request) {
        AuditLog auditLog = AuditLog.builder()
                .emailId(request.getEmailId())
                .userId(request.getUserId())
                .eventType(request.getEventType())
                .performedBy(request.getPerformedBy())
                .details(request.getDetails())
                .build();

        AuditLog savedLog = auditLogRepository.save(auditLog);

        return mapToResponse(savedLog);
    }

    public List<AuditLogResponse> getAllAuditLogs() {
        return auditLogRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AuditLogResponse> getAuditLogsByEmailId(Long emailId) {
        return auditLogRepository.findByEmailIdOrderByCreatedAtDesc(emailId)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    public List<AuditLogResponse> getAuditLogsByEventType(AuditEventType eventType) {
        return auditLogRepository.findByEventTypeOrderByCreatedAtDesc(eventType)
                .stream()
                .map(this::mapToResponse)
                .toList();
    }

    private AuditLogResponse mapToResponse(AuditLog auditLog) {
        return AuditLogResponse.builder()
                .id(auditLog.getId())
                .emailId(auditLog.getEmailId())
                .userId(auditLog.getUserId())
                .eventType(auditLog.getEventType())
                .performedBy(auditLog.getPerformedBy())
                .details(auditLog.getDetails())
                .createdAt(auditLog.getCreatedAt())
                .build();
    }
}
