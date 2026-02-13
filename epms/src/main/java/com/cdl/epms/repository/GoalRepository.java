package com.cdl.epms.repository;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // ================= PREDEFINED =================
    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarter(String employeeId, PerformanceCycle performanceCycle, Quarter quarter);

    //List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarter(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter);

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    long countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    long countByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(String managerId, String employeeId, PerformanceCycle performanceCycle, Quarter quarter, GoalType goalType);

    // ================= DEVELOPMENT GOALS =================
    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalTypeAndStatus(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType,
            com.cdl.epms.common.enums.GoalStatus status
    );

    List<Goal> findByManagerIdAndPerformanceCycleAndQuarter(
            String managerId,
            PerformanceCycle performanceCycle,
            Quarter quarter
    );

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarter(
            String managerId,
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter
    );

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalTypeIn(
            String managerId,
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            List<GoalType> goalTypes
    );

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndStatus(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalStatus status
    );

    List<Goal> findByEmployeeIdAndPerformanceCycle_Year(String employeeId, Integer year);
}