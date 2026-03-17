package com.cdl.epms.dto.goal;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class GoalResponseDto {

    private Long id;
    private String employeeId;
    private String managerId;

    private String title;
    private String description;

    private Integer weightage;

    private GoalType goalType;
    private GoalStatus status;

    private Quarter quarter;
    private Integer year;

    private Integer managerRating;
    private String managerComment;

    private LocalDateTime submittedToEmployeeAt;
}