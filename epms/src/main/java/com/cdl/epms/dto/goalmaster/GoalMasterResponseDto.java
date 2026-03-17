package com.cdl.epms.dto.goalmaster;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class GoalMasterResponseDto {
    private Long id;
    private String category;
    private String differentiatorName;
    private String definition;
    private Boolean isActive;
    private Integer displayOrder;
    private LocalDateTime createdDate;
    private LocalDateTime updatedDate;
}