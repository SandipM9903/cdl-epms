package com.cdl.epms.dto.goal;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class GoalRequestDto {

    @NotBlank
    private String employeeId;

    @NotBlank
    private String managerId;

    @NotBlank
    private String title;

    private String description;

    @NotNull
    @Min(1)
    @Max(100)
    private Integer weightage;
}