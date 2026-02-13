package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CertificationStatus;
import com.cdl.epms.exception.BusinessException;
import com.cdl.epms.model.Certification;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.repository.CertificationRepository;
import com.cdl.epms.repository.EmployeeCertificationRepository;
import com.cdl.epms.service.services.CertificationService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;

    public CertificationServiceImpl(CertificationRepository certificationRepository,
                                    EmployeeCertificationRepository employeeCertificationRepository) {
        this.certificationRepository = certificationRepository;
        this.employeeCertificationRepository = employeeCertificationRepository;
    }

    @Override
    public Certification createCertification(Certification certification) {

        if (certification.getCertificationName() == null || certification.getCertificationName().trim().isEmpty()) {
            throw new BusinessException("Certification name is required");
        }

        return certificationRepository.save(certification);
    }

    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    public EmployeeCertification completeCertification(String employeeId, Long certificationId, Integer year) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new BusinessException("Employee ID is required");
        }

        if (certificationId == null) {
            throw new BusinessException("Certification ID is required");
        }

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new BusinessException("Certification not found with id: " + certificationId));

        EmployeeCertification employeeCertification = employeeCertificationRepository
                .findByEmployeeIdAndCertification_IdAndYear(employeeId, certificationId, year)
                .orElse(new EmployeeCertification());

        employeeCertification.setEmployeeId(employeeId);
        employeeCertification.setCertification(certification);
        employeeCertification.setYear(year);
        employeeCertification.setStatus(CertificationStatus.COMPLETED);
        employeeCertification.setCompletedAt(LocalDateTime.now());

        return employeeCertificationRepository.save(employeeCertification);
    }
}