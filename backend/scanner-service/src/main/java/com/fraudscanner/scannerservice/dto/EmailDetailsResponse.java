package com.fraudscanner.scannerservice.dto;

import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailDetailsResponse {

    private Long emailId;
    private Long scanId;
    private String sender;
    private String receiverEmail;
    private String subject;
    private String body;
    private EmailStatus status;
    private RiskLevel riskLevel;
    private Integer riskScore;
    private List<String> reasons;
    private LocalDateTime receivedAt;
    private LocalDateTime scannedAt;
}
