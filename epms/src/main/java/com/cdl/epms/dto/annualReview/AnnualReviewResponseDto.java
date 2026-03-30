package com.cdl.epms.dto.annualReview;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
public class AnnualReviewResponseDto {
    private Long id;
    private String employeeId;
    private String managerId;
    private Integer year;
    private String status;
    private String nineBoxResult;
    private Boolean talentFlag;
    private Boolean criticalFlag;
    private String managerRemarks;
    private String managerRating;
    private String performanceRating;
    private String potentialRating;
    private LocalDateTime submittedAt;
    private LocalDateTime managerAnnualReviewSubmissionDate;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<Map<String, Object>> selectedAccomplishments;
    private List<Map<String, Object>> additionalAccomplishments;
    private List<Map<String, Object>> certifications;
}