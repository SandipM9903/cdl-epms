package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.hr.HrDashboardResponseDto;
import com.cdl.epms.dto.hr.HrProgressStatusResponseDto;

public interface HrService {

    HrProgressStatusResponseDto getProgressStatus(Quarter quarter);

    HrDashboardResponseDto getDashboard(Long cycleId);
}