package com.cdl.epms.dto.annualReview;

import lombok.Data;

@Data
public class SelectedAccomplishmentDto {

    private Long goalId;
    private String title;
    private String description;
    private String quarter;
}