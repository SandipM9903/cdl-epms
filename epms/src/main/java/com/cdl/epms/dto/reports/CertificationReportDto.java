package com.cdl.epms.dto.reports;

import com.cdl.epms.common.enums.CertificationStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CertificationReportDto {

    private String employeeId;
    private String certificationName;
    private Boolean mandatory;
    private Integer year;
    private CertificationStatus status;
    private LocalDateTime completedAt;
}