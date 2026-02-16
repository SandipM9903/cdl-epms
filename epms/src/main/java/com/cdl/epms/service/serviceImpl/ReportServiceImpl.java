package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.reports.CertificationReportDto;
import com.cdl.epms.dto.reports.ReportGoalResponseDto;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.model.Goal;
import com.cdl.epms.repository.EmployeeCertificationRepository;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.service.services.ReportService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final GoalRepository goalRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;
    private final ModelMapper modelMapper;

    private ReportGoalResponseDto mapToDto(Goal goal) {

        ReportGoalResponseDto dto = modelMapper.map(goal, ReportGoalResponseDto.class);

        dto.setGoalId(goal.getId());
        dto.setEmployeeId(goal.getEmployeeId());
        dto.setManagerId(goal.getManagerId());

        if (goal.getPerformanceCycle() != null) {
            dto.setYear(goal.getPerformanceCycle().getYear());
        }

        dto.setQuarter(goal.getQuarter());
        dto.setGoalType(goal.getGoalType());
        dto.setTitle(goal.getTitle());
        dto.setDescription(goal.getDescription());
        dto.setWeightage(goal.getWeightage());
        dto.setStatus(goal.getStatus());

        dto.setManagerRating(goal.getManagerRating());
        dto.setManagerComment(goal.getManagerComment());

        return dto;
    }

    @Override
    public List<ReportGoalResponseDto> getPredefinedGoalsReport(Integer year) {

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.PREDEFINED, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ReportGoalResponseDto> getGoalSettingsReport(Integer year) {

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.SMART, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ReportGoalResponseDto> getDevelopmentGoalsReport(Integer year) {

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.DEVELOPMENT, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<CertificationReportDto> getCertificationCompletionReport(Integer year) {

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        List<EmployeeCertification> list = employeeCertificationRepository.findByYear(year);

        return list.stream().map(ec -> {

            CertificationReportDto dto = modelMapper.map(ec, CertificationReportDto.class);

            dto.setEmployeeId(ec.getEmployeeId());

            if (ec.getCertification() != null) {
                dto.setCertificationName(ec.getCertification().getCertificationName());
                dto.setMandatory(ec.getCertification().getMandatory());
            }

            dto.setYear(ec.getYear());
            dto.setStatus(ec.getStatus());
            dto.setCompletedAt(ec.getCompletedAt());

            return dto;
        }).toList();
    }

    @Override
    public List<ReportGoalResponseDto> getDetailedGoalsQuarterWiseReport(Integer year, Quarter quarter) {

        if (year == null || year <= 0) {
            throw new ValidationException("Year is required");
        }

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        List<Goal> goals = goalRepository.findByQuarterAndPerformanceCycle_Year(quarter, year);

        return goals.stream().map(this::mapToDto).toList();
    }
}