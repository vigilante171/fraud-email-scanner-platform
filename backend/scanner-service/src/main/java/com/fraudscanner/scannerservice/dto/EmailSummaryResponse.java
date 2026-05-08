package com.fraudscanner.scannerservice.dto;


import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailSummaryResponse {

    private Long emailId;
    private String sender;
    private String receiverEmail;
    private String subject;
    private EmailStatus status;
    private RiskLevel riskLevel;
    private Integer riskScore;
    private LocalDateTime receivedAt;
    private LocalDateTime scannedAt;
}
