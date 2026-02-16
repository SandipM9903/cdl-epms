package com.cdl.epms.controller;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.dto.notifications.EmailerRequestDto;
import com.cdl.epms.dto.notifications.EmailerResponseDto;
import com.cdl.epms.service.services.EmailerService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/notifications/emailer")
@CrossOrigin(origins = "*")
public class NotificationController {

    private final EmailerService emailerService;

    public NotificationController(EmailerService emailerService) {
        this.emailerService = emailerService;
    }

    // ✅ Create Emailer
    @PostMapping("/create")
    public ResponseEntity<EmailerResponseDto> createEmailer(@RequestBody EmailerRequestDto dto) {
        return ResponseEntity.ok(emailerService.createEmailer(dto));
    }

    // ✅ Edit Emailer
    @PutMapping("/edit/{id}")
    public ResponseEntity<EmailerResponseDto> editEmailer(
            @PathVariable Long id,
            @RequestBody EmailerRequestDto dto
    ) {
        return ResponseEntity.ok(emailerService.editEmailer(id, dto));
    }

    // ✅ Preview Emailer
    @GetMapping("/preview/{id}")
    public ResponseEntity<EmailerResponseDto> previewEmailer(@PathVariable Long id) {
        return ResponseEntity.ok(emailerService.previewEmailer(id));
    }

    // ✅ Publish Emailer
    @PostMapping("/publish/{cycleType}")
    public ResponseEntity<String> publishEmailer(@PathVariable CycleType cycleType) {
        return ResponseEntity.ok(emailerService.publishEmailer(cycleType));
    }
}