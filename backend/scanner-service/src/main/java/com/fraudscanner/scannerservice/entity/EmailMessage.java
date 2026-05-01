package com.fraudscanner.scannerservice.entity;

import jakarta.persistence.*;
import jdk.jfr.DataAmount;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
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
    private Long id ;
    private String sender ;
    private String receiverEmail;
    @Column(columnDefinition = "TEXT" ,nullable = false)
    private String body ;
    private LocalDateTime receivedAt ;
    private LocalDateTime createdAt  ;
    @PrePersist
    public void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }

        if (receivedAt == null) {
            receivedAt = LocalDateTime.now();
        }
    }

}
