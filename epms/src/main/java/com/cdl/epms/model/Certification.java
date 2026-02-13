package com.cdl.epms.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "certification")
public class Certification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "certification_name", nullable = false, unique = true)
    private String certificationName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "mandatory", nullable = false)
    private Boolean mandatory = false;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}