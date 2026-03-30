package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CertificationStatus;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.Certification;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.repository.CertificationRepository;
import com.cdl.epms.repository.EmployeeCertificationRepository;
import com.cdl.epms.service.services.CertificationService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CertificationServiceImpl implements CertificationService {

    private final CertificationRepository certificationRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final ModelMapper modelMapper;

    @Override
    public Certification createCertification(Certification certification) {

        if (certification == null) {
            throw new ValidationException("Certification data is required");
        }

        if (certification.getName() == null || certification.getName().trim().isEmpty()) {
            throw new ValidationException("Certification name is required");
        }

        Certification newCertification = modelMapper.map(certification, Certification.class);

        return certificationRepository.save(newCertification);
    }

    @Override
    public List<Certification> getAllCertifications() {
        return certificationRepository.findAll();
    }

    @Override
    public EmployeeCertification completeCertification(String employeeId, Long certificationId, Integer year) {

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (certificationId == null) {
            throw new ValidationException("Certification ID is required");
        }

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        Certification certification = certificationRepository.findById(certificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Certification not found with id: " + certificationId));

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