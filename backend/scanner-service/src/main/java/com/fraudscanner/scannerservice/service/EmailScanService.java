package com.fraudscanner.scannerservice.service;

import com.fraudscanner.scannerservice.client.AuditServiceClient;
import com.fraudscanner.scannerservice.dto.AuditLogRequest;
import com.fraudscanner.scannerservice.dto.EmailScanRequest;
import com.fraudscanner.scannerservice.dto.ScanResponse;
import com.fraudscanner.scannerservice.entity.EmailMessage;
import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.AuditEventType;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.repository.EmailMessageRepository;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

@Service
public class EmailScanService {

    private final EmailMessageRepository emailMessageRepository;
    private final ScanResultRepository scanResultRepository;
    private final RuleDetectionService ruleDetectionService;
    private final AuditServiceClient auditServiceClient;

    public EmailScanService(
            EmailMessageRepository emailMessageRepository,
            ScanResultRepository scanResultRepository,
            RuleDetectionService ruleDetectionService,
            AuditServiceClient auditServiceClient
    ) {
        this.emailMessageRepository = emailMessageRepository;
        this.scanResultRepository = scanResultRepository;
        this.ruleDetectionService = ruleDetectionService;
        this.auditServiceClient = auditServiceClient;
    }

    public ScanResponse scanEmail(EmailScanRequest request) {
        // Build and save EmailMessage
        EmailMessage savedEmail = emailMessageRepository.save(
                EmailMessage.builder()
                        .sender(request.getSender())
                        .receiverEmail(request.getReceiverEmail())
                        .subject(request.getSubject())
                        .body(request.getBody())
                        .receivedAt(request.getReceivedAt())
                        .build()
        );

        // Run detection
        RuleDetectionService.DetectionResult detectionResult = ruleDetectionService.analyze(
                request.getSender(),
                request.getSubject(),
                request.getBody()
        );

        String reasonsAsText = String.join(" | ", detectionResult.getReasons());

        // Build and save ScanResult
        ScanResult savedScanResult = scanResultRepository.save(
                ScanResult.builder()
                        .riskScore(detectionResult.getRiskScore())
                        .riskLevel(detectionResult.getRiskLevel())
                        .status(detectionResult.getStatus())
                        .reasons(reasonsAsText)
                        .emailMessage(savedEmail)
                        .build()
        );

        // Build AuditLogRequest
        AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                .emailId(savedEmail.getId())
                .userId(null)
                .eventType(detectionResult.getStatus() == EmailStatus.FLAGGED
                        ? AuditEventType.EMAIL_FLAGGED
                        : AuditEventType.EMAIL_SCANNED)
                .performedBy("scanner-service")
                .details(detectionResult.getStatus() == EmailStatus.FLAGGED
                        ? "Email flagged with risk score " + detectionResult.getRiskScore()
                        : "Email scanned with status " + detectionResult.getStatus())
                .build();

        auditServiceClient.createAuditLog(auditLogRequest);

        // Build ScanResponse
        return ScanResponse.builder()
                .emailId(savedEmail.getId())
                .scanId(savedScanResult.getId())
                .status(detectionResult.getStatus())
                .riskLevel(detectionResult.getRiskLevel())
                .riskScore(detectionResult.getRiskScore())
                .reasons(detectionResult.getReasons())
                .scannedAt(savedScanResult.getScannedAt())
                .build();
    }
}
