package com.cdl.epms.controller;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
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

    // Create Email Template
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

    // Edit Email Template
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

    // Preview Email Template by ID
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

    // Publish Launch Email
    @PostMapping("/publish/{cycleType}")
    public ResponseEntity<ApiResponse<String>> publishEmailer(
            @PathVariable CycleType cycleType
    ) {

        String message = emailerService.publishEmailer(cycleType);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Emailer activated successfully")
                .data(message)
                .build();

        return ResponseEntity.ok(response);
    }

    // Preview Template by Type and Cycle
    @GetMapping("/preview/template/{cycleType}/{type}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> previewTemplate(
            @PathVariable CycleType cycleType,
            @PathVariable EmailTemplateType type
    ) {

        EmailerResponseDto emailer =
                emailerService.previewEmailerByType(cycleType, type);

        ApiResponse<EmailerResponseDto> response = ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Template fetched successfully")
                .data(emailer)
                .build();

        return ResponseEntity.ok(response);
    }

    // Send Email by Template Type
    @PostMapping("/send/{cycleType}/{type}")
    public ResponseEntity<ApiResponse<String>> sendEmailByTemplate(
            @PathVariable CycleType cycleType,
            @PathVariable EmailTemplateType type
    ) {

        String message =
                emailerService.sendEmailByTemplate(cycleType, type);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Email sent successfully")
                .data(message)
                .build();

        return ResponseEntity.ok(response);
    }

}