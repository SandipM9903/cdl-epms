package com.cdl.epms.dto.annualReview;

import lombok.Data;

@Data
public class AnnualReviewRequestDto {

    private Integer year;
    private String managerId;
    private Integer selfRating;
    private String selfComment;
}