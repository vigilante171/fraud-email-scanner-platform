package com.fraudscanner.scannerservice.repository;

import com.fraudscanner.scannerservice.entity.UserFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserFeedbackRepository extends JpaRepository<UserFeedback, Long> {

    List<UserFeedback> findByEmailMessageId(Long emailId);

    List<UserFeedback> findByUserId(Long userId);
}