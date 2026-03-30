package com.cdl.epms.dto.annualReview;

import lombok.Data;
import java.util.List;

@Data
public class AnnualReviewRequestDto {

    private String employeeId;
    private String managerId;
    private Integer year;

    // Accomplishments
    private List<SelectedAccomplishmentDto> selectedAccomplishments;
    private List<AdditionalAccomplishmentDto> additionalAccomplishments;
    private List<CertificationDto> certifications;

    // Manager Review Fields
    private String nineBoxResult;
    private Boolean talentFlag;
    private Boolean criticalFlag;
    private String managerRemarks;
    private String managerRating;
    private String performanceRating;
    private String potentialRating;

    private String status; // DRAFT / SUBMITTED_TO_EMPLOYEE / SUBMITTED_TO_R1
}