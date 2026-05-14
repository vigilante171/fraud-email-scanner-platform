package com.fraudscanner.scannerservice.repository;

import com.fraudscanner.scannerservice.entity.ScanResult;
import com.fraudscanner.scannerservice.enums.EmailStatus;
import com.fraudscanner.scannerservice.enums.RiskLevel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ScanResultRepository extends JpaRepository<ScanResult, Long> {
    List<ScanResult> findByEmailMessageUserId(Long userId);
    Optional<ScanResult> findByEmailMessageId(Long emailId);

    Optional<ScanResult> findByEmailMessageIdAndEmailMessageUserId(Long emailId, Long userId);

    List<ScanResult> findByStatus(EmailStatus status);

    List<ScanResult> findByStatusAndEmailMessageUserId(EmailStatus status, Long userId);

    List<ScanResult> findByRiskLevel(RiskLevel riskLevel);

    long countByStatus(EmailStatus status);

    long countByRiskLevel(RiskLevel riskLevel);

    @Query("SELECT AVG(s.riskScore) FROM ScanResult s")
    Double averageRiskScore();

    @Query("""
           SELECT s.status, COUNT(s)
           FROM ScanResult s
           GROUP BY s.status
           """)
    List<Object[]> countByStatusGroup();

    @Query("""
           SELECT s.riskLevel, COUNT(s)
           FROM ScanResult s
           GROUP BY s.riskLevel
           """)
    List<Object[]> countByRiskLevelGroup();

    @Query("""
           SELECT s
           FROM ScanResult s
           ORDER BY s.scannedAt DESC
           """)
    List<ScanResult> findAllOrderByScannedAtDesc();

    @Query("""
           SELECT s.emailMessage.sender, COUNT(s)
           FROM ScanResult s
           WHERE s.status = com.fraudscanner.scannerservice.enums.EmailStatus.FLAGGED
           GROUP BY s.emailMessage.sender
           ORDER BY COUNT(s) DESC
           """)
    List<Object[]> findTopFlaggedSenders();
}