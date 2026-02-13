package com.cdl.epms.repository;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GoalRepository extends JpaRepository<Goal, Long> {

    // ================= PREDEFINED =================

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarter(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter
    );

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType
    );

    long countByEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType
    );

    List<Goal> findByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
            String managerId,
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType
    );

    long countByManagerIdAndEmployeeIdAndPerformanceCycleAndQuarterAndGoalType(
            String managerId,
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType
    );

    // ================= DEVELOPMENT GOALS =================

    List<Goal> findByEmployeeIdAndPerformanceCycleAndQuarterAndGoalTypeAndStatus(
            String employeeId,
            PerformanceCycle performanceCycle,
            Quarter quarter,
            GoalType goalType,
            GoalStatus status
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

    // ================= ANNUAL REVIEW =================

    List<Goal> findByEmployeeIdAndPerformanceCycle_Year(String employeeId, Integer year);

    // ================= REPORTS =================

    List<Goal> findByGoalType(GoalType goalType);

    List<Goal> findByGoalTypeAndPerformanceCycle_Year(GoalType goalType, Integer year);

    List<Goal> findByGoalTypeAndQuarterAndPerformanceCycle_Year(GoalType goalType, Quarter quarter, Integer year);

    List<Goal> findByQuarterAndPerformanceCycle_Year(Quarter quarter, Integer year);

    List<Goal> findByPerformanceCycle_Year(Integer year);

    // ================= HR DASHBOARD / PROGRESS =================

    long countByQuarterAndStatus(Quarter quarter, GoalStatus status);

    long countByPerformanceCycle_Id(Long cycleId);

    long countByPerformanceCycle_IdAndGoalType(Long cycleId, GoalType goalType);

    @Query("SELECT COUNT(DISTINCT g.employeeId) FROM Goal g WHERE g.performanceCycle.id = :cycleId")
    long countDistinctEmployeesByCycleId(@Param("cycleId") Long cycleId);

    @Query("SELECT COUNT(DISTINCT g.employeeId) FROM Goal g WHERE g.quarter = :quarter")
    long countDistinctEmployeesByQuarter(@Param("quarter") Quarter quarter);

}