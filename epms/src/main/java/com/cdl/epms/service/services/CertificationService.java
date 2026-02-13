package com.cdl.epms.service.services;

import com.cdl.epms.model.Certification;
import com.cdl.epms.model.EmployeeCertification;

import java.util.List;

public interface CertificationService {

    Certification createCertification(Certification certification);

    List<Certification> getAllCertifications();

    EmployeeCertification completeCertification(String employeeId, Long certificationId, Integer year);
}