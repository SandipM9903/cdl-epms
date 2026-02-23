package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.CycleType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.CycleService;
import com.cdl.epms.service.services.EmailerService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CycleServiceImpl implements CycleService {

    private final PerformanceCycleRepository cycleRepository;
    private final ModelMapper modelMapper;
    private final EmailerService emailerService;

    @Override
    public PerformanceCycle createCycle(
            CycleType cycleType,
            Integer year,
            Quarter quarter,
            Integer reminderDays,
            LocalDate startDate,
            LocalDate endDate
    ) {

        validateCycleInput(cycleType, year, quarter, startDate, endDate);

        if (cycleType == CycleType.QUARTERLY) {
            Optional<PerformanceCycle> existing =
                    cycleRepository.findByYearAndQuarterAndCycleType(
                            year, quarter, cycleType
                    );

            if (existing.isPresent()) {
                throw new ConflictException("Cycle already exists for " + quarter + " " + year);
            }
        }

        PerformanceCycle cycle = modelMapper.map(new PerformanceCycle(), PerformanceCycle.class);

        cycle.setCycleType(cycleType);
        cycle.setYear(year);
        cycle.setQuarter(quarter);
        cycle.setReminderDays(reminderDays);
        cycle.setStartDate(startDate);
        cycle.setEndDate(endDate);
        cycle.setStatus(CycleStatus.NOT_STARTED);

        return cycleRepository.save(cycle);
    }

    @Transactional
    @Override
    public String publishCycle(Long cycleId) {

        if (cycleId == null) {
            throw new ValidationException("Cycle ID is required");
        }

        PerformanceCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.NOT_STARTED) {
            throw new ConflictException("Only NOT_STARTED cycles can be ACTIVE");
        }

        if (cycleRepository.existsByStatus(CycleStatus.ACTIVE)) {
            throw new ConflictException("Another performance cycle is already active");
        }

        cycle.setStatus(CycleStatus.ACTIVE);
        cycle.setPublishedDate(LocalDate.now());

        cycleRepository.save(cycle);

        try {
            emailerService.publishEmailer(cycle.getCycleType());
        } catch (Exception e) {
            System.err.println("Email sending failed: " + e.getMessage());
        }

        return "Performance cycle ACTIVE successfully and launch email sent.";
    }

    @Override
    public PerformanceCycle getActiveCycle() {

        return cycleRepository.findByStatus(CycleStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));
    }

    @Override
    public void closeCycle(Long cycleId) {

        if (cycleId == null) {
            throw new ValidationException("Cycle ID is required");
        }

        PerformanceCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found"));

        if (cycle.getStatus() != CycleStatus.ACTIVE) {
            throw new ConflictException("Only ACTIVE cycles can be closed");
        }

        cycle.setStatus(CycleStatus.CLOSED);
        cycleRepository.save(cycle);
    }

    private void validateCycleInput(
            CycleType cycleType,
            Integer year,
            Quarter quarter,
            LocalDate startDate,
            LocalDate endDate
    ) {

        if (cycleType == null) {
            throw new ValidationException("Cycle type is required");
        }

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        if (startDate == null) {
            throw new ValidationException("Start date is required");
        }

        if (endDate == null) {
            throw new ValidationException("End date is required");
        }

        if (startDate.isAfter(endDate)) {
            throw new ValidationException("Start date cannot be after end date");
        }

        if (cycleType == CycleType.QUARTERLY && quarter == null) {
            throw new ValidationException("Quarter is mandatory for quarterly cycle");
        }

        if (cycleType == CycleType.ANNUAL && quarter != null) {
            throw new ValidationException("Quarter should not be provided for annual cycle");
        }
    }

    @Override
    public List<PerformanceCycle> getCyclesByYear(Integer year) {
        return cycleRepository.findByYear(year);
    }
}