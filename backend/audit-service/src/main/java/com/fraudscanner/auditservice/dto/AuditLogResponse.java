package com.fraudscanner.auditservice.dto;

import com.fraudscanner.auditservice.enums.AuditEventType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogResponse {

    private Long id;
    private Long emailId;
    private Long userId;
    private AuditEventType eventType;
    private String performedBy;
    private String details;
    private LocalDateTime createdAt;
}
