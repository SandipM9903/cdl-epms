package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.PerformanceCycle;

import java.time.LocalDate;
import java.util.List;

public interface CycleService {

    PerformanceCycle createCycle(
            CycleType cycleType,
            Integer year,
            Quarter quarter,
            Integer reminderDays,
            LocalDate startDate,
            LocalDate endDate
    );

    String publishCycle(Long cycleId);

    PerformanceCycle getActiveCycle();

    void closeCycle(Long cycleId);

    List<PerformanceCycle> getCyclesByYear(Integer year);

    PerformanceCycle createAnnualCycle(
            Integer year,
            Integer reminderDays,
            LocalDate startDate,
            LocalDate endDate
    );
}
