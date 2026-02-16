package com.cdl.epms.model;

import com.cdl.epms.common.enums.CertificationStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "employee_certification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeCertification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Employee ID cannot be empty.")
    @Column(name = "employee_id", nullable = false)
    private String employeeId;

    @NotNull(message = "Certification cannot be null.")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "certification_id", nullable = false)
    private Certification certification;

    @NotNull(message = "Year cannot be null.")
    @Min(value = 2000, message = "Year must be a valid year.")
    @Column(name = "year", nullable = false)
    private Integer year;

    @NotNull(message = "Certification status cannot be null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CertificationStatus status;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @PrePersist
    public void onCreate() {
        if (this.status == null) {
            this.status = CertificationStatus.PENDING;
        }
    }
}