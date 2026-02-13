package com.cdl.epms.repository;

import com.cdl.epms.model.AnnualReview;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AnnualReviewRepository extends JpaRepository<AnnualReview, Long> {

    Optional<AnnualReview> findByEmployeeIdAndYear(String employeeId, Integer year);

    boolean existsByEmployeeIdAndYear(String employeeId, Integer year);

    List<AnnualReview> findByManagerIdAndYear(String managerId, Integer year);
}