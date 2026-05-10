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
public class ScanResponse {

    private Long emailId;
    private Long scanId;
    private EmailStatus status;
    private RiskLevel riskLevel;
    private Integer riskScore;
    private List<String> reasons;
    private LocalDateTime scannedAt;

    private Double mlFraudProbability;

    private String mlPrediction;

    private String mlRiskLevel;

    private String mlModelVersion;

    private List<String> mlReasons;

    private Integer ruleRiskScore;

    private Integer finalRiskScore;

}
