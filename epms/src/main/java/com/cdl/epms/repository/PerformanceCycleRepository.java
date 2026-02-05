package com.cdl.epms.repository;

import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PerformanceCycleRepository extends JpaRepository<PerformanceCycle, Long> {

    Optional<PerformanceCycle> findByStatus(CycleStatus status);

    boolean existsByStatus(CycleStatus status);

    Optional<PerformanceCycle> findByYearAndQuarterAndCycleType(
            Integer year,
            Quarter quarter,
            CycleType cycleType
    );
}
