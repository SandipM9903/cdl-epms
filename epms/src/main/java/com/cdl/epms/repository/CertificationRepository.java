package com.cdl.epms.repository;

import com.cdl.epms.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    Optional<Certification> findByCertificationName(String certificationName);
}