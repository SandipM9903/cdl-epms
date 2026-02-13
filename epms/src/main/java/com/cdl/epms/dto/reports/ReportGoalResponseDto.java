package com.cdl.epms.dto.reports;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import lombok.Data;

@Data
public class ReportGoalResponseDto {

    private Long goalId;
    private String employeeId;
    private String managerId;

    private Integer year;
    private Quarter quarter;

    private GoalType goalType;
    private String title;
    private String description;
    private Integer weightage;

    private GoalStatus status;

    private Integer managerRating;
    private String managerComment;
}