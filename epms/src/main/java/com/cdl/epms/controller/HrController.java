package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.hr.HrDashboardResponseDto;
import com.cdl.epms.dto.hr.HrProgressStatusResponseDto;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.HrService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class HrController {

    private final HrService hrService;

    @GetMapping("/progress-status/{quarter}")
    public ResponseEntity<ApiResponse<HrProgressStatusResponseDto>> getProgressStatus(
            @PathVariable Quarter quarter
    ) {

        HrProgressStatusResponseDto progressStatus = hrService.getProgressStatus(quarter);

        ApiResponse<HrProgressStatusResponseDto> response = ApiResponse.<HrProgressStatusResponseDto>builder()
                .success(true)
                .message("HR progress status fetched successfully")
                .data(progressStatus)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/{cycleId}")
    public ResponseEntity<ApiResponse<HrDashboardResponseDto>> getDashboard(
            @PathVariable Long cycleId
    ) {

        HrDashboardResponseDto dashboard = hrService.getDashboard(cycleId);

        ApiResponse<HrDashboardResponseDto> response = ApiResponse.<HrDashboardResponseDto>builder()
                .success(true)
                .message("HR dashboard fetched successfully")
                .data(dashboard)
                .build();

        return ResponseEntity.ok(response);
    }
}