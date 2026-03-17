package com.cdl.epms.controller;

import com.cdl.epms.dto.goalmaster.GoalMasterCategoryGroupDto;
import com.cdl.epms.dto.goalmaster.GoalMasterRequestDto;
import com.cdl.epms.dto.goalmaster.GoalMasterResponseDto;
import com.cdl.epms.payload.ApiResponse;
import com.cdl.epms.service.services.GoalMasterService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/goal-master")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class GoalMasterController {

    private final GoalMasterService goalMasterService;

    @PostMapping
    public ResponseEntity<ApiResponse<GoalMasterResponseDto>> createGoalMaster(
            @Valid @RequestBody GoalMasterRequestDto requestDto,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String createdBy
    ) {
        GoalMasterResponseDto createdGoal = goalMasterService.createGoalMaster(requestDto, createdBy);

        ApiResponse<GoalMasterResponseDto> response = ApiResponse.<GoalMasterResponseDto>builder()
                .success(true)
                .message("Goal master created successfully")
                .data(createdGoal)
                .build();

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalMasterResponseDto>> updateGoalMaster(
            @PathVariable Long id,
            @Valid @RequestBody GoalMasterRequestDto requestDto,
            @RequestHeader(value = "X-User-Id", defaultValue = "SYSTEM") String updatedBy
    ) {
        GoalMasterResponseDto updatedGoal = goalMasterService.updateGoalMaster(id, requestDto, updatedBy);

        ApiResponse<GoalMasterResponseDto> response = ApiResponse.<GoalMasterResponseDto>builder()
                .success(true)
                .message("Goal master updated successfully")
                .data(updatedGoal)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GoalMasterResponseDto>> getGoalMasterById(@PathVariable Long id) {
        GoalMasterResponseDto goalMaster = goalMasterService.getGoalMasterById(id);

        ApiResponse<GoalMasterResponseDto> response = ApiResponse.<GoalMasterResponseDto>builder()
                .success(true)
                .message("Goal master fetched successfully")
                .data(goalMaster)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<GoalMasterResponseDto>>> getAllGoalMasters() {
        List<GoalMasterResponseDto> goalMasters = goalMasterService.getAllGoalMasters();

        ApiResponse<List<GoalMasterResponseDto>> response = ApiResponse.<List<GoalMasterResponseDto>>builder()
                .success(true)
                .message("Goal masters fetched successfully")
                .data(goalMasters)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/grouped")
    public ResponseEntity<ApiResponse<List<GoalMasterCategoryGroupDto>>> getActiveGoalMastersGrouped() {
        List<GoalMasterCategoryGroupDto> groupedGoals = goalMasterService.getActiveGoalMastersGroupedByCategory();

        ApiResponse<List<GoalMasterCategoryGroupDto>> response = ApiResponse.<List<GoalMasterCategoryGroupDto>>builder()
                .success(true)
                .message("Goal masters grouped by category fetched successfully")
                .data(groupedGoals)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<ApiResponse<List<GoalMasterResponseDto>>> getGoalMastersByCategory(
            @PathVariable String category
    ) {
        List<GoalMasterResponseDto> goalMasters = goalMasterService.getGoalMastersByCategory(category);

        ApiResponse<List<GoalMasterResponseDto>> response = ApiResponse.<List<GoalMasterResponseDto>>builder()
                .success(true)
                .message("Goal masters for category " + category + " fetched successfully")
                .data(goalMasters)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<String>>> getAllCategories() {
        List<String> categories = goalMasterService.getAllCategories();

        ApiResponse<List<String>> response = ApiResponse.<List<String>>builder()
                .success(true)
                .message("Categories fetched successfully")
                .data(categories)
                .build();

        return ResponseEntity.ok(response);
    }

    @GetMapping("/category-counts")
    public ResponseEntity<ApiResponse<Map<String, Long>>> getCategoryCounts() {
        Map<String, Long> categoryCounts = goalMasterService.getCategoryCounts();

        ApiResponse<Map<String, Long>> response = ApiResponse.<Map<String, Long>>builder()
                .success(true)
                .message("Category counts fetched successfully")
                .data(categoryCounts)
                .build();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteGoalMaster(@PathVariable Long id) {
        goalMasterService.deleteGoalMaster(id);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Goal master deleted successfully")
                .data("Deleted successfully")
                .build();

        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<ApiResponse<String>> toggleActiveStatus(
            @PathVariable Long id,
            @RequestParam Boolean isActive
    ) {
        goalMasterService.toggleActiveStatus(id, isActive);

        ApiResponse<String> response = ApiResponse.<String>builder()
                .success(true)
                .message("Goal master status updated successfully")
                .data("Status updated to " + isActive)
                .build();

        return ResponseEntity.ok(response);
    }
}