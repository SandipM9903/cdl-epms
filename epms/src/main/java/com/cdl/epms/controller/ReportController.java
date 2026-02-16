package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.reports.CertificationReportDto;
import com.cdl.epms.dto.reports.ReportGoalResponseDto;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reports")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/predefined-goals")
    public ResponseEntity<ApiResponse<List<ReportGoalResponseDto>>> predefinedGoalsReport(
            @RequestParam Integer year
    ) {

        List<ReportGoalResponseDto> report = reportService.getPredefinedGoalsReport(year);

        ApiResponse<List<ReportGoalResponseDto>> response = ApiResponse.<List<ReportGoalResponseDto>>builder()
                .success(true)
                .message("Predefined goals report fetched successfully")
                .data(report)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/goal-settings")
    public ResponseEntity<ApiResponse<List<ReportGoalResponseDto>>> goalSettingsReport(
            @RequestParam Integer year
    ) {

        List<ReportGoalResponseDto> report = reportService.getGoalSettingsReport(year);

        ApiResponse<List<ReportGoalResponseDto>> response = ApiResponse.<List<ReportGoalResponseDto>>builder()
                .success(true)
                .message("Goal settings report fetched successfully")
                .data(report)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/development-goals")
    public ResponseEntity<ApiResponse<List<ReportGoalResponseDto>>> developmentGoalsReport(
            @RequestParam Integer year
    ) {

        List<ReportGoalResponseDto> report = reportService.getDevelopmentGoalsReport(year);

        ApiResponse<List<ReportGoalResponseDto>> response = ApiResponse.<List<ReportGoalResponseDto>>builder()
                .success(true)
                .message("Development goals report fetched successfully")
                .data(report)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/certification-completion")
    public ResponseEntity<ApiResponse<List<CertificationReportDto>>> certificationCompletionReport(
            @RequestParam Integer year
    ) {

        List<CertificationReportDto> report = reportService.getCertificationCompletionReport(year);

        ApiResponse<List<CertificationReportDto>> response = ApiResponse.<List<CertificationReportDto>>builder()
                .success(true)
                .message("Certification completion report fetched successfully")
                .data(report)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/detailed-goals-qwise")
    public ResponseEntity<ApiResponse<List<ReportGoalResponseDto>>> detailedGoalsQuarterWiseReport(
            @RequestParam Integer year,
            @RequestParam Quarter quarter
    ) {

        List<ReportGoalResponseDto> report =
                reportService.getDetailedGoalsQuarterWiseReport(year, quarter);

        ApiResponse<List<ReportGoalResponseDto>> response = ApiResponse.<List<ReportGoalResponseDto>>builder()
                .success(true)
                .message("Detailed goals quarter wise report fetched successfully")
                .data(report)
                .build();

        return ResponseEntity.ok(response);
    }
}