package com.fraudscanner.scannerservice.repository;


import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {

    Optional<ScanResult> findByEmailMessageId(Long emailId);

    List<ScanResult> findByStatus(EmailStatus status);

    List<ScanResult> findByRiskLevel(RiskLevel riskLevel);
}