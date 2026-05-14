package com.fraudscanner.scannerservice.repository;

import com.fraudscanner.scannerservice.entity.EmailMessage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EmailMessageRepository extends JpaRepository<EmailMessage, Long> {

    List<EmailMessage> findBySenderContainingIgnoreCase(String sender);

    List<EmailMessage> findBySubjectContainingIgnoreCase(String subject);
    List<EmailMessage> findByUserId(Long userId);
}