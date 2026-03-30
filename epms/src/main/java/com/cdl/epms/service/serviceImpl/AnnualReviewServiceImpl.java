package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.AccomplishmentType;
import com.cdl.epms.common.enums.AnnualReviewStatus;
import com.cdl.epms.dto.annualReview.*;
import com.cdl.epms.model.*;
import com.cdl.epms.repository.*;
import com.cdl.epms.service.services.AnnualReviewService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnnualReviewServiceImpl implements AnnualReviewService {

    private final AnnualReviewRepository annualReviewRepository;
    private final AccomplishmentRepository accomplishmentRepository;
    private final CertificationRepository certificationRepository;

    @Override
    @Transactional
    public void saveDraft(AnnualReviewRequestDto dto) {
        if (dto.getManagerId() == null || dto.getManagerId().trim().isEmpty()) {
            log.error("Manager ID is required but was null for employee: {}", dto.getEmployeeId());
            throw new RuntimeException("Manager ID is required to save draft");
        }

        saveOrUpdate(dto, AnnualReviewStatus.DRAFT);
    }

    @Override
    @Transactional
    public void submitReview(AnnualReviewRequestDto dto) {
        if (dto.getManagerId() == null || dto.getManagerId().trim().isEmpty()) {
            log.error("Manager ID is required but was null for employee: {}", dto.getEmployeeId());
            throw new RuntimeException("Manager ID is required to submit review");
        }

        if ((dto.getSelectedAccomplishments() == null || dto.getSelectedAccomplishments().isEmpty()) &&
                (dto.getAdditionalAccomplishments() == null || dto.getAdditionalAccomplishments().isEmpty())) {
            throw new RuntimeException("At least one accomplishment required");
        }

        saveOrUpdate(dto, AnnualReviewStatus.SUBMITTED_TO_R1);
    }

    @Override
    @Transactional
    public void saveManagerDraft(UpdateAnnualReviewDto dto) {
        log.info("Saving manager draft for review ID: {}", dto.getId());

        AnnualReview review = annualReviewRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Annual review not found with ID: " + dto.getId()));

        updateManagerFields(review, dto);
        review.setStatus(AnnualReviewStatus.DRAFT);
        review.setUpdatedAt(LocalDateTime.now());

        annualReviewRepository.save(review);
        log.info("Manager draft saved successfully for review ID: {}", review.getId());
    }

    @Override
    @Transactional
    public void submitToEmployee(UpdateAnnualReviewDto dto) {
        log.info("Submitting manager review to employee for review ID: {}", dto.getId());

        // Validate required fields
        if (dto.getNineBoxResult() == null || dto.getNineBoxResult().trim().isEmpty()) {
            throw new RuntimeException("9-Box Matrix result is required");
        }

        if (dto.getManagerRemarks() == null || dto.getManagerRemarks().trim().isEmpty()) {
            throw new RuntimeException("Manager remarks are required");
        }

        AnnualReview review = annualReviewRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Annual review not found with ID: " + dto.getId()));

        updateManagerFields(review, dto);
        review.setStatus(AnnualReviewStatus.SUBMITTED_TO_EMPLOYEE);
        review.setManagerAnnualReviewSubmissionDate(LocalDateTime.now());
        review.setUpdatedAt(LocalDateTime.now());

        annualReviewRepository.save(review);
        log.info("Manager review submitted to employee successfully for review ID: {}", review.getId());
    }

    @Override
    @Transactional
    public Object updateManagerReview(UpdateAnnualReviewDto dto) {
        log.info("Updating manager review for review ID: {}", dto.getId());

        AnnualReview review = annualReviewRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Annual review not found with ID: " + dto.getId()));

        updateManagerFields(review, dto);
        review.setUpdatedAt(LocalDateTime.now());

        AnnualReview savedReview = annualReviewRepository.save(review);

        return getFullReviewResponse(savedReview);
    }

    @Override
    @Transactional
    public void submitToHr(HrSubmissionDto dto) {
        log.info("Submitting annual review to HR for review ID: {}, Employee: {}, DiscussedWithR1: {}",
                dto.getId(), dto.getEmployeeId(), dto.getDiscussedWithR1());

        // Validate that discussedWithR1 is true
        if (dto.getDiscussedWithR1() == null || !dto.getDiscussedWithR1()) {
            log.error("Cannot submit to HR: discussedWithR1 is false for review ID: {}", dto.getId());
            throw new RuntimeException("Must confirm discussion with R1 before submitting to HR");
        }

        AnnualReview review = annualReviewRepository.findById(dto.getId())
                .orElseThrow(() -> new RuntimeException("Annual review not found with ID: " + dto.getId()));

        // Verify status
        if (review.getStatus() != AnnualReviewStatus.SUBMITTED_TO_EMPLOYEE) {
            log.error("Cannot submit to HR: Invalid status {} for review ID: {}", review.getStatus(), dto.getId());
            throw new RuntimeException("Review must be submitted to employee before submitting to HR. Current status: " + review.getStatus());
        }

        // Update HR submission fields - SET discussedWithR1 to TRUE
        review.setDiscussedWithR1(true); // Always set to true when submitting to HR
        review.setEmployeeComment(dto.getEmployeeComment() != null ? dto.getEmployeeComment() : false);
        if (dto.getEmployeeCommentText() != null && !dto.getEmployeeCommentText().trim().isEmpty()) {
            review.setEmployeeCommentText(dto.getEmployeeCommentText());
        }
        review.setSubmittedToHrDate(LocalDateTime.now());
        review.setSubmittedToHrBy(dto.getSubmittedBy() != null ? dto.getSubmittedBy() : dto.getEmployeeId());
        review.setStatus(AnnualReviewStatus.SUBMITTED_TO_HR);
        review.setUpdatedAt(LocalDateTime.now());

        AnnualReview savedReview = annualReviewRepository.save(review);
        log.info("Annual review submitted to HR successfully for review ID: {}, discussedWithR1 set to: {}",
                savedReview.getId(), savedReview.getDiscussedWithR1());
    }

    @Override
    public Object getHrReviews(Integer year) {
        log.info("Fetching HR reviews for year: {}", year);

        List<AnnualReview> reviews;
        if (year != null) {
            // If year is provided, fetch reviews for that specific year
            reviews = annualReviewRepository.findAll().stream()
                    .filter(r -> r.getYear().equals(year) &&
                            r.getStatus() == AnnualReviewStatus.SUBMITTED_TO_HR)
                    .collect(Collectors.toList());
        } else {
            // Otherwise fetch all submitted to HR reviews
            reviews = annualReviewRepository.findByStatus(AnnualReviewStatus.SUBMITTED_TO_HR);
        }

        List<Map<String, Object>> hrReviewList = new ArrayList<>();
        for (AnnualReview review : reviews) {
            Map<String, Object> hrReview = new HashMap<>();
            hrReview.put("id", review.getId());
            hrReview.put("employeeId", review.getEmployeeId());
            hrReview.put("year", review.getYear());
            hrReview.put("status", review.getStatus());
            hrReview.put("managerRating", review.getManagerRating());
            hrReview.put("nineBoxResult", review.getNineBoxResult());
            hrReview.put("submittedToHrDate", review.getSubmittedToHrDate());
            hrReview.put("discussedWithR1", review.getDiscussedWithR1());
            hrReview.put("employeeComment", review.getEmployeeComment());
            hrReview.put("employeeCommentText", review.getEmployeeCommentText());
            hrReview.put("submittedToHrBy", review.getSubmittedToHrBy());
            hrReviewList.add(hrReview);
        }

        return hrReviewList;
    }

    @Override
    public Object getHrReviewDetails(Long reviewId) {
        log.info("Fetching HR review details for review ID: {}", reviewId);

        AnnualReview review = annualReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        return getFullReviewResponse(review);
    }

    @Override
    @Transactional
    public void hrApproveOrReject(Long reviewId, Boolean approved, String hrRemarks) {
        log.info("HR {} review for review ID: {}", approved ? "approving" : "rejecting", reviewId);

        AnnualReview review = annualReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        review.setHrRemarks(hrRemarks);

        if (approved) {
            review.setStatus(AnnualReviewStatus.COMPLETED);
            log.info("Annual review completed and approved for review ID: {}", reviewId);
        } else {
            review.setStatus(AnnualReviewStatus.SUBMITTED_TO_EMPLOYEE);
            log.info("Annual review rejected and sent back to employee for review ID: {}", reviewId);
        }

        review.setUpdatedAt(LocalDateTime.now());
        annualReviewRepository.save(review);
    }

    @Override
    @Transactional
    public void sendBackToR1(Long reviewId, String remarks, Boolean discussedWithR1) {
        log.info("Sending annual review back to R1 for review ID: {}, discussedWithR1: {}", reviewId, discussedWithR1);

        AnnualReview review = annualReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        // Clear HR submission fields
        review.setDiscussedWithR1(discussedWithR1 != null ? discussedWithR1 : false);
        review.setEmployeeComment(false);
        review.setEmployeeCommentText(null);
        review.setSubmittedToHrDate(null);
        review.setSubmittedToHrBy(null);
        review.setHrRemarks(remarks);
        review.setStatus(AnnualReviewStatus.SUBMITTED_TO_R1);
        review.setUpdatedAt(LocalDateTime.now());

        annualReviewRepository.save(review);
        log.info("Annual review sent back to R1 for review ID: {}", reviewId);
    }

    private void updateManagerFields(AnnualReview review, UpdateAnnualReviewDto dto) {
        if (dto.getNineBoxResult() != null) {
            review.setNineBoxResult(dto.getNineBoxResult());
        }
        if (dto.getTalentFlag() != null) {
            review.setTalentFlag(dto.getTalentFlag());
        }
        if (dto.getCriticalFlag() != null) {
            review.setCriticalFlag(dto.getCriticalFlag());
        }
        if (dto.getManagerRemarks() != null) {
            review.setManagerRemarks(dto.getManagerRemarks());
        }
        if (dto.getManagerRating() != null) {
            review.setManagerRating(dto.getManagerRating());
        }
        if (dto.getPerformanceRating() != null) {
            review.setPerformanceRating(dto.getPerformanceRating());
        }
        if (dto.getPotentialRating() != null) {
            review.setPotentialRating(dto.getPotentialRating());
        }
    }

    private void saveOrUpdate(AnnualReviewRequestDto dto, AnnualReviewStatus status) {
        log.info("Saving annual review for employee: {}, year: {}, status: {}, managerId: {}",
                dto.getEmployeeId(), dto.getYear(), status, dto.getManagerId());

        AnnualReview review = annualReviewRepository
                .findByEmployeeIdAndYear(dto.getEmployeeId(), dto.getYear())
                .orElse(new AnnualReview());

        review.setEmployeeId(dto.getEmployeeId());
        review.setManagerId(dto.getManagerId());
        review.setYear(dto.getYear());
        review.setStatus(status);

        if (status == AnnualReviewStatus.SUBMITTED_TO_R1) {
            review.setSubmittedAt(LocalDateTime.now());
        }

        final AnnualReview savedReview = annualReviewRepository.save(review);
        log.debug("Saved annual review with ID: {}, managerId: {}", savedReview.getId(), savedReview.getManagerId());

        // Clear old children
        accomplishmentRepository.deleteByAnnualReviewId(savedReview.getId());
        certificationRepository.deleteByAnnualReviewId(savedReview.getId());

        // Save accomplishments
        List<Accomplishment> accomplishmentsToSave = new ArrayList<>();

        if (dto.getSelectedAccomplishments() != null && !dto.getSelectedAccomplishments().isEmpty()) {
            dto.getSelectedAccomplishments().forEach(accDto -> {
                accomplishmentsToSave.add(Accomplishment.builder()
                        .annualReviewId(savedReview.getId())
                        .goalId(accDto.getGoalId())
                        .title(accDto.getTitle())
                        .description(accDto.getDescription())
                        .quarter(accDto.getQuarter())
                        .type(AccomplishmentType.SELECTED)
                        .build());
            });
        }

        if (dto.getAdditionalAccomplishments() != null && !dto.getAdditionalAccomplishments().isEmpty()) {
            dto.getAdditionalAccomplishments().forEach(accDto -> {
                accomplishmentsToSave.add(Accomplishment.builder()
                        .annualReviewId(savedReview.getId())
                        .title(accDto.getTitle())
                        .quarter(accDto.getQuarter())
                        .type(AccomplishmentType.ADDITIONAL)
                        .build());
            });
        }

        if (!accomplishmentsToSave.isEmpty()) {
            accomplishmentRepository.saveAll(accomplishmentsToSave);
        }

        // Save certifications
        if (dto.getCertifications() != null && !dto.getCertifications().isEmpty()) {
            List<Certification> certs = dto.getCertifications().stream()
                    .filter(certDto -> certDto.getName() != null && !certDto.getName().trim().isEmpty())
                    .map(certDto -> Certification.builder()
                            .annualReviewId(savedReview.getId())
                            .name(certDto.getName())
                            .type(certDto.getType())
                            .fileName(certDto.getFile())
                            .build())
                    .collect(Collectors.toList());

            if (!certs.isEmpty()) {
                certificationRepository.saveAll(certs);
            }
        }

        log.info("Successfully saved annual review with ID: {}", savedReview.getId());
    }

    @Override
    public Object getDraft(String employeeId, Integer year) {
        log.info("Fetching draft for employee: {}, year: {}", employeeId, year);

        AnnualReview review = annualReviewRepository
                .findByEmployeeIdAndYear(employeeId, year)
                .orElse(null);

        if (review == null) {
            return Map.of(
                    "success", false,
                    "message", "No draft found",
                    "data", null
            );
        }

        List<Accomplishment> accList = accomplishmentRepository.findByAnnualReviewId(review.getId());
        List<Certification> certList = certificationRepository.findByAnnualReviewId(review.getId());

        List<Map<String, Object>> selected = new ArrayList<>();
        List<Map<String, Object>> additional = new ArrayList<>();

        for (Accomplishment acc : accList) {
            if (acc.getType() == AccomplishmentType.SELECTED) {
                selected.add(Map.of(
                        "goalId", acc.getGoalId(),
                        "title", acc.getTitle(),
                        "description", acc.getDescription(),
                        "quarter", acc.getQuarter()
                ));
            } else {
                additional.add(Map.of(
                        "title", acc.getTitle(),
                        "quarter", acc.getQuarter()
                ));
            }
        }

        List<Map<String, Object>> certifications = new ArrayList<>();
        for (Certification cert : certList) {
            certifications.add(Map.of(
                    "name", cert.getName(),
                    "type", cert.getType(),
                    "file", cert.getFileName()
            ));
        }

        return Map.of(
                "success", true,
                "data", Map.of(
                        "id", review.getId(),
                        "managerId", review.getManagerId(),
                        "selectedAccomplishments", selected,
                        "additionalAccomplishments", additional,
                        "certifications", certifications
                )
        );
    }

    @Override
    public Object getFullReview(String employeeId, Integer year) {
        AnnualReview review = annualReviewRepository
                .findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return getFullReviewResponse(review);
    }

    @Override
    public Object getManagerReview(String employeeId, Integer year) {
        AnnualReview review = annualReviewRepository
                .findByEmployeeIdAndYear(employeeId, year)
                .orElseThrow(() -> new RuntimeException("Review not found"));

        return getManagerReviewResponse(review);
    }

    @Override
    public Object getManagerDraft(Long reviewId) {
        AnnualReview review = annualReviewRepository.findById(reviewId)
                .orElseThrow(() -> new RuntimeException("Review not found with ID: " + reviewId));

        return getManagerReviewResponse(review);
    }

    private Object getFullReviewResponse(AnnualReview review) {
        List<Accomplishment> accList = accomplishmentRepository.findByAnnualReviewId(review.getId());
        List<Certification> certList = certificationRepository.findByAnnualReviewId(review.getId());

        List<Map<String, Object>> selected = new ArrayList<>();
        List<Map<String, Object>> additional = new ArrayList<>();

        for (Accomplishment acc : accList) {
            Map<String, Object> map = new HashMap<>();
            map.put("id", acc.getId());
            map.put("title", acc.getTitle());
            map.put("quarter", acc.getQuarter());
            map.put("type", acc.getType());

            if (acc.getType() == AccomplishmentType.SELECTED) {
                map.put("goalId", acc.getGoalId());
                map.put("description", acc.getDescription());
                selected.add(map);
            } else {
                additional.add(map);
            }
        }

        List<Map<String, Object>> certifications = certList.stream()
                .map(cert -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("id", cert.getId());
                    map.put("name", cert.getName());
                    map.put("type", cert.getType());
                    map.put("file", cert.getFileName());
                    return map;
                })
                .collect(Collectors.toList());

        Map<String, Object> response = new HashMap<>();
        response.put("id", review.getId());
        response.put("employeeId", review.getEmployeeId());
        response.put("managerId", review.getManagerId());
        response.put("year", review.getYear());
        response.put("status", review.getStatus());
        response.put("nineBoxResult", review.getNineBoxResult());
        response.put("talentFlag", review.getTalentFlag());
        response.put("criticalFlag", review.getCriticalFlag());
        response.put("managerRemarks", review.getManagerRemarks());
        response.put("managerRating", review.getManagerRating());
        response.put("performanceRating", review.getPerformanceRating());
        response.put("potentialRating", review.getPotentialRating());
        response.put("submittedAt", review.getSubmittedAt());
        response.put("managerAnnualReviewSubmissionDate", review.getManagerAnnualReviewSubmissionDate());
        response.put("discussedWithR1", review.getDiscussedWithR1());
        response.put("employeeComment", review.getEmployeeComment());
        response.put("employeeCommentText", review.getEmployeeCommentText());
        response.put("submittedToHrDate", review.getSubmittedToHrDate());
        response.put("submittedToHrBy", review.getSubmittedToHrBy());
        response.put("hrRemarks", review.getHrRemarks());
        response.put("createdAt", review.getCreatedAt());
        response.put("updatedAt", review.getUpdatedAt());
        response.put("selectedAccomplishments", selected);
        response.put("additionalAccomplishments", additional);
        response.put("certifications", certifications);

        return response;
    }

    private Object getManagerReviewResponse(AnnualReview review) {
        List<Accomplishment> accList = accomplishmentRepository.findByAnnualReviewId(review.getId());

        List<Map<String, Object>> selected = new ArrayList<>();

        for (Accomplishment acc : accList) {
            if (acc.getType() == AccomplishmentType.SELECTED) {
                Map<String, Object> map = new HashMap<>();
                map.put("id", acc.getId());
                map.put("goalId", acc.getGoalId());
                map.put("title", acc.getTitle());
                map.put("description", acc.getDescription());
                map.put("quarter", acc.getQuarter());
                selected.add(map);
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("id", review.getId());
        response.put("employeeId", review.getEmployeeId());
        response.put("managerId", review.getManagerId());
        response.put("year", review.getYear());
        response.put("status", review.getStatus());
        response.put("nineBoxResult", review.getNineBoxResult());
        response.put("talentFlag", review.getTalentFlag());
        response.put("criticalFlag", review.getCriticalFlag());
        response.put("managerRemarks", review.getManagerRemarks());
        response.put("managerRating", review.getManagerRating());
        response.put("performanceRating", review.getPerformanceRating());
        response.put("potentialRating", review.getPotentialRating());
        response.put("managerAnnualReviewSubmissionDate", review.getManagerAnnualReviewSubmissionDate());
        response.put("selectedAccomplishments", selected);

        return response;
    }
}