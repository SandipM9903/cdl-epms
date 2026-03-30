package com.cdl.epms.dto.goal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class ManagerReviewRequestDto {

    @NotNull
    private String managerId;

    @NotNull
    private String employeeId;

    @NotNull
    private String quarter;

    @NotNull
    private Integer year;
    private String achievementLevel;
    private String potential;
    private String performance;
    private String talentOrCriticalResource;

    @Min(1)
    @Max(5)
    private Integer managerOverallSelfAssessmentRating;

    private String managerOverallSelfReviewComments;

    @NotNull
    private List<ManagerReviewGoalDto> goals;

    @Data
    public static class ManagerReviewGoalDto {
        @NotNull
        private Long id;
    }
}