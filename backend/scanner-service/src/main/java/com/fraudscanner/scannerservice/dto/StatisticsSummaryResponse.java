package com.fraudscanner.scannerservice.dto;

public class StatisticsSummaryResponse {

    private Long totalEmails;
    private Long safeEmails;
    private Long suspiciousEmails;
    private Long flaggedEmails;
    private Double flaggedRate;
    private Double averageRiskScore;

    public StatisticsSummaryResponse() {
    }

    public StatisticsSummaryResponse(
            Long totalEmails,
            Long safeEmails,
            Long suspiciousEmails,
            Long flaggedEmails,
            Double flaggedRate,
            Double averageRiskScore
    ) {
        this.totalEmails = totalEmails;
        this.safeEmails = safeEmails;
        this.suspiciousEmails = suspiciousEmails;
        this.flaggedEmails = flaggedEmails;
        this.flaggedRate = flaggedRate;
        this.averageRiskScore = averageRiskScore;
    }

    public Long getTotalEmails() {
        return totalEmails;
    }

    public void setTotalEmails(Long totalEmails) {
        this.totalEmails = totalEmails;
    }

    public Long getSafeEmails() {
        return safeEmails;
    }

    public void setSafeEmails(Long safeEmails) {
        this.safeEmails = safeEmails;
    }

    public Long getSuspiciousEmails() {
        return suspiciousEmails;
    }

    public void setSuspiciousEmails(Long suspiciousEmails) {
        this.suspiciousEmails = suspiciousEmails;
    }

    public Long getFlaggedEmails() {
        return flaggedEmails;
    }

    public void setFlaggedEmails(Long flaggedEmails) {
        this.flaggedEmails = flaggedEmails;
    }

    public Double getFlaggedRate() {
        return flaggedRate;
    }

    public void setFlaggedRate(Double flaggedRate) {
        this.flaggedRate = flaggedRate;
    }

    public Double getAverageRiskScore() {
        return averageRiskScore;
    }

    public void setAverageRiskScore(Double averageRiskScore) {
        this.averageRiskScore = averageRiskScore;
    }
}