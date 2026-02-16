package com.cdl.epms.controller;

import com.cdl.epms.dto.annualReview.AnnualFinalSubmitRequestDto;
import com.cdl.epms.dto.annualReview.AnnualManagerReviewRequestDto;
import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.model.AnnualReview;
import com.cdl.epms.model.Goal;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.AnnualReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/annual-review")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class AnnualReviewController {

    private final AnnualReviewService annualReviewService;

    @GetMapping("/goals/{employeeId}/{year}")
    public ResponseEntity<ApiResponse<List<Goal>>> getAnnualGoals(
            @PathVariable String employeeId,
            @PathVariable Integer year
    ) {

        List<Goal> goals = annualReviewService.getAnnualGoals(employeeId, year);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Annual goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/submit/{employeeId}")
    public ResponseEntity<ApiResponse<AnnualReview>> submitSelfReview(
            @PathVariable String employeeId,
            @Valid @RequestBody AnnualReviewRequestDto dto
    ) {

        AnnualReview annualReview = annualReviewService.submitSelfReview(employeeId, dto);

        ApiResponse<AnnualReview> response = ApiResponse.<AnnualReview>builder()
                .success(true)
                .message("Self review submitted successfully")
                .data(annualReview)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/manager-review/{managerId}/{employeeId}")
    public ResponseEntity<ApiResponse<AnnualReview>> managerReview(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @Valid @RequestBody AnnualManagerReviewRequestDto dto
    ) {

        AnnualReview updatedReview = annualReviewService.updateManagerReview(
                managerId,
                employeeId,
                dto.getYear(),
                dto.getManagerRating(),
                dto.getManagerComment()
        );

        ApiResponse<AnnualReview> response = ApiResponse.<AnnualReview>builder()
                .success(true)
                .message("Manager review updated successfully")
                .data(updatedReview)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/submit-to-employee/{managerId}/{employeeId}")
    public ResponseEntity<ApiResponse<String>> submitToEmployee(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @Valid @RequestBody AnnualManagerReviewRequestDto dto
    ) {

        annualReviewService.submitToEmployee(managerId, employeeId, dto.getYear());

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Annual review submitted to employee successfully")
                .data("Submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/final-submit/{employeeId}")
    public ResponseEntity<ApiResponse<String>> finalSubmitToHR(
            @PathVariable String employeeId,
            @Valid @RequestBody AnnualFinalSubmitRequestDto dto
    ) {

        annualReviewService.finalSubmitToHR(employeeId, dto.getYear());

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Annual review final submitted to HR successfully")
                .data("Final submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}