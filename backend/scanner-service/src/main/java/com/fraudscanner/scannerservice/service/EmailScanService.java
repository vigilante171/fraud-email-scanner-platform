package com.fraudscanner.scannerservice.service;

import com.fraudscanner.scannerservice.client.AuditServiceClient;
import com.fraudscanner.scannerservice.client.MlServiceClient;
import com.fraudscanner.scannerservice.dto.AuditLogRequest;
import com.fraudscanner.scannerservice.dto.EmailScanRequest;
import com.fraudscanner.scannerservice.dto.MlPredictionRequest;
import com.fraudscanner.scannerservice.dto.MlPredictionResponse;
import com.fraudscanner.scannerservice.dto.ScanResponse;
import com.fraudscanner.scannerservice.entity.EmailMessage;
import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.AuditEventType;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import com.fraudscanner.scannerservice.repository.EmailMessageRepository;
import com.fraudscanner.scannerservice.repository.ScanResultRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class EmailScanService {

    private final EmailMessageRepository emailMessageRepository;
    private final ScanResultRepository scanResultRepository;
    private final RuleDetectionService ruleDetectionService;
    private final AuditServiceClient auditServiceClient;
    private final MlServiceClient mlServiceClient;

    public EmailScanService(
            EmailMessageRepository emailMessageRepository,
            ScanResultRepository scanResultRepository,
            RuleDetectionService ruleDetectionService,
            AuditServiceClient auditServiceClient,
            MlServiceClient mlServiceClient
    ) {
        this.emailMessageRepository = emailMessageRepository;
        this.scanResultRepository = scanResultRepository;
        this.ruleDetectionService = ruleDetectionService;
        this.auditServiceClient = auditServiceClient;
        this.mlServiceClient = mlServiceClient;
    }

    public ScanResponse scanEmail(EmailScanRequest request) {

        EmailMessage savedEmail = emailMessageRepository.save(
                EmailMessage.builder()
                        .sender(request.getSender())
                        .receiverEmail(request.getReceiverEmail())
                        .subject(request.getSubject())
                        .body(request.getBody())
                        .userId(request.getUserId())
                        .receivedAt(request.getReceivedAt() != null
                                ? request.getReceivedAt()
                                : LocalDateTime.now())
                        .build()
        );

        RuleDetectionService.DetectionResult detectionResult = ruleDetectionService.analyze(
                request.getSender(),
                request.getSubject(),
                request.getBody()
        );

        int ruleRiskScore = detectionResult.getRiskScore();

        MlPredictionResponse mlPrediction = mlServiceClient.predict(
                MlPredictionRequest.builder()
                        .sender(request.getSender())
                        .subject(request.getSubject())
                        .body(request.getBody())
                        .build()
        );

        int finalRiskScore = calculateFinalRiskScore(
                ruleRiskScore,
                mlPrediction.getFraudProbability()
        );

        EmailStatus finalStatus = calculateFinalStatus(finalRiskScore);
        RiskLevel finalRiskLevel = calculateFinalRiskLevel(finalRiskScore);

        List<String> combinedReasons = new ArrayList<>();

        if (detectionResult.getReasons() != null) {
            combinedReasons.addAll(detectionResult.getReasons());
        }

        if (mlPrediction.getReasons() != null) {
            mlPrediction.getReasons().forEach(reason ->
                    combinedReasons.add("[ML] " + reason)
            );
        }

        String reasonsAsText = String.join(" | ", combinedReasons);

        ScanResult savedScanResult = scanResultRepository.save(
                ScanResult.builder()
                        .riskScore(finalRiskScore)
                        .riskLevel(finalRiskLevel)
                        .status(finalStatus)
                        .reasons(reasonsAsText)
                        .emailMessage(savedEmail)
                        .build()
        );

        AuditLogRequest auditLogRequest = AuditLogRequest.builder()
                .emailId(savedEmail.getId())
                .userId(request.getUserId())
                .eventType(finalStatus == EmailStatus.FLAGGED
                        ? AuditEventType.EMAIL_FLAGGED
                        : AuditEventType.EMAIL_SCANNED)
                .performedBy("scanner-service")
                .details(finalStatus == EmailStatus.FLAGGED
                        ? "Email flagged with final risk score " + finalRiskScore
                        : "Email scanned with final status " + finalStatus + " and final risk score " + finalRiskScore)
                .build();

        auditServiceClient.createAuditLog(auditLogRequest);

        return ScanResponse.builder()
                .emailId(savedEmail.getId())
                .scanId(savedScanResult.getId())
                .status(finalStatus)
                .riskLevel(finalRiskLevel)
                .riskScore(finalRiskScore)
                .reasons(combinedReasons)
                .scannedAt(savedScanResult.getScannedAt())
                .mlFraudProbability(mlPrediction.getFraudProbability())
                .mlPrediction(mlPrediction.getPrediction())
                .mlRiskLevel(mlPrediction.getRiskLevel())
                .mlModelVersion(mlPrediction.getModelVersion())
                .mlReasons(mlPrediction.getReasons())
                .ruleRiskScore(ruleRiskScore)
                .finalRiskScore(finalRiskScore)
                .build();
    }

    private int calculateFinalRiskScore(int ruleScore, Double mlProbability) {
        int mlScore = mlProbability == null
                ? 0
                : (int) Math.round(mlProbability * 100);

        return (int) Math.round((ruleScore * 0.6) + (mlScore * 0.4));
    }

    private EmailStatus calculateFinalStatus(int finalScore) {
        if (finalScore >= 70) {
            return EmailStatus.FLAGGED;
        }

        if (finalScore >= 40) {
            return EmailStatus.SUSPICIOUS;
        }

        return EmailStatus.SAFE;
    }

    private RiskLevel calculateFinalRiskLevel(int finalScore) {
        if (finalScore >= 70) {
            return RiskLevel.HIGH;
        }

        if (finalScore >= 40) {
            return RiskLevel.MEDIUM;
        }

        return RiskLevel.LOW;
    }
}