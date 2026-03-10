package com.cdl.epms.dto.notifications;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.common.enums.EmailerStatus;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class EmailerResponseDto {

    private Long id;
    private CycleType cycleType;
    private String subject;
    private String content;
    private EmailerStatus status;
    private EmailTemplateType templateType;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime activeAt;
}