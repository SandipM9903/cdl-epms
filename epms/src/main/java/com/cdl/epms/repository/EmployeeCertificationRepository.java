package com.cdl.epms.repository;

import com.cdl.epms.model.EmployeeCertification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmployeeCertificationRepository extends JpaRepository<EmployeeCertification, Long> {

    List<EmployeeCertification> findByYear(Integer year);

    List<EmployeeCertification> findByEmployeeIdAndYear(String employeeId, Integer year);

    Optional<EmployeeCertification> findByEmployeeIdAndCertification_IdAndYear(String employeeId, Long certificationId, Integer year);
}