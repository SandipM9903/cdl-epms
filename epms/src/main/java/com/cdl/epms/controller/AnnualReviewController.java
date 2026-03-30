package com.cdl.epms.controller;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.dto.annualReview.HrSubmissionDto;
import com.cdl.epms.dto.annualReview.UpdateAnnualReviewDto;
import com.cdl.epms.model.AnnualReview;
import com.cdl.epms.repository.AnnualReviewRepository;
import com.cdl.epms.service.services.AnnualReviewService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api/annual-review")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AnnualReviewController {

    private final AnnualReviewService annualReviewService;
    private final AnnualReviewRepository annualReviewRepository;

    // Employee endpoints
    @PostMapping("/draft/save")
    public ResponseEntity<?> saveDraft(@RequestBody AnnualReviewRequestDto dto) {
        annualReviewService.saveDraft(dto);
        return ResponseEntity.ok("Draft saved successfully");
    }

    @PostMapping("/submit")
    public ResponseEntity<?> submitReview(@RequestBody AnnualReviewRequestDto dto) {
        annualReviewService.submitReview(dto);
        return ResponseEntity.ok("Submitted to R1 successfully");
    }

    @GetMapping("/draft/employee/{empId}")
    public ResponseEntity<?> getDraft(
            @PathVariable String empId,
            @RequestParam Integer year) {
        return ResponseEntity.ok(annualReviewService.getDraft(empId, year));
    }

    @GetMapping("/{empId}/{year}")
    public ResponseEntity<?> getFullReview(
            @PathVariable String empId,
            @PathVariable Integer year) {
        return ResponseEntity.ok(annualReviewService.getFullReview(empId, year));
    }

    // Manager endpoints
    @GetMapping("/manager/draft/{reviewId}")
    public ResponseEntity<?> getManagerDraft(@PathVariable Long reviewId) {
        return ResponseEntity.ok(annualReviewService.getManagerDraft(reviewId));
    }

    @GetMapping("/manager/{empId}/{year}")
    public ResponseEntity<?> getManagerReview(
            @PathVariable String empId,
            @PathVariable Integer year) {
        return ResponseEntity.ok(annualReviewService.getManagerReview(empId, year));
    }

    @PutMapping("/manager/draft/save")
    public ResponseEntity<?> saveManagerDraft(@RequestBody UpdateAnnualReviewDto dto) {
        annualReviewService.saveManagerDraft(dto);
        return ResponseEntity.ok("Manager draft saved successfully");
    }

    @PutMapping("/manager/submit-to-employee")
    public ResponseEntity<?> submitToEmployee(@RequestBody UpdateAnnualReviewDto dto) {
        annualReviewService.submitToEmployee(dto);
        return ResponseEntity.ok("Manager review submitted to employee successfully");
    }

    @PutMapping("/manager/update")
    public ResponseEntity<?> updateManagerReview(@RequestBody UpdateAnnualReviewDto dto) {
        return ResponseEntity.ok(annualReviewService.updateManagerReview(dto));
    }

    // HR Submission endpoints
    @PutMapping("/submit-to-hr/{reviewId}")
    public ResponseEntity<?> submitToHr(@PathVariable Long reviewId, @RequestBody HrSubmissionDto dto) {
        dto.setId(reviewId);
        annualReviewService.submitToHr(dto);
        return ResponseEntity.ok("Review submitted to HR successfully");
    }

    @GetMapping("/hr/reviews")
    public ResponseEntity<?> getHrReviews(@RequestParam(required = false) Integer year) {
        return ResponseEntity.ok(annualReviewService.getHrReviews(year));
    }

    @GetMapping("/hr/review/{reviewId}")
    public ResponseEntity<?> getHrReviewDetails(@PathVariable Long reviewId) {
        return ResponseEntity.ok(annualReviewService.getHrReviewDetails(reviewId));
    }

    @PutMapping("/hr/approve/{reviewId}")
    public ResponseEntity<?> hrApproveReview(
            @PathVariable Long reviewId,
            @RequestParam Boolean approved,
            @RequestParam(required = false) String remarks) {
        annualReviewService.hrApproveOrReject(reviewId, approved, remarks);
        return ResponseEntity.ok(approved ? "Review approved successfully" : "Review rejected successfully");
    }

    @PutMapping("/send-back-to-r1/{reviewId}")
    public ResponseEntity<?> sendBackToR1(
            @PathVariable Long reviewId,
            @RequestParam(required = false) String remarks,
            @RequestParam(required = false) Boolean discussedWithR1) {
        annualReviewService.sendBackToR1(reviewId, remarks, discussedWithR1);
        return ResponseEntity.ok("Review sent back to R1 for further discussion");
    }

    @PutMapping("/update-discussion-status/{reviewId}")
    public ResponseEntity<?> updateDiscussionStatus(
            @PathVariable Long reviewId,
            @RequestBody Map<String, Object> payload) {
        Boolean discussedWithR1 = (Boolean) payload.get("discussedWithR1");
        String status = (String) payload.get("status");

        AnnualReview review = annualReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        if (discussedWithR1 != null) {
            review.setDiscussedWithR1(discussedWithR1);
        }
        if (status != null) {
            review.setStatus(AnnualReviewStatus.valueOf(status));
        }
        review.setUpdatedAt(LocalDateTime.now());

        annualReviewRepository.save(review);
        return ResponseEntity.ok("Discussion status updated successfully");
    }
}