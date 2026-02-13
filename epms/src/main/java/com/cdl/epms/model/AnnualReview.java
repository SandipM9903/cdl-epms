package com.cdl.epms.model;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "annual_review")
public class AnnualReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @Column(name = "manager_id", nullable = false)
    private String managerId;

    @Column(name = "year", nullable = false)
    private Integer year;

    @Column(name = "self_rating")
    private Integer selfRating;

    @Column(name = "self_comment", columnDefinition = "TEXT")
    private String selfComment;

    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "manager_comment", columnDefinition = "TEXT")
    private String managerComment;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AnnualReviewStatus status;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "manager_reviewed_at")
    private LocalDateTime managerReviewedAt;

    @Column(name = "submitted_to_employee_at")
    private LocalDateTime submittedToEmployeeAt;

    @PrePersist
    public void onCreate() {
        this.submittedAt = LocalDateTime.now();
        this.status = AnnualReviewStatus.SELF_SUBMITTED;
    }
}