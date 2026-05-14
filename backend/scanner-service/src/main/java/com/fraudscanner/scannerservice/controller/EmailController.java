package com.fraudscanner.scannerservice.controller;

import com.fraudscanner.scannerservice.dto.EmailDetailsResponse;
import com.fraudscanner.scannerservice.dto.EmailScanRequest;
import com.fraudscanner.scannerservice.dto.EmailSummaryResponse;
import com.fraudscanner.scannerservice.dto.ScanResponse;
import com.fraudscanner.scannerservice.dto.UpdateEmailRequest;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.service.EmailQueryService;
import com.fraudscanner.scannerservice.service.EmailScanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/emails")
@CrossOrigin(origins = "*")
public class EmailController {

    private final EmailScanService emailScanService;
    private final EmailQueryService emailQueryService;

    public EmailController(
            EmailScanService emailScanService,
            EmailQueryService emailQueryService
    ) {
        this.emailScanService = emailScanService;
        this.emailQueryService = emailQueryService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponse> scanEmail(@Valid @RequestBody EmailScanRequest request) {
        return ResponseEntity.ok(emailScanService.scanEmail(request));
    }

    @GetMapping
    public ResponseEntity<List<EmailSummaryResponse>> getAllEmails(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(emailQueryService.getAllEmails(userId, role));
    }

    @GetMapping("/flagged")
    public ResponseEntity<List<EmailSummaryResponse>> getFlaggedEmails(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(emailQueryService.getFlaggedEmails(userId, role));
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EmailSummaryResponse>> getEmailsByStatus(
            @PathVariable EmailStatus status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(emailQueryService.getEmailsByStatus(status, userId, role));
    }

    @GetMapping("/{emailId}")
    public ResponseEntity<EmailDetailsResponse> getEmailDetails(
            @PathVariable Long emailId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(emailQueryService.getEmailDetails(emailId, userId, role));
    }

    @PutMapping("/{emailId}")
    public ResponseEntity<EmailDetailsResponse> updateEmail(
            @PathVariable Long emailId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role,
            @RequestBody UpdateEmailRequest request
    ) {
        return ResponseEntity.ok(emailQueryService.updateEmail(emailId, userId, role, request));
    }

    @DeleteMapping("/{emailId}")
    public ResponseEntity<Void> deleteEmail(
            @PathVariable Long emailId,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        emailQueryService.deleteEmail(emailId, userId, role);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Scanner Service is running");
    }
}