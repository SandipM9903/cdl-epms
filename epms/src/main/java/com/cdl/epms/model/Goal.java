package com.cdl.epms.model;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Entity
@Table(name = "goal")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Goal {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Performance cycle cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    private PerformanceCycle performanceCycle;

    @NotNull(message = "Year cannot be null.")
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull(message = "Quarter cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "quarter", nullable = false)
    private Quarter quarter;

    @NotBlank(message = "Employee ID cannot be empty.")
    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @NotBlank(message = "Manager ID cannot be empty.")
    @Column(name = "manager_id", nullable = false)
    private String managerId;

    @NotNull(message = "Goal type cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "goal_type", nullable = false)
    private GoalType goalType;

    @NotBlank(message = "Title cannot be empty.")
    @Size(max = 255, message = "Title cannot be more than 255 characters.")
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Min(value = 0, message = "Weightage must be at least 0.")
    @Max(value = 100, message = "Weightage cannot be more than 100.")
    @Column(name = "weightage", nullable = true)
    private Integer weightage;

    @NotNull(message = "Goal status cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private GoalStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Min(value = 1, message = "Manager rating must be at least 1.")
    @Max(value = 5, message = "Manager rating cannot be greater than 5.")
    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "manager_comment", columnDefinition = "TEXT")
    private String managerComment;

    @Column(name = "reviewed_at")
    private LocalDateTime reviewedAt;

    @Column(name = "goal_category")
    private String goalCategory;

    @Column(name = "submitted_to_employee_at")
    private LocalDateTime submittedToEmployeeAt;

    // Self-Review Fields
    @Column(name = "self_review", columnDefinition = "TEXT")
    private String selfReview;

    @Column(name = "self_review_submitted_date")
    private LocalDateTime selfReviewSubmittedDate;

    @Column(name = "achievable_target", columnDefinition = "TEXT")
    private String achievableTarget;

    @Min(value = 1, message = "Self assessment rating must be at least 1.")
    @Max(value = 5, message = "Self assessment rating cannot be greater than 5.")
    @Column(name = "self_assessment_rating")
    private Integer selfAssessmentRating;

    @Min(value = 1, message = "Self assessment rating must be at least 1.")
    @Max(value = 5, message = "Self assessment rating cannot be greater than 5.")
    @Column(name = "overall_self_assessment_rating")
    private Integer overallSelfAssessmentRating;
    @Column(name = "overall_self_review_comments", columnDefinition = "TEXT")
    private String overallSelfReviewComments;

    @Column(name = "self_review_comments", columnDefinition = "TEXT")
    private String selfReviewComments;

    @Min(value = 1, message = "Self assessment rating must be at least 1.")
    @Max(value = 5, message = "Self assessment rating cannot be greater than 5.")
    @Column(name = "manager_overall_self_assessment_rating")
    private Integer managerOverallSelfAssessmentRating;
    @Column(name = "manager_overall_self_review_comments", columnDefinition = "TEXT")
    private String managerOverallSelfReviewComments;
    @Column(name = "achievement_level", columnDefinition = "TEXT")
    private String achievementLevel;
    @Column(name = "potential", columnDefinition = "TEXT")
    private String potential;
    @Column(name = "performance", columnDefinition = "TEXT")
    private String performance;
    @Column(name = "talent_or_critical_resource", columnDefinition = "TEXT")
    private String talentOrCriticalResource;
    @Column(name = "talent_matrix_category", columnDefinition = "TEXT")
    private String talentMatrixCategory;

    @Column(name = "self_accecpted_date")
    private LocalDateTime selfAcceptedDate;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = GoalStatus.NOT_STARTED;
        }
        if (this.year == null && this.performanceCycle != null) {
            this.year = this.performanceCycle.getYear();
        }
    }

    @Column(name = "goal_description", columnDefinition = "TEXT")
    private String goalDescription;

    @Column(name = "target_kpi", columnDefinition = "TEXT")
    private String targetKPI;

    @Column(name = "timeline", columnDefinition = "TEXT")
    private String timeline;

    public List<String> getTimelineAsList() {
        if (this.timeline == null || this.timeline.isEmpty()) {
            return new ArrayList<>();
        }
        return Arrays.asList(this.timeline.split(","));
    }

    public void setTimelineFromList(List<String> timelineList) {
        if (timelineList == null || timelineList.isEmpty()) {
            this.timeline = null;
        } else {
            this.timeline = String.join(",", timelineList);
        }
    }
}