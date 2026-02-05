package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.PerformanceCycle;

import java.time.LocalDate;

public interface CycleService {

    PerformanceCycle createCycle(
            CycleType cycleType,
            Integer year,
            Quarter quarter,
            LocalDate startDate,
            LocalDate endDate
    );

    String publishCycle(Long cycleId);

    PerformanceCycle getActiveCycle();

    void closeCycle(Long cycleId);
}
