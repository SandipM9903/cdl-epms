package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.CycleStatus;
import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.GoalService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleRepository cycleRepository;
    private final ModelMapper modelMapper;

    private PerformanceCycle getActiveCycle() {
        return cycleRepository.findByStatus(CycleStatus.ACTIVE)
                .orElseThrow(() -> new ResourceNotFoundException("No active cycle found"));
    }

    @Override
    public Goal savePredefinedGoal(Goal goal, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (goal == null) {
            throw new ValidationException("Goal data is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (goal.getManagerId() == null || goal.getManagerId().trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new ValidationException("Goal title is required");
        }

        if (goal.getWeightage() == null || goal.getWeightage() <= 0) {
            throw new ValidationException("Weightage must be greater than 0");
        }

        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );

        if (count >= 5) {
            throw new ConflictException("Maximum 5 predefined goals allowed");
        }

        Goal newGoal = modelMapper.map(goal, Goal.class);
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setQuarter(quarter);
        newGoal.setGoalType(GoalType.PREDEFINED);
        newGoal.setStatus(GoalStatus.NOT_STARTED);

        return goalRepository.save(newGoal);
    }

    @Override
    public List<Goal> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        return goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );
    }

    @Override
    public List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

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

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                managerId,
                employeeId,
                activeCycle,
                quarter,
                GoalType.PREDEFINED
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No predefined goals found to submit");
        }

        if (goals.size() > 5) {
            throw new ConflictException("Maximum 5 predefined goals allowed");
        }

        int totalWeightage = goals.stream()
                .mapToInt(Goal::getWeightage)
                .sum();

        if (totalWeightage != 100) {
            throw new ConflictException("Total weightage must be 100%");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.PREDEFINED_SUBMITTED);
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public Goal saveSmartGoal(Goal goal, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (goal == null) {
            throw new ValidationException("Goal data is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new ValidationException("Goal title is required");
        }

        if (goal.getWeightage() == null || goal.getWeightage() <= 0) {
            throw new ValidationException("Weightage must be greater than 0");
        }

        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.SMART
        );

        if (count >= 5) {
            throw new ConflictException("Maximum 5 SMART goals allowed");
        }

        Goal newGoal = modelMapper.map(goal, Goal.class);
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setQuarter(quarter);
        newGoal.setGoalType(GoalType.SMART);
        newGoal.setStatus(GoalStatus.NOT_STARTED);

        return goalRepository.save(newGoal);
    }

    @Override
    public List<Goal> getSmartGoalsByEmployee(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        return goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.SMART
        );
    }

    @Override
    public void submitSmartGoals(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.SMART
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No SMART goals found to submit");
        }

        if (goals.size() > 5) {
            throw new ConflictException("Maximum 5 SMART goals allowed");
        }

        int totalWeightage = goals.stream()
                .mapToInt(Goal::getWeightage)
                .sum();

        if (totalWeightage != 100) {
            throw new ConflictException("Total weightage must be 100%");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.SUBMITTED_TO_MANAGER);
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public Goal saveDevelopmentGoal(Goal goal, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (goal == null) {
            throw new ValidationException("Goal data is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new ValidationException("Goal title is required");
        }

        if (goal.getWeightage() == null || goal.getWeightage() <= 0) {
            throw new ValidationException("Weightage must be greater than 0");
        }

        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.DEVELOPMENT
        );

        if (count >= 5) {
            throw new ConflictException("Maximum 5 development goals allowed");
        }

        Goal newGoal = modelMapper.map(goal, Goal.class);
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setQuarter(quarter);
        newGoal.setGoalType(GoalType.DEVELOPMENT);
        newGoal.setStatus(GoalStatus.NOT_STARTED);

        return goalRepository.save(newGoal);
    }

    @Override
    public List<Goal> getDevelopmentGoalsByEmployee(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        return goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.DEVELOPMENT
        );
    }

    @Override
    public void submitDevelopmentGoals(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                employeeId,
                activeCycle,
                quarter,
                GoalType.DEVELOPMENT
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No development goals found to submit");
        }

        if (goals.size() > 5) {
            throw new ConflictException("Maximum 5 development goals allowed");
        }

        int totalWeightage = goals.stream()
                .mapToInt(Goal::getWeightage)
                .sum();

        if (totalWeightage != 100) {
            throw new ConflictException("Total weightage must be 100%");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.SUBMITTED_TO_MANAGER);
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public List<String> getTeamEmployeesByManager(String managerId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        List<Goal> goals = goalRepository.findByManagerIdAndPerformanceCycleAndQuarter(
                managerId,
                activeCycle,
                quarter
        );

        return goals.stream()
                .map(Goal::getEmployeeId)
                .distinct()
                .toList();
    }

    @Override
    public List<Goal> getGoalsForManagerReview(String managerId, String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        return goalRepository.findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarter(
                managerId,
                employeeId,
                activeCycle,
                quarter
        );
    }

    @Override
    public Goal updateManagerReview(Long goalId, Integer rating, String comment) {

        if (goalId == null) {
            throw new ValidationException("Goal ID is required");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (rating == null || rating < 1 || rating > 5) {
            throw new ValidationException("Manager rating must be between 1 and 5");
        }

        goal.setManagerRating(rating);
        goal.setManagerComment(comment);
        goal.setReviewedAt(LocalDateTime.now());
        goal.setStatus(GoalStatus.MANAGER_REVIEWED);

        return goalRepository.save(goal);
    }

    @Override
    public void submitManagerReviewToEmployee(String managerId, String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalTypeIn(
                managerId,
                employeeId,
                activeCycle,
                quarter,
                List.of(GoalType.SMART, GoalType.DEVELOPMENT)
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No SMART/DEVELOPMENT goals found to submit");
        }

        for (Goal goal : goals) {
            if (goal.getManagerRating() == null) {
                throw new ValidationException("Manager rating is required before submission");
            }
            goal.setStatus(GoalStatus.SENT_TO_EMPLOYEE);
            goal.setSubmittedToEmployeeAt(LocalDateTime.now());
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public List<Goal> getPendingGoalsForAcceptance(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndStatus(
                employeeId,
                activeCycle,
                quarter,
                GoalStatus.SENT_TO_EMPLOYEE
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals pending for acceptance");
        }

        return goals;
    }

    @Override
    public void acceptReviewedGoals(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndStatus(
                employeeId,
                activeCycle,
                quarter,
                GoalStatus.SENT_TO_EMPLOYEE
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found for acceptance");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.ACCEPTED_BY_EMPLOYEE);
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public void finalSubmitToHR(String employeeId, Quarter quarter) {

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        List<Goal> goals = goalRepository.findByEmployeeIdAndPerformanceCycleAndQuarterAndStatus(
                employeeId,
                activeCycle,
                quarter,
                GoalStatus.ACCEPTED_BY_EMPLOYEE
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No accepted goals found to submit to HR");
        }

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.FINAL_SUBMITTED_TO_HR);
        }

        goalRepository.saveAll(goals);
    }
}