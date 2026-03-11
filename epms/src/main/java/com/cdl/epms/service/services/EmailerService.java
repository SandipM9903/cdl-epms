package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.dto.notifications.EmailSendRequest;
import com.cdl.epms.dto.notifications.EmailSendResult;

public interface EmailerService {

    EmailerResponseDto createEmailer(EmailerRequestDto dto);

    EmailerResponseDto editEmailer(Long id, EmailerRequestDto dto);

    EmailerResponseDto previewEmailer(Long id);

    String publishEmailer(CycleType cycleType);

    void sendReminderEmail(CycleType cycleType);

    EmailerResponseDto previewEmailerByType(CycleType cycleType, EmailTemplateType templateType);

    String sendEmailByTemplate(CycleType cycleType, EmailTemplateType templateType);

    // Add this new method to the interface
    EmailSendResult sendRoleBasedEmails(EmailSendRequest request);
}