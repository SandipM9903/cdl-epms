package com.cdl.epms.service.services;

import com.cdl.epms.dto.goalmaster.GoalMasterCategoryGroupDto;
import com.cdl.epms.dto.goalmaster.GoalMasterRequestDto;
import com.cdl.epms.dto.goalmaster.GoalMasterResponseDto;

import java.util.List;
import java.util.Map;

public interface GoalMasterService {

    GoalMasterResponseDto createGoalMaster(GoalMasterRequestDto requestDto, String createdBy);

    GoalMasterResponseDto updateGoalMaster(Long id, GoalMasterRequestDto requestDto, String updatedBy);

    GoalMasterResponseDto getGoalMasterById(Long id);

    List<GoalMasterResponseDto> getAllGoalMasters();

    List<GoalMasterCategoryGroupDto> getActiveGoalMastersGroupedByCategory();

    List<GoalMasterResponseDto> getGoalMastersByCategory(String category);

    List<String> getAllCategories();

    Map<String, Long> getCategoryCounts();

    void deleteGoalMaster(Long id);

    void toggleActiveStatus(Long id, Boolean isActive);
}