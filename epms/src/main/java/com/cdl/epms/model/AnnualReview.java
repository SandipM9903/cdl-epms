package com.cdl.epms.model;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "annual_review",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"employee_id", "year"})
        }
)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnnualReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee ID cannot be empty.")
    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "manager_id")
    private String managerId;

    @NotNull(message = "Year cannot be null.")
    @Column(name = "year", nullable = false)
    private Integer year;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnnualReviewStatus status = AnnualReviewStatus.DRAFT;

    // Manager Review fields
    @Column(name = "nine_box_result", length = 50)
    private String nineBoxResult;

    @Column(name = "talent_flag")
    private Boolean talentFlag;

    @Column(name = "critical_flag")
    private Boolean criticalFlag;

    @Column(name = "manager_remarks", columnDefinition = "TEXT")
    private String managerRemarks;

    @Column(name = "manager_rating", length = 10)
    private String managerRating;

    @Column(name = "performance_rating", length = 10)
    private String performanceRating;

    @Column(name = "potential_rating", length = 10)
    private String potentialRating;

    // Employee HR Submission fields
    @Column(name = "discussed_with_r1")
    private Boolean discussedWithR1;

    @Column(name = "employee_comment")
    private Boolean employeeComment;

    @Column(name = "employee_comment_text", columnDefinition = "TEXT")
    private String employeeCommentText;

    @Column(name = "submitted_to_hr_date")
    private LocalDateTime submittedToHrDate;

    @Column(name = "submitted_to_hr_by")
    private String submittedToHrBy;

    @Column(name = "hr_remarks", columnDefinition = "TEXT")
    private String hrRemarks;

    // Timestamps
    @Column(name = "manager_annual_review_submission_date")
    private LocalDateTime managerAnnualReviewSubmissionDate;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @PrePersist
    public void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}