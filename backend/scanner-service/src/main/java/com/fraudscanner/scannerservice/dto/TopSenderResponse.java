package com.fraudscanner.scannerservice.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TopSenderResponse {

    private String sender;
    private long flaggedCount;
}
