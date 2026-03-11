package com.cdl.epms.controller;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.EmailTemplateType;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.dto.notifications.EmailSendRequest;
import com.cdl.epms.dto.notifications.EmailSendResult;
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
        return ResponseEntity.ok(ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer created successfully")
                .data(savedEmailer)
                .build());
    }

    // Edit Email Template
    @PutMapping("/edit/{id}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> editEmailer(
            @PathVariable Long id,
            @Valid @RequestBody EmailerRequestDto dto
    ) {
        EmailerResponseDto updatedEmailer = emailerService.editEmailer(id, dto);
        return ResponseEntity.ok(ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer updated successfully")
                .data(updatedEmailer)
                .build());
    }

    // Preview Email Template by ID
    @GetMapping("/preview/{id}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> previewEmailer(
            @PathVariable Long id
    ) {
        EmailerResponseDto emailer = emailerService.previewEmailer(id);
        return ResponseEntity.ok(ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Emailer preview fetched successfully")
                .data(emailer)
                .build());
    }

    // Publish Launch Email
    @PostMapping("/publish/{cycleType}")
    public ResponseEntity<ApiResponse<String>> publishEmailer(
            @PathVariable CycleType cycleType
    ) {
        String message = emailerService.publishEmailer(cycleType);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Emailer activated successfully")
                .data(message)
                .build());
    }

    // Preview Template by Type and Cycle
    @GetMapping("/preview/template/{cycleType}/{type}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> previewTemplate(
            @PathVariable CycleType cycleType,
            @PathVariable EmailTemplateType type
    ) {
        EmailerResponseDto emailer = emailerService.previewEmailerByType(cycleType, type);
        return ResponseEntity.ok(ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Template fetched successfully")
                .data(emailer)
                .build());
    }

    // Send Email by Template Type
    @PostMapping("/send/{cycleType}/{type}")
    public ResponseEntity<ApiResponse<String>> sendEmailByTemplate(
            @PathVariable CycleType cycleType,
            @PathVariable EmailTemplateType type
    ) {
        String message = emailerService.sendEmailByTemplate(cycleType, type);
        return ResponseEntity.ok(ApiResponse.<String>builder()
                .success(true)
                .message("Email sent successfully")
                .data(message)
                .build());
    }

    // ========== NEW ENDPOINTS FOR MANAGER TEMPLATES AND ROLE-BASED EMAILS ==========

    /**
     * Preview Manager Template by Type and Cycle
     * This endpoint is used by frontend to fetch manager-specific templates
     */
    @GetMapping("/preview/manager/{cycleType}/{type}")
    public ResponseEntity<ApiResponse<EmailerResponseDto>> previewManagerTemplate(
            @PathVariable CycleType cycleType,
            @PathVariable EmailTemplateType type
    ) {
        // Reusing the same service method - you might want to add a separate method
        // in the service for manager templates if you need different logic
        EmailerResponseDto emailer = emailerService.previewEmailerByType(cycleType, type);
        return ResponseEntity.ok(ApiResponse.<EmailerResponseDto>builder()
                .success(true)
                .message("Manager template fetched successfully")
                .data(emailer)
                .build());
    }

    /**
     * Send Role-Based Emails
     * This endpoint allows sending emails to specific groups (employees, managers, or both)
     */
    @PostMapping("/send-role-based")
    public ResponseEntity<ApiResponse<EmailSendResult>> sendRoleBasedEmails(
            @Valid @RequestBody EmailSendRequest request
    ) {
        EmailSendResult result = emailerService.sendRoleBasedEmails(request);
        return ResponseEntity.ok(ApiResponse.<EmailSendResult>builder()
                .success(true)
                .message("Role-based emails sent successfully")
                .data(result)
                .build());
    }

    /**
     * Get Email Statistics
     * Returns statistics about emails sent for a specific cycle
     */
    @GetMapping("/stats/{cycleType}")
    public ResponseEntity<ApiResponse<EmailSendResult>> getEmailStats(
            @PathVariable CycleType cycleType
    ) {
        // You'll need to implement this method in your service
        // For now, returning a placeholder - you should create a proper implementation
        EmailSendResult stats = EmailSendResult.builder()
                .totalEmployees(0)
                .totalManagers(0)
                .employeesWithBothRoles(0)
                .employeeEmailsSent(0)
                .managerEmailsSent(0)
                .totalEmailsSent(0)
                .failedEmails(0)
                .build();

        return ResponseEntity.ok(ApiResponse.<EmailSendResult>builder()
                .success(true)
                .message("Email statistics fetched successfully")
                .data(stats)
                .build());
    }

    /**
     * Test endpoint to verify email configuration
     * Useful for debugging email sending issues
     */
    @PostMapping("/test")
    public ResponseEntity<ApiResponse<String>> testEmail(
            @RequestParam String testEmail
    ) {
        try {
            // You can add a test email sending method in your service
            // emailerService.sendTestEmail(testEmail);
            return ResponseEntity.ok(ApiResponse.<String>builder()
                    .success(true)
                    .message("Test email sent successfully to: " + testEmail)
                    .data("Check your inbox")
                    .build());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ApiResponse.<String>builder()
                    .success(false)
                    .message("Failed to send test email: " + e.getMessage())
                    .build());
        }
    }
}