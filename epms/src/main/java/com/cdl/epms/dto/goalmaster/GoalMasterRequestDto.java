package com.cdl.epms.dto.goalmaster;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GoalMasterRequestDto {

    @NotBlank(message = "Category is required")
    private String category;  // Can be "VALUES", "QUALITY", "KNOWLEDGE", "EXPERIENCE", "EMPOWER_ENGAGEMENT", etc.

    @NotBlank(message = "Differentiator name is required")
    private String differentiatorName;

    @NotBlank(message = "Definition is required")
    private String definition;

    private Boolean isActive = true;

    private Integer displayOrder;
}