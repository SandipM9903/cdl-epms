package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.hr.HrDashboardResponseDto;
import com.cdl.epms.dto.hr.HrProgressStatusResponseDto;
import com.cdl.epms.service.services.HrService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/hr")
@CrossOrigin(origins = "*")
public class HrController {

    private final HrService hrService;

    public HrController(HrService hrService) {
        this.hrService = hrService;
    }

    @GetMapping("/progress-status/{quarter}")
    public ResponseEntity<HrProgressStatusResponseDto> getProgressStatus(
            @PathVariable Quarter quarter
    ) {
        return ResponseEntity.ok(hrService.getProgressStatus(quarter));
    }

    @GetMapping("/dashboard/{cycleId}")
    public ResponseEntity<HrDashboardResponseDto> getDashboard(
            @PathVariable Long cycleId
    ) {
        return ResponseEntity.ok(hrService.getDashboard(cycleId));
    }
}