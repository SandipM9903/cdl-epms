package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;

public interface EmailerService {

    EmailerResponseDto createEmailer(EmailerRequestDto dto);

    EmailerResponseDto editEmailer(Long id, EmailerRequestDto dto);

    EmailerResponseDto previewEmailer(Long id);

    String publishEmailer(CycleType cycleType);
}