package com.fraudscanner.scannerservice.service;


import com.fraudscanner.scannerservice.dto.DistributionResponse;
import com.fraudscanner.scannerservice.dto.StatisticsSummaryResponse;
import com.fraudscanner.scannerservice.dto.TopSenderResponse;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.repository.EmailMessageRepository;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class StatisticsService {

    private final EmailMessageRepository emailMessageRepository;
    private final ScanResultRepository scanResultRepository;

    public StatisticsService(
            EmailMessageRepository emailMessageRepository,
            ScanResultRepository scanResultRepository
    ) {
        this.emailMessageRepository = emailMessageRepository;
        this.scanResultRepository = scanResultRepository;
    }

    public StatisticsSummaryResponse getSummary() {
        long totalEmails = emailMessageRepository.count();
        long safeEmails = scanResultRepository.countByStatus(EmailStatus.SAFE);
        long suspiciousEmails = scanResultRepository.countByStatus(EmailStatus.SUSPICIOUS);
        long flaggedEmails = scanResultRepository.countByStatus(EmailStatus.FLAGGED);

        double flaggedRate = totalEmails == 0 ? 0 : ((double) flaggedEmails / totalEmails) * 100;

        Double averageRiskScoreValue = scanResultRepository.averageRiskScore();
        double averageRiskScore = averageRiskScoreValue == null ? 0 : averageRiskScoreValue;

        return new StatisticsSummaryResponse(
                totalEmails,
                safeEmails,
                suspiciousEmails,
                flaggedEmails,
                round(flaggedRate),
                round(averageRiskScore)
        );
    }

    public List<DistributionResponse> getStatusDistribution() {
        return scanResultRepository.countByStatusGroup()
                .stream()
                .map(row -> new DistributionResponse(row[0].toString(), (Long) row[1]))
                .toList();
    }

    public List<DistributionResponse> getRiskDistribution() {
        return scanResultRepository.countByRiskLevelGroup()
                .stream()
                .map(row -> new DistributionResponse(row[0].toString(), (Long) row[1]))
                .toList();
    }

    public List<TopSenderResponse> getTopFlaggedSenders() {
        return scanResultRepository.findTopFlaggedSenders()
                .stream()
                .limit(5)
                .map(row -> new TopSenderResponse(
                        row[0] == null ? "Unknown sender" : row[0].toString(),
                        (Long) row[1]
                ))
                .toList();
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}