package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.reports.CertificationReportDto;
import com.cdl.epms.dto.reports.ReportGoalResponseDto;

import java.util.List;

public interface ReportService {

    List<ReportGoalResponseDto> getPredefinedGoalsReport(Integer year);

    List<ReportGoalResponseDto> getGoalSettingsReport(Integer year);

    List<ReportGoalResponseDto> getDevelopmentGoalsReport(Integer year);

    List<CertificationReportDto> getCertificationCompletionReport(Integer year);

    List<ReportGoalResponseDto> getDetailedGoalsQuarterWiseReport(Integer year, Quarter quarter);
}