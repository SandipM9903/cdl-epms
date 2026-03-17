package com.cdl.epms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "goal_master")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GoalMaster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category cannot be empty")
    @Column(name = "category", nullable = false)
    private String category;  // String instead of Enum - can be "VALUES", "QUALITY", "KNOWLEDGE", etc.

    @NotBlank(message = "Differentiator name cannot be empty")
    @Column(name = "differentiator_name", nullable = false)
    private String differentiatorName;

    @NotBlank(message = "Definition cannot be empty")
    @Column(name = "definition", nullable = false, columnDefinition = "TEXT")
    private String definition;

    @NotNull(message = "Active status cannot be null")
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "display_order")
    private Integer displayOrder;

    @CreationTimestamp
    @Column(name = "created_date", nullable = false, updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(name = "updated_date")
    private LocalDateTime updatedDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "updated_by")
    private String updatedBy;
}