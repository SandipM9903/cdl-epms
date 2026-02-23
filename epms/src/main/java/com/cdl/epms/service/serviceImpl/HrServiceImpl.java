package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.hr.HrDashboardResponseDto;
import com.cdl.epms.dto.hr.HrProgressStatusResponseDto;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.HrService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HrServiceImpl implements HrService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleRepository cycleRepository;
    private final ModelMapper modelMapper;

    @Override
    public HrProgressStatusResponseDto getProgressStatus(Quarter quarter) {

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        HrProgressStatusResponseDto dto = new HrProgressStatusResponseDto();
        dto.setQuarter(quarter.name());

        dto.setNOT_STARTEDCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.NOT_STARTED));
        dto.setPredefinedSubmittedCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.PREDEFINED_SUBMITTED));
        dto.setSubmittedToManagerCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.SUBMITTED_TO_MANAGER));
        dto.setManagerReviewedCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.MANAGER_REVIEWED));
        dto.setSentToEmployeeCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.SENT_TO_EMPLOYEE));
        dto.setAcceptedByEmployeeCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.ACCEPTED_BY_EMPLOYEE));
        dto.setFinalSubmittedToHrCount(goalRepository.countByQuarterAndStatus(quarter, GoalStatus.FINAL_SUBMITTED_TO_HR));

        return dto;
    }

    @Override
    public HrDashboardResponseDto getDashboard(Long cycleId) {

        if (cycleId == null) {
            throw new ValidationException("Cycle ID is required");
        }

        PerformanceCycle cycle = cycleRepository.findById(cycleId)
                .orElseThrow(() -> new ResourceNotFoundException("Cycle not found with id: " + cycleId));

        HrDashboardResponseDto dto = modelMapper.map(cycle, HrDashboardResponseDto.class);

        dto.setCycleId(cycle.getId());
        dto.setYear(cycle.getYear());
        dto.setCycleType(cycle.getCycleType().name());
        dto.setStatus(cycle.getStatus().name());

        dto.setQuarter(cycle.getQuarter() != null ? cycle.getQuarter().name() : null);

        dto.setTotalGoals(goalRepository.countByPerformanceCycle_Id(cycleId));

        dto.setPredefinedGoals(goalRepository.countByPerformanceCycle_IdAndGoalType(cycleId, GoalType.PREDEFINED));
        dto.setSmartGoals(goalRepository.countByPerformanceCycle_IdAndGoalType(cycleId, GoalType.SMART));
        dto.setDevelopmentGoals(goalRepository.countByPerformanceCycle_IdAndGoalType(cycleId, GoalType.DEVELOPMENT));

        dto.setTotalEmployees(goalRepository.countDistinctEmployeesByCycleId(cycleId));

        return dto;
    }
}