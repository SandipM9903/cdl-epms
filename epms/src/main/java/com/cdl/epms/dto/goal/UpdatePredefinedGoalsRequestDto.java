package com.cdl.epms.dto.goal;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UpdatePredefinedGoalsRequestDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotNull(message = "Quarter is required")
    private String quarter;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "Goals list is required")
    private List<GoalUpdateDto> goals;

    @Data
    public static class GoalUpdateDto {
        @NotNull(message = "Goal ID is required")
        private Long id;

        private String goalDescription;

        private String targetKPI;

        @NotNull(message = "Weightage is required")
        private Integer weightage;

        private List<String> timeline;
    }
}