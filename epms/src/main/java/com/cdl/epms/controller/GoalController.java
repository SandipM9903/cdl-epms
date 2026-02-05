package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.model.Goal;
import com.cdl.epms.service.services.GoalService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
public class GoalController {

    private final GoalService goalService;

    public GoalController(GoalService goalService) {
        this.goalService = goalService;
    }

    @PostMapping("/predefined/{quarter}")
    public ResponseEntity<Goal> savePredefinedGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {
        Goal saved = goalService.savePredefinedGoal(goal, quarter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/predefined/employee/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getPredefinedGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getPredefinedGoalsByEmployee(employeeId, quarter));
    }

    @GetMapping("/predefined/manager/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getPredefinedGoalsByManager(
            @PathVariable("managerId") String managerId,
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getPredefinedGoalsByManager(managerId, employeeId, quarter));
    }

    @PutMapping("/predefined/submit/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<String> submitPredefinedGoals(
            @PathVariable("managerId") String managerId,
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        goalService.submitPredefinedGoals(managerId, employeeId, quarter);
        return ResponseEntity.ok("Predefined goals submitted successfully");
    }

    @PostMapping("/smart/{quarter}")
    public ResponseEntity<Goal> saveSmartGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {
        Goal saved = goalService.saveSmartGoal(goal, quarter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/smart/employee/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getSmartGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getSmartGoalsByEmployee(employeeId, quarter));
    }

    @PutMapping("/smart/submit/{employeeId}/{quarter}")
    public ResponseEntity<String> submitSmartGoals(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        goalService.submitSmartGoals(employeeId, quarter);
        return ResponseEntity.ok("SMART goals submitted successfully");
    }
}