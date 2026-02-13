package com.cdl.epms.dto.hr;

import lombok.Data;

@Data
public class HrProgressStatusResponseDto {

    private String quarter;

    private long draftCount;
    private long predefinedSubmittedCount;
    private long submittedToManagerCount;
    private long managerReviewedCount;
    private long sentToEmployeeCount;
    private long acceptedByEmployeeCount;
    private long finalSubmittedToHrCount;
}