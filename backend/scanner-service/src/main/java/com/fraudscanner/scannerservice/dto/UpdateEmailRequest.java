package com.fraudscanner.scannerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateEmailRequest {

    private String sender;

    private String receiverEmail;

    private String subject;

    private String body;
}