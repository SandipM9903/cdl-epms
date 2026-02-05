package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.exception.BusinessException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.GoalService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleRepository cycleRepository;

    public GoalServiceImpl(GoalRepository goalRepository, PerformanceCycleRepository cycleRepository) {
        this.goalRepository = goalRepository;
        this.cycleRepository = cycleRepository;
    }

    @Override
    public Goal savePredefinedGoal(Goal goal, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        if (quarter == null) {
            throw new BusinessException("Quarter is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new BusinessException("Employee ID is required");
        }

        if (goal.getManagerId() == null || goal.getManagerId().trim().isEmpty()) {
            throw new BusinessException("Manager ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new BusinessException("Goal title is required");
        }

        if (goal.getWeightage() == null || goal.getWeightage() <= 0) {
            throw new BusinessException("Weightage must be greater than 0");
        }

        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );

        if (count >= 5) {
            throw new BusinessException("Maximum 5 predefined goals allowed");
        }

        goal.setPerformanceCycle(activeCycle);
        goal.setQuarter(quarter);
        goal.setGoalType(GoalType.PREDEFINED);
        goal.setStatus(GoalStatus.DRAFT);

        return goalRepository.save(goal);
    }

    @Override
    public List<Goal> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        return goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );
    }

    @Override
    public List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        return goalRepository.findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                managerId,
                employeeId,
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );
    }

    @Override
    public void submitPredefinedGoals(String managerId, String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        List<Goal> goals = goalRepository.findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                managerId,
                employeeId,
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );

        if (goals.isEmpty()) {
            throw new BusinessException("No predefined goals found to submit");
        }

        if (goals.size() > 5) {
            throw new BusinessException("Maximum 5 predefined goals allowed");
        }

        int totalWeightage = goals.stream()
                .mapToInt(Goal::getWeightage)
                .sum();

        if (totalWeightage != 100) {
            throw new BusinessException("Total weightage must be 100%");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.SUBMITTED);
        }

        goalRepository.saveAll(goals);
    }

    // ================= SMART GOALS (EMPLOYEE) =================

    @Override
    public Goal saveSmartGoal(Goal goal, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        if (quarter == null) {
            throw new BusinessException("Quarter is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new BusinessException("Employee ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new BusinessException("Goal title is required");
        }

        if (goal.getWeightage() == null || goal.getWeightage() <= 0) {
            throw new BusinessException("Weightage must be greater than 0");
        }

        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.SMART
        );

        if (count >= 5) {
            throw new BusinessException("Maximum 5 SMART goals allowed");
        }

        goal.setPerformanceCycle(activeCycle);
        goal.setQuarter(quarter);
        goal.setGoalType(GoalType.SMART);
        goal.setStatus(GoalStatus.DRAFT);

        return goalRepository.save(goal);
    }

    @Override
    public List<Goal> getSmartGoalsByEmployee(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        return goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.SMART
        );
    }

    @Override
    public void submitSmartGoals(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = cycleRepository.findByStatus(CycleStatus.PUBLISHED)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.SMART
        );

        if (goals.isEmpty()) {
            throw new BusinessException("No SMART goals found to submit");
        }

        if (goals.size() > 5) {
            throw new BusinessException("Maximum 5 SMART goals allowed");
        }

        int totalWeightage = goals.stream()
                .mapToInt(Goal::getWeightage)
                .sum();

        if (totalWeightage != 100) {
            throw new BusinessException("Total weightage must be 100%");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.SUBMITTED);
        }

        goalRepository.saveAll(goals);
    }
}