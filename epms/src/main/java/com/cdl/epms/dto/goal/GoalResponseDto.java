package com.cdl.epms.dto.goal;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.persistence.Column;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
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

    private String goalCategory;
    private String goalDescription;


    private Quarter quarter;
    private Integer year;

    private Integer managerRating;
    private String managerComment;

    private LocalDateTime submittedToEmployeeAt;
    private String targetKPI;

    // Self-Review Fields
    private String selfReview;
    private LocalDateTime selfReviewSubmittedDate;
    private String achievableTarget;
    private Integer selfAssessmentRating;
    private String selfReviewComments;
    private Integer overallSelfAssessmentRating;
    private String overallSelfReviewComments;

    //Manager review fields
    private Integer managerOverallSelfAssessmentRating;
    private String managerOverallSelfReviewComments;
    private String achievementLevel;
    private String potential;
    private String performance;
    private String talentOrCriticalResource;
    private LocalDateTime reviewedAt;
    private String talentMatrixCategory;
    private LocalDateTime selfAcceptedDate;
}