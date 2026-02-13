package com.cdl.epms.controller;

import com.cdl.epms.model.Certification;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.service.services.CertificationService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/certifications")
@CrossOrigin(origins = "*")
public class CertificationController {

    private final CertificationService certificationService;

    public CertificationController(CertificationService certificationService) {
        this.certificationService = certificationService;
    }

    // ✅ HR creates certification
    @PostMapping
    public ResponseEntity<Certification> createCertification(@RequestBody Certification certification) {
        return ResponseEntity.ok(certificationService.createCertification(certification));
    }

    // ✅ Get all certifications
    @GetMapping
    public ResponseEntity<List<Certification>> getAllCertifications() {
        return ResponseEntity.ok(certificationService.getAllCertifications());
    }

    // ✅ Mark certification completed
    @PutMapping("/complete/{employeeId}/{certificationId}/{year}")
    public ResponseEntity<EmployeeCertification> completeCertification(
            @PathVariable String employeeId,
            @PathVariable Long certificationId,
            @PathVariable Integer year
    ) {
        return ResponseEntity.ok(certificationService.completeCertification(employeeId, certificationId, year));
    }
}