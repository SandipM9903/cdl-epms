package com.cdl.epms.model;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "performance_cycle")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PerformanceCycle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Cycle type cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "cycle_type", nullable = false)
    private CycleType cycleType;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2000, message = "Year must be a valid year.")
    @Column(name = "year", nullable = false)
    private Integer year;

    @Enumerated(EnumType.STRING)
    @Column(name = "quarter")
    private Quarter quarter;

    @NotNull(message = "Start date cannot be null.")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null.")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @NotNull(message = "Cycle status cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CycleStatus status;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        if (this.status == null) {
            this.status = CycleStatus.DRAFT;
        }
    }
}