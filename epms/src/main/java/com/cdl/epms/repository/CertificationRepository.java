package com.cdl.epms.repository;

import com.cdl.epms.model.Certification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying; // Import this
import org.springframework.transaction.annotation.Transactional; // Import this

import java.util.List;

public interface CertificationRepository extends JpaRepository<Certification, Long> {

    @Modifying
    @Transactional
    void deleteByAnnualReviewId(Long annualReviewId);

    List<Certification> findByAnnualReviewId(Long annualReviewId);
}