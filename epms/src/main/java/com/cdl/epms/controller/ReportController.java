package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.reports.CertificationReportDto;
import com.cdl.epms.dto.reports.ReportGoalResponseDto;
import com.cdl.epms.service.services.ReportService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
public class ReportController {

    private final ReportService reportService;

    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @GetMapping("/predefined-goals")
    public ResponseEntity<List<ReportGoalResponseDto>> predefinedGoalsReport(
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(reportService.getPredefinedGoalsReport(year));
    }

    @GetMapping("/goal-settings")
    public ResponseEntity<List<ReportGoalResponseDto>> goalSettingsReport(
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(reportService.getGoalSettingsReport(year));
    }

    @GetMapping("/development-goals")
    public ResponseEntity<List<ReportGoalResponseDto>> developmentGoalsReport(
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(reportService.getDevelopmentGoalsReport(year));
    }

    @GetMapping("/certification-completion")
    public ResponseEntity<List<CertificationReportDto>> certificationCompletionReport(
            @RequestParam Integer year
    ) {
        return ResponseEntity.ok(reportService.getCertificationCompletionReport(year));
    }

    @GetMapping("/detailed-goals-qwise")
    public ResponseEntity<List<ReportGoalResponseDto>> detailedGoalsQuarterWiseReport(
            @RequestParam Integer year,
            @RequestParam Quarter quarter
    ) {
        return ResponseEntity.ok(reportService.getDetailedGoalsQuarterWiseReport(year, quarter));
    }
}