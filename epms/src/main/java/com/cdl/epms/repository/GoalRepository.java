package com.cdl.epms.repository;

import com.cdl.epms.common.enums.GoalStatus;
import com.cdl.epms.common.enums.GoalType;
import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;
import com.cdl.epms.model.PerformanceCycle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GoalRepository extends JpaRepository<Goal, Long> {

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

    List<Goal> findByEmployeeIdAndQuarterAndYearAndGoalType(
            String employeeId,
            Quarter quarter,
            Integer year,
            GoalType goalType
    );

    List<Goal> findByEmployeeIdAndQuarterAndYearAndGoalTypeIn(
            String employeeId,
            Quarter quarter,
            Integer year,
            List<GoalType> goalTypes
    );

    List<Goal> findByManagerIdAndEmployeeIdAndQuarterAndYearAndGoalType(
            String managerId,
            String employeeId,
            Quarter quarter,
            Integer year,
            GoalType goalType
    );

    long countByEmployeeIdAndQuarterAndYearAndGoalType(
            String employeeId,
            Quarter quarter,
            Integer year,
            GoalType goalType
    );

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

    List<Goal> findByEmployeeIdAndPerformanceCycle_Year(String employeeId, Integer year);

    List<Goal> findByGoalType(GoalType goalType);

    List<Goal> findByGoalTypeAndPerformanceCycle_Year(GoalType goalType, Integer year);

    List<Goal> findByGoalTypeAndQuarterAndPerformanceCycle_Year(GoalType goalType, Quarter quarter, Integer year);

    List<Goal> findByQuarterAndPerformanceCycle_Year(Quarter quarter, Integer year);

    List<Goal> findByPerformanceCycle_Year(Integer year);

    long countByQuarterAndStatus(Quarter quarter, GoalStatus status);

    long countByPerformanceCycle_Id(Long cycleId);

    long countByPerformanceCycle_IdAndGoalType(Long cycleId, GoalType goalType);

    @Query("SELECT COUNT(DISTINCT g.employeeId) FROM Goal g WHERE g.performanceCycle.id = :cycleId")
    long countDistinctEmployeesByCycleId(@Param("cycleId") Long cycleId);

    @Query("SELECT COUNT(DISTINCT g.employeeId) FROM Goal g WHERE g.quarter = :quarter")
    long countDistinctEmployeesByQuarter(@Param("quarter") Quarter quarter);

    boolean existsByEmployeeIdAndPerformanceCycleAndQuarterAndTitle(
            String employeeId,
            PerformanceCycle cycle,
            Quarter quarter,
            String title);

    boolean existsByEmployeeIdAndQuarterAndYearAndTitle(
            String employeeId,
            Quarter quarter,
            Integer year,
            String title
    );

    List<Goal> findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatusIn(
            String employeeId,
            Quarter quarter,
            Integer year,
            GoalType goalType,
            List<GoalStatus> statuses
    );

    List<Goal> findByEmployeeIdAndQuarterAndYearAndSelfReviewIsNotNull(
            String employeeId,
            Quarter quarter,
            Integer year
    );

    List<Goal> findByEmployeeIdAndQuarterAndYearAndStatusIn(
            String employeeId,
            Quarter quarter,
            Integer year,
            List<GoalStatus> statuses
    );

    List<Goal> findByEmployeeIdAndQuarterAndYearAndGoalTypeAndStatus(
            String employeeId,
            Quarter quarter,
            Integer year,
            GoalType goalType,
            GoalStatus status
    );
}