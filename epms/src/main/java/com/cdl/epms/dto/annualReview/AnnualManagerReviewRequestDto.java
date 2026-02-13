package com.cdl.epms.dto.annualReview;

import lombok.Data;

@Data
public class AnnualManagerReviewRequestDto {

    private Integer year;
    private Integer managerRating;
    private String managerComment;
}