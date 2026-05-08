package com.fraudscanner.scannerservice.service;


import com.fraudscanner.scannerservice.dto.EmailDetailsResponse;
import com.fraudscanner.scannerservice.dto.EmailSummaryResponse;
import com.fraudscanner.scannerservice.entity.EmailMessage;
import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EmailQueryService {

    private final ScanResultRepository scanResultRepository;

    public EmailQueryService(ScanResultRepository scanResultRepository) {
        this.scanResultRepository = scanResultRepository;
    }

    public List<EmailSummaryResponse> getAllEmails() {
        return scanResultRepository.findAllOrderByScannedAtDesc()
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<EmailSummaryResponse> getFlaggedEmails() {
        return scanResultRepository.findByStatus(EmailStatus.FLAGGED)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<EmailSummaryResponse> getEmailsByStatus(EmailStatus status) {
        return scanResultRepository.findByStatus(status)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public EmailDetailsResponse getEmailDetails(Long emailId) {
        ScanResult scanResult = scanResultRepository.findByEmailMessageId(emailId)
                .orElseThrow(() -> new RuntimeException("Email scan result not found"));

        EmailMessage email = scanResult.getEmailMessage();

        return EmailDetailsResponse.builder()
                .emailId(email.getId())
                .scanId(scanResult.getId())
                .sender(email.getSender())
                .receiverEmail(email.getReceiverEmail())
                .subject(email.getSubject())
                .body(email.getBody())
                .status(scanResult.getStatus())
                .riskLevel(scanResult.getRiskLevel())
                .riskScore(scanResult.getRiskScore())
                .reasons(splitReasons(scanResult.getReasons()))
                .receivedAt(email.getReceivedAt())
                .scannedAt(scanResult.getScannedAt())
                .build();
    }

    private EmailSummaryResponse mapToSummary(ScanResult scanResult) {
        EmailMessage email = scanResult.getEmailMessage();

        return EmailSummaryResponse.builder()
                .emailId(email.getId())
                .sender(email.getSender())
                .receiverEmail(email.getReceiverEmail())
                .subject(email.getSubject())
                .status(scanResult.getStatus())
                .riskLevel(scanResult.getRiskLevel())
                .riskScore(scanResult.getRiskScore())
                .receivedAt(email.getReceivedAt())
                .scannedAt(scanResult.getScannedAt())
                .build();
    }

    private List<String> splitReasons(String reasons) {
        if (reasons == null || reasons.isBlank()) {
            return List.of();
        }

        return Arrays.stream(reasons.split("\\|"))
                .map(String::trim)
                .filter(reason -> !reason.isBlank())
                .toList();
    }
}
