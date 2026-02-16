package com.cdl.epms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "certification")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Certification name cannot be empty.")
    @Column(name = "certification_name", nullable = false, unique = true)
    private String certificationName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Mandatory field cannot be null.")
    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = false;

    @NotNull(message = "Active field cannot be null.")
    @Column(name = "active", nullable = false)
    private Boolean active = true;
}