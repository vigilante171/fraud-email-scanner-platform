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

    public List<EmailSummaryResponse> getAllEmails(Long userId, String role) {
        if (isAdmin(role)) {
            return scanResultRepository.findAllOrderByScannedAtDesc()
                    .stream()
                    .map(this::mapToSummary)
                    .toList();
        }

        return scanResultRepository.findAllOrderByScannedAtDesc()
                .stream()
                .filter(scanResult -> belongsToUser(scanResult, userId))
                .map(this::mapToSummary)
                .toList();
    }

    public List<EmailSummaryResponse> getFlaggedEmails(Long userId, String role) {
        if (isAdmin(role)) {
            return scanResultRepository.findByStatus(EmailStatus.FLAGGED)
                    .stream()
                    .map(this::mapToSummary)
                    .toList();
        }

        return scanResultRepository.findByStatusAndEmailMessageUserId(EmailStatus.FLAGGED, userId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public List<EmailSummaryResponse> getEmailsByStatus(EmailStatus status, Long userId, String role) {
        if (isAdmin(role)) {
            return scanResultRepository.findByStatus(status)
                    .stream()
                    .map(this::mapToSummary)
                    .toList();
        }

        return scanResultRepository.findByStatusAndEmailMessageUserId(status, userId)
                .stream()
                .map(this::mapToSummary)
                .toList();
    }

    public EmailDetailsResponse getEmailDetails(Long emailId, Long userId, String role) {
        ScanResult scanResult;

        if (isAdmin(role)) {
            scanResult = scanResultRepository.findByEmailMessageId(emailId)
                    .orElseThrow(() -> new RuntimeException("Email scan result not found"));
        } else {
            scanResult = scanResultRepository.findByEmailMessageIdAndEmailMessageUserId(emailId, userId)
                    .orElseThrow(() -> new RuntimeException("Email scan result not found or access denied"));
        }

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

    private boolean isAdmin(String role) {
        return role != null && role.equalsIgnoreCase("ADMIN");
    }

    private boolean belongsToUser(ScanResult scanResult, Long userId) {
        if (scanResult == null || scanResult.getEmailMessage() == null || userId == null) {
            return false;
        }

        return userId.equals(scanResult.getEmailMessage().getUserId());
    }

    private EmailSummaryResponse mapToSummary(ScanResult scanResult) {
        EmailMessage email = scanResult.getEmailMessage();

        return EmailSummaryResponse.builder()
                .emailId(email.getId())
                .userId(email.getUserId())
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