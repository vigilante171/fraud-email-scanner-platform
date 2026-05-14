package com.fraudscanner.scannerservice.service;

import com.fraudscanner.scannerservice.dto.EmailDetailsResponse;
import com.fraudscanner.scannerservice.dto.EmailSummaryResponse;
import com.fraudscanner.scannerservice.dto.UpdateEmailRequest;
import com.fraudscanner.scannerservice.entity.EmailMessage;
import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.repository.EmailMessageRepository;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class EmailQueryService {

    private final ScanResultRepository scanResultRepository;
    private final EmailMessageRepository emailMessageRepository;

    public EmailQueryService(
            ScanResultRepository scanResultRepository,
            EmailMessageRepository emailMessageRepository
    ) {
        this.scanResultRepository = scanResultRepository;
        this.emailMessageRepository = emailMessageRepository;
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

        return mapToDetails(scanResult);
    }

    public EmailDetailsResponse updateEmail(Long emailId, Long userId, String role, UpdateEmailRequest request) {
        ScanResult scanResult;

        if (isAdmin(role)) {
            scanResult = scanResultRepository.findByEmailMessageId(emailId)
                    .orElseThrow(() -> new RuntimeException("Email scan result not found"));
        } else {
            throw new RuntimeException("Only admins can update emails");
        }

        EmailMessage email = scanResult.getEmailMessage();

        if (request.getSender() != null && !request.getSender().isBlank()) {
            email.setSender(request.getSender().trim());
        }

        if (request.getReceiverEmail() != null && !request.getReceiverEmail().isBlank()) {
            email.setReceiverEmail(request.getReceiverEmail().trim());
        }

        if (request.getSubject() != null && !request.getSubject().isBlank()) {
            email.setSubject(request.getSubject().trim());
        }

        if (request.getBody() != null && !request.getBody().isBlank()) {
            email.setBody(request.getBody());
        }

        emailMessageRepository.save(email);

        return mapToDetails(scanResult);
    }

    @Transactional
    public void deleteEmail(Long emailId, Long userId, String role) {
        if (!isAdmin(role)) {
            throw new RuntimeException("Only admins can delete emails");
        }

        if (!emailMessageRepository.existsById(emailId)) {
            throw new RuntimeException("Email not found with id: " + emailId);
        }

        scanResultRepository.deleteByEmailMessageId(emailId);
        emailMessageRepository.deleteById(emailId);
    }

    private EmailDetailsResponse mapToDetails(ScanResult scanResult) {
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