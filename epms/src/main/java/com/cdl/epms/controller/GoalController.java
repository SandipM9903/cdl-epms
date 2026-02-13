package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.managerRating.ManagerRatingRequestDTO;
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

    // ================= DEVELOPMENT GOALS (EMPLOYEE) =================

    @PostMapping("/development/{quarter}")
    public ResponseEntity<Goal> saveDevelopmentGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {
        Goal saved = goalService.saveDevelopmentGoal(goal, quarter);
        return ResponseEntity.status(HttpStatus.CREATED).body(saved);
    }

    @GetMapping("/development/employee/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getDevelopmentGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getDevelopmentGoalsByEmployee(employeeId, quarter));
    }

    @PutMapping("/development/submit/{employeeId}/{quarter}")
    public ResponseEntity<String> submitDevelopmentGoals(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {
        goalService.submitDevelopmentGoals(employeeId, quarter);
        return ResponseEntity.ok("Development goals submitted successfully");
    }

    // ================= MANAGER REVIEW (R1) =================

    @GetMapping("/manager/{managerId}/team/{quarter}")
    public ResponseEntity<List<String>> getTeamEmployees(
            @PathVariable String managerId,
            @PathVariable Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getTeamEmployeesByManager(managerId, quarter));
    }

    @GetMapping("/manager/{managerId}/employee/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getEmployeeGoalsForManager(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getGoalsForManagerReview(managerId, employeeId, quarter));
    }

    @PutMapping("/manager/review")
    public ResponseEntity<Goal> updateManagerReview(@RequestBody ManagerRatingRequestDTO dto) {

        Goal updated = goalService.updateManagerReview(
                dto.getGoalId(),
                dto.getManagerRating(),
                dto.getManagerRemark()
        );

        return ResponseEntity.ok(updated);
    }

    @PutMapping("/manager/submit-to-employee/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<String> submitManagerReviewToEmployee(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {
        goalService.submitManagerReviewToEmployee(managerId, employeeId, quarter);
        return ResponseEntity.ok("Manager review submitted to employee successfully");
    }

    @GetMapping("/employee/pending-acceptance/{employeeId}/{quarter}")
    public ResponseEntity<List<Goal>> getPendingAcceptanceGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {
        return ResponseEntity.ok(goalService.getPendingGoalsForAcceptance(employeeId, quarter));
    }

    @PutMapping("/employee/accept/{employeeId}/{quarter}")
    public ResponseEntity<String> acceptReviewedGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {
        goalService.acceptReviewedGoals(employeeId, quarter);
        return ResponseEntity.ok("Goals accepted successfully");
    }

    @PutMapping("/final-submit/{employeeId}/{quarter}")
    public ResponseEntity<String> finalSubmitToHR(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {
        goalService.finalSubmitToHR(employeeId, quarter);
        return ResponseEntity.ok("Goals final submitted to HR successfully");
    }
}