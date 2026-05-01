package com.fraudscanner.scannerservice.entity;


import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.time.LocalDateTime;

@Entity
@Table(name = "scan_results")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScanResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Integer riskScore;

    @Enumerated(EnumType.STRING)
    private RiskLevel riskLevel;

    @Enumerated(EnumType.STRING)
    private EmailStatus status;

    @Column(columnDefinition = "TEXT")
    private String reasons;

    @Builder.Default
    private LocalDateTime scannedAt = LocalDateTime.now();

    @OneToOne
    @JoinColumn(name = "email_id", nullable = false)
    private EmailMessage emailMessage;
}
