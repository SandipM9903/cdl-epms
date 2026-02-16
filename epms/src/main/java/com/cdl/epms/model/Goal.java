package com.cdl.epms.model;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

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

    @NotNull(message = "Weightage cannot be null.")
    @Min(value = 1, message = "Weightage must be at least 1.")
    @Max(value = 100, message = "Weightage cannot be more than 100.")
    @Column(name = "weightage", nullable = false)
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

    @Column(name = "submitted_to_employee_at")
    private LocalDateTime submittedToEmployeeAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = GoalStatus.DRAFT;
        }
    }
}