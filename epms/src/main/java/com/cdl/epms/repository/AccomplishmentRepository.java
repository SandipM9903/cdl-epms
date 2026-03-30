package com.cdl.epms.repository;

import com.cdl.epms.model.Accomplishment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface AccomplishmentRepository extends JpaRepository<Accomplishment, Long> {

    List<Accomplishment> findByAnnualReviewId(Long annualReviewId);

    @Transactional
    void deleteByAnnualReviewId(Long annualReviewId);
}