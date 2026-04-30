package com.fraudscanner.auditservice.repository;


import com.fraudscanner.auditservice.entity.AuditLog;
import com.fraudscanner.auditservice.enums.AuditEventType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    List<AuditLog> findByEmailIdOrderByCreatedAtDesc(Long emailId);

    List<AuditLog> findByEventTypeOrderByCreatedAtDesc(AuditEventType eventType);

    List<AuditLog> findAllByOrderByCreatedAtDesc();
}