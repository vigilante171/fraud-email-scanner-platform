package com.fraudscanner.scannerservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StatisticsSummaryResponse {

    private long totalEmails;
    private long safeEmails;
    private long suspiciousEmails;
    private long flaggedEmails;
    private double flaggedRate;
    private double averageRiskScore;
}
