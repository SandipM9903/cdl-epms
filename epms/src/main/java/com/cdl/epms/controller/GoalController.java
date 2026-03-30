package com.cdl.epms.controller;

import com.cdl.epms.common.enums.Quarter;
import com.cdl.epms.dto.goal.*;
import com.cdl.epms.dto.managerRating.ManagerRatingRequestDTO;
import com.cdl.epms.model.Goal;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.GoalService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.modelmapper.ModelMapper;

import java.util.List;

@RestController
@RequestMapping("/api/goals")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class GoalController {

    private final GoalService goalService;
    private final ModelMapper modelMapper;

    // ==================== PREDEFINED GOALS ENDPOINTS ====================

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
    public ResponseEntity<ApiResponse<List<GoalResponseDto>>> getPredefinedGoalsByEmployee(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter,
            @RequestParam Integer year
    ) {
        List<GoalResponseDto> goals = goalService.getPredefinedGoalsByEmployee(employeeId, quarter, year);
        ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
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
            @PathVariable("quarter") Quarter quarter,
            @RequestParam("year") Integer year
    ) {
        List<Goal> goals = goalService.getPredefinedGoalsByManager(managerId, employeeId, quarter, year);
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

    @PutMapping("/update-predefined")
    public ResponseEntity<ApiResponse<List<GoalResponseDto>>> updatePredefinedGoals(
            @Valid @RequestBody UpdatePredefinedGoalsRequestDto requestDto
    ) {
        List<Goal> updatedGoals = goalService.updatePredefinedGoals(requestDto);

        List<GoalResponseDto> responseDtos = updatedGoals.stream()
                .map(goal -> modelMapper.map(goal, GoalResponseDto.class))
                .toList();

        String message = requestDto.isSaveAsDraft()
                ? "Predefined goals saved as draft successfully"
                : "Predefined goals updated successfully";

        ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                .success(true)
                .message(message)
                .data(responseDtos)
                .build();

        return ResponseEntity.ok(response);
    }

    // ==================== SMART GOALS ENDPOINTS ====================

    @PostMapping("/smart/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> saveSmartGoal(
            @PathVariable("quarter") Quarter quarter,
            @RequestBody Goal goal
    ) {
        log.info("=== SAVE SMART GOAL API CALLED ===");
        log.info("Quarter: {}", quarter);
        log.info("EmployeeId: {}", goal.getEmployeeId());
        log.info("ManagerId: {}", goal.getManagerId());
        log.info("Title: {}", goal.getTitle());
        log.info("GoalDescription: {}", goal.getGoalDescription());
        log.info("TargetKPI: {}", goal.getTargetKPI());

        try {
            Goal savedGoal = goalService.saveSmartGoal(goal, quarter);
            ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                    .success(true)
                    .message("SMART goal created successfully")
                    .data(savedGoal)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.CREATED);
        } catch (Exception e) {
            log.error("ERROR in saveSmartGoal API: ", e);
            ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping("/smart/draft/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> saveSmartGoalAsDraft(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody Goal goal
    ) {
        log.info("Received request to save SMART goal as DRAFT");
        Goal savedGoal = goalService.saveSmartGoalAsDraft(goal, quarter);
        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("SMART goal saved as draft successfully")
                .data(savedGoal)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PostMapping("/smart/with-review/{quarter}")
    public ResponseEntity<ApiResponse<Goal>> saveSmartGoalWithSelfReview(
            @PathVariable("quarter") Quarter quarter,
            @Valid @RequestBody SmartGoalWithReviewRequestDto requestDto
    ) {
        log.info("Saving SMART goal with self-review for employee: {}", requestDto.getEmployeeId());

        Goal goal = new Goal();
        goal.setEmployeeId(requestDto.getEmployeeId());
        goal.setManagerId(requestDto.getManagerId());
        goal.setTitle(requestDto.getTitle());
        goal.setGoalDescription(requestDto.getGoalDescription());
        goal.setTargetKPI(requestDto.getTargetKPI());
        goal.setWeightage(requestDto.getWeightage() != null ? requestDto.getWeightage() : 0);
        goal.setAchievableTarget(requestDto.getAchievableTarget());
        goal.setSelfReviewComments(requestDto.getSelfReviewComments());

        Goal savedGoal = goalService.saveSmartGoalWithSelfReview(
                goal,
                quarter,
                requestDto.getOverallSelfAssessmentRating(),
                requestDto.getOverallSelfReviewComments()
        );

        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("SMART goal created and self-review submitted successfully")
                .data(savedGoal)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @GetMapping("/smart/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getSmartGoalsByEmployee(
            @PathVariable("employeeId") String employeeId,
            @PathVariable("quarter") Quarter quarter,
            @RequestParam("year") Integer year
    ) {
        List<Goal> goals = goalService.getSmartGoalsByEmployee(employeeId, quarter, year);
        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("SMART goals fetched successfully")
                .data(goals)
                .build();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/smart/draft/employee/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<Goal>>> getDraftSmartGoalsByEmployee(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter,
            @RequestParam Integer year
    ) {
        log.info("Fetching DRAFT SMART goals for employee: {}, quarter: {}, year: {}", employeeId, quarter, year);
        List<Goal> goals = goalService.getDraftSmartGoalsByEmployee(employeeId, quarter, year);
        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Draft SMART goals fetched successfully")
                .data(goals)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/smart/draft/{goalId}")
    public ResponseEntity<ApiResponse<Goal>> updateSmartGoalDraft(
            @PathVariable Long goalId,
            @Valid @RequestBody Goal goal
    ) {
        log.info("Received request to update SMART goal draft with ID: {}", goalId);
        Goal updatedGoal = goalService.updateSmartGoalDraft(goalId, goal);
        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("SMART goal draft updated successfully")
                .data(updatedGoal)
                .build();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/smart/draft/submit/{goalId}")
    public ResponseEntity<ApiResponse<Goal>> submitSmartGoalDraft(
            @PathVariable Long goalId
    ) {
        log.info("Received request to submit SMART goal draft with ID: {}", goalId);
        Goal submittedGoal = goalService.submitSmartGoalDraft(goalId);
        ApiResponse<Goal> response = ApiResponse.<Goal>builder()
                .success(true)
                .message("SMART goal draft submitted successfully")
                .data(submittedGoal)
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

    // ==================== DEVELOPMENT GOALS ENDPOINTS ====================

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
            @PathVariable("quarter") Quarter quarter,
            @RequestParam("year") Integer year
    ) {
        List<Goal> goals = goalService.getDevelopmentGoalsByEmployee(employeeId, quarter, year);
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

    // ==================== MANAGER ENDPOINTS ====================

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

    @PutMapping("/manager/review/submit")
    public ResponseEntity<ApiResponse<List<GoalResponseDto>>> submitManagerReview(
            @Valid @RequestBody ManagerReviewRequestDto requestDto
    ) {
        log.info("Received manager review submission request: {}", requestDto);

        try {
            List<Goal> updatedGoals = goalService.submitManagerReview(requestDto);
            List<GoalResponseDto> responseDtos = updatedGoals.stream()
                    .map(goal -> modelMapper.map(goal, GoalResponseDto.class))
                    .toList();

            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(true)
                    .message("Manager review submitted successfully")
                    .data(responseDtos)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting manager review", e);
            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
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

    // ==================== EMPLOYEE SELF REVIEW ENDPOINTS ====================

    @GetMapping("/employee/pending-self-review/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<List<GoalResponseDto>>> getPendingSelfReviewGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter,
            @RequestParam Integer year
    ) {
        log.info("Fetching pending self-review goals for employee: {}, quarter: {}, year: {}",
                employeeId, quarter, year);

        try {
            List<Goal> goals = goalService.getGoalsPendingSelfReview(employeeId, quarter, year);
            List<GoalResponseDto> responseDtos = goals.stream()
                    .map(goal -> modelMapper.map(goal, GoalResponseDto.class))
                    .toList();

            String message = goals.isEmpty()
                    ? "No goals pending self-review found"
                    : "Pending self-review goals fetched successfully";

            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(true)
                    .message(message)
                    .data(responseDtos)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error fetching pending self-review goals: {}", e.getMessage(), e);
            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(false)
                    .message("Error fetching pending self-review goals: " + e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }

    @PutMapping("/self-review/submit")
    public ResponseEntity<ApiResponse<List<GoalResponseDto>>> submitSelfReview(
            @Valid @RequestBody SelfReviewRequestDto requestDto
    ) {
        log.info("Received self-review submission request: {}", requestDto);

        try {
            List<Goal> updatedGoals = goalService.submitSelfReview(requestDto);
            List<GoalResponseDto> responseDtos = updatedGoals.stream()
                    .map(goal -> modelMapper.map(goal, GoalResponseDto.class))
                    .toList();

            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(true)
                    .message("Self-review submitted successfully")
                    .data(responseDtos)
                    .build();
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            log.error("Error submitting self-review", e);
            ApiResponse<List<GoalResponseDto>> response = ApiResponse.<List<GoalResponseDto>>builder()
                    .success(false)
                    .message(e.getMessage())
                    .data(null)
                    .build();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ACCEPTANCE ENDPOINTS ====================

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

    @PutMapping("/employee/self-accept/{employeeId}/{quarter}")
    public ResponseEntity<ApiResponse<String>> selfAcceptGoals(
            @PathVariable String employeeId,
            @PathVariable Quarter quarter,
            @RequestParam Integer year
    ) {
        goalService.selfAcceptGoals(employeeId, quarter, year);
        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Goals accepted successfully")
                .data("Accepted successfully")
                .build();
        return ResponseEntity.ok(response);
    }

    // ==================== HR ENDPOINTS ====================

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

    // ==================== ASSIGNMENT ENDPOINTS ====================

    @PostMapping("/assign-predefined")
    public ResponseEntity<ApiResponse<List<Goal>>> assignPredefinedGoals(
            @Valid @RequestBody AssignPredefinedGoalsRequestDto requestDto
    ) {
        List<Goal> savedGoals = goalService.assignPredefinedGoals(requestDto);
        ApiResponse<List<Goal>> response = ApiResponse.<List<Goal>>builder()
                .success(true)
                .message("Predefined goals assigned successfully")
                .data(savedGoals)
                .build();
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ==================== DELETE ENDPOINT ====================

    @DeleteMapping("/bulk")
    public ResponseEntity<ApiResponse<String>> deleteGoals(
            @RequestBody List<Long> goalIds
    ) {
        goalService.deleteGoals(goalIds);
        return ResponseEntity.ok(
                ApiResponse.<String>builder()
                        .success(true)
                        .message("Goals deleted successfully")
                        .data("Deleted")
                        .build()
        );
    }
}