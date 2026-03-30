package com.cdl.epms.service.serviceImpl;

import com.cdl.epms.common.enums.*;
import com.cdl.epms.dto.goal.*;
import com.cdl.epms.exception.ConflictException;
import com.cdl.epms.exception.ResourceNotFoundException;
import com.cdl.epms.exception.ValidationException;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.GoalMaster;
import com.cdl.epms.model.PerformanceCycle;
import com.cdl.epms.repository.GoalMasterRepository;
import com.cdl.epms.repository.GoalRepository;
import com.cdl.epms.repository.PerformanceCycleRepository;
import com.cdl.epms.service.services.GoalService;
import com.cdl.epms.util.GoalCategoryUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class GoalServiceImpl implements GoalService {

    private final GoalRepository goalRepository;
    private final PerformanceCycleRepository cycleRepository;
    private final ModelMapper modelMapper;
    private final GoalMasterRepository goalMasterRepository;

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
        newGoal.setYear(activeCycle.getYear());
        newGoal.setQuarter(quarter);
        newGoal.setGoalType(GoalType.PREDEFINED);
        newGoal.setStatus(GoalStatus.NOT_STARTED);

        return goalRepository.save(newGoal);
    }

    @Override
    public List<GoalResponseDto> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter, Integer year) {

        List<Goal> goals = goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalTypeIn(
                employeeId,
                quarter,
                year,
                List.of(GoalType.PREDEFINED, GoalType.SMART)
        );

        return goals.stream()
                .map(goal -> modelMapper.map(goal, GoalResponseDto.class))
                .toList();
    }

    @Override
    public List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter, Integer year) {
        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }
        if (managerId == null || managerId.trim().isEmpty()) {
            throw new ValidationException("Manager ID is required");
        }
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (year == null) {
            throw new ValidationException("Year is required");
        }

        return goalRepository.findByManagerIdAndEmployeeIdAndQuarterAndYearAndGoalType(
                managerId,
                employeeId,
                quarter,
                year,
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
        try {
            PerformanceCycle activeCycle = getActiveCycle();

            log.info("=== SAVE SMART GOAL START ===");
            log.info("EmployeeId: {}", goal.getEmployeeId());
            log.info("ManagerId: {}", goal.getManagerId());
            log.info("Title: {}", goal.getTitle());
            log.info("Quarter: {}", quarter);

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

            if (goal.getWeightage() == null) {
                goal.setWeightage(0);
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

            Goal newGoal = new Goal();
            newGoal.setPerformanceCycle(activeCycle);
            newGoal.setYear(activeCycle.getYear());
            newGoal.setQuarter(quarter);
            newGoal.setEmployeeId(goal.getEmployeeId());
            newGoal.setManagerId(goal.getManagerId());
            newGoal.setGoalType(GoalType.SMART);
            newGoal.setTitle(goal.getTitle());
            newGoal.setGoalDescription(goal.getGoalDescription());
            newGoal.setTargetKPI(goal.getTargetKPI());
            newGoal.setWeightage(goal.getWeightage());
            newGoal.setAchievableTarget(goal.getAchievableTarget());
            newGoal.setSelfReviewComments(goal.getSelfReviewComments());
            newGoal.setCreatedAt(LocalDateTime.now());
            newGoal.setStatus(GoalStatus.SELF_REVIEWED);

            log.info("Attempting to save SMART goal with ManagerId: {}", newGoal.getManagerId());

            Goal savedGoal = goalRepository.save(newGoal);
            log.info("SMART goal saved successfully with ID: {}", savedGoal.getId());
            return savedGoal;

        } catch (Exception e) {
            log.error("ERROR in saveSmartGoal: ", e);
            throw new RuntimeException("Failed to save SMART goal: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public Goal saveSmartGoalWithSelfReview(Goal goal, Quarter quarter, Integer overallRating, String overallComment) {
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

        // Check limit
        long count = goalRepository.countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
                goal.getEmployeeId(),
                activeCycle,
                quarter,
                GoalType.SMART
        );

        if (count >= 5) {
            throw new ConflictException("Maximum 5 SMART goals allowed");
        }

        // Create new Goal object (same table)
        Goal newGoal = new Goal();
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setYear(activeCycle.getYear());
        newGoal.setQuarter(quarter);
        newGoal.setEmployeeId(goal.getEmployeeId());
        newGoal.setManagerId(goal.getManagerId());
        newGoal.setGoalType(GoalType.SMART);
        newGoal.setTitle(goal.getTitle());
        newGoal.setGoalDescription(goal.getGoalDescription());
        newGoal.setTargetKPI(goal.getTargetKPI());
        newGoal.setWeightage(goal.getWeightage() != null ? goal.getWeightage() : 0);
        newGoal.setAchievableTarget(goal.getAchievableTarget());
        newGoal.setSelfReviewComments(goal.getSelfReviewComments());
        newGoal.setCreatedAt(LocalDateTime.now());
        newGoal.setSelfReviewSubmittedDate(LocalDateTime.now());
        newGoal.setOverallSelfAssessmentRating(overallRating);
        newGoal.setOverallSelfReviewComments(overallComment);
        newGoal.setStatus(GoalStatus.SELF_REVIEWED);

        Goal savedGoal = goalRepository.save(newGoal);
        log.info("SMART goal created with self-review, ID: {}, Title: {}", savedGoal.getId(), savedGoal.getTitle());
        return savedGoal;
    }

    @Override
    public List<Goal> getSmartGoalsByEmployee(String employeeId, Quarter quarter, Integer year) {
        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (year == null) {
            throw new ValidationException("Year is required");
        }

        return goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalType(
                employeeId,
                quarter,
                year,
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
        newGoal.setYear(activeCycle.getYear());
        newGoal.setQuarter(quarter);
        newGoal.setGoalType(GoalType.DEVELOPMENT);
        newGoal.setStatus(GoalStatus.NOT_STARTED);

        return goalRepository.save(newGoal);
    }

    @Override
    public List<Goal> getDevelopmentGoalsByEmployee(String employeeId, Quarter quarter, Integer year) {
        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (year == null) {
            throw new ValidationException("Year is required");
        }

        return goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalType(
                employeeId,
                quarter,
                year,
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
                List.of(GoalType.PREDEFINED, GoalType.SMART, GoalType.DEVELOPMENT)
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No SMART/DEVELOPMENT goals found to submit");
        }

        for (Goal goal : goals) {
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

        LocalDateTime now = LocalDateTime.now();

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.ACCEPTED_BY_EMPLOYEE);
            goal.setSelfAcceptedDate(now);
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

    @Override
    @Transactional
    public List<Goal> assignPredefinedGoals(AssignPredefinedGoalsRequestDto requestDto) {
        log.info("Assigning predefined goals to employee: {} for quarter {} year {}",
                requestDto.getEmployeeId(), requestDto.getQuarter(), requestDto.getYear());

        if (requestDto.getEmployeeId() == null || requestDto.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (requestDto.getQuarter() == null) {
            throw new ValidationException("Quarter is required");
        }
        if (requestDto.getYear() == null) {
            throw new ValidationException("Year is required");
        }
        if (requestDto.getGoalMasterIds() == null || requestDto.getGoalMasterIds().isEmpty()) {
            throw new ValidationException("At least one goal must be selected");
        }

        try {
            PerformanceCycle cycle = findCycle(requestDto.getYear(), requestDto.getQuarter());
            log.info("Using cycle: ID={}, Year={}, Quarter={}", cycle.getId(), cycle.getYear(), cycle.getQuarter());

            List<Goal> savedGoals = new ArrayList<>();

            for (Long goalMasterId : requestDto.getGoalMasterIds()) {
                GoalMaster goalMaster = goalMasterRepository.findById(goalMasterId)
                        .orElseThrow(() -> new ResourceNotFoundException("Goal master not found with id: " + goalMasterId));

                boolean goalExists = goalRepository.existsByEmployeeIdAndQuarterAndYearAndTitle(
                        requestDto.getEmployeeId(),
                        requestDto.getQuarter(),
                        requestDto.getYear(),
                        goalMaster.getDifferentiatorName()
                );

                if (goalExists) {
                    log.warn("Goal already exists for employee {}: {}", requestDto.getEmployeeId(), goalMaster.getDifferentiatorName());
                    continue;
                }

                Goal goal = new Goal();
                goal.setPerformanceCycle(cycle);
                goal.setYear(requestDto.getYear());
                goal.setQuarter(requestDto.getQuarter());
                goal.setEmployeeId(requestDto.getEmployeeId());
                goal.setManagerId(requestDto.getManagerId());
                goal.setGoalCategory(goalMaster.getCategory());
                goal.setGoalType(GoalType.PREDEFINED);
                goal.setTitle(goalMaster.getDifferentiatorName());
                goal.setDescription(goalMaster.getDefinition());
                goal.setStatus(GoalStatus.NOT_STARTED);
                goal.setCreatedAt(LocalDateTime.now());

                Goal savedGoal = goalRepository.save(goal);
                savedGoals.add(savedGoal);
                log.info("Saved goal with ID: {}, Title: {}", savedGoal.getId(), savedGoal.getTitle());
            }

            log.info("Successfully assigned {} goals to employee {}", savedGoals.size(), requestDto.getEmployeeId());
            return savedGoals;

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            throw new ConflictException("Failed to save goals due to duplicate entry or constraint violation");
        } catch (Exception e) {
            log.error("Error assigning predefined goals: {}", e.getMessage(), e);
            throw e;
        }
    }

    @Override
    @Transactional
    public List<Goal> updatePredefinedGoals(UpdatePredefinedGoalsRequestDto requestDto) {
        log.info("Updating predefined goals for employee: {}, quarter: {}, year: {}",
                requestDto.getEmployeeId(), requestDto.getQuarter(), requestDto.getYear());

        Quarter quarterEnum;
        try {
            quarterEnum = Quarter.valueOf(requestDto.getQuarter());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid quarter: " + requestDto.getQuarter());
        }

        List<Goal> updatedGoals = new ArrayList<>();

        for (UpdatePredefinedGoalsRequestDto.GoalUpdateDto goalDto : requestDto.getGoals()) {
            Goal goal = goalRepository.findById(goalDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalDto.getId()));

            if (!goal.getEmployeeId().equals(requestDto.getEmployeeId())) {
                throw new ValidationException("Goal does not belong to this employee");
            }
            if (!goal.getQuarter().equals(quarterEnum)) {
                throw new ValidationException("Goal quarter mismatch");
            }
            if (!goal.getYear().equals(requestDto.getYear())) {
                throw new ValidationException("Goal year mismatch");
            }

            if (goalDto.getGoalDescription() != null) {
                goal.setGoalDescription(goalDto.getGoalDescription());
            }
            if (goalDto.getTargetKPI() != null) {
                goal.setTargetKPI(goalDto.getTargetKPI());
            }
            if (goalDto.getWeightage() != null) {
                if (goalDto.getWeightage() < 0 || goalDto.getWeightage() > 100) {
                    throw new ValidationException("Weightage must be between 0 and 100");
                }
                goal.setWeightage(goalDto.getWeightage());
            }
            if (goalDto.getAchievableTarget() != null) {
                goal.setAchievableTarget(goalDto.getAchievableTarget());
            }
            if (goalDto.getSelfReviewComments() != null) {
                goal.setSelfReviewComments(goalDto.getSelfReviewComments());
            }

            if (goalDto.getTimeline() != null) {
                if (goalDto.getTimeline().isEmpty()) {
                    goal.setTimeline(null);
                } else {
                    for (String q : goalDto.getTimeline()) {
                        if (!q.matches("Q[1-4]")) {
                            throw new ValidationException("Invalid quarter format: " + q + ". Must be Q1, Q2, Q3, or Q4");
                        }
                    }
                    String timelineString = String.join(",", goalDto.getTimeline());
                    goal.setTimeline(timelineString);
                }
            }

            if (requestDto.isSaveAsDraft()) {
                goal.setStatus(GoalStatus.DRAFT);
            }

            goal.setSubmittedToEmployeeAt(LocalDateTime.now());
            updatedGoals.add(goalRepository.save(goal));
        }

        log.info("Successfully updated {} goals", updatedGoals.size());
        return updatedGoals;
    }

    private PerformanceCycle findCycle(Integer year, Quarter quarter) {
        Optional<PerformanceCycle> specificCycle = cycleRepository.findByYearAndQuarterAndCycleType(
                year, quarter, CycleType.QUARTERLY);

        if (specificCycle.isPresent()) {
            return specificCycle.get();
        }

        Optional<PerformanceCycle> activeCycle = cycleRepository.findByStatus(CycleStatus.ACTIVE);

        if (activeCycle.isPresent()) {
            return activeCycle.get();
        }

        List<PerformanceCycle> allCycles = cycleRepository.findAll();
        if (!allCycles.isEmpty()) {
            return allCycles.get(0);
        }

        throw new ResourceNotFoundException("No cycles found in the system. Please create a cycle first.");
    }

    @Override
    @Transactional
    public List<Goal> submitSelfReview(SelfReviewRequestDto requestDto) {
        log.info("Submitting self-review for employee: {}, quarter: {}, year: {}",
                requestDto.getEmployeeId(), requestDto.getQuarter(), requestDto.getYear());

        Quarter quarterEnum;
        try {
            quarterEnum = Quarter.valueOf(requestDto.getQuarter());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid quarter: " + requestDto.getQuarter());
        }

        if (requestDto.getGoals() == null || requestDto.getGoals().isEmpty()) {
            throw new ValidationException("At least one goal must be provided");
        }

        List<Goal> updatedGoals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        for (SelfReviewRequestDto.SelfReviewGoalDto goalDto : requestDto.getGoals()) {
            Goal goal = goalRepository.findById(goalDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalDto.getId()));

            log.info("Processing goal ID: {}, current status: {}", goal.getId(), goal.getStatus());

            if (!requestDto.getEmployeeId().equals(goal.getEmployeeId())) {
                throw new ValidationException("Goal does not belong to this employee");
            }

            if (!quarterEnum.equals(goal.getQuarter())) {
                throw new ValidationException("Goal quarter mismatch");
            }

            if (!requestDto.getYear().equals(goal.getYear())) {
                throw new ValidationException("Goal year mismatch");
            }

            if (goal.getStatus() != GoalStatus.SENT_TO_EMPLOYEE &&
                    goal.getStatus() != GoalStatus.MANAGER_REVIEWED &&
                    goal.getStatus() != GoalStatus.DRAFT) {
                throw new ValidationException(
                        "Goal is not eligible for self-review. Current status: " + goal.getStatus()
                );
            }

            if (goalDto.getGoalDescription() != null) {
                goal.setGoalDescription(goalDto.getGoalDescription());
            }

            if (goalDto.getTargetKPI() != null) {
                goal.setTargetKPI(goalDto.getTargetKPI());
            }

            if (goalDto.getAchievableTarget() != null) {
                goal.setAchievableTarget(goalDto.getAchievableTarget());
            }

            if (goalDto.getSelfReviewComments() != null) {
                goal.setSelfReviewComments(goalDto.getSelfReviewComments());
            }

            goal.setSelfAssessmentRating(goalDto.getSelfAssessmentRating());

            if (goal.getSelfAssessmentRating() != null &&
                    (goal.getSelfAssessmentRating() < 1 || goal.getSelfAssessmentRating() > 5)) {
                throw new ValidationException("Rating must be between 1 and 5");
            }

            if (goalDto.getOverallSelfAssessmentRating() != null) {
                goal.setOverallSelfAssessmentRating(goalDto.getOverallSelfAssessmentRating());
            }
            if (goalDto.getOverallSelfReviewComments() != null) {
                goal.setOverallSelfReviewComments(goalDto.getOverallSelfReviewComments());
            }

            goal.setSelfReviewSubmittedDate(now);
            goal.setStatus(GoalStatus.SELF_REVIEWED);

            updatedGoals.add(goalRepository.save(goal));
        }

        log.info("Successfully submitted self-review for {} goals", updatedGoals.size());
        return updatedGoals;
    }

    @Override
    public List<Goal> getGoalsPendingSelfReview(String employeeId, Quarter quarter, Integer year) {
        log.info("Fetching goals pending self-review for employee: {}, quarter: {}, year: {}",
                employeeId, quarter, year);

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }
        if (year == null) {
            throw new ValidationException("Year is required");
        }

        List<GoalStatus> eligibleStatuses = Arrays.asList(
                GoalStatus.SENT_TO_EMPLOYEE,
                GoalStatus.MANAGER_REVIEWED,
                GoalStatus.DRAFT
        );

        List<Goal> pendingGoals = new ArrayList<>();

        List<Goal> predefinedGoals = goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatusIn(
                employeeId,
                quarter,
                year,
                GoalType.PREDEFINED,
                eligibleStatuses
        );
        pendingGoals.addAll(predefinedGoals);

        List<Goal> smartGoals = goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatusIn(
                employeeId,
                quarter,
                year,
                GoalType.SMART,
                eligibleStatuses
        );
        pendingGoals.addAll(smartGoals);

        List<Goal> developmentGoals = goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatusIn(
                employeeId,
                quarter,
                year,
                GoalType.DEVELOPMENT,
                eligibleStatuses
        );
        pendingGoals.addAll(developmentGoals);

        log.info("Found {} goals pending self-review for employee {}", pendingGoals.size(), employeeId);
        return pendingGoals;
    }

    @Override
    @Transactional
    public List<Goal> submitManagerReview(ManagerReviewRequestDto requestDto) {
        log.info("Submitting manager review for employee: {}, quarter: {}, year: {}",
                requestDto.getEmployeeId(), requestDto.getQuarter(), requestDto.getYear());

        Quarter quarterEnum;
        try {
            quarterEnum = Quarter.valueOf(requestDto.getQuarter());
        } catch (IllegalArgumentException e) {
            throw new ValidationException("Invalid quarter: " + requestDto.getQuarter());
        }

        if (requestDto.getGoals() == null || requestDto.getGoals().isEmpty()) {
            throw new ValidationException("At least one goal must be provided");
        }

        if (requestDto.getManagerOverallSelfAssessmentRating() != null &&
                (requestDto.getManagerOverallSelfAssessmentRating() < 1 ||
                        requestDto.getManagerOverallSelfAssessmentRating() > 5)) {
            throw new ValidationException("Manager rating must be between 1 and 5");
        }

        List<Goal> updatedGoals = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();

        String category = GoalCategoryUtil.calculateCategory(
                requestDto.getPotential(),
                requestDto.getPerformance()
        );

        for (ManagerReviewRequestDto.ManagerReviewGoalDto goalDto : requestDto.getGoals()) {
            Goal goal = goalRepository.findById(goalDto.getId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                            "Goal not found with id: " + goalDto.getId()));

            if (!requestDto.getManagerId().equals(goal.getManagerId())) {
                throw new ValidationException("Goal does not belong to this manager");
            }

            if (!requestDto.getEmployeeId().equals(goal.getEmployeeId())) {
                throw new ValidationException("Goal does not belong to this employee");
            }

            if (!quarterEnum.equals(goal.getQuarter())) {
                throw new ValidationException("Goal quarter mismatch");
            }

            if (!requestDto.getYear().equals(goal.getYear())) {
                throw new ValidationException("Goal year mismatch");
            }

            if (goal.getStatus() != GoalStatus.SUBMITTED_TO_MANAGER &&
                    goal.getStatus() != GoalStatus.SELF_REVIEWED) {
                throw new ValidationException(
                        "Goal not eligible for manager review. Current status: " + goal.getStatus());
            }

            goal.setManagerOverallSelfAssessmentRating(
                    requestDto.getManagerOverallSelfAssessmentRating());
            goal.setManagerOverallSelfReviewComments(
                    requestDto.getManagerOverallSelfReviewComments());
            goal.setAchievementLevel(requestDto.getAchievementLevel());
            goal.setPotential(requestDto.getPotential());
            goal.setPerformance(requestDto.getPerformance());
            goal.setTalentOrCriticalResource(requestDto.getTalentOrCriticalResource());
            goal.setTalentMatrixCategory(category);
            goal.setStatus(GoalStatus.MANAGER_REVIEWED);
            goal.setReviewedAt(now);

            updatedGoals.add(goalRepository.save(goal));
        }

        log.info("Successfully submitted manager review for {} goals", updatedGoals.size());
        return updatedGoals;
    }

    @Override
    @Transactional
    public void selfAcceptGoals(String employeeId, Quarter quarter, Integer year) {
        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (year == null) {
            throw new ValidationException("Year is required");
        }

        List<GoalStatus> allowedStatuses = List.of(
                GoalStatus.SENT_TO_EMPLOYEE,
                GoalStatus.MANAGER_REVIEWED,
                GoalStatus.SELF_REVIEWED
        );

        List<Goal> goals = goalRepository.findByEmployeeIdAndQuarterAndYearAndStatusIn(
                employeeId,
                quarter,
                year,
                allowedStatuses
        );

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found for acceptance");
        }

        LocalDateTime now = LocalDateTime.now();

        for (Goal goal : goals) {
            goal.setStatus(GoalStatus.ACCEPTED_BY_EMPLOYEE);
            goal.setSelfAcceptedDate(now);
        }

        goalRepository.saveAll(goals);
    }

    @Override
    public void deleteGoals(List<Long> goalIds) {
        if (goalIds == null || goalIds.isEmpty()) {
            throw new ValidationException("Goal IDs are required");
        }

        List<Goal> goals = goalRepository.findAllById(goalIds);

        if (goals.isEmpty()) {
            throw new ResourceNotFoundException("No goals found");
        }

        goalRepository.deleteAll(goals);
    }

    @Override
    @Transactional
    public Goal updateSmartGoalDraft(Long goalId, Goal goal) {
        log.info("Updating SMART goal draft with ID: {}", goalId);

        if (goalId == null) {
            throw new ValidationException("Goal ID is required");
        }

        Goal existingGoal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (!existingGoal.getEmployeeId().equals(goal.getEmployeeId())) {
            throw new ValidationException("Goal does not belong to this employee");
        }

        if (existingGoal.getGoalType() != GoalType.SMART) {
            throw new ValidationException("Only SMART goals can be updated through this endpoint");
        }

        if (goal.getTitle() != null) {
            existingGoal.setTitle(goal.getTitle());
        }
        if (goal.getGoalDescription() != null) {
            existingGoal.setGoalDescription(goal.getGoalDescription());
        }
        if (goal.getTargetKPI() != null) {
            existingGoal.setTargetKPI(goal.getTargetKPI());
        }
        if (goal.getWeightage() != null) {
            existingGoal.setWeightage(goal.getWeightage());
        }
        if (goal.getAchievableTarget() != null) {
            existingGoal.setAchievableTarget(goal.getAchievableTarget());
        }
        if (goal.getSelfReviewComments() != null) {
            existingGoal.setSelfReviewComments(goal.getSelfReviewComments());
        }

        if (existingGoal.getStatus() != GoalStatus.DRAFT) {
            existingGoal.setStatus(GoalStatus.DRAFT);
        }

        existingGoal.setCreatedAt(LocalDateTime.now());

        Goal updatedGoal = goalRepository.save(existingGoal);
        log.info("SMART goal draft updated successfully with ID: {}", updatedGoal.getId());
        return updatedGoal;
    }

    @Override
    @Transactional
    public Goal submitSmartGoalDraft(Long goalId) {
        log.info("Submitting SMART goal draft with ID: {}", goalId);

        if (goalId == null) {
            throw new ValidationException("Goal ID is required");
        }

        Goal goal = goalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Goal not found with id: " + goalId));

        if (goal.getGoalType() != GoalType.SMART) {
            throw new ValidationException("Only SMART goals can be submitted");
        }

        if (goal.getStatus() != GoalStatus.DRAFT) {
            throw new ValidationException("Only goals in DRAFT status can be submitted. Current status: " + goal.getStatus());
        }

        goal.setStatus(GoalStatus.SUBMITTED_TO_MANAGER);
        goal.setCreatedAt(LocalDateTime.now());

        Goal submittedGoal = goalRepository.save(goal);
        log.info("SMART goal draft submitted successfully with ID: {}", submittedGoal.getId());
        return submittedGoal;
    }

    @Override
    public List<Goal> getDraftSmartGoalsByEmployee(String employeeId, Quarter quarter, Integer year) {
        log.info("Fetching DRAFT SMART goals for employee: {}, quarter: {}, year: {}", employeeId, quarter, year);

        if (employeeId == null || employeeId.trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }
        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }
        if (year == null) {
            throw new ValidationException("Year is required");
        }

        return goalRepository.findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatus(
                employeeId,
                quarter,
                year,
                GoalType.SMART,
                GoalStatus.DRAFT
        );
    }

    @Override
    @Transactional
    public Goal saveSmartGoalAndSubmitSelfReview(Goal goal, Quarter quarter, Integer overallRating, String overallComment) {
        log.info("Saving SMART goal and submitting self-review for employee: {}", goal.getEmployeeId());

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new ValidationException("Goal title is required");
        }

        Goal newGoal = new Goal();
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setYear(activeCycle.getYear());
        newGoal.setQuarter(quarter);
        newGoal.setEmployeeId(goal.getEmployeeId());
        newGoal.setManagerId(goal.getManagerId());
        newGoal.setGoalType(GoalType.SMART);
        newGoal.setTitle(goal.getTitle());
        newGoal.setGoalDescription(goal.getGoalDescription());
        newGoal.setTargetKPI(goal.getTargetKPI());
        newGoal.setWeightage(goal.getWeightage() != null ? goal.getWeightage() : 0);
        newGoal.setAchievableTarget(goal.getAchievableTarget());
        newGoal.setSelfReviewComments(goal.getSelfReviewComments());
        newGoal.setCreatedAt(LocalDateTime.now());
        newGoal.setSelfReviewSubmittedDate(LocalDateTime.now());
        newGoal.setOverallSelfAssessmentRating(overallRating);
        newGoal.setOverallSelfReviewComments(overallComment);
        newGoal.setStatus(GoalStatus.SELF_REVIEWED);

        Goal savedGoal = goalRepository.save(newGoal);
        log.info("SMART goal created with self-review, ID: {}", savedGoal.getId());
        return savedGoal;
    }

    @Override
    @Transactional
    public Goal saveSmartGoalAsDraft(Goal goal, Quarter quarter) {
        log.info("Saving SMART goal as DRAFT for employee: {}, quarter: {}", goal.getEmployeeId(), quarter);

        PerformanceCycle activeCycle = getActiveCycle();

        if (quarter == null) {
            throw new ValidationException("Quarter is required");
        }

        if (goal.getEmployeeId() == null || goal.getEmployeeId().trim().isEmpty()) {
            throw new ValidationException("Employee ID is required");
        }

        if (goal.getTitle() == null || goal.getTitle().trim().isEmpty()) {
            throw new ValidationException("Goal title is required");
        }

        if (goal.getWeightage() == null) {
            goal.setWeightage(0);
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

        Goal newGoal = new Goal();
        newGoal.setPerformanceCycle(activeCycle);
        newGoal.setYear(activeCycle.getYear());
        newGoal.setQuarter(quarter);
        newGoal.setEmployeeId(goal.getEmployeeId());
        newGoal.setManagerId(goal.getManagerId());
        newGoal.setGoalType(GoalType.SMART);
        newGoal.setTitle(goal.getTitle());
        newGoal.setGoalDescription(goal.getGoalDescription());
        newGoal.setTargetKPI(goal.getTargetKPI());
        newGoal.setWeightage(goal.getWeightage());
        newGoal.setAchievableTarget(goal.getAchievableTarget());
        newGoal.setSelfReviewComments(goal.getSelfReviewComments());
        newGoal.setCreatedAt(LocalDateTime.now());
        newGoal.setStatus(GoalStatus.DRAFT);

        Goal savedGoal = goalRepository.save(newGoal);
        log.info("SMART goal saved as DRAFT with ID: {}", savedGoal.getId());
        return savedGoal;
    }
}