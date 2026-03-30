package com.cdl.epms.dto.goal;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class SmartGoalWithReviewRequestDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String managerId;

    @NotBlank(message = "Goal title is required")
    @Size(max = 255, message = "Title cannot be more than 255 characters")
    private String title;

    @Size(max = 1000, message = "Goal description cannot be more than 1000 characters")
    private String goalDescription;

    @Size(max = 500, message = "Target KPI cannot be more than 500 characters")
    private String targetKPI;

    @Min(value = 0, message = "Weightage must be at least 0")
    @Max(value = 100, message = "Weightage cannot be more than 100")
    private Integer weightage = 0;

    @Size(max = 1000, message = "Achievable target cannot be more than 1000 characters")
    private String achievableTarget;

    @Size(max = 1000, message = "Self review comments cannot be more than 1000 characters")
    private String selfReviewComments;

    @NotNull(message = "Overall rating is required")
    @Min(value = 1, message = "Rating must be at least 1")
    @Max(value = 5, message = "Rating cannot be greater than 5")
    private Integer overallSelfAssessmentRating;

    @NotBlank(message = "Overall comment is required")
    @Size(max = 1000, message = "Overall comment cannot be more than 1000 characters")
    private String overallSelfReviewComments;
}