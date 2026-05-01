package com.fraudscanner.scannerservice.client;

import com.fraudscanner.scannerservice.dto.AuditLogRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class AuditServiceClient {

    private final RestTemplate restTemplate;

    @Value("${services.audit.base-url}")
    private String auditBaseUrl;

    public AuditServiceClient(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public void createAuditLog(AuditLogRequest request) {
        try {
            restTemplate.postForEntity(auditBaseUrl, request, Void.class);
        } catch (Exception exception) {
            System.err.println("Failed to send audit log: " + exception.getMessage());
        }
    }
}