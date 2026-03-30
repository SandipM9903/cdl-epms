package com.cdl.epms.repository;

import com.cdl.epms.common.enums.AnnualReviewStatus;
import com.cdl.epms.model.AnnualReview;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnnualReviewRepository extends JpaRepository<AnnualReview, Long> {

    Optional<AnnualReview> findByEmployeeIdAndYear(String employeeId, Integer year);

    List<AnnualReview> findByStatus(AnnualReviewStatus status);

    List<AnnualReview> findByManagerIdAndStatus(String managerId, AnnualReviewStatus status);

    @Query("SELECT a FROM AnnualReview a WHERE a.status = :status AND a.submittedToHrDate IS NULL")
    List<AnnualReview> findByStatusAndSubmittedToHrDateIsNull(@Param("status") AnnualReviewStatus status);

    @Query("SELECT a FROM AnnualReview a WHERE a.employeeId = :employeeId ORDER BY a.year DESC")
    List<AnnualReview> findAllByEmployeeIdOrderByYearDesc(@Param("employeeId") String employeeId);
}