package com.cdl.epms.dto.goal;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class SelfReviewRequestDto {

    @NotNull(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Quarter is required")
    private String quarter;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Goals list is required")
    private List<SelfReviewGoalDto> goals;

    @Data
    public static class SelfReviewGoalDto {

        @NotNull(message = "Goal ID is required")
        private Long id;

        // ✅ Editable by employee
        private String goalDescription;
        private String targetKPI;

        // ✅ Actual work done
        private String achievableTarget;

        @Min(value = 1, message = "Self assessment rating must be at least 1")
        @Max(value = 5, message = "Self assessment rating cannot be greater than 5")
        private Integer selfAssessmentRating;

        private String selfReviewComments;

        @Min(value = 1, message = "Overall rating must be at least 1")
        @Max(value = 5, message = "Overall rating cannot be greater than 5")
        private Integer overallSelfAssessmentRating;

        private String overallSelfReviewComments;
    }
}