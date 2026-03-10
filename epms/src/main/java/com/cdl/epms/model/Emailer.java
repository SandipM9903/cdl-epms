package com.cdl.epms.model;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.common.enums.EmailerStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "emailer")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Emailer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cycle type cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false)
    private CycleType cycleType;

    @NotBlank(message = "Subject cannot be empty.")
    @Column(name = "subject", nullable = false)
    private String subject;

    @NotBlank(message = "Content cannot be empty.")
    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @NotNull(message = "Emailer status cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private EmailerStatus status;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EmailTemplateType templateType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "active_at")
    private LocalDateTime activeAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = EmailerStatus.NOT_STARTED;
        }
    }

    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}