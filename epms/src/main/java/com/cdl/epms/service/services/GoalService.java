package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.goal.*;
import com.cdl.epms.model.Goal;

import java.util.List;

public interface GoalService {

    // ==================== PREDEFINED GOALS ====================

    Goal savePredefinedGoal(Goal goal, Quarter quarter);

    List<GoalResponseDto> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter, Integer year);

    List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter, Integer year);

    void submitPredefinedGoals(String managerId, String employeeId, Quarter quarter);

    List<Goal> updatePredefinedGoals(UpdatePredefinedGoalsRequestDto requestDto);

    List<Goal> assignPredefinedGoals(AssignPredefinedGoalsRequestDto requestDto);

    // ==================== SMART GOALS ====================

    Goal saveSmartGoal(Goal goal, Quarter quarter);

    Goal saveSmartGoalAsDraft(Goal goal, Quarter quarter);

    Goal saveSmartGoalWithSelfReview(Goal goal, Quarter quarter, Integer overallRating, String overallComment);

    Goal saveSmartGoalAndSubmitSelfReview(Goal goal, Quarter quarter, Integer overallRating, String overallComment);

    List<Goal> getSmartGoalsByEmployee(String employeeId, Quarter quarter, Integer year);

    List<Goal> getDraftSmartGoalsByEmployee(String employeeId, Quarter quarter, Integer year);

    void submitSmartGoals(String employeeId, Quarter quarter);

    Goal updateSmartGoalDraft(Long goalId, Goal goal);

    Goal submitSmartGoalDraft(Long goalId);

    // ==================== DEVELOPMENT GOALS ====================

    Goal saveDevelopmentGoal(Goal goal, Quarter quarter);

    List<Goal> getDevelopmentGoalsByEmployee(String employeeId, Quarter quarter, Integer year);

    void submitDevelopmentGoals(String employeeId, Quarter quarter);

    // ==================== MANAGER OPERATIONS ====================

    List<String> getTeamEmployeesByManager(String managerId, Quarter quarter);

    List<Goal> getGoalsForManagerReview(String managerId, String employeeId, Quarter quarter);

    Goal updateManagerReview(Long goalId, Integer rating, String comment);

    void submitManagerReviewToEmployee(String managerId, String employeeId, Quarter quarter);

    List<Goal> submitManagerReview(ManagerReviewRequestDto requestDto);

    // ==================== EMPLOYEE SELF REVIEW ====================

    List<Goal> submitSelfReview(SelfReviewRequestDto requestDto);

    List<Goal> getGoalsPendingSelfReview(String employeeId, Quarter quarter, Integer year);

    // ==================== ACCEPTANCE OPERATIONS ====================

    List<Goal> getPendingGoalsForAcceptance(String employeeId, Quarter quarter);

    void acceptReviewedGoals(String employeeId, Quarter quarter);

    void selfAcceptGoals(String employeeId, Quarter quarter, Integer year);

    // ==================== HR OPERATIONS ====================

    void finalSubmitToHR(String employeeId, Quarter quarter);

    // ==================== DELETE OPERATIONS ====================

    void deleteGoals(List<Long> goalIds);
}