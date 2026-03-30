package com.cdl.epms.dto.goal;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.util.List;

@Data
public class UpdatePredefinedGoalsRequestDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Quarter is required")
    private String quarter;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Goals list is required")
    private List<GoalUpdateDto> goals;

    private boolean saveAsDraft = false; // Flag to indicate if this is a draft save

    @Data
    public static class GoalUpdateDto {
        @NotNull(message = "Goal ID is required")
        private Long id;

        private String goalDescription;

        private String targetKPI;

        @Min(value = 0, message = "Weightage must be at least 0")
        @Max(value = 100, message = "Weightage cannot be more than 100")
        private Integer weightage;

        private List<String> timeline;

        // NEW fields for draft saving
        private String achievableTarget;

        private String selfReviewComments;
    }
}