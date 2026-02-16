package com.cdl.epms.controller;

import com.cdl.epms.model.Certification;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.CertificationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class CertificationController {

    private final CertificationService certificationService;

    @PostMapping
    public ResponseEntity<ApiResponse<Certification>> createCertification(
            @Valid @RequestBody Certification certification
    ) {

        Certification savedCertification = certificationService.createCertification(certification);

        ApiResponse<Certification> response = ApiResponse.<Certification>builder()
                .success(true)
                .message("Certification created successfully")
                .data(savedCertification)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<Certification>>> getAllCertifications() {

        List<Certification> certifications = certificationService.getAllCertifications();

        ApiResponse<List<Certification>> response = ApiResponse.<List<Certification>>builder()
                .success(true)
                .message("Certifications fetched successfully")
                .data(certifications)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/complete/{employeeId}/{certificationId}/{year}")
    public ResponseEntity<ApiResponse<EmployeeCertification>> completeCertification(
            @PathVariable String employeeId,
            @PathVariable Long certificationId,
            @PathVariable Integer year
    ) {

        EmployeeCertification completedCertification =
                certificationService.completeCertification(employeeId, certificationId, year);

        ApiResponse<EmployeeCertification> response = ApiResponse.<EmployeeCertification>builder()
                .success(true)
                .message("Certification marked as completed successfully")
                .data(completedCertification)
                .build();

        return ResponseEntity.ok(response);
    }
}