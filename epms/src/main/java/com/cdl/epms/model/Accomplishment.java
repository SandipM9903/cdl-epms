package com.cdl.epms.model;

import com.cdl.epms.common.enums.AccomplishmentType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Table(name = "accomplishments")
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Accomplishment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "annual_review_id", nullable = false)
    private Long annualReviewId;

    @Column(name = "goal_id")
    private Long goalId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "quarter", length = 2)
    private String quarter;

    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false)
    private AccomplishmentType type;
}