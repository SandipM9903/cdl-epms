package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.managerRating.ManagerRatingRequestDTO;
import com.cdl.epms.model.Goal;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GoalController {

    private final GoalService goalService;

    @PostMapping("/predefined/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> savePredefinedGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {

        Goal savedGoal = goalService.savePredefinedGoal(goal, quarter);

        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("Predefined goal created successfully")
                .data(savedGoal)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/predefined/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getPredefinedGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        List<Goal> goals = goalService.getPredefinedGoalsByEmployee(employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Predefined goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/predefined/manager/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getPredefinedGoalsByManager(
            @PathVariable("managerId") String managerId,
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        List<Goal> goals = goalService.getPredefinedGoalsByManager(managerId, employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Predefined goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/predefined/submit/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> submitPredefinedGoals(
            @PathVariable("managerId") String managerId,
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        goalService.submitPredefinedGoals(managerId, employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Predefined goals submitted successfully")
                .data("Submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/smart/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> saveSmartGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {

        Goal savedGoal = goalService.saveSmartGoal(goal, quarter);

        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("SMART goal created successfully")
                .data(savedGoal)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/smart/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getSmartGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        List<Goal> goals = goalService.getSmartGoalsByEmployee(employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("SMART goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/smart/submit/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> submitSmartGoals(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        goalService.submitSmartGoals(employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("SMART goals submitted successfully")
                .data("Submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PostMapping("/development/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> saveDevelopmentGoal(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {

        Goal savedGoal = goalService.saveDevelopmentGoal(goal, quarter);

        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("Development goal created successfully")
                .data(savedGoal)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/development/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getDevelopmentGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        List<Goal> goals = goalService.getDevelopmentGoalsByEmployee(employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Development goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/development/submit/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> submitDevelopmentGoals(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter
    ) {

        goalService.submitDevelopmentGoals(employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Development goals submitted successfully")
                .data("Submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/manager/{managerId}/team/{quarter}")
    public ResponseEntity<ApiResponse<List<String>>> getTeamEmployees(
            @PathVariable String managerId,
            @PathVariable Quarter quarter
    ) {

        List<String> employees = goalService.getTeamEmployeesByManager(managerId, quarter);

        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .success(true)
                .message("Team employees fetched successfully")
                .data(employees)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/manager/{managerId}/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getEmployeeGoalsForManager(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {

        List<Goal> goals = goalService.getGoalsForManagerReview(managerId, employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Employee goals fetched successfully for manager review")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/manager/review")
    public ResponseEntity<ApiResponse<Goal>> updateManagerReview(
            @Valid @RequestBody ManagerRatingRequestDTO dto
    ) {

        Goal updatedGoal = goalService.updateManagerReview(
                dto.getGoalId(),
                dto.getManagerRating(),
                dto.getManagerRemark()
        );

        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("Manager review updated successfully")
                .data(updatedGoal)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/manager/submit-to-employee/{managerId}/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> submitManagerReviewToEmployee(
            @PathVariable String managerId,
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {

        goalService.submitManagerReviewToEmployee(managerId, employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Manager review submitted to employee successfully")
                .data("Submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/employee/pending-acceptance/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getPendingAcceptanceGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {

        List<Goal> goals = goalService.getPendingGoalsForAcceptance(employeeId, quarter);

        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Pending acceptance goals fetched successfully")
                .data(goals)
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/employee/accept/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> acceptReviewedGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {

        goalService.acceptReviewedGoals(employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Goals accepted successfully")
                .data("Accepted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PutMapping("/final-submit/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> finalSubmitToHR(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter
    ) {

        goalService.finalSubmitToHR(employeeId, quarter);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Goals final submitted to HR successfully")
                .data("Final submitted successfully")
                .build();

        return ResponseEntity.ok(response);
    }
}