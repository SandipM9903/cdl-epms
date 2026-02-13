package com.cdl.epms.controller;

import com.cdl.epms.dto.annualReview.AnnualManagerReviewRequestDto;
import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.model.AnnualReview;
import com.cdl.epms.model.Goal;
import com.cdl.epms.service.services.AnnualReviewService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annual-review")
@CrossOrigin(origins = "*")
public class AnnualReviewController {

    private final AnnualReviewService annualReviewService;

    public AnnualReviewController(AnnualReviewService annualReviewService) {
        this.annualReviewService = annualReviewService;
    }

    @GetMapping("/goals/{employeeId}/{year}")
    public ResponseEntity<List<Goal>> getAnnualGoals(
            @PathVariable String employeeId,
            @PathVariable Integer year
    ) {
        return ResponseEntity.ok(annualReviewService.getAnnualGoals(employeeId, year));
    }

    @PostMapping("/submit/{employeeId}")
    public ResponseEntity<AnnualReview> submitSelfReview(
            @PathVariable String employeeId,
            @RequestBody AnnualReviewRequestDto dto
    ) {
        return ResponseEntity.ok(annualReviewService.submitSelfReview(employeeId, dto));
    }

    @PutMapping("/manager-review/{managerId}/{employeeId}")
    public ResponseEntity<AnnualReview> managerReview(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @RequestBody AnnualManagerReviewRequestDto dto
    ) {
        AnnualReview updated = annualReviewService.updateManagerReview(
                managerId,
                employeeId,
                dto.getYear(),
                dto.getManagerRating(),
                dto.getManagerComment()
        );

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/submit-to-employee/{managerId}/{employeeId}")
    public ResponseEntity<String> submitToEmployee(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @RequestBody AnnualManagerReviewRequestDto dto
    ) {
        annualReviewService.submitToEmployee(managerId, employeeId, dto.getYear());
        return ResponseEntity.ok("Annual review submitted to employee successfully");
    }
}