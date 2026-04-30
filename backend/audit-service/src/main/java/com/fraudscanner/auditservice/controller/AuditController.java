package com.fraudscanner.auditservice.controller;

import com.fraudscanner.auditservice.dto.AuditLogRequest;
import com.fraudscanner.auditservice.dto.AuditLogResponse;
import com.fraudscanner.auditservice.enums.AuditEventType;
import com.fraudscanner.auditservice.service.AuditLogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/audit")
@CrossOrigin(origins = "*")
public class AuditController {

        private final AuditLogService auditLogService;

        public AuditController(AuditLogService auditLogService) {
                this.auditLogService = auditLogService;
        }

        @PostMapping
        public ResponseEntity<AuditLogResponse> createAuditLog(@Valid @RequestBody AuditLogRequest request) {
                AuditLogResponse response = auditLogService.createAuditLog(request);
                return ResponseEntity.status(HttpStatus.CREATED).body(response);
        }

        @GetMapping
        public ResponseEntity<List<AuditLogResponse>> getAllAuditLogs() {
                return ResponseEntity.ok(auditLogService.getAllAuditLogs());
        }

        @GetMapping("/email/{emailId}")
        public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEmailId(@PathVariable Long emailId) {
                return ResponseEntity.ok(auditLogService.getAuditLogsByEmailId(emailId));
        }

        @GetMapping("/event/{eventType}")
        public ResponseEntity<List<AuditLogResponse>> getAuditLogsByEventType(@PathVariable AuditEventType eventType) {
                return ResponseEntity.ok(auditLogService.getAuditLogsByEventType(eventType));
        }

        @GetMapping("/health")
        public ResponseEntity<String> health() {
                return ResponseEntity.ok("Audit Service is running");
        }
}