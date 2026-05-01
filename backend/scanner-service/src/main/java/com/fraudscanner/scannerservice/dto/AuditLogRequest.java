package com.fraudscanner.scannerservice.dto;

import com.fraudscanner.scannerservice.enums.AuditEventType;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLogRequest {

    private Long emailId;
    private Long userId;
    private AuditEventType eventType;
    private String performedBy;
    private String details;
}
