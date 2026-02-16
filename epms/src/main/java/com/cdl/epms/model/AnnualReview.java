package com.cdl.epms.model;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "annual_review")
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

    @NotBlank(message = "Manager ID cannot be empty.")
    @Column(name = "manager_id", nullable = false)
    private String managerId;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2000, message = "Year must be a valid year.")
    @Column(name = "year", nullable = false)
    private Integer year;

    @Min(value = 1, message = "Self rating must be at least 1.")
    @Max(value = 5, message = "Self rating cannot be greater than 5.")
    @Column(name = "self_rating")
    private Integer selfRating;

    @Column(name = "self_comment", columnDefinition = "TEXT")
    private String selfComment;

    @Min(value = 1, message = "Manager rating must be at least 1.")
    @Max(value = 5, message = "Manager rating cannot be greater than 5.")
    @Column(name = "manager_rating")
    private Integer managerRating;

    @Column(name = "manager_comment", columnDefinition = "TEXT")
    private String managerComment;

    @NotNull(message = "Annual review status cannot be null.")
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
        if (this.submittedAt == null) {
            this.submittedAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = AnnualReviewStatus.SELF_SUBMITTED;
        }
    }
}