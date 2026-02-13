package com.cdl.epms.service.services;

import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.model.AnnualReview;
import com.cdl.epms.model.Goal;

import java.util.List;

public interface AnnualReviewService {

    List<Goal> getAnnualGoals(String employeeId, Integer year);

    AnnualReview submitSelfReview(String employeeId, AnnualReviewRequestDto dto);

    AnnualReview updateManagerReview(String managerId, String employeeId, Integer year, Integer rating, String comment);

    void submitToEmployee(String managerId, String employeeId, Integer year);
}