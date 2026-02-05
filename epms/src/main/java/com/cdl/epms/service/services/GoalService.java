package com.cdl.epms.service.services;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;

import java.util.List;

public interface GoalService {

    Goal savePredefinedGoal(Goal goal, Quarter quarter);

    List<Goal> getPredefinedGoalsByEmployee(String employeeId, Quarter quarter);

    List<Goal> getPredefinedGoalsByManager(String managerId, String employeeId, Quarter quarter);

    void submitPredefinedGoals(String managerId, String employeeId, Quarter quarter);
}