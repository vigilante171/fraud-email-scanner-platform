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
    public ResponseEntity<StatisticsSummaryResponse> getSummary(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(statisticsService.getSummary(userId, role));
    }

    @GetMapping("/status-distribution")
    public ResponseEntity<List<DistributionResponse>> getStatusDistribution(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(statisticsService.getStatusDistribution(userId, role));
    }

    @GetMapping("/risk-distribution")
    public ResponseEntity<List<DistributionResponse>> getRiskDistribution(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(statisticsService.getRiskDistribution(userId, role));
    }

    @GetMapping("/top-flagged-senders")
    public ResponseEntity<List<TopSenderResponse>> getTopFlaggedSenders(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false, defaultValue = "USER") String role
    ) {
        return ResponseEntity.ok(statisticsService.getTopFlaggedSenders(userId, role));
    }
}