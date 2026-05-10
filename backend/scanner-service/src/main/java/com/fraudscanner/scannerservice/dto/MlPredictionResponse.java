package com.fraudscanner.scannerservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MlPredictionResponse {

    private Double fraudProbability;

    private String prediction;

    private String riskLevel;

    private List<String> reasons;

    private String modelVersion;
}