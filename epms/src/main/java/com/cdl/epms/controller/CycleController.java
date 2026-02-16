package com.cdl.epms.controller;

import com.cdl.epms.dto.cycle.CreateCycleRequestDto;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.CycleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/cycles")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CycleController {

    private final CycleService cycleService;

    @PostMapping
    public ResponseEntity<ApiResponse<PerformanceCycle>> save(
            @Valid @RequestBody CreateCycleRequestDto requestDto
    ) {

        PerformanceCycle savedCycle = cycleService.createCycle(
                requestDto.getCycleType(),
                requestDto.getYear(),
                requestDto.getQuarter(),
                requestDto.getStartDate(),
                requestDto.getEndDate()
        );

        ApiResponse<PerformanceCycle> response = ApiResponse.<PerformanceCycle>builder()
                .success(true)
                .message("Performance cycle created successfully")
                .data(savedCycle)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}/publish")
    public ResponseEntity<ApiResponse<String>> publish(@PathVariable Long id) {

        String message = cycleService.publishCycle(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Performance cycle published successfully")
                .data(message)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/active")
    public ResponseEntity<ApiResponse<PerformanceCycle>> findActive() {

        PerformanceCycle activeCycle = cycleService.getActiveCycle();

        ApiResponse<PerformanceCycle> response = ApiResponse.<PerformanceCycle>builder()
                .success(true)
                .message("Active performance cycle fetched successfully")
                .data(activeCycle)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/close")
    public ResponseEntity<ApiResponse<String>> close(@PathVariable Long id) {

        cycleService.closeCycle(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Performance cycle closed successfully")
                .data("Performance cycle closed successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}