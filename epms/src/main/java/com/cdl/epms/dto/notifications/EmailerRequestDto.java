package com.cdl.epms.dto.notifications;

import com.cdl.epms.common.enums.CycleType;
import lombok.Data;

@Data
public class EmailerRequestDto {

    private CycleType cycleType;
    private String subject;
    private String content;
}