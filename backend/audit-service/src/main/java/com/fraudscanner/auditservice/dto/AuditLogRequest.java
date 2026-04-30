package com.fraudscanner.auditservice.dto;

import com.fraudscanner.auditservice.enums.AuditEventType;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AuditLogRequest {

    private Long emailId;

    private Long userId;

    @NotNull(message = "Event type is required")
    private AuditEventType eventType;

    private String performedBy;

    private String details;
}
