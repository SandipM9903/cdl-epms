package com.cdl.epms.dto.notifications;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
public class EmailSendRequest {
    private CycleType cycleType;
    private EmailTemplateType templateType;
    private List<EmployeeDto> employees;
    private List<EmployeeDto> managers;
    private String subject;
    private String content;
    private LocalDate deadline;
    private Integer pendingTeamMembers;
}