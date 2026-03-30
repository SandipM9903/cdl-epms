package com.cdl.epms.dto.annualReview;

import lombok.Data;

@Data
public class HrSubmissionDto {
    private Long id;
    private String employeeId;
    private Integer year;
    private Boolean discussedWithR1;
    private Boolean employeeComment;
    private String employeeCommentText;
    private String submittedBy;
}