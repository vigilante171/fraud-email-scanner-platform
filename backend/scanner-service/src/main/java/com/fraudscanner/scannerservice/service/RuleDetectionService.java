package com.fraudscanner.scannerservice.service;

import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import lombok.Value;
import lombok.Builder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RuleDetectionService {

    public DetectionResult analyze(String sender, String subject, String body) {
        int score = 0;
        List<String> reasons = new ArrayList<>();

        String safeSender = sender == null ? "" : sender.toLowerCase();
        String safeSubject = subject == null ? "" : subject.toLowerCase();
        String safeBody = body == null ? "" : body.toLowerCase();
        String fullText = safeSubject + " " + safeBody;

        if (containsUrgentWords(fullText)) {
            score += 20;
            reasons.add("Email contains urgent or pressure-based words");
        }

        if (containsFinancialWords(fullText)) {
            score += 15;
            reasons.add("Email contains financial or account-related keywords");
        }

        if (containsSuspiciousLinks(fullText)) {
            score += 25;
            reasons.add("Email contains suspicious or shortened links");
        }

        if (isSuspiciousSender(safeSender)) {
            score += 25;
            reasons.add("Sender domain looks suspicious");
        }

        if (containsCredentialRequest(fullText)) {
            score += 20;
            reasons.add("Email asks for credentials or account verification");
        }

        if (containsAttachmentRisk(fullText)) {
            score += 15;
            reasons.add("Email mentions potentially risky attachments");
        }

        if (score > 100) {
            score = 100;
        }

        RiskLevel riskLevel = resolveRiskLevel(score);
        EmailStatus status = resolveStatus(score);

        if (reasons.isEmpty()) {
            reasons.add("No suspicious indicators detected");
        }

        return DetectionResult.builder()
                .riskScore(score)
                .riskLevel(riskLevel)
                .status(status)
                .reasons(reasons)
                .build();
    }

    private boolean containsUrgentWords(String text) {
        return text.contains("urgent")
                || text.contains("immediately")
                || text.contains("limited time")
                || text.contains("act now")
                || text.contains("suspended")
                || text.contains("blocked")
                || text.contains("verify now");
    }

    private boolean containsFinancialWords(String text) {
        return text.contains("bank")
                || text.contains("payment")
                || text.contains("invoice")
                || text.contains("account")
                || text.contains("credit card")
                || text.contains("transaction")
                || text.contains("refund");
    }

    private boolean containsSuspiciousLinks(String text) {
        return text.contains("http://")
                || text.contains("https://")
                || text.contains("bit.ly")
                || text.contains("tinyurl")
                || text.contains("click here")
                || text.contains("login here");
    }

    private boolean isSuspiciousSender(String sender) {
        return sender.contains("fake")
                || sender.contains("security-alert")
                || sender.contains("verify")
                || sender.contains("support-login")
                || sender.endsWith(".ru")
                || sender.endsWith(".xyz");
    }

    private boolean containsCredentialRequest(String text) {
        return text.contains("password")
                || text.contains("verify your account")
                || text.contains("confirm your identity")
                || text.contains("login to your account")
                || text.contains("update your information");
    }

    private boolean containsAttachmentRisk(String text) {
        return text.contains(".exe")
                || text.contains(".bat")
                || text.contains(".cmd")
                || text.contains(".scr")
                || text.contains("macro enabled");
    }

    private RiskLevel resolveRiskLevel(int score) {
        if (score >= 70) {
            return RiskLevel.HIGH;
        }
        if (score >= 35) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.LOW;
    }

    private EmailStatus resolveStatus(int score) {
        if (score >= 70) {
            return EmailStatus.FLAGGED;
        }
        if (score >= 35) {
            return EmailStatus.SUSPICIOUS;
        }
        return EmailStatus.SAFE;
    }

    @Value
    @Builder
    public static class DetectionResult {
        int riskScore;
        RiskLevel riskLevel;
        EmailStatus status;
        List<String> reasons;
    }
}
