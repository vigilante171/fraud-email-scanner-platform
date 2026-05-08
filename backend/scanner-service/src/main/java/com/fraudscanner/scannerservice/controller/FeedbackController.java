package com.fraudscanner.scannerservice.controller;


import com.fraudscanner.scannerservice.dto.FeedbackRequest;
import com.fraudscanner.scannerservice.service.FeedbackService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/feedback")
@CrossOrigin(origins = "*")
public class FeedbackController {

    private final FeedbackService feedbackService;

    public FeedbackController(FeedbackService feedbackService) {
        this.feedbackService = feedbackService;
    }

    @PostMapping
    public ResponseEntity<String> submitFeedback(@Valid @RequestBody FeedbackRequest request) {
        return ResponseEntity.ok(feedbackService.submitFeedback(request));
    }
}