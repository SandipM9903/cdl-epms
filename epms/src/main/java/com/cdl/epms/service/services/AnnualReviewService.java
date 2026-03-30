package com.cdl.epms.service.services;

import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.dto.annualReview.HrSubmissionDto;
import com.cdl.epms.dto.annualReview.UpdateAnnualReviewDto;

public interface AnnualReviewService {
    void saveDraft(AnnualReviewRequestDto dto);
    void submitReview(AnnualReviewRequestDto dto);
    void saveManagerDraft(UpdateAnnualReviewDto dto);
    void submitToEmployee(UpdateAnnualReviewDto dto);
    Object updateManagerReview(UpdateAnnualReviewDto dto);
    void submitToHr(HrSubmissionDto dto);
    Object getHrReviews(Integer year);
    Object getHrReviewDetails(Long reviewId);
    void hrApproveOrReject(Long reviewId, Boolean approved, String hrRemarks);
    void sendBackToR1(Long reviewId, String remarks, Boolean discussedWithR1);
    Object getDraft(String employeeId, Integer year);
    Object getFullReview(String employeeId, Integer year);
    Object getManagerReview(String employeeId, Integer year);
    Object getManagerDraft(Long reviewId);
}