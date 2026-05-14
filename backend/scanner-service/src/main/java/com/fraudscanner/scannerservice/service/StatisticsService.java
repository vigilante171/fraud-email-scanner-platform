package com.fraudscanner.scannerservice.service;

import com.fraudscanner.scannerservice.dto.DistributionResponse;
import com.fraudscanner.scannerservice.dto.StatisticsSummaryResponse;
import com.fraudscanner.scannerservice.dto.TopSenderResponse;
import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.repository.EmailMessageRepository;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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

    public StatisticsSummaryResponse getSummary(Long userId, String role) {
        List<ScanResult> scanResults = getAccessibleScanResults(userId, role);

        long totalEmails = isAdmin(role)
                ? emailMessageRepository.count()
                : scanResults.size();

        long safeEmails = scanResults.stream()
                .filter(result -> result.getStatus() == EmailStatus.SAFE)
                .count();

        long suspiciousEmails = scanResults.stream()
                .filter(result -> result.getStatus() == EmailStatus.SUSPICIOUS)
                .count();

        long flaggedEmails = scanResults.stream()
                .filter(result -> result.getStatus() == EmailStatus.FLAGGED)
                .count();

        double flaggedRate = totalEmails == 0
                ? 0
                : ((double) flaggedEmails / totalEmails) * 100;

        double averageRiskScore = scanResults.isEmpty()
                ? 0
                : scanResults.stream()
                .mapToInt(result -> result.getRiskScore() == null ? 0 : result.getRiskScore())
                .average()
                .orElse(0);

        return new StatisticsSummaryResponse(
                totalEmails,
                safeEmails,
                suspiciousEmails,
                flaggedEmails,
                round(flaggedRate),
                round(averageRiskScore)
        );
    }

    public List<DistributionResponse> getStatusDistribution(Long userId, String role) {
        List<ScanResult> scanResults = getAccessibleScanResults(userId, role);

        return scanResults.stream()
                .collect(Collectors.groupingBy(
                        result -> result.getStatus().toString(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> new DistributionResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<DistributionResponse> getRiskDistribution(Long userId, String role) {
        List<ScanResult> scanResults = getAccessibleScanResults(userId, role);

        return scanResults.stream()
                .collect(Collectors.groupingBy(
                        result -> result.getRiskLevel().toString(),
                        Collectors.counting()
                ))
                .entrySet()
                .stream()
                .map(entry -> new DistributionResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    public List<TopSenderResponse> getTopFlaggedSenders(Long userId, String role) {
        List<ScanResult> scanResults = getAccessibleScanResults(userId, role);

        Map<String, Long> senderCounts = scanResults.stream()
                .filter(result -> result.getStatus() == EmailStatus.FLAGGED)
                .filter(result -> result.getEmailMessage() != null)
                .collect(Collectors.groupingBy(
                        result -> result.getEmailMessage().getSender() == null
                                ? "Unknown sender"
                                : result.getEmailMessage().getSender(),
                        Collectors.counting()
                ));

        return senderCounts.entrySet()
                .stream()
                .sorted((a, b) -> Long.compare(b.getValue(), a.getValue()))
                .limit(5)
                .map(entry -> new TopSenderResponse(entry.getKey(), entry.getValue()))
                .toList();
    }

    private List<ScanResult> getAccessibleScanResults(Long userId, String role) {
        if (isAdmin(role)) {
            return scanResultRepository.findAll();
        }

        if (userId == null) {
            return List.of();
        }

        return scanResultRepository.findByEmailMessageUserId(userId);
    }

    private boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    private double round(double value) {
        return Math.round(value * 100.0) / 100.0;
    }
}