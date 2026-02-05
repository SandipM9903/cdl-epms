package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.exception.BusinessException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.CycleService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class CycleServiceImpl implements CycleService {

    private final PerformanceCycleRepository cycleRepository;

    public CycleServiceImpl(PerformanceCycleRepository cycleRepository) {
        this.cycleRepository = cycleRepository;
    }

    @Override
    public PerformanceCycle createCycle(
            CycleType cycleType,
            Integer year,
            Quarter quarter,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateCycleInput(cycleType, quarter, startDate, endDate);

        if (cycleType == CycleType.QUARTERLY) {
            Optional<PerformanceCycle> existing =
                    cycleRepository.findByYearAndQuarterAndCycleType(
                            year, quarter, cycleType
                    );

            if (existing.isPresent()) {
                throw new BusinessException(
                        "Cycle already exists for " + quarter + " " + year
                );
            }
        }

        PerformanceCycle cycle = new PerformanceCycle();
        cycle.setCycleType(cycleType);
        cycle.setYear(year);
        cycle.setQuarter(quarter);
        cycle.setStartDate(startDate);
        cycle.setEndDate(endDate);
        cycle.setStatus(CycleStatus.DRAFT);

        return cycleRepository.save(cycle);
    }

    @Override
    public String publishCycle(Long cycleId) {

        PerformanceCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.DRAFT) {
            throw new BusinessException("Only DRAFT cycles can be published");
        }

        if (cycleRepository.existsByStatus(CycleStatus.PUBLISHED)) {
            throw new BusinessException("Another performance cycle is already active");
        }

        cycle.setStatus(CycleStatus.PUBLISHED);
        cycleRepository.save(cycle);

        return "Performance cycle published successfully. This cycle is now active.";
    }


    @Override
    public PerformanceCycle getActiveCycle() {
        return cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() ->
                        new ResourceNotFoundException("No active cycle found"));
    }

    @Override
    public void closeCycle(Long cycleId) {

        PerformanceCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.PUBLISHED) {
            throw new BusinessException(
                    "Only PUBLISHED cycles can be closed"
            );
        }

        cycle.setStatus(CycleStatus.CLOSED);
        cycleRepository.save(cycle);
    }

    /**
     * Shared validation logic
     */
    private void validateCycleInput(
            CycleType cycleType,
            Quarter quarter,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (startDate.isAfter(endDate)) {
            throw new BusinessException(
                    "Start date cannot be after end date"
            );
        }

        if (cycleType == CycleType.QUARTERLY && quarter == null) {
            throw new BusinessException(
                    "Quarter is mandatory for quarterly cycle"
            );
        }

        if (cycleType == CycleType.ANNUAL && quarter != null) {
            throw new BusinessException(
                    "Quarter should not be provided for annual cycle"
            );
        }
    }
}
