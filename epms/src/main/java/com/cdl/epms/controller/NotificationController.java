package com.cdl.epms.controller;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.EmailerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/emailer")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class NotificationController {

    private final EmailerService emailerService;

    @PostMapping("/create")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> createEmailer(
            @Valid @RequestBody EmailerRequestDto dto
    ) {

        EmailerResponseDto savedEmailer = emailerService.createEmailer(dto);

        ApiResponse<EmailerResponseDto> response = ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer created successfully")
                .data(savedEmailer)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> editEmailer(
            @PathVariable Long id,
            @Valid @RequestBody EmailerRequestDto dto
    ) {

        EmailerResponseDto updatedEmailer = emailerService.editEmailer(id, dto);

        ApiResponse<EmailerResponseDto> response = ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer updated successfully")
                .data(updatedEmailer)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/preview/{id}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> previewEmailer(
            @PathVariable Long id
    ) {

        EmailerResponseDto emailer = emailerService.previewEmailer(id);

        ApiResponse<EmailerResponseDto> response = ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer preview fetched successfully")
                .data(emailer)
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/publish/{cycleType}")
    public ResponseEntity<ApiResponse<String>> publishEmailer(
            @PathVariable CycleType cycleType
    ) {

        String message = emailerService.publishEmailer(cycleType);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Emailer ACTIVE successfully")
                .data(message)
                .build();

        return ResponseEntity.ok(response);
    }
}