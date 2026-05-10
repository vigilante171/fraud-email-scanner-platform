package com.fraudscanner.scannerservice.client;

import com.fraudscanner.scannerservice.dto.MlPredictionRequest;
import com.fraudscanner.scannerservice.dto.MlPredictionResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Component
@RequiredArgsConstructor
public class MlServiceClient {

    private final RestTemplate restTemplate;

    @Value("${ml.service.url:http://localhost:8000}")
    private String mlServiceUrl;

    public MlPredictionResponse predict(MlPredictionRequest request) {
        String url = mlServiceUrl + "/api/ml/predict";

        try {
            MlPredictionResponse response = restTemplate.postForObject(
                    url,
                    request,
                    MlPredictionResponse.class
            );

            if (response == null) {
                return fallback("ML service returned empty response");
            }

            return response;

        } catch (Exception exception) {
            log.error("Failed to call ML service: {}", exception.getMessage());

            return fallback("ML service unavailable, fallback to rule-based result only");
        }
    }

    private MlPredictionResponse fallback(String reason) {
        return MlPredictionResponse.builder()
                .fraudProbability(0.0)
                .prediction("UNAVAILABLE")
                .riskLevel("UNKNOWN")
                .reasons(java.util.List.of(reason))
                .modelVersion("fallback")
                .build();
    }
}