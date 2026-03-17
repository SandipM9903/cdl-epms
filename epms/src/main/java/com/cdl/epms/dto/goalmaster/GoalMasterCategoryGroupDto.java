package com.cdl.epms.dto.goalmaster;

import lombok.Data;
import java.util.List;

@Data
public class GoalMasterCategoryGroupDto {
    private String category;
    private List<GoalMasterResponseDto> items;
}