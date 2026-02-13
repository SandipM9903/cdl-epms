package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;

import java.util.List;

public interface GoalService {

    Goal savePredefinedGoal(Goal goal, Quarter quarter);

    List<Goal> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter);

    List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter);

    void submitPredefinedGoals(String managerId, String employeeId, Quarter quarter);

    Goal saveSmartGoal(Goal goal, Quarter quarter);

    List<Goal> getSmartGoalsByEmployee(String employeeId, Quarter quarter);

    void submitSmartGoals(String employeeId, Quarter quarter);

    // ================= DEVELOPMENT GOALS =================

    Goal saveDevelopmentGoal(Goal goal, Quarter quarter);

    List<Goal> getDevelopmentGoalsByEmployee(String employeeId, Quarter quarter);

    void submitDevelopmentGoals(String employeeId, Quarter quarter);

    List<String> getTeamEmployeesByManager(String managerId, Quarter quarter);

    List<Goal> getGoalsForManagerReview(String managerId, String employeeId, Quarter quarter);

    Goal updateManagerReview(Long goalId, Integer rating, String comment);

    void submitManagerReviewToEmployee(String managerId, String employeeId, Quarter quarter);

    List<Goal> getPendingGoalsForAcceptance(String employeeId, Quarter quarter);

    void acceptReviewedGoals(String employeeId, Quarter quarter);

    void finalSubmitToHR(String employeeId, Quarter quarter);
}