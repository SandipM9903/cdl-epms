package com.cdl.epms.dto.goal;

import com.cdl.epms.common.enums.Quarter;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class AssignPredefinedGoalsRequestDto {

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @NotBlank(message = "Manager ID is required")
    private String managerId;

    @NotNull(message = "Quarter is required")
    private Quarter quarter;

    @NotNull(message = "Year is required")
    private Integer year;

    @NotNull(message = "At least one goal must be selected")
    private List<Long> goalMasterIds;

    private String createdBy;
}