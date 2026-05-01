package com.fraudscanner.scannerservice.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "email_messages")
public class EmailMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sender;

    private String receiverEmail;

    @Column(nullable = false)
    private String subject;   // ✅ Added subject field

    @Column(columnDefinition = "TEXT", nullable = false)
    private String body;

    @Builder.Default
    private LocalDateTime receivedAt = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();
}
