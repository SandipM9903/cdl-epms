package com.cdl.epms.repository;

import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarter(String employeeId, PerformanceCycle performanceCycle, Quarter quarter);

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarter(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter);

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    long countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    long countByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);
}