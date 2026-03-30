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

    @NotNull(message = "Annual Review ID cannot be null.")
    @Column(name = "annual_review_id", nullable = false)
    private Long annualReviewId;

    @NotBlank(message = "Certification name cannot be empty.")
    @Column(name = "name", nullable = false)
    private String name;

    @NotBlank(message = "Certification type cannot be empty.")
    @Column(name = "type", nullable = false)
    private String type;

    @Column(name = "file_name")
    private String fileName;
}