package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import com.cdl.epms.dto.annualReview.AnnualReviewRequestDto;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.UnauthorizedException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.AnnualReview;
import com.cdl.epms.model.Goal;
import com.cdl.epms.repository.AnnualReviewRepository;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.service.services.AnnualReviewService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AnnualReviewServiceImpl implements AnnualReviewService {

    private final GoalRepository goalRepository;
    private final AnnualReviewRepository annualReviewRepository;
    private final ModelMapper modelMapper;

    @Override
    public List<Goal> getAnnualGoals(String employeeId, Integer year) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycle_Year(employeeId, year);

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found for this employee in the given year");
        }

        return goals;
    }

    @Override
    public AnnualReview submitSelfReview(String employeeId, AnnualReviewRequestDto dto) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (dto == null) {
            throw new ValidationException("Request body is required");
        }

        if (dto.getYear() == null) {
            throw new ValidationException("Year is required");
        }

        if (dto.getManagerId() == null || dto.getManagerId().trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (dto.getSelfRating() == null || dto.getSelfRating() < 1 || dto.getSelfRating() > 5) {
            throw new ValidationException("Self rating must be between 1 and 5");
        }

        if (annualReviewRepository.existsByEmployeeIdAndYear(employeeId, dto.getYear())) {
            throw new ConflictException("Annual self review already submitted for this year");
        }

        AnnualReview review = modelMapper.map(dto, AnnualReview.class);

        review.setEmployeeId(employeeId);
        review.setStatus(AnnualReviewStatus.SELF_SUBMITTED);
        review.setSubmittedAt(LocalDateTime.now());

        return annualReviewRepository.save(review);
    }

    @Override
    public AnnualReview updateManagerReview(String managerId, String employeeId, Integer year,
                                            Integer rating, String comment) {

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (year == null) {
            throw new ValidationException("Year is required");
        }

        if (rating == null || rating < 1 || rating > 5) {
            throw new ValidationException("Manager rating must be between 1 and 5");
        }

        AnnualReview review = annualReviewRepository.findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Annual review not found for employee"));

        if (!review.getManagerId().equals(managerId)) {
            throw new UnauthorizedException("You are not authorized to review this employee");
        }

        if (review.getStatus() != AnnualReviewStatus.SELF_SUBMITTED) {
            throw new ConflictException("Annual review is not in SELF_SUBMITTED status");
        }

        review.setManagerRating(rating);
        review.setManagerComment(comment);
        review.setStatus(AnnualReviewStatus.MANAGER_REVIEWED);
        review.setManagerReviewedAt(LocalDateTime.now());

        return annualReviewRepository.save(review);
    }

    @Override
    public void submitToEmployee(String managerId, String employeeId, Integer year) {

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (year == null) {
            throw new ValidationException("Year is required");
        }

        AnnualReview review = annualReviewRepository.findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Annual review not found for employee"));

        if (!review.getManagerId().equals(managerId)) {
            throw new UnauthorizedException("You are not authorized to submit this employee review");
        }

        if (review.getStatus() != AnnualReviewStatus.MANAGER_REVIEWED) {
            throw new ConflictException("Manager review is not completed yet");
        }

        if (review.getManagerRating() == null) {
            throw new ValidationException("Manager rating is required before submission");
        }

        review.setStatus(AnnualReviewStatus.SENT_TO_EMPLOYEE);
        review.setSubmittedToEmployeeAt(LocalDateTime.now());

        annualReviewRepository.save(review);
    }

    @Override
    public void finalSubmitToHR(String employeeId, Integer year) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (year == null) {
            throw new ValidationException("Year is required");
        }

        AnnualReview review = annualReviewRepository.findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new ResourceNotFoundException("Annual review not found for employee"));

        if (review.getStatus() != AnnualReviewStatus.SENT_TO_EMPLOYEE) {
            throw new ConflictException("Annual review is not submitted to employee yet");
        }

        review.setStatus(AnnualReviewStatus.FINAL_SUBMITTED_TO_HR);

        annualReviewRepository.save(review);
    }
}