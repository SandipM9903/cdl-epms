package com.cdl.epms.dto.hr;

import lombok.Data;

@Data
public class HrDashboardResponseDto {

    private Long cycleId;
    private Integer year;
    private String cycleType;
    private String quarter;
    private String status;

    private long totalGoals;
    private long totalEmployees;

    private long predefinedGoals;
    private long smartGoals;
    private long developmentGoals;
}