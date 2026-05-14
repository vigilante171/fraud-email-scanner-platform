package com.fraudscanner.scannerservice.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmailScanRequest {

    private Long userId;

    @NotBlank(message = "Sender is required")
    private String sender;

    @NotBlank(message = "Receiver email is required")
    private String receiverEmail;

    @NotBlank(message = "Subject is required")
    private String subject;

    @NotBlank(message = "Body is required")
    private String body;

    private LocalDateTime receivedAt;
}