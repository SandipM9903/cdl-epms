package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.reports.CertificationReportDto;
import com.cdl.epms.dto.reports.ReportGoalResponseDto;
import com.cdl.epms.exception.BusinessException;
import com.cdl.epms.model.EmployeeCertification;
import com.cdl.epms.model.Goal;
import com.cdl.epms.repository.EmployeeCertificationRepository;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.service.services.ReportService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReportServiceImpl implements ReportService {

    private final GoalRepository goalRepository;
    private final EmployeeCertificationRepository employeeCertificationRepository;

    public ReportServiceImpl(GoalRepository goalRepository, EmployeeCertificationRepository employeeCertificationRepository) {
        this.goalRepository = goalRepository;
        this.employeeCertificationRepository = employeeCertificationRepository;
    }

    private ReportGoalResponseDto mapToDto(Goal goal) {

        ReportGoalResponseDto dto = new ReportGoalResponseDto();

        dto.setGoalId(goal.getId());
        dto.setEmployeeId(goal.getEmployeeId());
        dto.setManagerId(goal.getManagerId());

        dto.setYear(goal.getPerformanceCycle().getYear());
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

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.PREDEFINED, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ReportGoalResponseDto> getGoalSettingsReport(Integer year) {

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.SMART, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<ReportGoalResponseDto> getDevelopmentGoalsReport(Integer year) {

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        List<Goal> goals = goalRepository.findByGoalTypeAndPerformanceCycle_Year(GoalType.DEVELOPMENT, year);

        return goals.stream().map(this::mapToDto).toList();
    }

    @Override
    public List<CertificationReportDto> getCertificationCompletionReport(Integer year) {

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        List<EmployeeCertification> list = employeeCertificationRepository.findByYear(year);

        return list.stream().map(ec -> {
            CertificationReportDto dto = new CertificationReportDto();
            dto.setEmployeeId(ec.getEmployeeId());
            dto.setCertificationName(ec.getCertification().getCertificationName());
            dto.setMandatory(ec.getCertification().getMandatory());
            dto.setYear(ec.getYear());
            dto.setStatus(ec.getStatus());
            dto.setCompletedAt(ec.getCompletedAt());
            return dto;
        }).toList();
    }
    @Override
    public List<ReportGoalResponseDto> getDetailedGoalsQuarterWiseReport(Integer year, Quarter quarter) {

        if (year == null) {
            throw new BusinessException("Year is required");
        }

        if (quarter == null) {
            throw new BusinessException("Quarter is required");
        }

        List<Goal> goals = goalRepository.findByQuarterAndPerformanceCycle_Year(quarter, year);

        return goals.stream().map(this::mapToDto).toList();
    }
}