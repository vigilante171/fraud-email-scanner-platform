package com.fraudscanner.scannerservice.controller;


import com.fraudscanner.scannerservice.dto.DistributionResponse;
import com.fraudscanner.scannerservice.dto.StatisticsSummaryResponse;
import com.fraudscanner.scannerservice.dto.TopSenderResponse;
import com.fraudscanner.scannerservice.service.StatisticsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/statistics")
@CrossOrigin(origins = "*")
public class StatisticsController {

    private final StatisticsService statisticsService;

    public StatisticsController(StatisticsService statisticsService) {
        this.statisticsService = statisticsService;
    }

    @GetMapping("/summary")
    public ResponseEntity<StatisticsSummaryResponse> getSummary() {
        return ResponseEntity.ok(statisticsService.getSummary());
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<List<DistributionResponse>> getStatusDistribution() {
        return ResponseEntity.ok(statisticsService.getStatusDistribution());
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<List<DistributionResponse>> getRiskDistribution() {
        return ResponseEntity.ok(statisticsService.getRiskDistribution());
    }

    @GetMapping("/top-flagged-senders")
    public ResponseEntity<List<TopSenderResponse>> getTopFlaggedSenders() {
        return ResponseEntity.ok(statisticsService.getTopFlaggedSenders());
    }
}