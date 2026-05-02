package com.fraudscanner.scannerservice.controller;

import com.fraudscanner.scannerservice.dto.EmailScanRequest;
import com.fraudscanner.scannerservice.dto.ScanResponse;
import com.fraudscanner.scannerservice.service.EmailScanService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin("*")
public class EmailController {

    private final EmailScanService emailScanService;

    public EmailController(EmailScanService emailScanService) {
        this.emailScanService = emailScanService;
    }

    @PostMapping("/scan")
    public ResponseEntity<ScanResponse> scanEmail(@Valid @RequestBody EmailScanRequest request) {
        return ResponseEntity.ok(emailScanService.scanEmail(request));
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Scanner Service is running");
    }
}
